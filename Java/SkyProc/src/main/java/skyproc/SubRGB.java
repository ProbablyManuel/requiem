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
class SubRGB extends SubRecordTyped {

    float r;
    float g;
    float b;

    SubRGB(String type, float red, float green, float blue) {
	this(type);
	r = red;
	g = green;
	b = blue;
    }

    SubRGB(String type) {
	super(type);
    }

    @Override
    SubRecord getNew(String type) {
	return new SubRGB(type);
    }

    @Override
    int getContentLength(ModExporter out) {
	return 12;
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	r = in.extractFloat();
	g = in.extractFloat();
	b = in.extractFloat();
	if (SPGlobal.logMods){
	    logMod(srcMod, toString(), "Setting " + toString() + " to : " + print());
	}
    }

    @Override
    public String print() {
	if (isValid()) {
	    return "R: " + r + " G: " + g + " B: " + b;
	} else {
	    return super.toString();
	}
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    super.export(out);
	    out.write(r);
	    out.write(g);
	    out.write(b);
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubRGB other = (SubRGB) obj;
	if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r)) {
	    return false;
	}
	if (Float.floatToIntBits(this.g) != Float.floatToIntBits(other.g)) {
	    return false;
	}
	if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 19 * hash + Float.floatToIntBits(this.r);
	hash = 19 * hash + Float.floatToIntBits(this.g);
	hash = 19 * hash + Float.floatToIntBits(this.b);
	return hash;
    }

    /**
     *
     * @param color
     * @param val
     */
    public void set (RGB color, float val) {
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
	}
    }

    /**
     *
     * @param color
     * @return
     */
    public float get (RGB color) {
	if (!isValid()) {
	    return 0;
	}
	switch (color) {
	    case Red:
		return r;
	    case Green:
		return g;
	    default:
		return b;
	}
    }
}
