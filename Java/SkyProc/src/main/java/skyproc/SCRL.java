/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;

/**
 *
 * @author Justin Swanson
 */
public class SCRL extends MagicItem {

    // Static prototypes and definitions
    static final SubPrototype SCRLproto = new SubPrototype(MagicItem.magicItemProto){

	@Override
	protected void addRecords() {
            add(new SubForm("MDOB"));
            add(new SubForm("ETYP"));
            reposition("DESC");
            add(new Model());
	    add(new DATA());
            forceExport.add("DATA");
	    add(new SPIT());
            reposition("EFID");
            add(SubString.getNew("ICON", true));
            add(SubString.getNew("MICO", true));
	}
    };
    
    // Common Functions
    SCRL() {
	super();
	subRecords.setPrototype(SCRLproto);
    }
    
    public SCRL(String edid){
        super();
        subRecords.setPrototype(SCRLproto);
        originateFromPatch(edid);
    }
    
    @Override
    ArrayList<String> getTypes() {
        return Record.getTypeList("SCRL");
    }
    
    @Override
    Record getNew() {
	return new SCRL();
    }

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
	    return new INGR.DATA();
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
    
    // Get Set
    public void setMenuDisplayObject(FormID obj) {
	subRecords.setSubForm("MDOB", obj);
    }
    
    public FormID getMenuDisplayObject() {
	return subRecords.getSubForm("MDOB").getForm();
    }
    
    public void setEquipmentType(FormID obj) {
	subRecords.setSubForm("ETYP", obj);
    }
    
    public FormID getEquipmentType() {
	return subRecords.getSubForm("ETYP").getForm();
    }
    
    public Model getModelData() {
	return subRecords.getModel();
    }
    
    public void setValue(int val)
    {
        DATA data = (DATA) subRecords.get("DATA");
        data.value = val;
    }
    
    public int getValue()
    {
        DATA data = (DATA) subRecords.get("DATA");
        return data.value;
    }
    
    public void setWeight(float weight)
    {
        DATA data = (DATA) subRecords.get("DATA");
        data.weight = weight;
    }
    
    public float getWeight()
    {
        DATA data = (DATA) subRecords.get("DATA");
        return data.weight;
    }
    
    final SPIT getSPIT() {
	return (SPIT) subRecords.get("SPIT");
    }

    /**
     *
     * @param baseCost
     */
    public void setBaseCost(int baseCost) {
	getSPIT().baseCost = baseCost;
    }

    /**
     *
     * @return
     */
    public int getBaseCost() {
	return getSPIT().baseCost;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(SPELFlag flag, boolean on) {
	getSPIT().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(SPELFlag flag) {
	return getSPIT().flags.get(flag.value);
    }

    /**
     *
     * @param type
     */
    public void setSpellType(SPEL.SPELType type) {
	getSPIT().baseType = type.value;
    }

    /**
     *
     * @return
     */
    public SPEL.SPELType getSpellType() {
	return SPEL.SPELType.value(getSPIT().baseType);
    }

    /**
     *
     * @param chargeTime
     */
    public void setChargeTime (float chargeTime) {
	getSPIT().chargeTime = chargeTime;
    }

    /**
     *
     * @return
     */
    public float getChargeTime () {
	return getSPIT().chargeTime;
    }

    /**
     *
     * @param type
     */
    public void setCastType (CastType type) {
	getSPIT().castType = type;
    }

    /**
     *
     * @return
     */
    public CastType getCastType () {
	return getSPIT().castType;
    }

    /**
     *
     * @param type
     */
    public void setDeliveryType (DeliveryType type) {
	getSPIT().targetType = type;
    }

    /**
     *
     * @return
     */
    public DeliveryType getDeliveryType () {
	return getSPIT().targetType;
    }

    /**
     *
     * @param duration
     */
    public void setCastDuration (float duration) {
	getSPIT().castDuration = duration;
    }

    /**
     *
     * @return
     */
    public float getCastDuration () {
	return getSPIT().castDuration;
    }

    /**
     *
     * @param range
     */
    public void setRange (float range) {
	getSPIT().range = range;
    }

    /**
     *
     * @return
     */
    public float getRange () {
	return getSPIT().range;
    }

    /**
     *
     * @return The PERK ref associated with the SPEL.
     */
    public FormID getPerkRef() {
	return getSPIT().perkType;
    }

    /**
     *
     * @param perkRef FormID to set the SPELs PERK ref to.
     */
    public void setPerkRef(FormID perkRef) {
	getSPIT().perkType = perkRef;
    }

    /**
     *
     * @param filename
     */
    public void setInventoryIcon(String filename) {
	subRecords.setSubString("ICON", filename);
    }

    /**
     *
     * @return
     */
    public String getInventoryIcon() {
	return subRecords.getSubString("ICON").print();
    }

    /**
     *
     * @param filename
     */
    public void setMessageIcon(String filename) {
	subRecords.setSubString("MICO", filename);
    }

    /**
     *
     * @return
     */
    public String getMessageIcon() {
	return subRecords.getSubString("MICO").print();
    }
}
