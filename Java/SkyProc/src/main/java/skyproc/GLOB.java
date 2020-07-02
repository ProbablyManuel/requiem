/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class GLOB extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype GLOBproto = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    SubData fnam = new SubData("FNAM");
	    fnam.data = new byte[1];
	    add(fnam);
	    add(new SubFloat("FLTV"));
	}
    };

    // Enums
    /**
     *
     */
    public enum GLOBType {
	/**
	 *
	 */
	Short (0x73),
	/**
	 *
	 */
	Long (0x6C),
	/**
	 *
	 */
	Float (0x66);

	int value;

	GLOBType (int value) {
	    this.value = value;
	}
    }

    // Get/Set
    GLOB () {
	super();
	subRecords.setPrototype(GLOBproto);
    }

    /**
     *
     * @param edid
     * @param type
     */
    public GLOB(String edid, GLOBType type) {
	this();
	originateFromPatch(edid);
	setType(type);
    }

    /**
     *
     * @param edid
     * @param type
     * @param value
     * @param constant
     */
    public GLOB(String edid, GLOBType type, float value, Boolean constant) {
	this(edid, type);
	setValue(value);
	setConstant(constant);
    }

    @Override
    Record getNew() {
	return new GLOB();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("GLOB");
    }

    // Get/Set
    /**
     *
     * @return
     */
    public GLOBType getGLOBType () {
	SubData fnam = subRecords.getSubData("FNAM");
	if ((int)fnam.data[0] == GLOBType.Short.value) {
	    return GLOBType.Short;
	} else if ((int)fnam.data[0] == GLOBType.Long.value) {
	    return GLOBType.Long;
	}
	return GLOBType.Float;
    }

    /**
     *
     * @param type
     */
    final public void setType (GLOBType type) {
	subRecords.getSubData("FNAM").data[0] = (byte) type.value;
    }

    @Override
    public ModListing getFormMaster() {
	return super.getFormMaster();
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	if (SPGlobal.logMods){
	    logMod(srcMod, "GLOB", "Constant: " + get(MajorFlags.RelatedToShields));
	}
    }

    /**
     *
     * @return
     */
    public float getValue () {
	return subRecords.getSubFloat("FLTV").get();
    }

    /**
     *
     * @param value
     */
    final public void setValue (Float value) {
	subRecords.setSubFloat("FLTV", value);
    }

    /**
     *
     * @param value
     */
    final public void setValue (Boolean value) {
	setValue((float)Integer.valueOf(Ln.convertBoolTo1(value)));
    }

    /**
     *
     * @return
     */
    public boolean isConstant () {
	return get(MajorFlags.RelatedToShields);
    }

    /**
     *
     * @param on
     */
    final public void setConstant (boolean on) {
	set(MajorFlags.RelatedToShields, on);
    }
}
