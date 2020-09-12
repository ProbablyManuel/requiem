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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Processing class for Leveled Items.
 * This class processes the Leveled Items. The performed operations are the
 * unrolling of compact leveled lists, merging changes from Requiem patches and
 * the tempering of items in LeveledItems that are flagged as Tempered Lists.
 * @author Ogerboss
 */
public class LeveledItems extends LeveledLists {

    private final static Logger logger = LogManager.getLogger();
    final private TextManager texts;

    /**
     * Internal Cache for item quality distributions. This is cache for the item
     * quality distributions definec by a specific (tier, size, distribution)
     * tuple extracted from EditorIDs.
     */
    private final TreeMap<Quality, ArrayList<Float>> templates;
    /**
     * The allowed origin mods for unrolling Leveled Items.
     */
    private final Set<ModListing> modsWithCompactLists;
    /**
     * The allowed origin mods for tempered Leveled Items.
     */
    private final Set<ModListing> modsWithTemperedItems;

    private final Pattern patternTemperedItem;

    private final Pattern patternCompactList;

    /**
     * Maps segments sizes specified in EditorID to internal data. This map
     * transforms the segment size (H, N, D) extracted from the EditorID into a
     * SizeFactor enum element.
     */
    private final Map<String, SizeFactor> trafo_sizefactor;
    /**
     * Maps quality distributions specified in EditorID to internal data. This
     * map transforms the segment size (fall, const, rise) extracted from the
     * EditorID into a QualityDistribution enum element.
     */
    private final Map<String, QualityDistribution> trafo_distribution;

    /**
     * Create a new LeveledItems processing instance.
     * While processing LeveledItems, it will merge changes made by Requiem
     * patches and it will unroll suitable LeveledItems given in compact
     * notation into a Skyrim-understandable version. Furthermore, it will also
     * fill Tempered Lists with various tempered versions of the base item.
     *
     * @param modsWithCompactLists list of origin mods with compact LeveledItems
     * @param modsWithTemperedItems list of origin mods with Tempered Lists
     * @param merger the merged import mod to fetch the exclusive lists
     * @throws PatchingException if a patch requested exclusive edit rights on
     * a form it does not edit
     */
    public LeveledItems(TextManager texts, Set<ModListing> modsWithCompactLists,
                        Set<ModListing> modsWithTemperedItems, Mod merger)
            throws PatchingException {
        super(texts, merger);
        this.texts = texts;
        this.patternCompactList = Pattern.compile("^[^_]+_CLI_.+",
                Pattern.CASE_INSENSITIVE);
        this.patternTemperedItem = Pattern.compile("^[^_]+.+_Quality([0-9]+)_([A-z])_([A-z]+)$",
                Pattern.CASE_INSENSITIVE);
        this.templates = new TreeMap<>();
        this.modsWithCompactLists = modsWithCompactLists;
        this.modsWithTemperedItems = modsWithTemperedItems;

        trafo_sizefactor = new TreeMap<>();
        trafo_sizefactor.put("h", SizeFactor.half);
        trafo_sizefactor.put("n", SizeFactor.normal);
        trafo_sizefactor.put("d", SizeFactor.twice);
        trafo_distribution = new TreeMap<>();
        trafo_distribution.put("fall", QualityDistribution.fall);
        trafo_distribution.put("const", QualityDistribution.constant);
        trafo_distribution.put("rise", QualityDistribution.rise);
    }

    /**Reqtify LeveledItems.
     * Loop over the LeveledItems and reqtify those that have merge conflicts
     * from Requiem-patches. Also the Requiem-defined compact spawnlists are
     * unrolled. (These use counts = N entries instead of N entries to denote
     * the spawn probabilities in an easier editable way.) In addition Tempered
     * items are processed. (These contain 1 item and will be filled with
     * tempered variations of this item.)
     *
     * @param merger the mod contained the merged imports
     * @param patch the global to export to
     * @param merge true if LeveledItem edits from patches shall be merged
     * @throws PatchingException if an error occurs while merging changes from
     * Requiem patches
     */
    public void reqtifyLeveledItems(Mod merger, Mod patch, boolean merge)
            throws PatchingException, UnexpectedException {
        GRUP<LVLI> leveledItems = merger.getLeveledItems();
        int maxRecords = leveledItems.numRecords();
        startCategory(maxRecords, "Leveled Items",
                texts.format("patch.gui_titles.leveled_items"));
        for (LVLI lItem: leveledItems) {
            ThreadContext.put(contextRecord, formatRecord(lItem));
            try {
                boolean changed = false;
                Quality qualitykey = getTemperingData(lItem);
                if (qualitykey != null) {
                } else if (is_merge_candidate(merge, lItem)) {
                    changed = true;
                    lItem = (LVLI) merge_lists(lItem, should_be_unrolled(lItem));
                }
                if (should_be_unrolled(lItem)) {
                    changed = true;
                    unrollLeveledlist(lItem);
                } else if (qualitykey != null) {
                    reqtifyItemQuality(lItem, qualitykey);
                    changed = true;
                }
                if (changed) {
                    patch.addRecord(lItem);
                }
                updateProgressBar();
            } catch (PatchingException ex) {
              throw ex;
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage(),
                        ex.getLocalizedMessage(), lItem, ex);
            }

        }
        endCategory();
    }

    /**Checks if a LeveledItem is a Tempered List.
     * The test is true, if:<ol>
     * <li>the EditorID is matched by <code>patternTemperedItem</code></li>
     * <li>the record originates from a mod registered for compact lists</ol>
     *
     * @param lvlitem the Leveled Item to test
     * @return the <code>Quality</code> extracted from the EditorID or null if
     * the test was negative
     * @throws PatchingException if the parameters specified in the EditorID are
     * invalid or the LeveledItem contains more than one entry
     */
    private Quality getTemperingData(LVLI lvlitem) throws PatchingException {
        Matcher match = patternTemperedItem.matcher(lvlitem.getEDID());
        if (match.find() && modsWithTemperedItems.contains(lvlitem.getFormMaster())) {
            if (lvlitem.numEntries() != 1) {
                String reason = texts.format(
                        "patch.leveled_items.temper_data_error_moreitems");
                String gui = texts.format(
                        "patch.leveled_items.tempering_data_error",
                        lvlitem.getEDID(), formatRecordPretty(lvlitem),
                        lvlitem.getModImportedFrom().print(), reason,
                        texts.format("urls.tempered_lists", true));
                throw new PatchingException("invalid tempering data", gui);
            }
            return new Quality(Integer.valueOf(match.group(1)),
                    match.group(2), match.group(3), lvlitem);
        }
        return null;
    }

    /**Test if the given Leveled Item is in compact notation.
     * The test is true, if:<ol>
     * <li>the EditorID is matched by <code>patternCompactList</code></li>
     * <li>the record originates from a mod registered for compact lists</ol>
     *
     * @param leveleditem the Leveled Item to test
     * @return true if the list is compact and must be unrolled
     */
    private boolean should_be_unrolled(LVLI leveleditem) {
        Matcher search = patternCompactList.matcher(leveleditem.getEDID());
        return search.find() && modsWithCompactLists.contains(leveleditem.getFormMaster());
    }

    /**Temper the given Leveled Item.
     * Replace the single item in the specified leveled item by several variants
     * of varying quality. The occuring tempering values are determined by the
     * given quality instance.
     *
     * @param litem the Leveled Item to temper
     * @param key the quality scheme to use
     */
    private void reqtifyItemQuality(LVLI litem, Quality key) {
        ArrayList<Float> template = get_tempering_levels(key);
        FormID baseitem = litem.getEntry(0).getForm();
        litem.clearEntries();
        for (Float health: template) {
            LeveledEntry entry = new LeveledEntry(baseitem, 1, 1);
            entry.setItemCondition(health);
            litem.addEntry(entry);
        }
        logger.debug("created tempered item list");
    }

    /**Translate a Quality instance into a list of tempering health values.
     * This function takes the tier, size and distribution information from the
     * given Quality object and constructs a list of the tempering health values
     * that should appear in the list. Depending on the chosen distribution, the
     * same value can appear multiple times in the list.
     *
     * @param key the quality instance with the information
     * @return an ArrayList with all tempering health values to use
     */
    private ArrayList<Float> get_tempering_levels(Quality key) {
        if (!templates.containsKey(key)) {
            int tier = key.tier;
            int size = key.factor.getValue();
            ArrayList<Float> tempering = new ArrayList<>();
            int offset = (tier  - 1) * 3 * size;
            for (int seg = 0; seg < 3; seg++) {
                int factor = key.distribution.getValue(seg);
                for (int health = 0; health < size; health++) {
                    for (int mult = 0; mult < factor; mult++) {
                        float val = offset + health + seg * size;
                        tempering.add(1.0f + val/10.0f);
                    }
                }
            }
            templates.put(key, tempering);
        }
        return templates.get(key);
    }

    /**Enum for the known segment sizes.
     * This Enum indicates how large the tempering quality segments of a list
     * should be. Larger sections are usually used for bigger items.
     */
    private enum SizeFactor {

        /**Segment size with one level per segment.
         * This size is used for small weapons like daggers.
         */
        half(1),
        /**Segment size with two levels per segments.
         * This size is used for normal one-handed weapons and all armor. Skyrim
         * applies special rules to armors, the chest pieces automatically get
         * twice the armor rating from tempering.
         */
        normal(2),
        /**Segment size with four levels per segments.
         * This size is used for two-handed weapons, including bows and
         * crossbows.
         */
        twice(4);

        /**The segment size of this SizeFactor instance.
         * See the documentation of <code>Quality</code> for usage details.
         */
        private final int value;

        /**Create a new SizeFactor instance.
         * The new SizeFactor will have the specified segment size.
         *
         * @param value segment size to use
         */
        SizeFactor(int value) {
            this.value = value;
        }

        /**Query the segment size of this SizeFactor.
         *
         * @return the number of distinct tempering levels in each segment.
         */
        public int getValue() {
            return value;
        }
    }

    /**Enum for the known quality distributions.
     * This Enum indicates how likely values from the different quality segments
     * of a list should spawn. The distribution can be either uniform or biased
     * towards either end of the quality range spanned by the Tempered List.
     */
    private enum QualityDistribution {

        /**A QualityDistribution with a bias towards lower quality gear.
         * This Distribution will make spawns of items with qualities from the
         * lowest segment more likely.
         */
        fall(3, 2, 1),

        /**A QualityDistribution with no bias.
         * With this Distribution, tempering levels from all three segments are
         * equally likely to be spawned.
         */
        constant(1, 1, 1),

        /**A QualityDistribution with a bias towards higher quality gear.
         * This Distribution has a bias towards spawns of items with tempering
         * levels from the third segment of the list.
         */
        rise(1, 2, 3);

        /**The spawn ratios for this list.
         * The three elements of the array are the multipliers for the uniform
         * spawn ratios found in the tempering range constructed from the tier
         * and size information. The first element is for the lowest quality
         * segment, the second for the middle one and the last for the highest
         * quality segment.
         */
        private final int[] values;

        /**Construct a QualityDistribution with the given spawn ratios.
         *
         * @param seg1 spawn rate multiplier for the low quality segment
         * @param seg2 spawn rate multiplier for the medium quality segment
         * @param seg3 spawn rate multiplier for the high quality segment
         */
        QualityDistribution(int seg1, int seg2, int seg3) {
            this.values = new int[3];
            this.values[0] = seg1;
            this.values[1] = seg2;
            this.values[2] = seg3;
        }

        /**Query the spawn rate multiplier for the given segment.
         *
         * @param index the segment index, must be 0, 1 or 2
         * @return the spawn rate multiplier for the queried segment
         */
        public int getValue(int index) {
            return values[index];
        }
    }

    /**A representation of the tempering quality in a Tempered List.
     * The tempering quality consists of three components: the tier, size and
     * distribution scheme.<ul>
     * <li>tier T - determines the overall tempering levels found in the
     * list</li>
     * <li>size S - determines how large the range is, larger weapons will have
     * larger tempering ranges</li>
     * <li>distribution - determines how likely the tempering levels in the
     * given range will spawn, the distribution can either be uniform or make
     * a subrange spawn more likely than the rest</li>
     *
     * Each tempering range is divided into three segments: the low, average and
     * high quality segments. These segments can vary in the number of the
     * tempering levels each of them contains, the number is specified by the
     * SizeFactor Enum. The Tempered List thus contains 3*size different
     * tempering levels. The first quality tier starts at the the base health of
     * 1.0 (no tempering), the next tier starts with the lowest value not
     * included in the previous tier. The distributions can make some tempering
     * levels occur multiple times to adjust the spawn probabilities.
     *
     * lowest tempering level in list: 1.0+[3(T - 1) * S] / 10.0
     * highest tempering level in list: 1.0+[3(T * S) - 1.0] / 10.0
     */
    private class Quality implements Comparable<Quality>{

        /**The tier of this quality item.
         * see the class description for a detailed explanation.
         */
        final Integer tier;
        /**The size factor of this quality item.
         * see the class description and the SizeFactor Enum for a detailed
         * explanation.
         */
        final SizeFactor factor;
        /**The distribution scheme for this quality item.
         * see the class description and the QualityDistribution Enum for a
         * detailed explanation.
         */
        final QualityDistribution distribution;

        /**Constructor for Quality instances.
         * This constructor will validate the quality settings extracted from
         * the EditorID and throw a PatchingException if they are invalid.
         *
         * @param tier the tier extracted from the EditorID
         * @param factor the size factor extracted from the EditorID
         * @param distribution the size factor extracted from the EditorID
         * @param record the record from which the EditorID is analyzed
         * @throws PatchingException if the EditorID did supply invalid values
         */
        private Quality (int tier, String factor, String distribution,
                LVLI record) throws PatchingException{
            this.tier = tier;
            if (trafo_sizefactor.containsKey(factor.toLowerCase())) {
                this.factor = trafo_sizefactor.get(factor.toLowerCase());
            } else {
                String reason = texts.format(
                        "patch.leveled_items.temper_data_error_factor", factor);
                String gui = texts.format(
                        "patch.leveled_items.tempering_data_error",
                        formatRecordPretty(record), reason,
                        texts.format("urls.tempered_lists", true));
                throw new PatchingException("invalid tempering data", gui);
            }
            if (trafo_distribution.containsKey(distribution.toLowerCase())) {
                this.distribution = trafo_distribution.get(
                        distribution.toLowerCase());
            } else {
                String reason = texts.format(
                        "patch.leveled_items.temper_data_error_distribution", distribution);
                String gui = texts.format(
                        "patch.leveled_items.tempering_data_error",
                        formatRecordPretty(record), reason,
                        texts.format("urls.tempered_lists", true));
                throw new PatchingException("invalid tempering data", gui);
            }
        }

        @Override
        public int compareTo(Quality other) {
            int retval = this.tier.compareTo(other.tier);
            if (retval == 0) {
                retval = this.factor.compareTo(other.factor);
            }
            if (retval == 0) {
                retval = this.distribution.compareTo(other.distribution);
            }
            return retval;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Quality && this.compareTo((Quality) obj) == 0;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 41 * hash + Objects.hashCode(this.tier);
            hash = 41 * hash + Objects.hashCode(this.factor);
            hash = 41 * hash + Objects.hashCode(this.distribution);
            return hash;
        }


    }
}