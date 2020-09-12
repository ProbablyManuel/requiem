/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author Justin Swanson
 */
public class TXST extends MajorRecord implements Iterable<String> {

    // Static prototypes and definitions
    static final SubPrototype TXSTproto = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    add(SubString.getNew("TX00", true));
	    add(SubString.getNew("TX01", true));
	    add(SubString.getNew("TX02", true));
	    add(SubString.getNew("TX03", true));
	    add(SubString.getNew("TX04", true));
	    add(SubString.getNew("TX05", true));
	    add(SubString.getNew("TX06", true));
	    add(SubString.getNew("TX07", true));
	    add(new SubData("DODT"));
	    add(new SubFlag("DNAM", 2));
	}
    };
    static int NUM_MAPS = 8;

    // Common Functions
    /**
     * Constructor to create a blank new TXST record.
     *
     * @param edid The edid to give the new record (make it unique)
     */
    public TXST(String edid) {
	this();
	originateFromPatch(edid);
    }

    TXST() {
	super();
	subRecords.setPrototype(TXSTproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("TXST");
    }

    @Override
    Record getNew() {
	return new TXST();
    }

    // Get/Set
    /**
     * Sets the TX00 - TX07 records in order.
     *
     * @param i the nth map to set
     * @param path The filepath to set the map to.
     */
    public void setNthMap(int i, String path) {
	SubString map = getNthMapInternal(i);
	if (map != null) {
	    map.setString(path);
	}
    }

    SubString getNthMapInternal(int i) {
	switch (i) {
	    case 0:
		return subRecords.getSubString("TX00");
	    case 1:
		return subRecords.getSubString("TX01");
	    case 2:
		return subRecords.getSubString("TX02");
	    case 3:
		return subRecords.getSubString("TX03");
	    case 4:
		return subRecords.getSubString("TX04");
	    case 5:
		return subRecords.getSubString("TX05");
	    case 6:
		return subRecords.getSubString("TX06");
	    case 7:
		return subRecords.getSubString("TX07");
	    default:
		return null;
	}
    }

    /**
     * Sets the TX00 - TX07 records in order.
     *
     * @param i the nth map to set
     * @return The filepath the map is set to.
     */
    public String getNthMap(int i) {
	SubString map = getNthMapInternal(i);
	if (map != null) {
	    return map.print();
	}
	return "";
    }

    /**
     *
     * @param path
     */
    public void setColorMap(String path) {
	setNthMap(0, path);
    }

    /**
     *
     * @return
     */
    public String getColorMap() {
	return getNthMap(0);
    }

    /**
     *
     * @param path
     */
    public void setNormalMap(String path) {
	setNthMap(1, path);
    }

    /**
     *
     * @return
     */
    public String getNormalMap() {
	return getNthMap(1);
    }

    /**
     *
     * @param path
     */
    public void setMaskMap(String path) {
	setNthMap(2, path);
    }

    /**
     *
     * @return
     */
    public String getMaskMap() {
	return getNthMap(2);
    }

    /**
     *
     * @param path
     */
    public void setToneMap(String path) {
	setNthMap(3, path);
    }

    /**
     *
     * @return
     */
    public String getToneMap() {
	return getNthMap(3);
    }

    /**
     *
     * @param path
     */
    public void setDetailMap(String path) {
	setNthMap(4, path);
    }

    /**
     *
     * @return
     */
    public String getDetailMap() {
	return getNthMap(4);
    }

    /**
     *
     * @param path
     */
    public void setEnvironmentMap(String path) {
	setNthMap(5, path);
    }

    /**
     *
     * @return
     */
    public String getEnvironmentMap() {
	return getNthMap(5);
    }

    /**
     *
     * @param path
     */
    public void setSpecularityMap(String path) {
	setNthMap(7, path);
    }

    /**
     *
     * @return
     */
    public String getSpecularityMap() {
	return getNthMap(7);
    }

    /**
     *
     * @param flag TXST flag to check
     * @return True if flag is on.
     */
    public boolean get(TXSTflag flag) {
	return subRecords.getSubFlag("DNAM").is(flag.ordinal());
    }

    /**
     *
     * @param flag TXST flag to set
     * @param to Boolean to set the flag to
     */
    public void set(TXSTflag flag, boolean to) {
	subRecords.setSubFlag("DNAM", flag.ordinal(), to);
    }

    /**
     * Flags associated with TXST records
     */
    public enum TXSTflag {

	/**
	 *
	 */
	SPECULAR_MAP_OFF,
	/**
	 *
	 */
	FACEGEN_TEXTURES
    }

    /**
     *
     * @return All texture path names
     */
    public ArrayList<String> getTextures() {
	ArrayList<String> temp = new ArrayList<>();
	for (int i = 0; i < NUM_MAPS; i++) {
	    temp.add(getNthMap(i));
	}
	return temp;
    }

    /**
     *
     * @return An iterator that steps through each record in the GRUP, in the
     * order they were added.
     */
    @Override
    public Iterator<String> iterator() {
	return getTextures().iterator();
    }
}
