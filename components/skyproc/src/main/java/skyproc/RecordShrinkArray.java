/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import lev.LImport;
import lev.LShrinkArray;

/**
 * For use with DirtyParsingIterator to retrieve subrecord data.
 * @see SPImporter
 * @author Justin Swanson
 */
public class RecordShrinkArray extends LShrinkArray {

    @Override
    public String toString() {
        return "RecordShrinkArray{" + "offset=" + offset + '}' ;
    }

    int offset;

    /**
     *
     * @param rhs
     * @param high
     */
    public RecordShrinkArray(final LImport rhs, final int high) {
	super(rhs, high);
	offset = (int) rhs.pos();
    }

    /**
     *
     * @param rhs
     */
    public RecordShrinkArray(LShrinkArray rhs) {
	super(rhs);
	offset = (int) rhs.pos();
    }

    /**
     *
     */
    public RecordShrinkArray() {
	super(new byte[0]);
	offset = 0;
    }

    /**
     *
     * @param in
     */
    public RecordShrinkArray(byte[] in) {
	super(in);
	offset = 0;
    }

    @Override
    public long pos() {
	return super.pos() + offset;
    }

    @Override
    public void pos(long in) {
	super.pos(in - offset);
    }

    /**
     * Extracts the next four bytes of data and interprets them as a FormID
     * (regardless of whether it actually is), and standardizes it to the
     * given mod.
     * @param modToStandardizeTo
     * @return
     */
    public FormID extractFormID(Mod modToStandardizeTo) {
	FormID out = new FormID();
	if (!isDone()) {
	    byte[] extract = extract(4);
	    byte[] copy = new byte[4];
	    System.arraycopy(extract, 0, copy, 0, copy.length);
	    out.setInternal(copy, modToStandardizeTo);
	}
	return out;
    }

    /**
     * Extracts the next four bytes of data and interprets them as a FormID
     * (regardless of whether it actually is), and standardizes it to the
     * given mod.
     * @param modToStandardizeTo
     * @return
     */
    public FormID extractFormID(ModListing modToStandardizeTo) {
	Mod mod = SPGlobal.getDB().getMod(modToStandardizeTo);
	if (mod == null) {
	    return new FormID();
	}
	return extractFormID(mod);
    }

    /**
     * Scans to the typestring, skips the length bytes (2),
     * and extracts 4 bytes of data interprets them as a FormID
     * (regardless of whether it actually is), and standardizes it to the
     * given mod.
     * @param type
     * @param modToStandardizeTo
     * @return
     */
    public FormID extractFormID(String type, Mod modToStandardizeTo) {
	extractUntil(type);
	if (!isDone()) {
	    skip(2); // Length bytes
	}
	return extractFormID(modToStandardizeTo);
    }

    /**
     * Scans to the typestring, skips the length bytes (2),
     * and extracts 4 bytes of data interprets them as a FormID
     * (regardless of whether it actually is), and standardizes it to the
     * given mod.
     * @param type
     * @param modToStandardizeTo
     * @return
     */
    public FormID extractFormID(String type, ModListing modToStandardizeTo) {
	Mod mod = SPGlobal.getDB().getMod(modToStandardizeTo);
	if (mod == null) {
	    return new FormID();
	}
	return extractFormID(type, mod);
    }
}
