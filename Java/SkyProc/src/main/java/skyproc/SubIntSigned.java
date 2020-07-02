/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubIntSigned extends SubInt {

    //private Integer data;
    //int length = 4;

    SubIntSigned(String type) {
	super(type);
    }

    SubIntSigned(String type, int length) {
	this(type);
	this.length = length;
    }

    /*
    @Override
    SubRecord getNew(String type) {
	return new SubIntSigned(type, length);
    }

    @Override
    int getContentLength(ModExporter out) {
	return length;
    }

    @Override
    public void set(int in) {
	data = in;
    }

    @Override
    public int get() {
	if (data == null) {
	    data = 0;
	}
	return data;
    }
    */

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
//	data = in.extractIntSigned(length);
        if (length < 4) {
            int value = get();
            boolean isNeg = (value & (0x80 << (length-1))) != 0;
            if (isNeg) {
                for (int i = length; i < 4; i++) {
                    int high = 0xFF << i * 8;
                    value |= high;
                }
                set(value);
            }
        }
	if (SPGlobal.logMods){
	    logMod(srcMod, toString(), "Setting " + toString() + " to : " + print());
	}
    }
/*
    @Override
    public String print() {
	if (isValid()) {
	    return String.valueOf(data);
	} else {
	    return super.toString();
	}
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    super.export(out);
	    out.write(data, length);
	}
    }

    @Override
    boolean isValid() {
	return data != null;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubIntSigned other = (SubIntSigned) obj;
	return Objects.equals(this.data, other.data);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 71 * hash + Objects.hashCode(this.data);
	return hash;
    }

    @Override
    Integer translate() {
	return data;
    }

    @Override
    SubRecord<Integer> translate(Integer in) {
	SubIntSigned out = (SubIntSigned) getNew(getType());
	out.set(in);
	return out;
    }
    */
}
