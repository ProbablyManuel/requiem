/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Form List Record
 * @author AliTheLord
 */
public class FLST extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype FLSTproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubList<>(new SubForm("LNAM")));
	}
    };

    // Common Functions
    FLST() {
	super();
	subRecords.setPrototype(FLSTproto);
    }

    /**
     *
     * @param edid EDID to give the new record.  Make sure it is unique.
     */
    public FLST (String edid) {
	this();
	originateFromPatch(edid);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("FLST");
    }

    @Override
    Record getNew() {
	return new FLST();
    }

    // Get/Set
    /**
     *
     * @return List of all the FormIDs in the Form list.
     */
    public ArrayList<FormID> getFormIDEntries() {
	return subRecords.getSubList("LNAM").toPublic();
    }

    /**
     * 
     * @param entry FormID to add to the list.
     */
    public void addFormEntry(FormID entry) {
	subRecords.getSubList("LNAM").add(entry);
    }

    /**
     * 
     * @param entries
     */
    public void addAll(Collection<FormID> entries) {
	subRecords.getSubList("LNAM").addAll(entries);
    }

    /**
     * 
     * @param entry FormID to remove (if it exists).
     */
    public void removeFormEntry(FormID entry) {
	subRecords.getSubList("LNAM").remove(entry);
    }

    /**
     *
     * @return
     */
    public int getSize() {
	return subRecords.getSubList("LNAM").size();
    }

    /**
     * 
     * @param entry
     * @param i
     */
    public void addFormEntryAtIndex(FormID entry, int i) {
	subRecords.getSubList("LNAM").addAtIndex(entry, i);
    }

    /**
     * 
     */
    public void clearEntries() {
	subRecords.getSubList("LNAM").clear();
    }
}
