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
import skyproc.GRUP;
import skyproc.LVLN;
import skyproc.Mod;
import skyproc.ModListing;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Processing class for Leveled Characters.
 * This class processes the Leveled Characters. The performed operations are the
 * unrolling of compact leveled lists, merging changes from Requiem patches and
 * the identification of ActorVariations. Due to the complexity of
 * ActorVariations their treatment is deferred to a specialized class
 * <code>ActorVariations</code>.
 * @author Ogerboss
 */
public class LeveledCharacters extends LeveledLists {

    private final static Logger logger = LogManager.getLogger();
    final private TextManager texts;

    /**The allowed origin mods for unrolling Leveled Characters.
     */
    private final Set<ModListing> modsWithCompactLists;
    /**The allowed origin mods for ActorVariaton Leveled Characters.
     */
    private final Set<ModListing> modsWithActorVariations;
    /**RegEx pattern for compact Leveled Characters.
     * If the EditorID of a record matches this pattern and the extracted prefix
     * is found in <code>modsWithCompactLists</code>, it is identified as compact
     * Leveled Character.
     */
    private final Pattern re_unroll;
    /**RegEx pattern for ActorVariation Leveled Characters.
     * If the EditorID of a record matches this pattern and the extracted prefix
     * is found in <code>modsWithActorVariations</code>, it is identified as an
     * ActorVariation type of Leveled Character.
     */
    private final Pattern re_variation;

    /**Create a new LeveledCharacter processing instance. While processing
     * LeveledItems, it will merge changes made by Requiem patches and it will
     * unroll suitable LeveledItems given in compact notation into a
     * Skyrim-understandable version.
     *
     * @param modsWithCompactLists list of mods containing compact leveled lists
     * @param modsWithActorVariations list of mods containing ActorVariations
     * @param merger the merged import mod to fetch the exclusive lists
     * @throws PatchingException if a patch requested exclusive edit rights on
     * a form it does not edit
     */
    public LeveledCharacters(TextManager texts, Set<ModListing> modsWithCompactLists,
                             Set<ModListing> modsWithActorVariations, Mod merger)
            throws PatchingException {
        super(texts, merger);
        this.texts = texts;
        re_unroll = Pattern.compile("^[^_]+_CLChar_.+",
                Pattern.CASE_INSENSITIVE);
        re_variation = Pattern.compile("^[^_]+_LChar_(Variations|VoiceSpawns).+",
                Pattern.CASE_INSENSITIVE);
        this.modsWithCompactLists = modsWithCompactLists;
        this.modsWithActorVariations = modsWithActorVariations;
    }

    /**Reqtify LeveledCharacters.
     * Loop over the LeveledCharacters and reqtify those that have merge
     * conflicts from Requiem-patches. Also the Requiem-defined compact
     * spawnlists are unrolled. (These use counts = N entries instead of N
     * entries to denote the spawn probabilities in an easier editable way.)
     * Actorvariations are not processed directly, but the function returns a
     * list of all LeveledCharacters that were identified as ActorVariations.
     *
     * @param merger the mod contained the merged imports
     * @param patch the global to export to
     * @param merge true if LeveledCharacter edits from patches shall be merged
     * @return an ArrayList of all records that need to be processed as
     * ActorVariations
     * @throws PatchingException if an error occurs while merging changes from
     * Requiem patches
     */
    public ArrayList<LVLN> reqtifyLeveledChars(Mod merger, Mod patch, boolean
            merge) throws PatchingException, UnexpectedException {
        GRUP<LVLN> leveledChars = merger.getLeveledCreatures();
        ArrayList<LVLN> actorvariations = new ArrayList<>();
        int maxRecords = leveledChars.numRecords();
        startCategory(maxRecords, "Leveled Chars",
                texts.format("patch.gui_titles.leveled_chars"));
        for (LVLN lChar: leveledChars) {
            ThreadContext.put(contextRecord, formatRecord(lChar));
            try {
                boolean changed = false;
                if (is_merge_candidate(merge, lChar)) {
                    changed = true;
                    lChar = (LVLN) merge_lists(lChar,
                            shouldBeUnrolled(lChar));
                }
                if (isActorVariation(lChar)) {
                    actorvariations.add(lChar);
                    continue;
                }
                if (shouldBeUnrolled(lChar)) {
                    changed = true;
                    unrollLeveledlist(lChar);
                }
                if (changed) {
                    patch.addRecord(lChar);
                }
                updateProgressBar();
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage(),
                        ex.getLocalizedMessage(), lChar, ex);
            }

        }
        endCategory();
        return actorvariations;
    }

    /**Test if the given Leveled Character is in compact notation.
     * The test is true, if:<ol>
     * <li>the EditorID is matched by <code>re_unroll</code></li>
     * <li>the record originates from a mod registered for compact lists</ol>
     *
     * @param leveledchar the Leveled Character to test
     * @return true if the list is compact and must be unrolled
     */
    private boolean shouldBeUnrolled(LVLN leveledchar) {
        Matcher match = re_unroll.matcher(leveledchar.getEDID());
        return match.find() && modsWithCompactLists.contains(leveledchar.getFormMaster());
    }

    /**Test if the given Leveled Character is an ActorVariation.
     * The test is true, if:<ol>
     * <li>the EditorID is matched by <code>re_variation</code></li>
     * <li>the record originates from a mod registered for ActorVariations</ol>
     *
     * @param leveledchar the Leveled Character to test
     * @return true if the list is an ActorVariation and must be cross-combined
     */
    private boolean isActorVariation(LVLN leveledchar) {
        Matcher match = re_variation.matcher(leveledchar.getEDID());
        return match.find() && modsWithActorVariations.contains(leveledchar.getFormMaster());
    }
}
