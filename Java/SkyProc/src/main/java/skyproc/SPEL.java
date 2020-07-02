/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.genenums.DeliveryType;
import skyproc.genenums.CastType;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Spell Records
 * @author Plutoman101
 */
public class SPEL extends MagicItem {

    // Static prototypes and definitions
    static final SubPrototype SPELproto = new SubPrototype(MagicItem.magicItemProto){

	@Override
	protected void addRecords() {
	    add(new SubForm("MDOB"));
	    add(new SubForm("ETYP"));
	    reposition("DESC");
	    add(new SPIT());
	    reposition("EFID");
	}
    };

    // Enums
    /**
     *
     */
    public enum SPELType {

	/**
	 *
	 */
	Spell(0),
	/**
	 *
	 */
	Disease(1),
	/**
	 *
	 */
	Power(2),
	/**
	 *
	 */
	LesserPower(3),
	/**
	 *
	 */
	Ability(4),
	/**
	 *
	 */
	Addition(10),
	/**
	 *
	 */
	Voice(11),
	/**
	 *
	 */
	UNKNOWN(-1);
	int value;

	SPELType(int valuein) {
	    value = valuein;
	}

	static SPELType value(int value) {
	    for (SPELType s : SPELType.values()) {
		if (s.value == value) {
		    return s;
		}
	    }
	    return UNKNOWN;
	}
    }

    // Common Functions
    SPEL() {
	super();
	subRecords.setPrototype(SPELproto);
    }

    /**
     * Creates a new empty SPEL record that originates from the mod designated.<br>
     * Make sure the EDID you assign is unique.
     * @param edid
     */
    public SPEL(String edid) {
	this();
	originateFromPatch(edid);
	SubForm ETYP = subRecords.getSubForm("ETYP");
	FormID id = new FormID("013F44Skyrim.esm");
	ETYP.setForm(id);
	getSPIT().valid = true;
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("SPEL");
    }

    @Override
    Record getNew() {
	return new SPEL();
    }

    // Get Set functions
    /**
     *
     * @param invModel
     */
    public void setInventoryModel(FormID invModel) {
	subRecords.setSubForm("MODB", invModel);
    }

    /**
     *
     * @return
     */
    public FormID getInventoryModel() {
	return subRecords.getSubForm("MODB").getForm();
    }

    /**
     *
     * @param equipType
     */
    public void setEquipSlot(FormID equipType) {
	subRecords.setSubForm("ETYP", equipType);
    }

    /**
     *
     * @return
     */
    public FormID getEquipSlot() {
	return subRecords.getSubForm("ETYP").getForm();
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
    public void setSpellType(SPELType type) {
	getSPIT().baseType = type.value;
    }

    /**
     *
     * @return
     */
    public SPELType getSpellType() {
	return SPELType.value(getSPIT().baseType);
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

}
