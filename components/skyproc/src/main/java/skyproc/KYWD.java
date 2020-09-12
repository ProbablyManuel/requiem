/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 * Keyword Records
 * @author Justin Swanson
 */
public class KYWD extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype KYWDproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubData("CNAM"));
	}
    };

    // Common Functions
    KYWD () {
	super();
	subRecords.setPrototype(KYWDproto);
    }

    /**
     *
     * @param edid EDID to assign the record.  Make sure it's unique.
     * @param color Color to have the keyword highlight as.
     */
    public KYWD (String edid, int color) {
	this(edid);
	subRecords.setSubData("CNAM", color);
    }

    /**
     *
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public KYWD (String edid) {
	this();
	originateFromPatch(edid);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("KYWD");
    }

    @Override
    Record getNew() {
	return new KYWD();
    }

}
