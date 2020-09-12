/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
abstract class SubRecords implements Serializable, Iterable<SubRecord> {

    protected Map<String, SubRecord> map = new HashMap<>(0);

    public SubRecords() {
    }

    public void setMajor(MajorRecord in) {
    }

    public void setPrototype(SubPrototype proto) {
    }

    public abstract SubPrototype getPrototype();

    public void add(SubRecord r) {
	for (String t : r.getTypes()) {
	    map.put(t, r);
	}
    }

    protected void export(ModExporter out) throws IOException {
	for (SubRecord s : this) {
	    s.export(out);

	}
    }

    void clear() {
	map.clear();
    }

    public boolean shouldExport(SubRecord s) {
	return s.isValid();
    }

    public boolean contains(String t) {
	return map.containsKey(t);
    }

    public SubRecord get(String in) {
	return map.get(in);
    }

    public SubString getSubString(String in) {
	return (SubString) get(in);
    }

    public void setSubString(String in, String str) {
	getSubString(in).setString(str);
    }

    public SubStringPointer getSubStringPointer(String in) {
	return (SubStringPointer) get(in);
    }

    public void setSubStringPointer(String in, String s) {
	getSubStringPointer(in).setText(s);
    }

    public SubForm getSubForm(String in) {
	return (SubForm) get(in);
    }

    public void setSubForm(String in, FormID id) {
	getSubForm(in).setForm(id);
    }

    public SubFloat getSubFloat(String in) {
	return (SubFloat) get(in);
    }

    public void setSubFloat(String in, float f) {
	getSubFloat(in).set(f);
    }

    public SubData getSubData(String in) {
	return (SubData) get(in);
    }

    public void setSubData(String in, byte[] b) {
	getSubData(in).setData(b);
    }

    public void setSubData(String in, int i) {
	getSubData(in).setData(i);
    }

    public void setSubData(String in, int i, int size) {
	getSubData(in).setData(size, size);
    }

    public SubFlag getSubFlag(String in) {
	return (SubFlag) get(in);
    }

    public void setSubFlag(String in, int i, boolean b) {
	getSubFlag(in).set(i, b);
    }

    public SubInt getSubInt(String in) {
	return (SubInt) get(in);
    }

    public void setSubInt(String in, int i) {
	getSubInt(in).set(i);
    }

    public SubRGB getSubRGB(String in) {
	return (SubRGB) get(in);
    }

    public void setSubRGB(String in, RGB c, float f) {
	getSubRGB(in).set(c, f);
    }

    public SubRGBshort getSubRGBshort(String in) {
	return (SubRGBshort) get(in);
    }

    public void setSubRGBshort(String in, RGBA c, short val) {
	getSubRGBshort(in).set(c, val);
    }

    public SubMarkerSet getSubMarker(String in) {
	return (SubMarkerSet) get(in);
    }

    public KeywordSet getKeywords() {
	return (KeywordSet) get("KWDA");
    }

    public SubFormArray getSubFormArray(String in) {
	return (SubFormArray) get(in);
    }

    public SubList getSubList(String in) {
	return (SubList) get(in);
    }

    public ScriptPackage getScripts() {
	return (ScriptPackage) get("VMAD");
    }

    public BodyTemplate getBodyTemplate() {
        BodyTemplate b = (BodyTemplate) get("BODT");
        if (b == null){
            b = (BodyTemplate) get("BOD2");
        }
	return b;
    }

    public SubShell getSubShell(String t) {
	return (SubShell) get(t);
    }

    public SubFormInt getSubFormInt(String t) {
	return (SubFormInt) get(t);
    }

    public void setSubFormInt(String t, FormID id, int val) {
	SubFormInt s = getSubFormInt(t);
	s.setForm(id);
	s.setNum(val);
    }

    public void setSubFormInt(String t, FormID id) {
	getSubFormInt(t).setForm(id);
    }

    public void setSubFormInt(String t, int val) {
	getSubFormInt(t).setNum(val);
    }

    public Model getModel() {
	return (Model) get("MODL");
    }

    boolean isValid() {
	for (SubRecord s : this) {
	    if (!s.isValid()) {
		return false;
	    }
	}
	return true;
    }

    boolean isAnyValid() {
	for (SubRecord s : this) {
	    if (s.isValid()) {
		return true;
	    }
	}
	return false;
    }

    ArrayList<String> summaryLines() {
	ArrayList<String> out = new ArrayList<>();
	String header = "Summary: ";
	String data = "";
	int counter = 0;
	ArrayList<String> printedTypes = new ArrayList<>();
	for (String type : getTypes()) {
	    SubRecord s = map.get(type);
	    if (s != null && s.isValid() && !printedTypes.contains(type)) {
		data = data + type + " ";
		if (s instanceof SubList) {
		    data = data + "(" + ((SubList) s).size() + ") ";
		}
		printedTypes.addAll(s.getTypes());
		if (counter++ == 12) {
		    out.add(header + data);
		    header = "-------- ";
		    data = "";
		    counter = 0;
		}
	    }
	}
	if (counter > 0) {
	    out.add(header + data);
	}
	return out;
    }

    void importSubRecords(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	while (!in.isDone()) {
	    importSubRecord(in, srcMod);
	}
    }

    void importSubRecord(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	String nextType = Record.getNextType(in);
	SubRecord record = get(nextType);
	if (record != null) {
	    record.parseData(record.extractRecordData(in), srcMod);
	} else {
	    throw new BadRecord(getTypes().get(0).toString() + " doesn't know what to do with a " + nextType + " record.");
	}
    }

    public void remove(String in) {
	if (map.containsKey(in)) {
	    map.remove(in);
	}
    }

    public int length(ModExporter out) {
	int length = 0;
	for (SubRecord s : this) {
	    length += s.getTotalLength(out);
	}
	return length;
    }

    public abstract ArrayList<String> getTypes();

    public abstract ArrayList<String> getTopLevelTypes();

    public ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (SubRecord s : this) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof SubRecords)) {
	    return false;
	}
	final SubRecords other = (SubRecords) obj;
	Iterator<SubRecord> lhs = this.iterator();
	Iterator<SubRecord> rhs = other.iterator();
	while (lhs.hasNext() && rhs.hasNext()) {
	    SubRecord lhsNext = lhs.next();
	    SubRecord rhsNext = rhs.next();
	    if (!"EDID".equals(lhsNext.getType()) && !lhsNext.equals(rhsNext)) {
		return false;
	    }
	}
	return !lhs.hasNext() && !rhs.hasNext();
    }
}