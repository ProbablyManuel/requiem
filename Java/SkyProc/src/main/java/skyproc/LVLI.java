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
public class LVLI extends LeveledRecord {

    // Static prototypes and definitions
    static final SubPrototype LVLIproto = new SubPrototype(LeveledRecord.LeveledProto){

	@Override
	protected void addRecords() {
//	    before(new SubForm("LVLG"), "LVLO");  // moved to LeveledRecord to support tesedit added LVLG in LVLN
	}
    };

    // Common Functions
    LVLI () {
	super();
	subRecords.setPrototype(LVLIproto);
    }

    /**
     * Creates a new LVLI record with a FormID originating from the mod parameter.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLI(String edid) {
        super(edid);
	subRecords.setPrototype(LVLIproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("LVLI");
    }

    @Override
    Record getNew() {
	return new LVLI();
    }

}
