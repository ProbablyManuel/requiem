/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.genenums.FavorLevel;
import skyproc.genenums.EmotionType;
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
public class INFO extends MajorRecord {

    static final SubPrototype responseProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new TRDT());
	    add(new SubStringPointer("NAM1", SubStringPointer.Files.ILSTRINGS));
	    add(new SubString("NAM2"));
	    add(new SubData("NAM3"));
	    add(new SubForm("SNAM"));
//	    add(new SubList<>(new Condition()));
	    add(new SubForm("LNAM"));
	}
    };
    static final SubPrototype INFOprototype = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage(new ScriptFragments()));
	    add(new SubData("DATA"));
	    add(new ENAM());
	    add(new SubForm("TPIC"));
	    add(new SubForm("PNAM"));
	    add(new SubInt("CNAM", 1));
	    add(new SubList<>(new SubForm("TCLT")));
	    add(new SubForm("DNAM"));
            
            add(new SubList<>(new SubShellBulkType(responseProto, false)));
            
	    add(new SubList<>(new Condition()));
	    add(new SubList<>(new SubShell(new SubPrototype() {

		@Override
		protected void addRecords() {
		    add(new SubData("SCHR"));
		    add(new SubForm("QNAM"));
		    add(new SubData("NEXT"));
		}
	    })));
	    
	    add(new SubStringPointer("RNAM", SubStringPointer.Files.STRINGS));
	    add(new SubForm("ANAM"));
	    add(new SubForm("TWAT"));
	    add(new SubForm("ONAM"));
	}
    };

    static class TRDT extends SubRecordTyped {

	EmotionType emotion = EmotionType.Neutral;
	int emotionValue = 0;
	byte[] fluff = new byte[16];

	public TRDT() {
	    super("TRDT");
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(emotion.ordinal());
	    out.write(emotionValue);
	    out.write(fluff);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    emotion = EmotionType.values()[in.extractInt(4)];
	    emotionValue = in.extractInt(4);
	    fluff = in.extract(16);
	}

	@Override
	SubRecord getNew(String type) {
	    return new TRDT();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 24;
	}
    }

    static class ENAM extends SubRecordTyped {

	LFlags flags;
	int hoursReset = 0;

	public ENAM() {
	    super("ENAM");
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(flags.export(), 2);
	    out.write(hoursReset, 2);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    flags = new LFlags(2);
	    flags.set(in.extract(2));
	    hoursReset = in.extractInt(2);
	}

	@Override
	boolean isValid() {
	    return flags != null;
	}

	@Override
	SubRecord getNew(String type) {
	    return new ENAM();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 4;
	}
    }

    static class ScriptFragments extends SubRecord {

	byte unknown = 0;
	LFlags fragmentFlags = new LFlags(1);
	StringNonNull fragmentFile = new StringNonNull();
	ArrayList<ScriptFragment> fragments = new ArrayList<>();
	boolean valid = false;

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    fragmentFlags.set(in.extract(1));
	    fragmentFile.set(in.extractString(in.extractInt(2)));
	    while (!in.isDone()) {
		ScriptFragment frag = new ScriptFragment();
		frag.parseData(in, srcMod);
		fragments.add(frag);
	    }
	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    if (!valid) {
		return;
	    }
	    out.write(unknown, 1);
	    out.write(fragmentFlags.export());
	    fragmentFile.export(out);
	    for (ScriptFragment frag : fragments) {
		frag.export(out);
	    }
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (!valid) {
		return 0;
	    }
	    int len = 2;
	    len += fragmentFile.getTotalLength(out);
	    for (ScriptFragment frag : fragments) {
		len += frag.getContentLength(out);
	    }
	    return len;
	}

	@Override
	SubRecord getNew(String type) {
	    return new ScriptFragments();
	}

	@Override
	ArrayList<String> getTypes() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
    }

    static class ScriptFragment {

	byte unknown = 0;
	StringNonNull scriptName = new StringNonNull();
	StringNonNull fragmentName = new StringNonNull();

	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    scriptName.set(in.extractString(in.extractInt(2)));
	    fragmentName.set(in.extractString(in.extractInt(2)));
	}

	void export(ModExporter out) throws IOException {
	    out.write(unknown, 1);
	    scriptName.export(out);
	    fragmentName.export(out);
	}

	int getContentLength(ModExporter out) {
	    return 1 + scriptName.getTotalLength(out)
		    + fragmentName.getTotalLength(out);
	}
    }

    /**
     *
     */
    public class Response extends SubShell {

	Response() {
	    super(responseProto);
	}

	TRDT getTRDT() {
	    return (TRDT) subRecords.get("TRDT");
	}

	/**
	 *
	 * @param type
	 */
	public void setEmotionType(EmotionType type) {
	    getTRDT().emotion = type;
	}

	/**
	 *
	 * @return
	 */
	public EmotionType getEmotionType() {
	    return getTRDT().emotion;
	}

	/**
	 *
	 * @param val
	 */
	public void setEmotionValue(int val) {
	    val %= 100;
	    getTRDT().emotionValue = val;
	}

	/**
	 *
	 * @return
	 */
	public int getEmotionValue() {
	    return getTRDT().emotionValue;
	}

	/**
	 *
	 * @param text
	 */
	public void setResponseText(String text) {
	    subRecords.setSubStringPointer("NAM1", text);
	}

	/**
	 *
	 * @return
	 */
	public String getResponseText() {
	    return subRecords.getSubStringPointer("NAM1").print();
	}

	/**
	 *
	 * @param text
	 */
	public void setActorNotes(String text) {
	    subRecords.setSubStringPointer("NAM2", text);
	}

	/**
	 *
	 * @return
	 */
	public String getActorNotes() {
	    return subRecords.getSubStringPointer("NAM2").print();
	}

	/**
	 *
	 * @param id
	 */
	public void setIdleAnimSpeaker(FormID id) {
	    subRecords.setSubForm("SNAM", id);
	}

	/**
	 *
	 * @return
	 */
	public FormID getIdleAnimSpeaker() {
	    return subRecords.getSubForm("SNAM").getForm();
	}

	/**
	 *
	 * @param id
	 */
	public void setIdleAnimListener(FormID id) {
	    subRecords.setSubForm("LNAM", id);
	}

	/**
	 *
	 * @return
	 */
	public FormID getIdleAnimListener() {
	    return subRecords.getSubForm("LNAM").getForm();
	}

	/**
	 *
	 * @return
	 */
//	public ArrayList<Condition> getConditions() {
//	    return subRecords.getSubList("CTDA").toPublic();
//	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
//	public void addCondition(Condition c) {
//	    subRecords.getSubList("CTDA").add(c);
//	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
//	public void removeCondition(Condition c) {
//            throw
//	    subRecords.getSubList("CTDA").remove(c);
//	}
    }

    // Enums
    /**
     *
     */
    public enum ResponseFlag {

	/**
	 *
	 */
	Goodbye(0),
	/**
	 *
	 */
	Random(1),
	/**
	 *
	 */
	SayOnce(2),
	/**
	 *
	 */
	RandomEnd(5),
	/**
	 *
	 */
	InvisibleContinue(6),
	/**
	 *
	 */
	WalkAway(7),
	/**
	 *
	 */
	WalkAwayInvisbleInMenu(8),
	/**
	 *
	 */
	ForceSubtitle(9),
	/**
	 *
	 */
	CanMoveWhileGreeting(10),
	/**
	 *
	 */
	HasNoLipFile(11),
	/**
	 *
	 */
	RequiresPostProcessing(12),
	/**
	 *
	 */
	AudioOutputOverride(13),
	/**
	 *
	 */
	SpendsFavorPoints(14),;
	int value;

	ResponseFlag(int val) {
	    value = val;
	}
    }

    // Common Functions
    INFO() {
	super();
	subRecords.setPrototype(INFOprototype);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("INFO");
    }

    @Override
    Record getNew() {
	return new INFO();
    }

    // Get/set
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    ENAM getENAM() {
	return (ENAM) subRecords.get("ENAM");
    }

    /**
     *
     * @param res
     * @param on
     */
    public void set(ResponseFlag res, boolean on) {
	getENAM().flags.set(res.value, on);
    }

    /**
     *
     * @param res
     * @return
     */
    public boolean get(ResponseFlag res) {
	return getENAM().flags.get(res.value);
    }

    /**
     *
     * @param hours
     */
    public void setResetTime(float hours) {
	hours /= 24;
	getENAM().hoursReset = (int) (hours * 65535);
    }

    /**
     *
     * @return
     */
    public float getResetTime() {
	float tmp = ((float) getENAM().hoursReset) / 65535;
	return tmp * 24;
    }

    /**
     *
     * @param id
     */
    public void setTopic(FormID id) {
	subRecords.setSubForm("TPIC", id);
    }

    /**
     *
     * @return
     */
    public FormID getTopic() {
	return subRecords.getSubForm("TPIC").getForm();
    }

    /**
     *
     * @param id
     */
    public void setPreviousINFO(FormID id) {
	subRecords.setSubForm("PNAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getPreviousINFO() {
	return subRecords.getSubForm("PNAM").getForm();
    }

    /**
     *
     * @param lev
     */
    public void setFavorLevel(FavorLevel lev) {
	subRecords.setSubInt("CNAM", lev.ordinal());
    }

    /**
     *
     * @return
     */
    public FavorLevel getFavorLevel() {
	return FavorLevel.values()[subRecords.getSubInt("CNAM").get()];
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getLinkTo() {
	return subRecords.getSubList("TCLT").toPublic();
    }

    /**
     * 
     * @param id
     */
    public void removeLinkTo(FormID id) {
	subRecords.getSubList("TCLT").remove(id);
    }

    /**
     * 
     * @param id
     */
    public void addLinkTo(FormID id) {
	subRecords.getSubList("TCLT").add(id);
    }

    /**
     * 
     */
    public void clearLinkTo() {
	subRecords.getSubList("TCLT").clear();
    }

    /**
     *
     * @param id
     */
    public void setResponseData(FormID id) {
	subRecords.setSubForm("DNAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getResponseData() {
	return subRecords.getSubForm("DNAM").getForm();
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

    /**
     *
     * @return
     */
    public ArrayList<Response> getResponses() {
	return subRecords.getSubList("TRDT").toPublic();
    }

    /**
     *
     * @param text
     */
    public void setPrompt(String text) {
	subRecords.setSubStringPointer("RNAM", text);
    }

    /**
     *
     * @return
     */
    public String getPrompt() {
	return subRecords.getSubStringPointer("RNAM").print();
    }

    /**
     *
     * @param id
     */
    public void setSpeaker(FormID id) {
	subRecords.setSubForm("ANAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getSpeaker() {
	return subRecords.getSubForm("ANAM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setWalkAwayTopic(FormID id) {
	subRecords.setSubForm("TWAT", id);
    }

    /**
     *
     * @return
     */
    public FormID getWalkAwayTopic() {
	return subRecords.getSubForm("TWAT").getForm();
    }

    /**
     *
     * @param id
     */
    public void setAudioOverride(FormID id) {
	subRecords.setSubForm("ONAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getAudioOverride() {
	return subRecords.getSubForm("ONAM").getForm();
    }
}
