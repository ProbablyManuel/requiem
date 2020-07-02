/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * This class represents a FormID that distinguishes one record from another.
 * The FormID is value unique across all mods. This class supports automatic mod
 * index correction.
 *
 * @author Justin Swanson
 */
public class FormID implements Comparable, Serializable {

    static ArrayList<FormID> allIDs = new ArrayList<>();
    /**
     * FormID to compare to when determining NULL formIDs.
     */
    public static final FormID NULL = new FormID();
    byte[] form = new byte[4];
    ModListing master = null;

    /**
     * An empty FormID for easy NULL checking.
     */
    FormID() {
	if (SPGlobal.testing) {
	    allIDs.add(this);
	}
    }

    /**
     *
     * @param id String containing the last 6 digits of a FormID. Acceptable
     * forms: "000123", "00 01 23", "0x00 0x01 0x23".
     * @param master String containing the mod it originates from. Eg.
     * "Skyrim.esm"
     */
    public FormID(String id, String master) {
	this(id, new ModListing(master));
    }

    /**
     *
     * @param form String containing the last 6 digits of a FormID, followed
     * immediately by the plugin it originates from. eg "000123Skyrim.esm"
     */
    public FormID(String form) {
	this(form.substring(0, 6), form.substring(6));
    }

    /**
     *
     * @param id String containing the last 6 digits of a FormID. Acceptable
     * forms: "000123", "00 01 23", "0x00 0x01 0x23".
     * @param master The mod from which this formID originates.
     */
    public FormID(String id, ModListing master) {
	this(Ln.parseHexString(id, 4, false), master);
    }

    /**
     *
     * @param id Byte array (usually size 3), which contains FormID bytes.
     * @param master The mod from which this formID originates.
     */
    public FormID(byte[] id, ModListing master) {
	this();
	set(id);
	this.master = master;
    }

    /**
     *
     * @param id An int array containing the last 6 digits of the FormID.
     * @param master The modname from which this formID originates.
     */
    public FormID(int[] id, ModListing master) {
	this(Ln.toByteArray(id), master);
    }

    FormID(int id, ModListing master) {
	this();
	setInternal(Ln.toByteArray(id));
	this.master = master;
    }

    /**
     * Copy constructor. Creates a separate but identical formid.
     *
     * @param in
     */
    public FormID(FormID in) {
	System.arraycopy(in.form, 0, form, 0, in.form.length);
	master = in.master;
    }

    final void set(byte[] id) {
	setInternal(Ln.reverse(id));
    }
    
    void setTo(FormID id) {
	form = id.form;
	master = id.master;
    }

    private void setInternal(byte[] id) {
	form = id;
	if (id.length > 4) {
	    form = Arrays.copyOfRange(form, 0, 3);
	} else if (id.length < 4) {
	    form = new byte[4];
	    System.arraycopy(id, 0, form, 0, id.length);
	}
    }
    
    final void setInternal(byte[] id, Mod srcMod) {
	setInternal(id);
	standardize(srcMod);
    }

    void export(ModExporter out) throws IOException {
	standardize(out.getSourceMod());
	adjustMasterIndex(out.getExportMod());
	out.write(getInternal(true), 4);
    }
    
    void parseData(LImport in, Mod srcMod) {
	setInternal(in.extract(4), srcMod);
    }

    String getArrayStr(Boolean masterIndex) {
	if (isValid()) {
	    return Ln.printHex(getInternal(masterIndex), false, true);
	} else {
	    return "No FormID";
	}
    }

    /**
     *
     * @return An int array of length 8, including the current master index.
     */
    public byte[] get() {
	return Ln.reverse(getInternal(true));
    }

    byte[] getInternal(Boolean masterIndex) {
	if (!masterIndex) {
	    return Arrays.copyOfRange(form, 0, 3);
	} else {
	    return form;
	}
    }

    /**
     *
     * @return The name of the mod from which this FormID originates.
     */
    public ModListing getMaster() {
	return master;
    }

    /**
     *
     * @return A string representation of this FormID, with the master name
     * printed instead of numerical mod index.
     */
    public String getFormStr() {
	if (master == null) {
	    return getArrayStr(false);
	} else {
	    return getArrayStr(false) + getMaster().print();
	}
    }

    /**
     *
     * @return A string representing the FormID.
     */
    public String getTitle() {
	return toString();
    }

    /**
     *
     * @return A representation of the formID. (eg. "000123Skyrim.esm")
     */
    @Override
    public String toString() {
	if (isStandardized()) {
	    return getFormStr();
	} else if (isValid()) {
	    return getArrayStr(true);
	} else {
	    return "NULL";
	}
    }

    void standardize(Mod srcMod) {
	if (master == null) {
	    master = srcMod.getNthMaster(form[3]);
	}
    }

    void adjustMasterIndex(Mod srcMod) {
	form[3] = (byte) srcMod.getMasterIndex(master);
    }

    /**
     * Parses a string like "023abeSkyrim.esm" and returns:<br>
     * ["023abe"]["Skyrim.esm"]
     *
     * @param s
     * @return
     */
    public static String[] parseString(String s) {
	String[] ida = new String[2];
	if (s.length() > 6) {
	    ida[0] = s.substring(0, 6);
	    String modName = s.substring(6);
	    modName = Ln.cleanLine(modName, "//");
	    modName = Ln.cleanLine(modName, ";");
	    ida[1] = modName;
	} else {
	    ida[0] = "000000";
	    ida[1] = SPGlobal.gameName + ".esm";
	}
	return ida;
    }

    /**
     * Expects something like ["023abe"]["Skyrim.esm"] and returns a FormID
     * object.
     *
     * @param s String array of length 2. First index must be length 6.
     * @return A FormID object representing the array, or NULL if malformed.
     */
    public static FormID parseString(String[] s) {
	if (s.length == 2 && s[0].length() == 6) {
	    return new FormID(s[0], s[1]);
	} else {
	    return new FormID(FormID.NULL);
	}
    }

    boolean isValid() {
	return !equals(NULL);
    }

    Boolean isStandardized() {
	return master != null;
    }

    int getContentLength() {
	return 4;
    }

    /**
     *
     * @param obj Another FormID
     * @return True if the originating masters and the last 6 indices match.
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final FormID other = (FormID) obj;
	for (int i = 0; i < 3; i++) {
	    if (form[i] != other.form[i]) {
		return false;
	    }
	}
	if ((this.master == null) ? (other.master != null) : !this.master.equals(other.master)) {
	    return false;
	}
	return true;
    }

    /**
     *
     * @return True if equal to FormID.NULL
     */
    public boolean isNull() {
	return equals(FormID.NULL);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 7;
	hash = 97 * hash + Arrays.hashCode(Arrays.copyOf(this.form, 3));
	hash = 97 * hash + (this.master != null ? this.master.hashCode() : 0);
	return hash;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
	if (o == null || getClass() != o.getClass()) {
	    return 0;
	}
	FormID rhs = (FormID) o;
	if (master.equals(rhs.master)) {
	    return Ln.arrayToInt(form) - Ln.arrayToInt(rhs.form);
	} else {
	    return master.compareTo(rhs.master);
	}
    }
}
