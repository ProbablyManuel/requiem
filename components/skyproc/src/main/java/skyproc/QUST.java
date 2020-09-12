/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LByteChannel;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class QUST extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype ALSTALLSproto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(SubString.getNew("ALID", true));
	    add(new SubFlag("FNAM", 4));
            add(new SubInt("ALFI"));
            // start fill types. Going by xEdit
            add(new SubForm("ALFL"));
	    add(new SubForm("ALFR"));
            add(new SubForm("ALUA"));
            
            // Location Alias Reference
            add(new SubInt("ALFA"));
            add(new SubForm("KNAM"));
            add(new SubForm("ALRT"));
            
            // External Alias Reference
            add(new SubForm("ALEQ"));
            add(new SubInt("ALEA"));
            
            // Create Reference to Object
            add(new SubForm("ALCO"));
            add(new SubData("ALCA"));
            add(new SubInt("ALCL"));
            
            // Find Matching Reference Near Alias
            add(new SubInt("ALNA"));
	    add(new SubInt("ALNT"));
            
            // Find Matching Reference From Event
            add(SubString.getNew("ALFE", false));
            add(new SubInt("ALFD"));
            
            // end fill types
	    add(new SubList<>(new Condition()));
            add(new KeywordSet());
	    add(new SubListCounted<>("COCT", 4, new SubFormInt("CNTO")));
            add(new SubForm("SPOR"));
            add(new SubForm("OCOR"));
            add(new SubForm("GWOR"));
	    add(new SubForm("ECOR"));
	    add(new SubForm("ALDN"));
            add(new SubList<>(new SubForm("ALSP")));
	    add(new SubList<>(new SubForm("ALFC")));
	    add(new SubList<>(new SubForm("ALPC")));
	    add(new SubForm("VTCK"));
            add(new SubData("ALED"));
	}
    };
    static final SubPrototype aliasLocationProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new SubInt("ALLS"));
	    mergeIn(ALSTALLSproto);
	}
    };
    static final SubPrototype aliasReferenceProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new SubInt("ALST"));
	    mergeIn(ALSTALLSproto);
	    forceExport("VTCK");
	}
    };
    static final SubPrototype questLogEntryProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new SubFlag("QSDT", 1));
	    add(new SubForm("NAM0"));
	    add(new SubStringPointer("CNAM", SubStringPointer.Files.DLSTRINGS));
	    add(new SubData("SCHR"));
	    add(new SubForm("QNAM"));
	    add(SubString.getNew("SCTX", false));
	    add(new SubList<>(new Condition()));
	}
    };
    static final SubPrototype questStageProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new INDX());
	    add(new SubList<>(new QuestLogEntry()));
	}
    };
    static final SubPrototype questTargetProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new QuestTargetData());
	    add(new SubList<>(new Condition()));
	}
    };
    static final SubPrototype questObjectiveProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new SubInt("QOBJ", 2));
	    add(new SubData("FNAM"));
	    add(new SubStringPointer("NNAM", SubStringPointer.Files.STRINGS));
	    add(new SubList<>(new QuestTarget()));
	}
    };
    static final SubPrototype QUSTproto = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(new QUSTScriptFragments()), "EDID");
	    reposition("FULL");
	    add(new DNAM());
	    add(SubString.getNew("ENAM", false));
	    add(new SubList<>(new SubForm("QTGL")));
	    add(SubString.getNew("FLTR", true));
	    add(new SubList<>(new Condition()));
	    add(new SubShellBulkType(new SubPrototype() {

		@Override
		protected void addRecords() {
		    add(new SubData("NEXT"));
		    forceExport("NEXT");
		    add(new SubList<>(new Condition()));
		}
	    }, false));
	    add(new SubList<>(new QuestStage()));
	    add(new SubList<>(new QuestObjective()));
	    add(new SubInt("ANAM"));
	    add(new SubListMulti<Alias>(new AliasLocation(), new AliasReference()));
	}
    };

    /**
     *
     */
    public abstract static class Alias extends SubShellBulkType {

	Alias(SubPrototype proto) {
	    super(proto, false);
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name) {
	    subRecords.setSubStringPointer("ALID", name);
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
	    return subRecords.getSubString("ALID").print();
	}

	/**
	 *
	 * @param val
	 */
	public abstract void setAliasID(int val);

	/**
	 *
	 * @return
	 */
	public abstract int getAliasID();

	/**
	 *
	 * @param name
	 */
	public void setAliasName(String name) {
	    subRecords.setSubString("ALID", name);
	}

	/**
	 *
	 * @return
	 */
	public String getAliasName() {
	    return subRecords.getSubString("ALID").print();
	}

	/**
	 *
	 * @param id
	 */
	public void setUniqueActor(FormID id) {
	}

	/**
	 *
	 * @return
	 */
	public FormID getUniqueActor() {
	    return FormID.NULL;
	}
    }

    /**
     *
     */
    public static class AliasLocation extends Alias {

	/**
	 *
	 * @param val
	 */
	public AliasLocation(int val) {
	    this();
	    setAliasID(val);
	}

	AliasLocation() {
	    super(aliasLocationProto);
	}

	/**
	 *
	 * @param name
	 */
	public AliasLocation(String name) {
	    this();
	    setName(name);
	}

	/**
	 *
	 * @param val
	 */
	@Override
	public void setAliasID(int val) {
	    subRecords.setSubInt("ALLS", val);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getAliasID() {
	    return subRecords.getSubInt("ALLS").get();
	}
    }

    /**
     *
     */
    public static class AliasReference extends Alias {

	/**
	 *
	 * @param val
	 */
	public AliasReference(int val) {
	    this();
	    setAliasID(val);
	}

	AliasReference() {
	    super(aliasReferenceProto);
	}

	/**
	 *
	 * @param name
	 */
	public AliasReference(String name) {
	    this();
	    setName(name);
	}

	/**
	 *
	 * @param val
	 */
	@Override
	public void setAliasID(int val) {
	    subRecords.setSubInt("ALST", val);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getAliasID() {
	    return subRecords.getSubInt("ALST").get();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public FormID getUniqueActor() {
	    return subRecords.getSubForm("ALUA").getForm();
	}

	/**
	 *
	 * @param id
	 */
	@Override
	public void setUniqueActor(FormID id) {
	    subRecords.setSubForm("ALUA", id);
	}
    }

    static class DNAM extends SubRecord {

	LFlags flags = new LFlags(2);
	byte priority = 0;
	byte unknown = 0;
	int unknown2 = 0;
	QuestType questType = QuestType.None;

	DNAM() {
	    super();
	}

	@Override
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 12;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(flags.export());
	    out.write(priority, 1);
	    out.write(unknown, 1);
	    out.write(unknown2);
	    out.write(questType.ordinal());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    flags.set(in.extract(2));
	    priority = in.extract(1)[0];
	    unknown = in.extract(1)[0];
	    unknown2 = in.extractInt(4);
	    questType = QuestType.values()[in.extractInt(4)];
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DNAM");
	}
    }

    static class INDX extends SubRecord {

	int index = 0;
	LFlags flags = new LFlags(2);

	INDX() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(index, 2);
	    out.write(flags.export());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    index = in.extractInt(2);
	    flags.set(in.extract(2));
	}

	@Override
	SubRecord getNew(String type) {
	    return new INDX();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 4;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("INDX");
	}
    }

    /**
     *
     */
    public static class QuestStage extends SubShellBulkType {

	/**
	 *
	 */
	public QuestStage() {
	    super(questStageProto, false);
	}

	INDX getINDX() {
	    return (INDX) subRecords.get("INDX");
	}

	/**
	 *
	 * @return
	 */
	public int getJournalIndex() {
	    return getINDX().index;
	}

	/**
	 *
	 * @param value
	 */
	public void setJournalIndex(int value) {
	    getINDX().index = value;
	}

	/**
	 *
	 * @param flag
	 * @return
	 */
	public boolean get(QuestStageFlags flag) {
	    return getINDX().flags.get(flag.ordinal() + 1);
	}

	/**
	 *
	 * @param flag
	 * @param on
	 */
	public void set(QuestStageFlags flag, boolean on) {
	    getINDX().flags.set(flag.ordinal() + 1, on);
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<QuestLogEntry> getLogEntries() {
	    return subRecords.getSubList("QSDT").toPublic();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param entry
	 */
	public void addLogEntry(QuestLogEntry entry) {
	    subRecords.getSubList("QSDT").add(entry);
	}
    }

    /**
     *
     */
    public static class QuestLogEntry extends SubShell {

	/**
	 *
	 */
	public QuestLogEntry() {
	    super(questLogEntryProto);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestLogEntry();
	}

	/**
	 *
	 * @param flag
	 * @param on
	 */
	public void set(QuestLogFlags flag, boolean on) {
	    subRecords.setSubFlag("QSDT", flag.ordinal(), on);
	}

	/**
	 *
	 * @param flag
	 * @return
	 */
	public boolean get(QuestLogFlags flag) {
	    return subRecords.getSubFlag("QSDT").is(flag.ordinal());
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Condition> getConditions() {
	    return subRecords.getSubList("CTDA").toPublic();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}

	/**
	 *
	 * @param text
	 */
	public void setJournalText(String text) {
	    subRecords.setSubStringPointer("CNAM", text);
	}

	/**
	 *
	 * @return
	 */
	public String getJournalText() {
	    return subRecords.getSubStringPointer("CNAM").print();
	}

	/**
	 *
	 * @param id
	 */
	public void setNextQuest(FormID id) {
	    subRecords.setSubForm("NAM0", id);
	}

	/**
	 *
	 * @return
	 */
	public FormID getNextQuest() {
	    return subRecords.getSubForm("NAM0").getForm();
	}
    }

    /**
     *
     */
    public static class QuestObjective extends SubShellBulkType {

	QuestObjective() {
	    super(questObjectiveProto, false);
	}

	/**
	 *
	 * @param index
	 * @param name
	 */
	public QuestObjective(int index, String name) {
	    this();
	    setIndex(index);
	    setName(name);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestObjective();
	}

	/**
	 *
	 * @param index
	 */
	public final void setIndex(int index) {
	    subRecords.setSubInt("QOBJ", index);
	}

	/**
	 *
	 * @return
	 */
	public int getIndex() {
	    return subRecords.getSubInt("QOBJ").get();
	}

	/**
	 *
	 * @param in
	 */
	public final void setName(String in) {
	    subRecords.setSubStringPointer("NNAM", in);
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
	    return subRecords.getSubStringPointer("NNAM").print();
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<QuestTarget> getTargets() {
	    return subRecords.getSubList("QSTA").toPublic();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 */
	public void clearTargets() {
	    subRecords.getSubList("QSTA").clear();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param target
	 */
	public void addTarget(QuestTarget target) {
	    subRecords.getSubList("QSTA").add(target);
	}
    }

    static class QuestTargetData extends SubRecord {

	int targetAlias = 0;
	LFlags flags = new LFlags(4);

	QuestTargetData() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(targetAlias);
	    out.write(flags.export());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    targetAlias = in.extractInt(4);
	    flags.set(in.extract(4));
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTargetData();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 8;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("QSTA");
	}
    }

    /**
     *
     */
    public static class QuestTarget extends SubShell {

	QuestTarget() {
	    super(questTargetProto);
	}

	/**
	 *
	 * @param aliasID
	 */
	public QuestTarget(int aliasID) {
	    this();
	    setTargetAlias(aliasID);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTarget();
	}

	QuestTargetData getData() {
	    return (QuestTargetData) subRecords.get("QSDT");
	}

	/**
	 *
	 * @param aliasID
	 */
	public final void setTargetAlias(int aliasID) {
	    getData().targetAlias = aliasID;

	}

	/**
	 *
	 * @return
	 */
	public int getTargetAlias() {
	    return getData().targetAlias;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Condition> getConditions() {
	    return subRecords.getSubList("CTDA").toPublic();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}

	/**
	 *
	 * @param on
	 */
	public void setCompassMarkersIgnoreLocks(boolean on) {
	    getData().flags.set(0, on);
	}

	/**
	 *
	 * @return
	 */
	public boolean getCompassMarkersIgnoreLocks() {
	    return getData().flags.get(0);
	}
    }

    static class QUSTScriptFragments extends SubRecord {

	byte unknown = 0;
	StringNonNull fragmentFile = new StringNonNull();
	ArrayList<QUSTScriptFragment> questFragments = new ArrayList<>();
        ArrayList<AliasScriptFragment> aliasFragments = new ArrayList<>();
	boolean valid = false;

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    int fragmentCount = in.extractInt(2);
	    fragmentFile.set(in.extractString(in.extractInt(2)));
	    for (int i = 0 ; i < fragmentCount ; i++) {
		QUSTScriptFragment frag = new QUSTScriptFragment();
		frag.parseData(in, srcMod);
		questFragments.add(frag);
	    }
            int aliasCount = in.extractInt(2);
            for (int i = 0 ; i < aliasCount ; i++) {
		AliasScriptFragment frag = new AliasScriptFragment();
		frag.parseData(in, srcMod);
		aliasFragments.add(frag);
	    }
	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    if (!valid) {
		return;
	    }
	    out.write(unknown, 1);
	    out.write(questFragments.size(), 2);
	    fragmentFile.export(out);
	    for (QUSTScriptFragment frag : questFragments) {
		frag.export(out);
	    }
            out.write(aliasFragments.size(), 2);
            for (AliasScriptFragment frag : aliasFragments) {
		frag.export(out);
	    }
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (!valid) {
		return 0;
	    }
	    int len = 3; // unknown + fragment count
	    len += fragmentFile.getTotalLength(out);
	    for (QUSTScriptFragment frag : questFragments) {
		len += frag.getContentLength(out);
	    }
            len += 2; // alias count
            for (AliasScriptFragment frag : aliasFragments) {
		len += frag.getContentLength(out);
	    }
	    return len;
	}

	@Override
	SubRecord getNew(String type) {
	    return new QUSTScriptFragments();
	}

	@Override
	ArrayList<String> getTypes() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
    }

    static class QUSTScriptFragment {

        int questStage;
        int unknown1;
        int questStageIndex;
        int unknown2;
	StringNonNull scriptName = new StringNonNull();
	StringNonNull fragmentName = new StringNonNull();

	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    questStage = in.extractInt(2);
            unknown1 = in.extractInt(2);
            questStageIndex = in.extractInt(4);
            unknown2 = in.extractInt(1);
	    scriptName.set(in.extractString(in.extractInt(2)));
	    fragmentName.set(in.extractString(in.extractInt(2)));
	}

	void export(ModExporter out) throws IOException {
	    out.write(questStage, 2);
            out.write(unknown1, 2);
            out.write(questStageIndex);
            out.write(unknown2, 1);
	    scriptName.export(out);
	    fragmentName.export(out);
	}

	int getContentLength(ModExporter out) {
	    return 9 + scriptName.getTotalLength(out)
		    + fragmentName.getTotalLength(out);
	}
    }
    
    static class AliasScriptFragment {

	byte[] object = new byte[8]; // VMAD property object. Need adjusting for formID etc
        FormID object_FormID = FormID.NULL;
        int object_alias = -1;
        byte[] object_unused = new byte[2];
	int version; // int16
        int format; // int16
        int scriptCount; // uint16
        ArrayList<ScriptRef> scripts = new ArrayList<>(); // scriptCount of VMAD scripts section

	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    object = in.extract(8);
	    version = in.extractInt(2);
            format = in.extractInt(2);
            LByteChannel objectChannel = new LByteChannel(object);
            if(format == 1){
                object_FormID = new FormID();
                object_FormID.parseData(objectChannel, srcMod);
                object_alias = objectChannel.extractInt(2);
                object_unused = objectChannel.extract(2);
            } else if (format == 2) {
                object_unused = objectChannel.extract(2);
                object_alias = objectChannel.extractInt(2);
                object_FormID = new FormID();
                object_FormID.parseData(objectChannel, srcMod);
            } else {
                throw new UnsupportedOperationException("Unsupported VMAD Object format: " + format);
            }
            scriptCount = in.extractInt(2);
            for (int i = 0; i < scriptCount; i++) {
                scripts.add(new ScriptRef(in, srcMod));
            }
	}

	void export(ModExporter out) throws IOException {
//	    out.write(object);
            if (format == 1){
                object_FormID.export(out);
                out.write(object_alias, 2);
                out.write(object_unused);
            } else if (format == 2){
                out.write(object_unused);
                out.write(object_alias, 2);
                object_FormID.export(out);
            }
	    out.write(version, 2);
            out.write(format, 2);
            out.write(scripts.size(), 2);
            for (ScriptRef s : scripts){
                s.export(out);
            }
	}

	int getContentLength(ModExporter out) {
            int size = 0;
            for (ScriptRef s : scripts) {
                size += s.getTotalLength(out);
            }
	    return 14 + size;
	}
    }

    // Enums
    /**
     *
     */
    public enum QuestStageFlags {

	/**
	 *
	 */
	StartUpStage,
	/**
	 *
	 */
	ShutDownStage,
	/**
	 *
	 */
	KeepInstanceDataFromHereOn;
    }

    /**
     *
     */
    public enum QuestLogFlags {

	/**
	 *
	 */
	CompleteQuest,
	/**
	 *
	 */
	FailQuest;
    }

    /**
     *
     */
    public enum QuestFlags {

	/**
	 *
	 */
	StartGameEnabled(0),
	/**
	 *
	 */
	WildernessEncounter(2),
	/**
	 *
	 */
	AllowRepeatedStages(3),
	/**
	 *
	 */
	RunOnce(4),
	/**
	 *
	 */
	ExcludeFromDialogueExport(5),
	/**
	 *
	 */
	WarnOnAliasFillFailure(6);
	int value;

	QuestFlags(int val) {
	    value = val;
	}
    }

    /**
     *
     */
    public enum QuestType {

	/**
	 *
	 */
	None,
	/**
	 *
	 */
	MainQuest,
	/**
	 *
	 */
	MageGuild,
	/**
	 *
	 */
	ThievesGuild,
	/**
	 *
	 */
	DarkBrotherhood,
	/**
	 *
	 */
	Companion,
	/**
	 *
	 */
	Misc,
	/**
	 *
	 */
	Daedric,
	/**
	 *
	 */
	Side,
	/**
	 *
	 */
	CivilWar,
	/**
	 *
	 */
	Vampire,
	/**
	 *
	 */
	Dragonborn
    }

    QUST() {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    /**
     *
     * @param edid
     */
    public QUST(String edid) {
	this();
	originateFromPatch(edid);
	DNAM dnam = (DNAM) subRecords.get("DNAM");
	dnam.flags.set(0, true);
	dnam.flags.set(4, true);
	dnam.flags.set(8, true);
	subRecords.getSubInt("ANAM").set(0);
    }

    @Override
    Record getNew() {
	return new QUST();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("QUST");
    }

    // Get Set Functions
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
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
     * @deprecated Duplicate accessor
     * @return
     */
    public ArrayList<QuestStage> getQuestStages() {
	return subRecords.getSubList("INDX").toPublic();
    }

    /**
     * 
     * @param stage
     */
    public void addQuestStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
    }

    /**
     *
     * @return
     */
    public int getPriority() {
	return getDNAM().priority;
    }

    /**
     *
     * @param priority
     */
    public void setPriority(int priority) {
	if (priority < 0) {
	    priority = 0;
	} else if (priority > 100) {
	    priority = 100;
	}
	getDNAM().priority = (byte) priority;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(QuestFlags flag, boolean on) {
	getDNAM().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(QuestFlags flag) {
	return getDNAM().flags.get(flag.value);
    }

    /**
     *
     * @param type
     */
    public void setQuestType(QuestType type) {
	getDNAM().questType = type;
    }

    /**
     *
     * @return
     */
    public QuestType getQuestType() {
	return getDNAM().questType;
    }

    /**
     *
     * @return
     */
    public String getShortName() {
	return subRecords.getSubString("ENAM").print();
    }

    /**
     *
     * @param shortName
     */
    public void setShortName(String shortName) {
	subRecords.setSubString("ENAM", shortName);
    }

    /**
     *
     * @return
     */
    public String getObjectWindowFilter() {
	return subRecords.getSubString("FLTR").print();
    }

    /**
     *
     * @param name
     */
    public void setObjectWindowFilter(String name) {
	subRecords.setSubString("FLTR", name);
    }

    /**
     *
     * @return
     */
    public ArrayList<QuestStage> getStages() {
	return subRecords.getSubList("INDX").toPublic();
    }

    /**
     * 
     */
    public void clearStages() {
	subRecords.getSubList("INDX").clear();
    }

    /**
     * 
     * @param stage
     */
    public void addStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }

    /**
     *
     * @return
     */
    public ArrayList<QuestObjective> getObjectives() {
	return subRecords.getSubList("QOBJ").toPublic();
    }

    /**
     * 
     */
    public void clearObjectives() {
	subRecords.getSubList("QOBJ").clear();
    }

    /**
     * 
     * @param objective
     */
    public void addObjective(QuestObjective objective) {
	subRecords.getSubList("QOBJ").add(objective);
    }

    /**
     *
     * @return
     */
    public ArrayList<Alias> getAliases() {
	return subRecords.getSubList("ALLS").toPublic();
    }

    /**
     * 
     * @param alias
     */
    public void addAlias(Alias alias) {
	subRecords.getSubList("ALLS").add(alias);
    }

    /**
     * 
     */
    public void clearAliases() {
	subRecords.getSubList("ALLS").clear();
    }
}
