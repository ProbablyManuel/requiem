/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.*;
import lev.Ln;
import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
abstract public class LeveledRecord extends MajorRecord implements Iterable<LeveledEntry> {

    static final SubPrototype LeveledProto = new SubPrototype(MajorRecord.majorProto) {
	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    add(new SubData("LVLD", new byte[1]));
	    add(new SubFlag("LVLF", 1));
            add(new SubForm("LVLG"));
	    add(new SubListCounted<>("LLCT", 1, new LeveledEntry()));
	}
    };

    /**
     * Creates a Leveled List with no entries and default settings. LVLN_Flags
     * set to 0x01=All levels Chance none set to 0. Empty MODL, MODT, and COED.
     */
    LeveledRecord() {
	super();
	subRecords.setPrototype(LeveledProto);
    }

    /**
     * Creates a new LVLN record with a FormID originating from the mod
     * parameter.
     *
     * @param edid EDID to assign the record. Make sure it's unique.
     */
    public LeveledRecord(String edid) {
	this();
	originateFromPatch(edid);
	set(LVLFlag.CalcAllLevelsEqualOrBelowPC, true);
    }

    /**
     *
     * @return An iterator that steps through each entry in the LVLN.
     */
    @Override
    public Iterator<LeveledEntry> iterator() {
	return subRecords.getSubList("LVLO").iterator();
    }

    /**
     * This enum represents the different flags offered by LVLN.
     */
    public enum LVLFlag {

	/**
	 *
	 */
	CalcAllLevelsEqualOrBelowPC,
	/**
	 *
	 */
	CalcForEachItemInCount,
	/**
	 *
	 */
	UseAll,
        /**
         * 
         */
        SpecialLoot;
    }

    /**
     *
     */
    public void clearEntries() {
	subRecords.getSubList("LVLO").clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<LeveledEntry> getEntries() {
	return subRecords.getSubList("LVLO").toPublic();
    }
    
    public ArrayList<FormID> getEntryForms() {
	ArrayList<LeveledEntry> entries = getEntries();
	ArrayList<FormID> out = new ArrayList<>(entries.size());
	for (LeveledEntry e : entries) {
	    out.add(e.getForm());
	}
	return out;
    }

    /**
     * Returns all non-leveled list entries, with leveled list entries
     * recursively replaced with their contents. NOTE: Making additions/removals
     * to this list will not actually affect the record.
     *
     * @return
     */
    public ArrayList<LeveledEntry> getFlattenedEntries() {
	ArrayList<LeveledEntry> out = new ArrayList<>();
	for (LeveledEntry entry : getEntries()) {
	    MajorRecord o = SPDatabase.getMajor(entry.getForm());
	    if (o instanceof LeveledRecord) {
		out.addAll(((LeveledRecord) o).getFlattenedEntries());
	    } else {
		out.add(entry);
	    }
	}
	return out;
    }

    /**
     * Adds the desired entry to the LVLN. Duplicates are accepted.
     *
     * @param entry LVLO to add to this LVLN
     */
    public void addEntry(LeveledEntry entry) {
	subRecords.getSubList("LVLO").add(entry);
    }

    /**
     * Adds an entry with the actor, level, and count provided.
     *
     * @param id The formID of the actor to put on the entry.
     * @param level Level to mark the entry at.
     * @param count Number of actors to spawn.
     */
    public void addEntry(FormID id, int level, int count) {
	addEntry(new LeveledEntry(id, level, count));
    }

    /**
     *
     * @return The number of entries contained in the LVLN.
     */
    public int numEntries() {
	return subRecords.getSubList("LVLO").size();
    }

    /**
     *
     * @return True if LVLN has no entries.
     */
    public Boolean isEmpty() {
	return numEntries() == 0;
    }

    /**
     *
     * @param i The zero based index to query.
     * @return The ith entry in the LVLN.
     */
    public LeveledEntry getEntry(int i) {
	return (LeveledEntry) subRecords.getSubList("LVLO").get(i);
    }

    /**
     * Removes the ith entry from the LVLN.
     *
     * @param i The zero based index to remove.
     */
    public void removeEntry(int i) {
	subRecords.getSubList("LVLO").remove(i);
    }

    /**
     *
     * @param id
     */
    public void removeFirstEntry(FormID id) {
	ArrayList<LeveledEntry> list = getEntries();
	for (int i = 0; i < list.size(); i++) {
	    if (list.get(i).getForm().equals(id)) {
		list.remove(i);
		break;
	    }
	}
    }

    /**
     *
     * @param id
     */
    public void removeAllEntries(FormID id) {
	ArrayList<LeveledEntry> list = getEntries();
	for (int i = 0; i < list.size();) {
	    if (list.get(i).getForm().equals(id)) {
		list.remove(i);
	    } else {
		i++;
	    }
	}
    }

    /**
     * Reduces a Leveled List to have the least entries possible while
     * maintaining the spawning probabilities.
     */
    public void reduce() {
	LListSummary sum = new LListSummary(this);
	sum.reduce();
	setTo(sum);
    }

    void setTo(LListSummary sum) {
	this.clearEntries();
	for (LListEntry e : sum.entries) {
	    for (int i = 0; i < e.numEntries; i++) {
		this.addEntry(e.id, e.level, e.count);
	    }
	}
    }

    /**
     *
     * @return The percent chance nothing will spawn from this LVLN. (0.0-1.0)
     */
    public Double getChanceNonePct() {
	SubData lvld = subRecords.getSubData("LVLD");
	if (lvld.isValid()) {
	    return lvld.toInt() / 100.0;
	} else {
	    return 0.0;
	}
    }

    /**
     * Sets the chance none for this LVLN.
     *
     * @param in The chance that no creature will spawn from this LVLN.
     * @throws BadParameter If in is outside the range: (0-100)
     */
    public void setChanceNone(final int in) throws BadParameter {
	if (in >= 0 && in <= 100) {
	    subRecords.setSubData("LVLD", in, 1);
	} else {
	    throw new BadParameter("Chance none set outside range 0-100: " + in);
	}
    }

    /**
     *
     * @return The chance that no actor will spawn from this LVLN.
     */
    public int getChanceNone() {
	return subRecords.getSubData("LVLD").toInt();
    }

    /**
     * Checks a flag of the LVLN given by flag parameter.
     *
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to check.
     * @return True if given flag is true.
     */
    public boolean get(LVLFlag flag) {
	return subRecords.getSubFlag("LVLF").is(flag.ordinal());
    }

    /**
     * Sets a flag of the LVLN given by flag parameter
     *
     * @see LVLN_Flags
     * @param flag LVLN_Flags enum representing the flag to set.
     * @param on Boolean to set flag to.
     */
    final public void set(LVLFlag flag, boolean on) {
	subRecords.setSubFlag("LVLF", flag.ordinal(), on);
    }
    
    /**
     * s
     * @return
     */
    public FormID getGlobalForm() {
        return subRecords.getSubForm("LVLG").getForm();
    }

    // Get/Set
    /**
     *
     * @param id
     */
    public void setGlobalForm(FormID id) {
        subRecords.setSubForm("LVLG", id);
    }

    /**
     *
     * @param target
     * @return
     */
    final public boolean contains(MajorRecord target) {
	for (LeveledEntry entry : this) {
	    if (entry.getForm().equals(target.getForm())) {
		return true;
	    }
	}
	return false;
    }

    /**
     *
     * @param target Record to look for.
     * @param replacement Record to replace with.
     * @return Number of replacements executed.
     */
    final public int replace(MajorRecord target, MajorRecord replacement) {
	int out = 0;
	FormID targetF = target.getForm();
	FormID replaceF = replacement.getForm();
	for (LeveledEntry entry : this) {
	    if (entry.getForm().equals(targetF)) {
		out++;
		entry.setForm(replaceF);
	    }
	}
	return out;
    }

    // Large LList splitting
    static class LListEntry {

	FormID id;
	int level;
	int count;
	int numEntries = 1;

	LListEntry(LeveledEntry e) {
	    id = e.getForm();
	    level = e.getLevel();
	    count = e.getCount();
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    final LListEntry other = (LListEntry) obj;
	    if (!Objects.equals(this.id, other.id)) {
		return false;
	    }
	    if (this.level != other.level) {
		return false;
	    }
	    if (this.count != other.count) {
		return false;
	    }
	    return true;
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 47 * hash + Objects.hashCode(this.id);
	    hash = 47 * hash + this.level;
	    hash = 47 * hash + this.count;
	    return hash;
	}
    }

    static class LListSummary {

	ArrayList<LListEntry> entries = new ArrayList<>();

	LListSummary(LeveledRecord in) {
	    this(in.getEntries());
	}

	LListSummary(Collection<LeveledEntry> in) {
	    for (LeveledEntry e : in) {
		LListEntry trans = new LListEntry(e);
		int existing = entries.indexOf(trans);
		if (existing != -1) {
		    entries.get(existing).numEntries++;
		} else {
		    entries.add(trans);
		}
	    }
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    final LListSummary other = (LListSummary) obj;
	    if (entries.size() != other.entries.size()) {
		return false;
	    }
	    for (LListEntry entry : other.entries) {
		if (!entries.contains(entry)) {
		    return false;
		}
	    }
	    return true;
	}

	public void reduce() {
	    int[] divs = new int[entries.size()];
	    for (int i = 0; i < divs.length; i++) {
		divs[i] = entries.get(i).numEntries;
	    }
	    int reduc = Ln.gcd(divs);
	    if (reduc != 1) {
		for (LListEntry e : entries) {
		    e.numEntries /= reduc;
		}
	    }
	}

	public LListEntry oneEntry() {
	    if (entries.size() == 1) {
		return entries.get(0);
	    }
	    return null;
	}
    }

    /**
     * Reduces leveled list, and then splits it into smaller sub lists if larger
     * than 255 (the max Skyrim can actually handle).
     */
    public void splitEntries() {
	reduce();

	if (numEntries() <= 255) {
	    return;
	}

	// Create Summaries
	int numSplits = (int) (numEntries() / 255.0 + 1);
	int numPerSplit = numEntries() / numSplits;
	ArrayList<LeveledEntry> list = getEntries();
	ArrayList<LListSummary> sums = new ArrayList<>(numSplits);
	for (int i = 0; i < numSplits; i++) {
	    int index = i * numPerSplit;
	    int max = index + numPerSplit;
	    if (max > list.size()) {
		max = list.size();
	    }
	    LListSummary sum = new LListSummary(list.subList(index, max));
	    sum.reduce();
	    sums.add(sum);
	}

	this.clearEntries();
	// Make sub Llists
	for (int i = 0; i < sums.size(); i++) {
	    LListSummary s = sums.get(i);
	    LListEntry e = s.oneEntry();
	    if (e == null) {
		LeveledRecord copy = (LeveledRecord) this.copy(getEDID() + "_" + i);
		copy.setTo(s);
		this.addEntry(copy.getForm(), 1, 1);
	    } else {
		addEntry(e.id, e.level, e.count);
	    }
	}
    }
}
