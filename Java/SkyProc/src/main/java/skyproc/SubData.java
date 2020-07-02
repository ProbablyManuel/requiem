/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LOutFile;
import lev.LShrinkArray;
import lev.LImport;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubData extends SubRecordTyped<byte[]> {

    byte[] data;

    SubData(String type_) {
	super(type_);
    }

    SubData(String type_, byte[] in) {
	this(type_);
	if (in != null) {
	    data = new byte[in.length];
	    System.arraycopy(in, 0, data, 0, in.length);
	}
    }

    SubData(String type_, int in) {
	this(type_, Ln.toByteArray(in));
    }

    void initialize(int size) {
	data = new byte[size];
    }

    public void modValue(int mod) {
	setData(Ln.toByteArray(Ln.arrayToInt(getData()) + mod, getData().length));
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	setData(in.extractAllBytes());
    }

    @Override
    boolean isValid() {
	return data != null;
    }

    void setData(byte[] data_) {
	data = data_;
    }

    void setData(int data) {
	setData(data, 4);
    }

    void setData(int data, int size) {
	setData(Ln.toByteArray(data, size));
    }

    void setDataAbs(int data, int min, int max) {
	setData(Ln.toByteArray(Math.abs(data), min, max));
    }

    byte[] getData() {
	return data;
    }

    int toInt() {
	return Ln.arrayToInt(data);
    }

    @Override
    public String print() {
	if (isValid()) {
	    return Ln.printHex(data, true, false);
	} else {
	    return super.toString();
	}
    }

    @Override
    int getContentLength(ModExporter out) {
	if (isValid()) {
	    return data.length;
	} else {
	    return 0;
	}
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (data == null) {
	    data = new byte[0];
	}
	super.export(out);
	out.write(data, 0);
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubData(type_, data);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 23 * hash + Arrays.hashCode(this.data);
	return hash;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null) {
	    return false;
	}
	if (!(o instanceof byte[])) {
	    return false;
	}
	byte[] b = (byte[]) o;
	return (Arrays.equals(this.data, b));
    }

    @Override
    byte[] translate() {
	return data;
    }

    @Override
    SubRecord<byte[]> translate(byte[] in) {
	return new SubData(getType(), in);
    }

}
