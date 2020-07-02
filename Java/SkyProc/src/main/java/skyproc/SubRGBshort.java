/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubRGBshort extends SubRecordTyped {

    short r;
    short g;
    short b;
    short a;

    SubRGBshort(String type, short red, short green, short blue, short alpha) {
	this(type);
	r = red;
	g = green;
	b = blue;
    }

    SubRGBshort(String type) {
	super(type);
    }

    @Override
    SubRecord getNew(String type) {
	return new SubRGBshort(type);
    }

    @Override
    int getContentLength(ModExporter out) {
	return 4;
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	r = (short) in.extractInt(1);
	g = (short) in.extractInt(1);
	b = (short) in.extractInt(1);
	a = (short) in.extractInt(1);
	if (SPGlobal.logMods){
	    logMod(srcMod, toString(), "Setting " + toString() + " to : " + print());
	}
    }

    @Override
    public String print() {
	    return "R: " + r + " G: " + g + " B: " + b + " A: " + a;
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    super.export(out);
	    out.write(r, 1);
	    out.write(g, 1);
	    out.write(b, 1);
	    out.write(a, 1);
	}
    }

    /**
     *
     * @param color
     * @param val
     */
    public void set (RGBA color, short val) {
	switch (color) {
	    case Red:
		r = val;
		break;
	    case Green:
		g = val;
		break;
	    case Blue:
		b = val;
		break;
	    case Alpha:
		a = val;
		break;
	}
    }

    /**
     *
     * @param color
     * @return
     */
    public short get (RGBA color) {
	if (!isValid()) {
	    return 0;
	}
	switch (color) {
	    case Red:
		return r;
	    case Green:
		return g;
	    case Blue:
		return b;
	    default:
		return a;
	}
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 97 * hash + this.r;
	hash = 97 * hash + this.g;
	hash = 97 * hash + this.b;
	hash = 97 * hash + this.a;
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubRGBshort other = (SubRGBshort) obj;
	if (this.r != other.r) {
	    return false;
	}
	if (this.g != other.g) {
	    return false;
	}
	if (this.b != other.b) {
	    return false;
	}
	if (this.a != other.a) {
	    return false;
	}
	return true;
    }


}
