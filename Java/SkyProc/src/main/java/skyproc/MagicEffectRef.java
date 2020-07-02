/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LOutFile;
import lev.LShrinkArray;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A Magic Effect reference object that is used to represent attached MGEF
 * records on spells.
 *
 * @author Justin Swanson
 */
public class MagicEffectRef extends SubShellBulkType {

    static SubPrototype magicEffProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubForm("EFID"));
	    add(new EFIT());
	    add(new SubList<>(new Condition()));
	}
    };

    /**
     * @param magicEffectRef A formID to a MGEF record.
     */
    public MagicEffectRef(FormID magicEffectRef) {
	this();
	subRecords.setSubForm("EFID", magicEffectRef);
    }

    MagicEffectRef() {
	super(magicEffProto, false);
    }

    @Override
    SubRecord getNew(String type) {
	return new MagicEffectRef();
    }

    @Override
    boolean isValid() {
	return subRecords.isAnyValid();
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final MagicEffectRef other = (MagicEffectRef) obj;
	if (!this.getMagicRef().equals(other.getMagicRef())) {
	    return false;
	}
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 7;
	hash = 71 * hash + getMagicRef().hashCode();
	return hash;
    }

    static class EFIT extends SubRecord {

	float magnitude = 0;
	int AOE = 0;
	int duration = 0;

	EFIT() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(magnitude);
	    out.write(AOE);
	    out.write(duration);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    magnitude = in.extractFloat();
	    AOE = in.extractInt(4);
	    duration = in.extractInt(4);
	}

	@Override
	SubRecord getNew(String type) {
	    return new EFIT();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 12;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("EFIT");
	}
    }

    // Get/Set
    /**
     *
     * @param magicRef
     */
    public void setMagicRef(FormID magicRef) {
	subRecords.setSubForm("EFID", magicRef);
    }

    /**
     *
     * @return
     */
    public FormID getMagicRef() {
	return subRecords.getSubForm("EFID").getForm();
    }

    EFIT getEFIT() {
	return (EFIT)subRecords.get("EFIT");
    }

    /**
     *
     * @param magnitude
     */
    public void setMagnitude(float magnitude) {
	getEFIT().magnitude = magnitude;
    }

    /**
     *
     * @return
     */
    public float getMagnitude() {
	return getEFIT().magnitude;
    }

    /**
     *
     * @param aoe
     */
    public void setAreaOfEffect(int aoe) {
	getEFIT().AOE = aoe;
    }

    /**
     *
     * @return
     */
    public int getAreaOfEffect() {
	return getEFIT().AOE;
    }

    /**
     *
     * @param duration
     */
    public void setDuration(int duration) {
	getEFIT().duration = duration;
    }

    /**
     *
     * @return
     */
    public int getDuration() {
	return getEFIT().duration;
    }

    /**
     *
     * @return
     */
    public ArrayList<Condition> getConditions() {
	return subRecords.getSubList("CTDA").toPublic();
    }

    /**
     * 
     * @param c
     */
    public void addCondition(Condition c) {
	subRecords.getSubList("CTDA").add(c);
    }

    /**
     * 
     * @param c
     */
    public void removeCondition(Condition c) {
	subRecords.getSubList("CTDA").remove(c);
    }
}