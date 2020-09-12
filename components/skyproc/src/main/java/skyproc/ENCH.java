/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.genenums.DeliveryType;
import skyproc.genenums.CastType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class ENCH extends MagicItem {

    // Static prototypes and definitions
    static final SubPrototype ENCHproto = new SubPrototype(MagicItem.magicItemProto) {
	@Override
	protected void addRecords() {
	    reposition("OBND");
	    reposition("FULL");
	    remove("DESC");
	    add(new ENIT());
	    reposition("EFID");
	    reposition("KWDA");
	}
    };
    static final class ENIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);
	CastType castType = CastType.ConstantEffect;
	int chargeAmount = 0;
	DeliveryType targetType = DeliveryType.Self;
	EnchantType enchantType = EnchantType.Enchantment;
	float chargeTime = 0;
	FormID baseEnchantment = new FormID();
	FormID wornRestrictions = new FormID();
	boolean old = false;

	ENIT() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(baseCost, 4);
	    out.write(flags.export());
	    out.write(castType.ordinal(), 4);
	    out.write(chargeAmount, 4);
	    out.write(targetType.ordinal(), 4);
	    out.write(enchantType.value, 4);
	    out.write(chargeTime);
	    baseEnchantment.export(out);
	    if (!old) {
		wornRestrictions.export(out);
	    }
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    baseCost = in.extractInt(4);
	    flags.set(in.extract(4));
	    castType = CastType.values()[in.extractInt(4)];
	    chargeAmount = in.extractInt(4);
	    targetType = DeliveryType.values()[in.extractInt(4)];
	    enchantType = EnchantType.value(in.extractInt(4));
	    chargeTime = in.extractFloat();
	    baseEnchantment.parseData(in, srcMod);
	    if (!in.isDone()) {
		wornRestrictions.parseData(in, srcMod);
	    } else {
		old = true;
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (old) {
		return 32;
	    } else {
		return 36;
	    }
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(2);
	    out.add(baseEnchantment);
	    out.add(wornRestrictions);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("ENIT");
	}
    }

    // Enums
    /**
     *
     */
    public enum EnchantType {

	/**
	 *
	 */
	Enchantment(6),
	/**
	 *
	 */
	StaffEnchantment(12);
	int value;

	EnchantType(int value) {
	    this.value = value;
	}

	static EnchantType value(int in) {
	    switch (in) {
		case 12:
		    return StaffEnchantment;
		default:
		    return Enchantment;
	    }
	}
    }

    /**
     *
     */
    public enum ENCHFlag {

	/**
	 *
	 */
	ManualCalc(0),
	/**
	 *
	 */
	ExtendDurationOnRecast(3);
	int value;

	ENCHFlag(int in) {
	    value = in;
	}
    }

    // Common Functions
    ENCH() {
	super();
	subRecords.setPrototype(ENCHproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("ENCH");
    }

    @Override
    Record getNew() {
	return new ENCH();
    }

    // Get/Set
    ENIT getENIT() {
	return (ENIT) subRecords.get("ENIT");
    }

    /**
     *
     * @param cost
     */
    public void setBaseCost(int cost) {
	getENIT().baseCost = cost;
    }

    /**
     *
     * @return
     */
    public int getBaseCost() {
	return getENIT().baseCost;
    }

    /**
     *
     * @param in
     * @param on
     */
    public void set(ENCHFlag in, boolean on) {
	getENIT().flags.set(in.value, on);
    }

    /**
     *
     * @param in
     * @return
     */
    public boolean get(ENCHFlag in) {
	return getENIT().flags.get(in.value);
    }

    /**
     *
     * @param type
     */
    public void setCastType(CastType type) {
	getENIT().castType = type;
    }

    /**
     *
     * @return
     */
    public CastType getCastType() {
	return getENIT().castType;
    }

    /**
     *
     * @param amount
     */
    public void setChargeAmount(int amount) {
	getENIT().chargeAmount = amount;
    }

    /**
     *
     * @return
     */
    public int getChargeAmount() {
	return getENIT().chargeAmount;
    }

    /**
     *
     * @param type
     */
    public void setDeliveryType(DeliveryType type) {
	getENIT().targetType = type;
    }

    /**
     *
     * @return
     */
    public DeliveryType getDeliveryType() {
	return getENIT().targetType;
    }

    /**
     *
     * @param type
     */
    public void setEnchantType(EnchantType type) {
	getENIT().enchantType = type;
    }

    /**
     *
     * @return
     */
    public EnchantType getEnchantType() {
	return getENIT().enchantType;
    }

    /**
     *
     * @param time
     */
    public void setChargeTime(float time) {
	getENIT().chargeTime = time;
    }

    /**
     *
     * @return
     */
    public float getChargeTime() {
	return getENIT().chargeTime;
    }

    /**
     *
     * @param id
     */
    public void setBaseEnchantment(FormID id) {
	getENIT().baseEnchantment = id;
    }

    /**
     *
     * @return
     */
    public FormID getBaseEnchantment() {
	return getENIT().baseEnchantment;
    }

    /**
     *
     * @param id
     */
    public void setWornRestrictions(FormID id) {
	getENIT().wornRestrictions = id;
    }

    /**
     *
     * @return
     */
    public FormID getWornRestrictions() {
	return getENIT().wornRestrictions;
    }
}
