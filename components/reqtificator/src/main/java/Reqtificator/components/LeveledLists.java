/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.components;

import Reqtificator.FormIDStash;
import Reqtificator.exceptions.PatchingException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;

import java.util.*;

/**
 * Abstract Base for classes processing Leveled Records. This abstract base
 * provides the necessary functionality to unroll LeveledRecords (Leveled
 * Characters and Leveled Items) on a common ground. The functionality provided
 * here includes the merging of these lists and the unrolling of those given in
 * compact notation.
 *
 * @author Ogerboss
 */
public abstract class LeveledLists extends Component{

    private final static Logger logger = LogManager.getLogger();
    private final TextManager texts;
    /**
     * History of overwrites for the currently processed record. Starting from
     * the last overwrite, this ArrayList contains all versions of the record
     * from mods that depend on the Requiem.esp, in inverse load order. The last
     * entry will be the Requiem.esp version, if the LeveledRecord qualifies for
     * merging.
     */
    private final ArrayList<LeveledRecord> merge_history;
    /**
     * The computed differences for each overwrite, relative to Requiem. Each
     * set in the the ArrayList (indexes correspond to the merge_history)
     * contains the differences computed relative to the Leveled Record as it
     * was defined in Requiem.esp. An ordered set was chosen to ensure an
     * ordered and deterministic output order in Async Log.
     */
    private final ArrayList<TreeSet<Difference>> differences;
    
    private final HashSet<ModListing> basemods;

    private final HashMap<FormID,ModListing> exclusive_overwrites;

    /**
     * Create a new LeveledLists instance. This will setup the internal
     * variables. REQ-Tags for the merging and unrolling features should be
     * processed by the constructors of subclasses.
     *
     * @param merger the merged import, used to extract exclusive edit rights
     * @throws PatchingException if a patch requested exclusive edit rights on
     * a form it does not edit
     */
    public LeveledLists(TextManager texts, Mod merger)
            throws PatchingException {
        super();
        this.texts = texts;
        merge_history = new ArrayList<>();
        differences = new ArrayList<>();
        basemods = new HashSet<>(3);
        basemods.add(FormIDStash.REQ);
        exclusive_overwrites = new HashMap<>();
        FLST[] prioritylists = new FLST[2];
        prioritylists[0] = (FLST) merger.getMajor(
                FormIDStash.formlist_LLmerge_mediumpriority, GRUP_TYPE.FLST);
        prioritylists[1] = (FLST) merger.getMajor(
                FormIDStash.formlist_LLmerge_highpriority, GRUP_TYPE.FLST);
        for (FLST priority: prioritylists) {
            ThreadContext.push(formatRecord(priority));
            for (MajorRecord history: priority.getRecordHistory()) {
                FLST historylist = (FLST) history;
                ThreadContext.push(formatRecord(historylist));
                ModListing origin = historylist.getModImportedFrom();
                for (FormID entry: historylist.getFormIDEntries()) {
                    exclusive_overwrites.put(entry, origin);
                    MajorRecord record = merger.getMajor(entry, GRUP_TYPE.LVLI,
                        GRUP_TYPE.LVLN);
                    ThreadContext.push(formatRecord(record));
                    boolean found = false;
                    if (record != null) {
                        for (MajorRecord version: record.getRecordHistory())  {
                            if (version.getModImportedFrom().equals(origin)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        String gui = texts.format("patch.leveled_lists." +
                                "incorrect_exclusive_rights", origin.print(),
                                entry.toString());
                        throw new PatchingException("incorrect exclusive edit" +
                                "right request", gui);
                    }
                    ThreadContext.pop();
                }
                ThreadContext.pop();
            }
            ThreadContext.pop();
        }
    }

    /**
     * Unroll a leveled list in compact notation. All entries of items with
     * count = N > 1 are replaced by N entries with count = 1. Optionally, the
     * output in the Async Log can be suppressed, e.g. for unrolling compact
     * lists before merging them.
     *
     * @param wrap the compact LeveledList
     */
    private void flattenLeveledlist(LeveledRecord wrap) {
        for ( int j = wrap.numEntries()-1; j >= 0 ; j--) {
            LeveledEntry item = wrap.getEntry(j);
            for (int i =item.getCount(); i > 1; i--) {
                wrap.addEntry(item.getForm(), 1, 1);
                item.setCount(i-1);
            }
        }
    }

    /**
     * Unroll a leveled list in compact notation. All entries of items with
     * count = N > 1 are replaced by N entries with count = 1.
     *
     * @param wrap the compact LeveledList
     */
    public void unrollLeveledlist(LeveledRecord wrap) {
        flattenLeveledlist(wrap);
        logger.debug("unrolled compact leveled list");
    }

    /**
     * Analyze a leveled record to determine if entry merging is needed. A
     * leveled list is considered to be a merge candidate, if:<br>
     * -the last overwrite has a dependency on Requiem.esp<br>
     * -the record is modified by Requiem.esp (the "base" in the merge)<br>
     * -there are at least overwrites from two mods depending on Requiem.esp<br>
     * Note that this function updates private fields of the LeveledLists
     * instance, therefore the merge must be performed before calling this
     * function for the next record.
     *
     * @param merge       true if merging should be happening at all (false will
     *                    produce a skip message in the log)
     * @param leveledList the leveled list to check
     * @return true if merging is needed for this record
     */
     boolean is_merge_candidate(boolean merge, LeveledRecord leveledList) {
        if (!merge) {
            return false;
        }
        merge_history.clear();
        int depth = ThreadContext.getDepth();
        ArrayList<MajorRecord> history = leveledList.getRecordHistory();
        try {
            for (int i = history.size()-1; i >= 0; i--) {
                MajorRecord version = history.get(i);
                ThreadContext.push(formatRecord(version));
                Mod importer = SPDatabase.getMod(version.getModImportedFrom());
                if (basemods.contains(version.getModImportedFrom())) {
                    merge_history.add(0, (LeveledRecord) version);
                    LeveledRecord last = merge_history.get(
                            merge_history.size()-1);
                    if (merge_history.size() < 3) {
                        return false;
                    } else {
                        return last.getModImportedFrom() == leveledList.getModImportedFrom();
                    }
                } else if (importer.getMasters().contains(requiem_ml)) {
                    merge_history.add(0, (LeveledRecord) version);
                }
                ThreadContext.pop();
            }
            return false;
        } finally {
            ThreadContext.trim(depth);
        }
    }

    /**
     * Create a merged LeveledList from the previously analyzed record. To
     * perform the merge, this function at first identifies the content in each
     * relevant import. The content is divided into (FormID, count, level)
     * tuples, which appear M times in the lists. Afterwards the patcher
     * computes the changes in the M count for each patch relative to the
     * Requiem base to determine additions (tuple not present in Requiem),
     * modifications (M has changed with respect to Requiem) and deletions (
     * tuple from Requiem not present in patch). Then the following merge rules
     * are used:<ul>
     * <li>-additions are always merged</li>
     * <li>modifications are only merged if they are requested by all
     * patches</li>
     * <li>deletions are only merged if they are requested by all patches</li>
     * </ul>If the LeveledList is given in compact notation, you must specify
     * this via the "compact" argument to ensure that the list is properly
     * merged.
     *
     * @param toMerge the leveled list to merge
     * @param compact true if the leveled list is given in compact notation and
     *                must be unrolled for each version before merging
     * @return a LeveledRecord containing the merged List, derived from the
     * "Base" version (Requiem.esp version of the list)
     * @throws PatchingException if the given LeveledList is not the one
     * previously prepared by is_merge_candidate() or not all diffs where
     * processed (internal error)
     */
     LeveledRecord merge_lists(LeveledRecord toMerge, boolean compact)
            throws PatchingException {
        if (merge_history.get(merge_history.size()-1) != toMerge) {
            LeveledRecord history = merge_history.get(merge_history.size()-1);
            String gui = texts.format("patch.leveled_lists.wrong_tasks",
                    toMerge.getFormStr(), toMerge.getModImportedFrom().print(),
                    history.getFormStr(),history.getModImportedFrom().print(),
                    texts.format("urls.service_desk", true));
            throw new PatchingException("invalid merge task", gui);
        }
        //let's check if a patch was requesting exclusive rights for this list
        if (exclusive_overwrites.containsKey(toMerge.getForm())) {
            ModListing dominator = exclusive_overwrites.get(toMerge.getForm());
            for (LeveledRecord version: merge_history) {
                if (version.getModImportedFrom().equals(dominator)) {
                    logger.debug("exclusive modification rights for {}",
                            dominator.print());
                    return version;
                }
            }
            //oops, apparently the mod requesting the exclusive right did not
            //modify the list in question at all
            String gui = texts.format("patch.leveled_lists." +
                            "incorrect_exclusive_rights", dominator.print(),
                    toMerge.toString());
            throw new PatchingException("incorrect exclusive edit" +
                    "right request", gui);
        }
        //compute the changes of the patches with respect to the Requiem version
        differences.clear();
        LeveledRecord requiem_version = merge_history.remove(0);
        ThreadContext.push(formatRecord(requiem_version));
        if (compact) {
            unrollLeveledlist(requiem_version);
        }
        TreeMap<EntryPoint, Integer> base_version = extract_counts(
                requiem_version, extract_distinct(requiem_version));
        for (LeveledRecord version : merge_history) {
            ThreadContext.push(formatRecord(version));
            if (compact) {
                unrollLeveledlist(version);
            }
            TreeMap<EntryPoint, Integer> patch_version = extract_counts(
                version, extract_distinct(version));
            differences.add(compute_diff(base_version, patch_version,
                    version.getModImportedFrom()));
            ThreadContext.pop();
        }
        include_additions(requiem_version, differences);
        include_modifications(requiem_version, differences);
        include_deletions(requiem_version, differences);
        for (TreeSet<Difference> diffset: differences) {
            if (diffset.size() > 0) {
                String gui = texts.format(
                        "patch.leveled_lists.incomplete_merge",
                        formatRecordPretty(requiem_version),
                        texts.format("urls.service_desk", true));
                throw new PatchingException("merge not completed", gui);
            }
        }
        logger.debug("merged changes from {} Requiem-dependent mods",
                merge_history.size());
        ThreadContext.pop();
        return requiem_version;
    }

    /**
     * Incorporate all additions from the computed diffs in the merge target.
     * This function iterates over the list of computed differences and
     * incorporates all diff-entries of type addition into the merge target
     * list, which should be the base version from Requiem. Once processed,
     * these entries are deleted from the set of differences they originatet
     * from.
     *
     * @param mergetarget the leveled list in which the changes are merged
     * @param diffsets the list of differences, each entry of the list is a set
     * of the individual diffs computed for one Requiem patch
     */
    private void include_additions(LeveledRecord mergetarget,
            ArrayList<TreeSet<Difference>> diffsets) {
        for (TreeSet<Difference> diffset: diffsets) {
            Iterator<Difference> iterator = diffset.iterator();
            while (iterator.hasNext()) {
                Difference diff = iterator.next();
                if (diff.mode == Difference.operation.addition) {
                    for (int i = 0; i< diff.magnitude; i++) {
                        mergetarget.addEntry(diff.entry.reference,
                                diff.entry.level, diff.entry.count);
                    }
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Incorporate modifications from the computed diffs in the merge target.
     * This function will merge all modification diff-entries that are contained
     * in all patches. Modifications only contained in some of the patches will
     * be discarded.
     *
     * @param mergetarget the leveled list in which the changes are merged
     * @param diffsets the list of differences, each entry of the list is a set
     * of the individual diffs computed for one Requiem patch
     */
    private void include_modifications(LeveledRecord mergetarget,
            ArrayList<TreeSet<Difference>> diffsets) {
        for (int i=0; i < diffsets.size(); i++) {
            Iterator<Difference> iterator = diffsets.get(i).iterator();
            while (iterator.hasNext()) {
                Difference diff = iterator.next();
                if (diff.mode != Difference.operation.update) {
                    continue;
                }
                iterator.remove();
                boolean todo = i == 0;
                for (TreeSet<Difference> otherdiff: diffsets.subList(1,
                        diffsets.size())) {
                    if (otherdiff.contains(diff)) {
                        otherdiff.remove(diff);
                    } else {
                        todo = false;
                    }
                }
                if (todo) {
                    if (diff.magnitude > 0) {
                        for (int j = 0; j < diff.magnitude; j++) {
                            mergetarget.addEntry(diff.entry.reference,
                                    diff.entry.level, diff.entry.count);
                        }
                    } else {
                        Iterator<LeveledEntry> iter = mergetarget.iterator();
                        int deletions = - diff.magnitude;
                        while (iter.hasNext() && deletions > 0) {
                            LeveledEntry entry = iter.next();
                            if (entry.getForm().equals(diff.entry.reference) &&
                                    entry.getLevel() == diff.entry.level &&
                                    entry.getCount() == diff.entry.count) {
                                iter.remove();
                                deletions--;
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Incorporate deletions from the computed diffs in the merge target. This
     * function will merge all deletion diff-entries that are contained in all
     * patches. Deletions only contained in some of the patches will be
     * discarded.
     *
     * @param mergetarget the leveled list in which the changes are merged
     * @param diffsets the list of differences, each entry of the list is a set
     * of the individual diffs computed for one Requiem patch
     */
    private void include_deletions(LeveledRecord mergetarget,
            ArrayList<TreeSet<Difference>> diffsets) {
        for (int i = 0; i < diffsets.size(); i++) {
            Iterator<Difference> iterator = diffsets.get(i).iterator();
            while (iterator.hasNext()) {
                Difference diff = iterator.next();
                if (diff.mode != Difference.operation.deletion) {
                    continue;
                }
                iterator.remove();
                boolean todo = i == 0;
                for (TreeSet<Difference> otherdiff: diffsets.subList(i+1,
                        diffsets.size())) {
                    if (otherdiff.contains(diff)) {
                        otherdiff.remove(diff);
                    } else {
                        todo = false;
                    }
                }
                if (todo) {
                    Iterator<LeveledEntry> iter = mergetarget.iterator();
                    while (iter.hasNext()) {
                        LeveledEntry entry = iter.next();
                        if (entry.getForm().equals(diff.entry.reference) &&
                                entry.getLevel() == diff.entry.level &&
                                entry.getCount() == diff.entry.count) {
                            iter.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * Extract all unique (FormID, Level, Count) tuples. This function iterates
     * over the given LeveledRecord and returns a set of all unique (FormID,
     * Level, Count) entries found in the list.
     *
     * @param source the LeveledRecord to analyze
     * @return a set of the unique (FormID, Level, Count) tuples
     */
    private TreeSet<EntryPoint> extract_distinct(LeveledRecord source) {
        TreeSet<EntryPoint> retval = new TreeSet<>();
        for (LeveledEntry record : source.getEntries()) {
            retval.add(new EntryPoint(record));
        }
        return retval;
    }

    /**
     * Determines the occurrences for each unique (FormID, Level, Count) tuples.
     * For each (FormID, Level, Count) tuple given in the set argument, the
     * number of occurrences is extracted from the LeveledRecord and stored in a
     * TreeMap where the tuples (represented as EntryPoints) are mapped to their
     * count.
     *
     * @param source the LeveledRecord to count in
     * @param uniques the uniques (FormID, Level, Count) tuples for which the
     * number of occurrences should be determined
     * @return a map of EntryPoints to their number of occurrences
     */
    private TreeMap<EntryPoint, Integer> extract_counts(LeveledRecord source,
            TreeSet<EntryPoint> uniques) {
        TreeMap<EntryPoint, Integer> map = new TreeMap<>();
        for (EntryPoint entry : uniques) {
            map.put(entry, 0);
        }
        for (LeveledEntry entry : source.getEntries()) {
            EntryPoint node = new EntryPoint(entry);
            if (map.containsKey(node)) {
                map.put(node, map.get(node) + 1);
            }
        }
        return map;
    }

    /**
     * Compute the diffs between the reference Leveled list and the new version.
     * This function takes two TreeMaps generated from extract_counts() and then
     * computes the differences between these two lists, represented as
     * Difference objects. Each Difference can be either an Addition,
     * Modification or Deletion.
     *
     * @param ref the base version of the LeveledRecord in question
     * @param update the new version of the LeveledRecord in question
     * @param origin the origin mod of the "update" version, used for logging
     * @return a set of all differences between the two Leveled Lists
     */
    private TreeSet<Difference> compute_diff(TreeMap<EntryPoint, Integer> ref,
            TreeMap<EntryPoint, Integer> update, ModListing origin) {
        TreeSet<Difference> diff = new TreeSet<>();
        for (EntryPoint entry: update.descendingKeySet()) {
            Integer refcount = ref.get(entry);
            Integer modcount = update.get(entry);
            if (refcount == null) {
                diff.add(new Difference(entry, Difference.operation.addition,
                        modcount, origin));
            } else if (modcount - refcount != 0) {
                diff.add(new Difference(entry, Difference.operation.update,
                        modcount - refcount, origin));
            }
        }
        for (EntryPoint entry: ref.descendingKeySet()) {
            Integer modcount = update.get(entry);
            if (modcount == null) {
                diff.add(new Difference(entry, Difference.operation.deletion,
                        0, origin));
            }
        }
        return diff;
    }

    /**A representation for a difference between two versions of Leveled List.
     * The total difference is represent as a set of atomic differences on the
     * unique EntryPoints. Each Difference can represent the addition of a new
     * entry point, the deletion of an previously existing one or an update of
     * an existing by modifying the number of occurrences in the Leveled List.
     */
    private static class Difference implements Comparable<Difference>{

        /**Enum for the supported difference types.
         */
        static enum operation {
            /**The Addition operation.
             * EntryPoint diffs should be qualified as addition, if the
             * EntryPoint is not present in the base version.
             */
            addition,
            /**The Deletion operation.
             * EntryPoint diffs should be qualified as deletion, if the
             * EntryPoint from the base version is not present in the new one.
             */
            deletion,
            /**The Modification operation.
             * EntryPoint diffs should be qualified as modification, if the
             * EntryPoint is present in both versions, but with different
             * occurrence counts.
             */
            update
        };

        /**The Entry point documented by this Difference.
         */
        final EntryPoint entry;
        /**The operation that was determined for the EntryPoint.
         */
        final operation mode;
        /**The magnitude of the change.
         * For additions and modifications this field denotes how many
         * occurrences of the EntryPoint should be added (or removed). In the
         * case of a deletion operation it has no meaning.
         */
        final Integer magnitude;
        /**The origin mod of the overwrite which is compared to the base.
         * This field is only used for logging purposes. It is not used to
         * identify this class, Differences with unequal origins but otherwise
         * identical fields compare as equal.
         */
        final ModListing origin;

        /**Create a new Difference instance.
         *
         * @param entry the entry this Difference describes
         * @param mode the type of change relative to the base version
         * @param magnitude the magnitude of the change
         * @param origin the origin mod of the updated version
         */
        Difference(EntryPoint entry, operation mode, int magnitude,
                ModListing origin) {
            this.entry = entry;
            this.mode = mode;
            if (mode == operation.deletion) {
                this.magnitude = 0;
            } else {
                this.magnitude = magnitude;
            }
            this.origin = origin;
        }

        @Override
        public int compareTo(Difference other) {
            int retval = entry.compareTo(other.entry);
            if (retval == 0) {
                retval = mode.compareTo(other.mode);
            }
            if (retval == 0) {
                retval = magnitude.compareTo(other.magnitude);
            }
            return retval;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Difference) {
                return this.compareTo((Difference) obj) == 0;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + Objects.hashCode(this.entry);
            hash = 29 * hash + Objects.hashCode(this.mode);
            hash = 29 * hash + Objects.hashCode(this.magnitude);
            return hash;
        }
    }

    /**A compact notation for an unique entry in a Leveled List.
     * An entry in a leveled list is determined by three components: the FormID
     * of the contained object, the minimum level and the spawn count. This
     * helper class helps to denote these unique entries of the Leveled Lists.
     * This class is similar to the <code>skyproc.LeveledEntry</code> class, but
     * defines a custom ordering to have consistent output order in the log and
     * does not have all the overhead inherited from parent classes.
     */
    private class EntryPoint implements Comparable<EntryPoint>{

        /**The level requirement of the Leveled List entry.
         * The player level must at least be equal to the specified level
         * before the item can spawn.
         */
        final Integer level;
        /**The spawn count of the Leveled List entry.
         * If this entry is chosen to spawn, it will create as many instances as
         * specified by this field.
         */
        final Integer count;
        /**The reference to spawn for this Leveled List entry.
         * This is the FormID of the object/character to spawn, optionally it
         * can also be a nested Leveled List.
         */
        final FormID reference;

        /**Construct an EntryPoint directly from a LeveledEntry.
         *
         * @param entry the LeveledEntry to represent
         */
        EntryPoint(LeveledEntry entry) {
            this.level = entry.getLevel();
            this.count = entry.getCount();
            this.reference = entry.getForm();
        }

        /**Construct an EntryPoint from raw parameters.
         *
         * @param level the minimum spawn level
         * @param count the spawn count
         * @param ref the object to spawn
         */
        EntryPoint(int level, int count, FormID ref) {
            this.level = level;
            this.count = count;
            this.reference = ref;
        }

        @Override
        public int compareTo(EntryPoint other) {
            int retval = reference.getFormStr().compareTo(other.reference.getFormStr());
            if (retval == 0) {
                retval = level.compareTo(other.level);
            }
            if (retval == 0) {
                retval = count.compareTo(other.count);
            }
            return retval;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof EntryPoint) {
                return this.compareTo((EntryPoint) obj) == 0;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 83 * hash + Objects.hashCode(this.level);
            hash = 83 * hash + Objects.hashCode(this.count);
            hash = 83 * hash + Objects.hashCode(this.reference);
            return hash;
        }

    }

}
