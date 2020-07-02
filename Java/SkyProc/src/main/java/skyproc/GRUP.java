package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A GRUP is a collection of Major Records.
 *
 * @param <T> The type of Major Record a GRUP contains.
 * @author Justin Swanson
 */
public class GRUP<T extends MajorRecord> extends SubRecord implements Iterable<T> {

    byte[] contained;
    byte[] grupType = new byte[4];
    byte[] dateStamp = new byte[4];
    byte[] version = new byte[4];
    ArrayList<T> listRecords = new ArrayList<>();
    Map<FormID, T> mapRecords = new HashMap<>();
    Map<String, T> edidRecords = new HashMap<>();
    T prototype;

    GRUP(T prototype) {
	this.prototype = prototype;
	contained = Ln.toByteArray(prototype.getType());
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("GRUP");
    }

    /**
     *
     * @return An enum constant representing the type of record the GRUP
     * contains.
     */
    public GRUP_TYPE getContainedType() {
	return GRUP_TYPE.valueOf(prototype.getType());
    }

    /**
     *
     * @return A generic title of the GRUP. (eg. "Skyrim.esm - LVLN GRUP")
     */
    @Override
    public String toString() {
	return getContainedType().toString() + " GRUP";
    }

    /**
     *
     * @return True if GRUP has records. (size > 0)
     */
    @Override
    boolean isValid() {
	return !isEmpty();
    }

    /**
     *
     * @return Returns true if GRUP contains no records.
     */
    public Boolean isEmpty() {
	return mapRecords.isEmpty();
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	contained = in.extract(4);
	grupType = in.extract(4); // What kind of GRUP data it has.
	dateStamp = in.extract(4);
	version = in.extract(4);
	while (!in.isDone()) {
	    extractMajor(in, srcMod);
	}
	if (SPGlobal.logMods){
	    logMod(srcMod, toString(), "Data exhausted");
	}
    }

    MajorRecord extractMajor(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	if (SPGlobal.logMods){
	    logMod(srcMod, toString(), "============== Extracting Next " + getContainedType() + " =============");
	}
	T item = (T) prototype.getNew();
	item.srcMod = srcMod;
	item.subRecords.setMajor(item);
	try {

	    item.parseData(item.extractRecordData(in), srcMod);

	    // Add to GRUP
	    if (item.isValid()) {
		addRecord(item);
	    } else if (SPGlobal.logMods){
		logMod(srcMod, toString(), "Did not add " + getContainedType().toString() + " " + item.toString() + " because it was not valid.");
	    }

	    return item;
	} catch (java.nio.BufferUnderflowException e) {
	    SPGlobal.logException(e);
	    handleBadRecord(item, e.toString());
	}
	return null;
    }

    /**
     *
     * @return Number of records the GRUP contains
     */
    public int size() {
	return listRecords.size();
    }

    /**
     * Prints contents of GRUP to the asynchronous log.
     *
     * @return Returns the empty string.
     */
    @Override
    public String print() {
	if (!isEmpty()) {
	    for (T t : mapRecords.values()) {
		t.toString();
	    }
	}
	return "";
    }

    @Override
    void export(ModExporter out) throws IOException {
	out.write(getType().toString());
	out.write(getContentLength(out) + getHeaderLength(), getSizeLength());
	out.write(contained);
	out.write(grupType);
	out.write(dateStamp);
	out.write(version);
	if (logging()) {
	    logSync(this.toString(), "Exporting " + this.numRecords() + " " + getContainedType() + " records.");
	}
	for (MajorRecord t : this) {
	    if (logging()) {
		logSync(this.toString(), t.toString());
	    }
	    t.export(out);
	}
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (T item : listRecords) {
	    out.addAll(item.allFormIDs());
	}
	return out;
    }

    /**
     *
     * @return The number of contained records.
     */
    public int numRecords() {
	return mapRecords.size();
    }

    /**
     * Removes a record from the GRUP.
     *
     * @param id The FormID of the record to remove.
     * @return Returns true if a record was removed; False if no record with id
     * was contained.
     */
    public boolean removeRecord(FormID id) {
	if (mapRecords.containsKey(id)) {
	    listRecords.remove(mapRecords.get(id));
	    MajorRecord r = mapRecords.get(id);
	    edidRecords.remove(r.getEDID().toUpperCase());
	    mapRecords.remove(id);
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Removes a record from the GRUP.
     *
     * @param item Major Record to remove. Removal is based on FormID match.
     * @return Returns true if a record was removed; False if no record with id
     * was contained.
     */
    public boolean removeRecord(T item) {
	return removeRecord(item.getForm());
    }

    void handleBadRecord(MajorRecord r, String reason) {
	if (SPGlobal.logMods){
	    if (r.isValid()) {
		logMod(r.srcMod, toString(), "Caught a bad record: " + r + ", reason: " + reason);
		logSpecial(SPLogger.SpecialTypes.BLOCKED, toString(), "Caught a bad record: " + r + " from " + r.srcMod + ", reason: " + reason);
	    } else {
		logMod(r.srcMod, toString(), "Caught a bad record, reason:" + reason);
		logSpecial(SPLogger.SpecialTypes.BLOCKED, toString(), "Caught a bad record, reason:" + reason);
	    }
	}
    }

    /**
     * Adds a record to the group, and does the following: 1) Standardizes the
     * record's FormIDs to the database the GRUP is contained in.
     *
     * @param item Record to add to the GRUP.
     */
    public void addRecord(T item) {
	removeRecord(item);
	mapRecords.put(item.getForm(), item);
	edidRecords.put(item.getEDID().toUpperCase(), item);
	listRecords.add(item);
    }

    /**
     *
     * @return A list of all records in the GRUP
     */
    public ArrayList<T> getRecords() {
	return listRecords;
    }

    void addRecord(Object item) {
	addRecord((T) item);
    }

    /**
     *
     * @param id FormID to look for.
     * @return Returns true if GRUP contains a record with id.
     */
    public boolean contains(FormID id) {
	return mapRecords.containsKey(id);
    }

    /**
     *
     * @param edid EDID to look for.
     * @return Returns true if GRUP contains a record with edid.
     */
    public boolean contains(String edid) {
	return edidRecords.containsKey(edid.toUpperCase());
    }

    /**
     *
     * @param item Record to check check for containment. (based on its FormID)
     * @return Returns true if GRUP contains a record with FormID == id.
     */
    public boolean contains(T item) {
	return contains(item.getForm());
    }

    /**
     *
     * @param id FormID to query the GRUP for.
     * @return Major Record with FormID equaling parameter. Null if one does not exist.
     */
    public MajorRecord get(FormID id) {
	return mapRecords.get(id);
    }

    /**
     *
     * @param edid EDID to query the GRUP for.
     * @return Major Record with FormID equaling parameter. Null if one does not exist.
     */
    public MajorRecord get(String edid) {
	return edidRecords.get(edid.toUpperCase());
    }

    /**
     * Deletes all records from the GRUP.
     */
    public void clear() {
	listRecords.clear();
	mapRecords.clear();
	edidRecords.clear();
    }

    @Override
    Record getNew() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Takes all records from rhs and adds them to the GRUP. Any conflicting
     * records (those whose FormIDs match) will be replaced with rhs's version.
     *
     * @param rhs GRUP to copy records from.
     */
    public void merge(GRUP<T> rhs) {
	if (logging() && SPGlobal.debugModMerge) {
	    log(toString(), "Size before: " + numRecords());
	}
	for (MajorRecord item : rhs) {
	    if (logging() && SPGlobal.debugModMerge) {
		if (contains(item.getForm())) {
		    log(toString(), "Replacing record " + item.toString() + " with one from " + rhs.toString());
		} else {
		    log(toString(), "Adding record " + item.toString());
		}
	    }
	    addRecord(item);
	}
	if (logging() && SPGlobal.debugModMerge) {
	    log(toString(), "Size after: " + numRecords());
	}
    }

    @Override
    int getFluffLength() {
	return 16;
    }

    @Override
    int getSizeLength() {
	return 4;
    }

    @Override
    int getContentLength(ModExporter out) {
	int length = 0;
	for (T t : listRecords) {
	    if (t.isValid()) {
		length += t.getTotalLength(out);
	    }
	}
	return length;
    }

    /**
     *
     * @return An iterator that steps through each record in the GRUP, in the
     * order they were added.
     */
    @Override
    public Iterator<T> iterator() {
	ArrayList<T> temp = new ArrayList<>();
	temp.addAll(listRecords);
	return temp.iterator();
    }

    @Override
    SubRecord getNew(String type) {
	return new GRUP<>(prototype);
    }

    @Override
    public int getRecordLength(LImport in) {
	return Ln.arrayToInt(in.getInts(getIdentifierLength(), getSizeLength()));
    }
}
