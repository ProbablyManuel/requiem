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
public class WTHR extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype WTHRprototype = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubString("DNAM"));
	    add(new SubString("CNAM"));
	    add(new SubString("ANAM"));
	    add(new SubString("BNAM"));
	    add(new SubString("00TX"));
	    add(new SubString("10TX"));
	    add(new SubString("20TX"));
	    add(new SubString("30TX"));
	    add(new SubString("40TX"));
	    add(new SubString("50TX"));
	    add(new SubString("60TX"));
	    add(new SubString("70TX"));
	    add(new SubString("80TX"));
	    add(new SubString("90TX"));
	    add(new SubString(":0TX"));
	    add(new SubString(";0TX"));
	    add(new SubString("<0TX"));
	    add(new SubString("=0TX"));
	    add(new SubString(">0TX"));
	    add(new SubString("?0TX"));
	    add(new SubString("@0TX"));
	    add(new SubString("A0TX"));
	    add(new SubString("B0TX"));
	    add(new SubString("C0TX"));
	    add(new SubString("D0TX"));
	    add(new SubString("E0TX"));
	    add(new SubString("F0TX"));
	    add(new SubString("G0TX"));
	    add(new SubString("H0TX"));
	    add(new SubString("I0TX"));
	    add(new SubString("J0TX"));
	    add(new SubString("K0TX"));
	    add(new SubString("L0TX"));
	    add(new SubData("LNAM"));
	    add(new SubForm("MNAM"));
	    forceExport("MNAM");
	    add(new SubForm("NNAM"));
	    forceExport("NNAM");
	    add(new SubData("RNAM"));
	    add(new SubData("QNAM"));
	    add(new SubData("ONAM"));
	    add(new SubData("PNAM"));
	    add(new SubData("JNAM"));
	    add(new SubData("NAM0"));
	    add(new SubData("FNAM"));
	    add(new SubData("DATA"));
	    add(new SubData("NAM1"));
	    add(new SubList<>(new SubFormInt("SNAM")));
	    add(new SubList<>(new SubForm("TNAM")));
	    add(new SubFormArray("IMSP", 4));
	    add(new SubList<>(new SubData("DALC")));
	    add(new SubString("MODL"));
	    add(new SubData("MODT"));
	    add(new SubData("NAM2"));
	    add(new SubData("NAM3"));
	}
    };

    // Common Functions
    WTHR() {
	super();
	subRecords.setPrototype(WTHRprototype);
    }

    @Override
    Record getNew() {
	return new WTHR();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("WTHR");
    }
}