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
public class FACT extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype FACTproto = new SubPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    add(new SubList<>(new SubData("XNAM")));
	    add(new SubData("DATA"));
	    add(new SubForm("JAIL"));
	    add(new SubForm("WAIT"));
	    add(new SubForm("STOL"));
	    add(new SubForm("PLCN"));
	    add(new SubForm("CRGR"));
	    add(new SubForm("JOUT"));
	    add(new SubData("CRVA"));
	    add(new SubList<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt("RNAM"));
		    add(new SubStringPointer("MNAM", SubStringPointer.Files.STRINGS));
		    add(new SubData("FNAM"));
		}
	    })));
	    add(new SubForm("VEND"));
	    add(new SubForm("VENC"));
	    add(new SubData("VENV"));
	    add(new SubData("PLVD"));
	    add(new SubInt("CITC"));
	    add(new SubData("CTDA"));
	    add(SubString.getNew("CIS2", true));
	}
    };

    // Common Functions
    FACT() {
	super();
	subRecords.setPrototype(FACTproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("FACT");
    }

    @Override
    Record getNew() {
	return new FACT();
    }
}
