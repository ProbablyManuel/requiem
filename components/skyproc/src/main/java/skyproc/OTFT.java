/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public class OTFT extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype OTFTproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubFormArray("INAM", 0));
	}
    };

    // Common Functions
    OTFT() {
	super();
	subRecords.setPrototype(OTFTproto);
    }

    /**
     *
     * @param uniqueEDID
     */
    public OTFT(String uniqueEDID) {
	this();
	originateFromPatch(uniqueEDID);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("OTFT");
    }

    @Override
    Record getNew() {
	return new OTFT();
    }

    // Get/Set
    /**
     *
     * @return
     */
    public ArrayList<FormID> getInventoryList() {
	return subRecords.get("INAM").allFormIDs();
    }

    /**
     *
     * @param item
     */
    public void addInventoryItem(FormID item) {
	subRecords.getSubFormArray("INAM").add(item);
    }

    /**
     *
     * @param item
     */
    public void removeInventoryItem (FormID item) {
	subRecords.getSubFormArray("INAM").remove(item);
    }

    /**
     *
     */
    public void clearInventoryItems() {
	subRecords.getSubFormArray("INAM").clear();
    }
}
