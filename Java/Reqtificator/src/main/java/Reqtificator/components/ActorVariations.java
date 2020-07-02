/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.components;

import Reqtificator.exceptions.PatchingException;
import Reqtificator.exceptions.UnexpectedException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;
import skyproc.NPC_.TemplateFlag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

/**The patching component for ActorVariations.
 * This component creates the ActorVariations for Requiem, which improve the
 * visual variety of generic actors like guards and bandits. Instead of defining
 * an actor with gameplay and visual data, Requiem splits actors in two distinct
 * parts: the skills and the visuals. By encoding meta-data into Leveled
 * Characters, the Reqtificator can combine each skill template with a large
 * number of visual templates to create a variety in race, gender and look for
 * each skill template.
 *
 * @author Ogerboss
 */
public class ActorVariations extends Component{

    /**Maps the keys of already processed skill-look pairs to their result.
     */
    private final TreeMap<VariationIndex, LVLN> variationmap;

    /**The ActorMerger to use for relinking actor templates.
     */
    private final ActorFusion fusion;

    private final static Logger logger = LogManager.getLogger();

    final private TextManager texts;

    public ActorVariations(TextManager texts) {
        super();
        this.texts = texts;
        this.fusion = new ActorFusion(texts);
        variationmap = new TreeMap<>();
    }

    /**Test the correctness of the metadata in the ActorVariations.
     * The ActorVariations contain meta-data in the form of the count and level
     * fields in the LeveledCharacter's entries. Since there are unleveling
     * scripts for Tes5Edit flying around, these might destroy this metadata
     * if applied before the Reqtificator.
     *
     * @param workload the list of ActorVariations to check
     * @param merger the import mod to use for "is Actor" tests
     * @throws PatchingException if none of the tested ActorVariations has any
     * leveled actors
     */
    private void test_metadata(ArrayList<LVLN> workload, Mod merger)
            throws PatchingException{
        for (LVLN list: workload) {
            ThreadContext.push(formatRecord(list));
            for (LeveledEntry entry: list) {
                FormID fid = entry.getForm();
                NPC_ test = (NPC_) merger.getMajor(fid, GRUP_TYPE.NPC_);
                if (test != null && entry.getLevel() > 1) {
                    return;
                }
            }
            ThreadContext.pop();
        }
        String msg = texts.format(
                "patch.actor_variations.error_unleveled_data");
        throw new PatchingException("invalid actor variation meta data", msg);
    }

    /**
     * Generate all valid actor variations from the leveled lists in the
     * workload and export them to the patch. Creating NPC variations consists
     * of two steps: first create a new LeveledCharacter for each Actor-LChar
     * combination in the processed LeveledCharacter and fill it with new actors
     * that have the skills of the single actors and the visuals of each actor
     * in the LChar. Then add the new LChars to the processed LChar and use the
     * count of the replaced LChar to determine the number of instances to add
     * to the processed list.
     * At the end, the original FormList thus will contain a list of LChars with
     * the skills of the original actors, but all the possible visuals
     * variations that were contained in the original LChars.
     * In addition, symbolic or hard links (user's choice) will be created for
     * each new actor's FaceGen data to overcome Skyrim's grey face bug.
     * @param merger the merged import
     * @param patch the patch for exporting modified records
     * @param workload the list LeveledCharacters to process
     * @throws PatchingException if the reference FaceGen-data is missing
     */
    public void reqtifyActorVariations(Mod merger, Mod patch,
                                       ArrayList<LVLN> workload)
            throws PatchingException, UnexpectedException {
        int maxRecords = workload.size();
        startCategory(maxRecords, "Actor Variations",
                texts.format("patch.gui_titles.actor_variations"));
        test_metadata(workload, merger);
        TreeMap<NPC_, Integer> templates_skill = new TreeMap<>(
                new FID_Comparator());
        TreeMap<LVLN, Integer> templates_look = new TreeMap<>(
                new FID_Comparator());
        ArrayList<LVLN> spawnlist = new ArrayList<>();
        for (LVLN variation: workload) {
            ThreadContext.put(contextRecord, formatRecord(variation));
            try {
                templates_look.clear();
                templates_skill.clear();
                partition_variation(variation, merger, templates_skill,
                        templates_look);
                LVLN container;
                spawnlist.clear();
                for (NPC_ skill : templates_skill.keySet()) {
                    ThreadContext.push(formatRecord(skill));
                    for (LVLN look : templates_look.keySet()) {
                        ThreadContext.push(formatRecord(look));
                        VariationIndex key = new VariationIndex(skill, look);
                        if (variationmap.containsKey(key)) {
                            container = variationmap.get(key);
                        } else {
                            container = create_variation(skill, look, merger);
                            variationmap.put(key, container);
                        }
                        int totalcount = templates_skill.get(skill)
                                * templates_look.get(look);
                        for (int k = 0; k < totalcount; k++) {
                            spawnlist.add(container);
                        }
                        ThreadContext.pop();
                    }
                    ThreadContext.pop();
                }
                variation.clearEntries();
                for (LVLN spawn : spawnlist) {
                    variation.addEntry(spawn.getForm(), 1, 1);
                }
                logger.debug("created actor variations");
                patch.addRecord(variation);
                updateProgressBar();
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage(),
                        ex.getLocalizedMessage(), variation, ex);
            }
        }
        endCategory();
    }

    /** Create a new varied Leveled Character.
     * This function will at first create a new actor for each actor in the
     * look LChar. These actors are the visuals from the LChar entry merged with
     * the stats and skills of the specified skill actor. Afterwards, the
     * resulting actors are collected in a new leveled character. The usage of
     * the FormIDManager ensures that already existing combinations keep their
     * FormID in consecutive patches.
     * @param skill the actor whose skills and stats should be used
     * @param look the LChar with all actors whose visuals should be used
     * @param merger the global importer, needed to extract NPCs from the LChar
     * @return the leveled Character with all the new actors
     * @throws PatchingException if inheritances are not defined uniquely
     */
    private LVLN create_variation(NPC_ skill, LVLN look, Mod merger)
             throws PatchingException {
        String eid = "REQI_LChar_Variations_" + skill.getFormStr() + "_"
                + look.getFormStr();
        LVLN container = new LVLN(eid);
        for (FormID fid_look: look.getEntryForms()) {
            NPC_ lookactor = (NPC_) merger.getMajor(fid_look, GRUP_TYPE.NPC_);
            ThreadContext.push(formatRecord(lookactor));
            eid = "REQI_Actor_Variations_" + skill.getFormStr() + "_"
                    + lookactor.getFormStr();
            NPC_ newactor = fusion.fuse_actors(skill, lookactor.getForm(),
                    eid, TemplateFlag.USE_TRAITS, TemplateFlag.USE_ATTACK_DATA);
            ThreadContext.push(formatRecord(newactor));
            container.addEntry(newactor.getForm(), 1, 1);
            ThreadContext.pop();
            ThreadContext.pop();
        }
        return container;
    }

    /**Partition an ActorVariation LeveledCharacter into its components.
     * This method will separate the contained actors (skill templates) and
     * leveled characters (look template lists) and also extract their spawn
     * likeliness.
     * @param template the ActorVariation to partition
     * @param merger the global importer, used to translate the FormIDs in the
     * template into either actors or LChars
     * @param templates_skill data container for skill templates
     * @param templates_look data container for look templates
     * @throws PatchingException if the ActorVariation contains unexpected data
     */
    private void partition_variation(LVLN template, Mod merger,
            TreeMap<NPC_, Integer> templates_skill,
            TreeMap<LVLN, Integer> templates_look) throws PatchingException {
        String detail = null;
        for (LeveledEntry entry: template.getEntries()) {
            FormID fid = entry.getForm();
            NPC_ skill = (NPC_) merger.getMajor(fid, GRUP_TYPE.NPC_);
            LVLN look = (LVLN) merger.getMajor(fid, GRUP_TYPE.LVLN);
            if (skill != null) {
                ThreadContext.push(formatRecord(skill));
                if (templates_skill.containsKey(skill)) {
                    detail = texts.format(
                            "patch.actor_variations.error_multiskill",
                            formatRecord(skill));
                    break;
                } else if (entry.getCount() != 1) {
                    detail = texts.format(
                            "patch.actor_variations.error_factors_skill",
                            formatRecord(skill));
                    break;
                }
                templates_skill.put(skill, entry.getLevel());
            }
            else if (look != null) {
                ThreadContext.push(formatRecord(look));
                if (templates_look.containsKey(look)) {
                    detail = texts.format(
                            "patch.actor_variations.error_multi_looks",
                            formatRecord(look));
                    break;
                } else if (entry.getLevel() != 1) {
                    detail = texts.format(
                            "patch.actor_variations.error_factors_look",
                            formatRecord(look));
                    break;
                }
                templates_look.put(look, entry.getCount());
            } else {
                detail = texts.format(
                        "patch.actor_variations.error_unknown_record",
                        fid.toString());
                break;
            }
            ThreadContext.pop();
        }
        if (detail != null) {
            String msg = texts.format(
                    "patch.actor_variations.error_variations",
                    formatRecordPretty(template), detail,
                    texts.format("urls.actor_variations", true));
            throw new PatchingException("invalid actor variation data", msg);
        }
    }

    /**Helper class for defining a unique order of FormIDs.
     * To ensure that the FormIDs in the treeMaps are well-ordered, we define
     * a custom comparison here, which is just a string comparison of their
     * FormStrings.
     */
    private class FID_Comparator implements Comparator<MajorRecord> {

        @Override
        public int compare(MajorRecord o1, MajorRecord o2) {
            return o1.getFormStr().compareTo(o2.getFormStr());
        }

    }

    /**Index class, represents a skill+look combination.
     * This is a helper class used as a map index. It represents the
     * combination of a skill actor with a look leveled list.
     */
    private class VariationIndex implements Comparable<VariationIndex>{

        /**The Actor which provides the skills.
         */
        NPC_ target;

        /**The LeveledLists that provides the looks to be used.
         */
        LVLN variations;

        /**Create a new map key for the given skill/look pair.
         *
         * @param target the NPC providing the skills
         * @param variations the LeveledCharacter providing the looks
         */
        private VariationIndex(NPC_ target, LVLN variations) {
            this.target = target;
            this.variations = variations;
        }

        @Override
        public int compareTo(VariationIndex other) {
            int comp = target.getEDID().compareTo(other.target.getEDID());
            if (comp == 0) {
                comp = variations.getEDID().compareTo(other.variations.getEDID());
            }
            return comp;
        }
    }
}
