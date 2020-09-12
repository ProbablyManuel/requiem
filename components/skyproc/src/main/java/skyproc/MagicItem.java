/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LFlags;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;

/**
 *
 * @author Justin Swanson
 */
public abstract class MagicItem extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype magicItemProto = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    reposition("DESC");
	    add(new SubList<>(new MagicEffectRef()));
	    add(new KeywordSet());
	}
    };
    static class SPIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);
	int baseType = 0;
	float chargeTime = 0;
	CastType castType = CastType.ConstantEffect;
	DeliveryType targetType = DeliveryType.Self;
	float castDuration = 0;
	float range = 0;
	boolean valid = true;
	FormID perkType = new FormID();

	SPIT() {
	    super();
	    valid = false;
	}

	@Override
	SubRecord getNew(String type) {
	    return new SPIT();
	}

	@Override
	final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);

	    baseCost = in.extractInt(4);
	    flags = new LFlags(in.extract(4));
	    baseType = in.extractInt(4);
	    chargeTime = in.extractFloat();
	    castType = CastType.values()[in.extractInt(4)];
	    targetType = DeliveryType.values()[in.extractInt(4)];
	    castDuration = in.extractFloat();
	    range = in.extractFloat();
	    perkType.parseData(in, srcMod);

	    if (SPGlobal.logMods){
		logMod(srcMod, "", "SPIT record: ");
		logMod(srcMod, "", "  " + "Base Spell Cost: " + baseCost + ", flags: " + flags
			+ ", Base Type: " + baseType + ", Spell Charge Time: " + chargeTime);
		logMod(srcMod, "", "  " + "cast type: " + castType + ", targetType: " + targetType
			+ ", Cast Duration: " + castDuration
			+ ", Spell Range: " + range + ", Perk for Spell: " + perkType);
	    }

	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    if (isValid()) {
		out.write(baseCost);
		out.write(flags.export(), 4);
		out.write(baseType);
		out.write(chargeTime);
		out.write(castType.ordinal());
		out.write(targetType.ordinal());
		out.write(castDuration);
		out.write(range);
		perkType.export(out);
	    }
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (isValid()) {
		return 36;
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(perkType);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("SPIT");
	}
    }

    // Common Functions
    MagicItem() {
	super();
    }
    
    /**
     *
     */
    public enum SPELFlag {

	/**
	 *
	 */
	ManualCostCalculation(0),
	/**
	 *
	 */
	PCStartSpell(17),
	/**
	 *
	 */
	AreaEffectIgnoresLOS(19),
	/**
	 *
	 */
	IgnoreResistance(20),
	/**
	 *
	 */
	NoAbsorbOrReflect(21),
	/**
	 *
	 */
	NoDualCastModification(23);
	int value;

	SPELFlag(int valuein) {
	    value = valuein;
	}
    }
    
    // Get/Set
    /**
     * 
     * @return
     */
    public ArrayList<MagicEffectRef> getMagicEffects() {
	return subRecords.getSubList("EFID").toPublic();
    }

    /**
     * 
     * @param magicEffect
     */
    public void removeMagicEffect(MagicEffectRef magicEffect) {
	subRecords.getSubList("EFID").remove(magicEffect);
    }

    /**
     * 
     * @param magicEffect
     */
    public void addMagicEffect(MagicEffectRef magicEffect) {
	subRecords.getSubList("EFID").add(magicEffect);
    }

    /**
     * 
     * @param magicEffect
     */
    public void addMagicEffect(MGEF magicEffect) {
	subRecords.getSubList("EFID").add(new MagicEffectRef(magicEffect.getForm()));
    }

    /**
     * 
     */
    public void clearMagicEffects() {
	subRecords.getSubList("EFID").clear();
    }
}
