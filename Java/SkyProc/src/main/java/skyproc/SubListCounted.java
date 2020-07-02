/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubListCounted<T extends SubRecord> extends SubList {

    String counterType = "";
    private int counterLength = 4;

    SubListCounted(String counter, int counterLength, T prototype_) {
        super(prototype_);
        counterType = counter;
        this.counterLength = counterLength;
    }

    SubListCounted(SubListCounted rhs) {
	super(rhs);
	counterType = rhs.counterType;
	counterLength = rhs.counterLength;
    }

    @Override
    int getContentLength(ModExporter out) {
	int length = super.getContentLength(out);
	if (!"".equals(counterType)) {
	    length += counterLength + 6;
	}
	return length;
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
        String t = getNextType(in);
        if (!t.equals(counterType)) {
            super.parseData(in, srcMod, t);
        }
    }

    @Override
    SubRecord getNew(String type) {
	return new SubListCounted(this);
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    if (!"".equals(counterType)) {
		SubData counter = new SubData(counterType, Ln.toByteArray(collection.size(), counterLength));
		counter.export(out);
	    }
	    super.export(out);
	}
    }

    @Override
    ArrayList<String> getTypes() {
	ArrayList<String> out = new ArrayList<>(super.getTypes());
	out.add(counterType);
	return out;
    }


}
