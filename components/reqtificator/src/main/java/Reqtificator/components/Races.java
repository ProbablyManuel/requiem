/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.components;

import Reqtificator.FormIDStash;
import Reqtificator.exceptions.PatchingException;
import Reqtificator.exceptions.UnexpectedException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;
import skyproc.RACE.AttackData;
import skyproc.genenums.Gender;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**The patching component for Races.
 * This component will ensure that all playable races (Vanilla, Vanilla Vampires
 * and custom playable races) are properly set up for Requiem. If any race fails
 * the tests, the patch will be aborted and the user will be informed about the
 * failing race.
 * To pass the sanity tests, a playable race must fulfill the following
 * conditions:<ul>
 * <li>have the NPC MassEffect spell</li>
 * <li>have the MassEffect baseline spell</li>
 * <li>have more than 50 points in all three primary attributes</li></ul>
 * If defined in the Reqtificator.ini, this component will also take care of
 * merging the visuals from the specified template mods with the gameplay
 * changes of Requiem (or any patch derived from it).
 *
 * @author Ogerboss
 */
public class Races extends Component {

    private final static Logger logger = LogManager.getLogger();
    final private TextManager texts;
    /**The set of all mods that are allowed to provide visual race templates.
     */
    private final Set<ModListing> visuals;

    public Races (TextManager texts, Set<ModListing> visuals_races) {
        super();
        this.texts = texts;
        visuals = visuals_races;
    }

    /**Visual-Merge races and test their sanity.
     * This function iterates over all races in the merger, merges those that
     * have visual and skill templates and tests the sanity for all playable
     * and Vanilla vampire races.
     *
     * @param merger the merged import mod with the races to process
     * @param patch the global patch for exporting modified records
     * @throws PatchingException if a raee fails the sanity test
     * @throws UnexpectedException wrapper around any unexpected errors
     */
    public void reqtify_races(Mod merger, Mod patch) throws PatchingException,
            UnexpectedException {
        GRUP<RACE> races = merger.getRaces();
        int maxRecords = races.numRecords();
        startCategory(maxRecords, "Races",
                texts.format("patch.gui_titles.races"));
        FormID massEffectBase = FormIDStash.spell_ME_base;
        FormID massEffectNpc = FormIDStash.spell_ME_npc;
        ThreadContext.push(FormIDStash.formlist_ME_races.toString());
        FLST playableRacesFormList = (FLST) merger.getMajor(FormIDStash.formlist_ME_races, GRUP_TYPE.FLST);
        List<FormID> playableRaces = playableRacesFormList.getFormIDEntries();
        ThreadContext.pop();
        String passed = texts.format("patch.races.check_passed");
        String failed = texts.format("patch.races.check_failed");
        boolean[] checks = new boolean[5];
        String states[] = new String[5];
        for (RACE race: races) {
            ThreadContext.put(contextRecord, formatRecord(race));
            try {
                RACE mergedRace = mergeRaces(race);
                if (mergedRace != null) {
                    race = mergedRace;
                    patch.addRecord(race);
                }
                if (playableRaces.contains(race.getForm())
                        || race.get(RACE.RACEFlags.Playable)) {
                    checks[0] = race.getSpells().contains(massEffectNpc);
                    checks[1] = race.getSpells().contains(massEffectBase);
                    checks[2] = race.getStartingHealth() > 50.0;
                    checks[3] = race.getStartingMagicka() > 50.0;
                    checks[4] = race.getStartingStamina() > 50.0;
                    if (!(checks[0] && checks[1] && checks[2] && checks[3]
                            && checks[4])) {
                        for (int i=0; i < 5; i++) {
                            states[i] = checks[i] ? passed : failed;
                        }
                        String advice;
                        if (playableRaces.contains(race.getForm())) {
                            advice = texts.format("patch.races.vanilla_race",
                                    texts.format("urls.vanilla_races", true));
                        } else {
                            advice = texts.format("patch.races.custom_race",
                                    texts.format("urls.custom_races", true));
                        }
                        String message = texts.format(
                                "patch.races.race_failed_tests",
                                formatRecordPretty(race), advice, states[0],
                                states[1], states[2], states[3], states[4]);
                        throw new PatchingException("race sanity tests failed",
                                message);
                    }
                }
                updateProgressBar();
            } catch (PatchingException ex) {
              throw ex;
            } catch (Exception ex) {
            throw new UnexpectedException(ex.getMessage(),
                    ex.getLocalizedMessage(), race, ex);
            }
        }
        endCategory();
    }

    /**Inspect the import history of a race and merge it, if appropriate.
     * Try to extract a skill and visual template from the import history of the
     * given record. If both are found, they are merged and the merge result is
     * returned.
     *
     * @param current the last overwrite of the given race in the load order
     * @return either the merge result race or null, if no merge has been done
     */
    private RACE mergeRaces(RACE current) {
        if (this.visuals.isEmpty()) {
            return null;
        }
        RACE stats = null;
        RACE visual = null;
        Mod overwrite = SPDatabase.getMod(current.getModImportedFrom());
        boolean from_requiem;
        boolean from_visual;
        from_requiem = overwrite == requiem ||
                overwrite.getMasters().contains(requiem_ml);
        from_visual = visuals.contains(current.getModImportedFrom());
        //if the last overwrite is neither from a Requiem(-dependent) mod nor a
        //registered visual mod, we do no cosmetic merges at all and end here
        if (!(from_requiem || from_visual)) {
            return null;
        }
        //let's figure out the  mods that shall determine stats and visuals
        ArrayList<MajorRecord> overwritelist = current.getRecordHistory();
        for (int i=overwritelist.size()-1;i > 0;i--) {
            RACE imported = (RACE) overwritelist.get(i);
            ModListing overwriteML = imported.getModImportedFrom();
            overwrite = SPDatabase.getMod(overwriteML);
            if (visuals.contains(overwriteML) && visual == null) {
                visual = imported;
            }
            from_requiem = overwriteML.equals(requiem_ml) ||
                    overwrite.getMasters().contains(requiem_ml);
            if (from_requiem && stats == null) {
                stats = imported;
            }
        }
        if ((visual == null || stats == null)) {
            return null;
        }
        visual_merge(visual, stats);
        logger.debug(MessageFormat.format(
                "merged {0} game play data with {1} visuals",
                stats.getModImportedFrom(), visual.getModImportedFrom()));
        return visual;
    }

    /**Merge the gameplay-relevant information into the visual template.
     * This merge function will overwrite all gameplay-relevant information
     * (spells, core stats, core combat data, keywords, racial skills) in the
     * given visual template with the data from the specified stats template
     *
     * @param visual merge target with visual appearance to use
     * @param stats merge source with gameplay-relevant data to use
     */
    private void visual_merge(RACE visual, RACE stats) {
        //replace spells
        visual.clearSpells();
        for (FormID ability: stats.getSpells()) {
            visual.addSpell(ability);
        }
        //copy core stats
        visual.setStartingHealth(stats.getStartingHealth());
        visual.setStartingStamina(stats.getStartingStamina());
        visual.setStartingMagicka(stats.getStartingMagicka());
        visual.setHealthRegen(stats.getHealthRegen());
        visual.setStaminaRegen(stats.getStaminaRegen());
        visual.setMagickaRegen(stats.getMagickaRegen());
        visual.setBaseCarryWeight(stats.getBaseCarryWeight());
        //copy core combat data
        visual.clearAttackData();
        for (AttackData data: stats.getAttackData()) {
            visual.addAttackData(data);
        }
        visual.setUnarmedDamage(stats.getUnarmedDamage());
        visual.setUnarmedReach(stats.getUnarmedReach());
        //replace keywords
        visual.getKeywordSet().clearKeywordRefs();
        for (FormID keyword: stats.getKeywordSet().getKeywordRefs()) {
            visual.getKeywordSet().addKeywordRef(keyword);
        }
        //replace racial skill bonuses
        for (int i=0; i<7; i++) {
            visual.setSkillBoost(i, stats.getSkillBoostSkill(i), stats.getSkillBoostValue(i));
        }
        visual.setDescription(stats.getDescription());
        //height affects the movement speed and thus affects gameplay
        visual.setHeight(Gender.MALE, stats.getHeight(Gender.MALE));
        visual.setHeight(Gender.FEMALE, stats.getHeight(Gender.FEMALE));
        //and finally the common flags, if there's a mismatch, the skill
        //template takes precedence, but we write a warning about it to the log
        for (RACE.RACEFlags flag : RACE.RACEFlags.values()) {
            if (visual.get(flag) != stats.get(flag)) {
                logger.warn(MessageFormat.format(
                        "flag {0} differs on game play and visual templates",
                        flag.toString()));
            }
            visual.set(flag, stats.get(flag));
        }
        for (RACE.RaceFlags2 flag : RACE.RaceFlags2.values()) {
            if (visual.get(flag) != stats.get(flag)) {
                logger.warn(MessageFormat.format(
                        "flag {0} differs on game play and visual templates",
                        flag.toString()));
            }
            visual.set(flag, stats.get(flag));
        }
    }
}
