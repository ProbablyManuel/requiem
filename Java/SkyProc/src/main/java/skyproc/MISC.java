/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import skyproc.AltTextures.AltTexture;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.interfaces.KeywordRecord;

/**
 *
 * @author Justin Swanson
 */
public class MISC extends MajorRecordNamed implements KeywordRecord {

    // Static prototypes and definitions
    static final SubPrototype MISCproto = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(new Model());
	    add(SubString.getNew("ICON", true));
	    add(new SubForm("YNAM"));
	    add(new SubForm("ZNAM"));
	    add(new KeywordSet());
	    add(new DATA());
	    add(SubString.getNew("MICO", true));
	    add(new DestructionData());
	}
    };
    static class DATA extends SubRecord {

	int value = 0;
	float weight = 0;

	DATA() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(value);
	    out.write(weight);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	    if (SPGlobal.logMods){
		logMod(srcMod, "", "Setting DATA:    Weight: " + weight);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new MISC.DATA();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 8;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    // Common Functions
    MISC() {
	super();
	subRecords.setPrototype(MISCproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("MISC");
    }

    @Override
    Record getNew() {
	return new MISC();
    }

    /**
     * @deprecated use getModelData()
     * @param path
     */
    public void setModel(String path) {
	subRecords.getModel().setFileName(path);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModel() {
	return subRecords.getModel().getFileName();
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getDATA().value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getDATA().value;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	getDATA().weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return getDATA().weight;
    }

    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     * @deprecated use getModelData()
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return subRecords.getModel().getAltTextures();
    }

    /**
     * 
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }

    /**
     *
     * @param sound
     */
    public void setPickupSound(FormID sound) {
	subRecords.setSubForm("YNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return subRecords.getSubForm("YNAM").getForm();
    }

    /**
     *
     * @param sound
     */
    public void setDropSound(FormID sound) {
	subRecords.setSubForm("ZNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm("ZNAM").getForm();
    }

    /**
     *
     * @param path
     */
    public void setInventoryImage(String path) {
	subRecords.setSubString("ICON", path);
    }

    /**
     *
     * @return
     */
    public String getInventoryImage() {
	return subRecords.getSubString("ICON").print();
    }

    /**
     *
     * @param path
     */
    public void setMessageImage(String path) {
	subRecords.setSubString("MICO", path);
    }

    /**
     *
     * @return
     */
    public String getMessageImage() {
	return subRecords.getSubString("MICO").print();
    }
}
