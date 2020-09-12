package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A record fully describing and specifying a perk, including level up perks, as
 * well as more obscure hidden perks related to quests and NPCs.
 *
 * @author Justin Swanson
 */
public class PERK extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype PERKproto = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(new PERKScriptFragments()), "EDID");
	    add(new SubList<>(new Condition()));
	    add(new SubData("DATA"));
	    add(new SubForm("NNAM"));
	    add(SubString.getNew("ICON", true));
	    add(new SubList<>(new PRKEPackage(new SubPrototype() {

		@Override
		protected void addRecords() {
		    add(new SubData("PRKE"));
		    add(new PRKEComplexSubPackage(new SubPrototype() {

			@Override
			protected void addRecords() {
			    add(new SubData("DATA"));
			    add(new SubList<>(new SubShell(new SubPrototype() {

				@Override
				protected void addRecords() {
				    add(new SubData("PRKC"));
				    add(new SubList<>(new Condition()));
				}
			    })));
			    add(new SubData("EPFT"));
			    add(new SubStringPointer("EPF2", SubStringPointer.Files.STRINGS));
			    add(new SubData("EPF3"));
			    add(new SubData("EPFD"));
			}
		    }));
		    add(new SubData("PRKF"));
		    forceExport("PRKF");
		}
	    })));
	}
    };

    static class PRKEPackage extends SubShellBulkType {

	PRKEPackage(SubPrototype proto) {
	    super(proto, false);
	}

	@Override
	final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    SubData PRKE = subRecords.getSubData("PRKE");
	    PRKE.parseData(PRKE.extractRecordData(in), srcMod);
	    PerkType perkType = PerkType.values()[subRecords.getSubData("PRKE").getData()[0]];
	    switch (perkType) {
		case QUEST:
		    subRecords.remove("DATA");
		    subRecords.add(new SubFormData("DATA"));
		    break;
		case ABILITY:
		    subRecords.remove("DATA");
		    subRecords.add(new SubForm("DATA"));
		    break;
	    }
	    super.parseData(in, srcMod);
	}

	@Override
	SubRecord getNew(String type) {
	    return new PRKEPackage(getPrototype());
	}

	@Override
	boolean isValid() {
	    return subRecords.get("PRKE").isValid() && subRecords.get("DATA").isValid();
	}
    }

    static class PRKEComplexSubPackage extends SubShell {

	PRKEComplexSubPackage(SubPrototype proto) {
	    super(proto);
	}

	@Override
	SubRecord getNew(String type) {
	    return new PRKEComplexSubPackage(getPrototype());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    switch (getNextType(in)) {
		case "EPFT":
		    SubData EPFT = subRecords.getSubData("EPFT");
		    EPFT.parseData(in, srcMod);
		    if (EPFT.toInt() >= 3 && EPFT.toInt() <= 5) {
			subRecords.remove("EPFD");
			subRecords.add(new SubForm("EPFD"));
		    }
		    return;
	    }
	    super.parseData(in, srcMod);
	}

	@Override
	boolean isValid() {
	    return true;
	}
    }

    static class PERKScriptFragments extends SubRecord {

	byte unknown = 0;
	StringNonNull fragmentFile = new StringNonNull();
	ArrayList<PERKScriptFragment> fragments = new ArrayList<>();
	boolean valid = false;

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    fragmentFile.set(in.extractString(in.extractInt(2)));
	    int numFrag = in.extractInt(2);
	    for (int i = 0 ; i < numFrag ; i++) {
		PERKScriptFragment frag = new PERKScriptFragment();
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
	    fragmentFile.export(out);
	    out.write(fragments.size(), 2);
	    for (PERKScriptFragment frag : fragments) {
		frag.export(out);
	    }
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (!valid) {
		return 0;
	    }
	    int len = 3;
	    len += fragmentFile.getTotalLength(out);
	    for (PERKScriptFragment frag : fragments) {
		len += frag.getContentLength(out);
	    }
	    return len;
	}

	@Override
	SubRecord getNew(String type) {
	    return new PERKScriptFragments();
	}

	@Override
	ArrayList<String> getTypes() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
    }

    static class PERKScriptFragment {

	int index = 0;
	byte[] unknown = new byte[3];
	StringNonNull scriptName = new StringNonNull();
	StringNonNull fragmentName = new StringNonNull();

	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    index = in.extractInt(2);
	    unknown = in.extract(3);
	    scriptName.set(in.extractString(in.extractInt(2)));
	    fragmentName.set(in.extractString(in.extractInt(2)));
	}

	void export(ModExporter out) throws IOException {
	    out.write(index, 2);
	    out.write(unknown);
	    scriptName.export(out);
	    fragmentName.export(out);
	}

	int getContentLength(ModExporter out) {
	    return 5 + scriptName.getTotalLength(out)
		    + fragmentName.getTotalLength(out);
	}
    }

    // Enums
    enum PerkType {

	QUEST, ABILITY, COMPLEX;
    }

    // Common Functions
    PERK() {
	super();
	subRecords.setPrototype(PERKproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("PERK");
    }

    @Override
    Record getNew() {
	return new PERK();
    }

    // Get/Set
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
     *
     * @param perk
     */
    public void setNextPerk(FormID perk) {
	subRecords.setSubForm("NNAM", perk);
    }

    /**
     *
     * @return
     */
    public FormID getNextPerk() {
	return subRecords.getSubForm("NNAM").getForm();
    }
}
