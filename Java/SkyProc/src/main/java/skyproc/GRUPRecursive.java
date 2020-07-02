/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class GRUPRecursive<T extends MajorRecord> extends GRUP<T> {

    GRUPRecursive(T prototype) {
	super(prototype);
    }

    @Override
    public MajorRecord extractMajor(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	MajorRecord m = super.extractMajor(in, srcMod);
	if (m != null && !in.isDone() && "GRUP".equals(getNextType(in))) {
	    if (SPGlobal.logging()) {
		SPGlobal.log("GRUPRecursive", "Extracting an appended GRUP.");
	    }
	    GRUP g = m.getGRUPAppend();
	    g.parseData(g.extractRecordData(in), srcMod);
	}
	return m;
    }
}