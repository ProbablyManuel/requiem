/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Plutoman101
 */
public class ECZN extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype ECZNproto = new SubPrototype(MajorRecord.majorProto) {
	@Override
	protected void addRecords() {
	    add(new DATA());
	}
    };
    static final class DATA extends SubRecord implements Serializable {

	private FormID owner = new FormID();
	private FormID location = new FormID();
	private int rank = 0;
	private int minLevel = 0;
	LFlags flags = new LFlags(1);
	private int maxLevel = 0;
	private boolean valid = true;

	DATA() {
	    super();
	    valid = false;
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);

	    owner.parseData(in, srcMod);
	    location.parseData(in, srcMod);
	    if (!in.isDone()) {
		rank = in.extractInt(1);
		minLevel = in.extractInt(1);
		flags.set(in.extract(1));
		maxLevel = in.extractInt(1);
	    }
	    if (SPGlobal.logMods){
		logMod(srcMod, "", "DATA record: ");
		logMod(srcMod, "", "  " + "Owner: " + owner.getFormStr() + ", Location: " + location.getFormStr());
		logMod(srcMod, "", "  " + "Required Rank: " + rank + ", Minimum Level: " + minLevel);
		logMod(srcMod, "", "  " + "Max Level: " + maxLevel + ", Flags: " + flags);
	    }

	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    if (isValid()) {
		owner.export(out);
		location.export(out);
		out.write(rank, 1);
		out.write(minLevel, 1);
		out.write(flags.export(), 1);
		out.write(maxLevel, 1);
	    }
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (isValid()) {
		return 12;
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(2);
	    out.add(owner);
	    out.add(location);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    // Enums
    /**
     *
     */
    public enum ECZNFlags {

	/**
	 *
	 */
	NeverResets(0),
	/**
	 *
	 */
	MatchPCBelowMin(1),
	/**
	 *
	 */
	DisableCombatBoundary(2),;
	int value;

	ECZNFlags(int value) {
	    this.value = value;
	}
    }

    // Common Functions
    /**
     * Creates a new ECZN record.
     */
    ECZN() {
	super();
	subRecords.setPrototype(ECZNproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("ECZN");
    }

    @Override
    Record getNew() {
	return new ECZN();
    }

    // Get/set
    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ECZNFlags flag) {
	return getDATA().flags.get(flag.value);
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ECZNFlags flag, boolean on) {
	getDATA().flags.set(flag.value, on);
    }

    /**
     *
     * @return
     */
    public FormID getLocation() {
	return getDATA().location;
    }

    /**
     *
     * @param location
     */
    public void setLocation(FormID location) {
	getDATA().location = location;
    }

    /**
     *
     * @return
     */
    public int getMaxLevel() {
	return getDATA().maxLevel;
    }

    /**
     *
     * @param maxLevel
     */
    public void setMaxLevel(int maxLevel) {
	getDATA().maxLevel = maxLevel;
    }

    /**
     *
     * @return
     */
    public int getMinLevel() {
	return getDATA().minLevel;
    }

    /**
     *
     * @param minLevel
     */
    public void setMinLevel(int minLevel) {
	getDATA().minLevel = minLevel;
    }

    /**
     *
     * @return
     */
    public FormID getOwner() {
	return getDATA().owner;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(FormID owner) {
	getDATA().owner = owner;
    }

    /**
     *
     * @return
     */
    public int getRank() {
	return getDATA().rank;
    }

    /**
     *
     * @param rank
     */
    public void setRank(int rank) {
	getDATA().rank = rank;
    }
}
