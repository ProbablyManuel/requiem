/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class ScriptProperty extends Record implements Serializable {

    StringNonNull name = new StringNonNull();
    int unknown = 1;
    ScriptData data;

    ScriptProperty() {
    }

    ScriptProperty(String name) {
	this.name.set(name);
    }

    ScriptProperty(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	parseData(in, srcMod);
    }

    public ScriptProperty(String name, boolean b) {
	this(name);
	BooleanData tmp = new BooleanData();
	tmp.data = b;
	data = tmp;
    }

    public ScriptProperty(String name, int in) {
	this(name);
	IntData tmp = new IntData();
	tmp.data = in;
	data = tmp;
    }

    public ScriptProperty(String name, String in) {
        this(name);
        StringData tmp = new StringData();
        tmp.data = in;
        data = tmp;
    }

    public ScriptProperty(String name, FormID id) {
	this(name);
	FormIDData tmp = new FormIDData();
	tmp.id = id;
	data = tmp;
    }

    public ScriptProperty(String name, float in) {
	this(name);
	FloatData tmp = new FloatData();
	tmp.data = in;
	data = tmp;
    }

    public ScriptProperty(String name, Integer... in) {
	this(name);
	IntegerArrayData tmp = new IntegerArrayData();
	tmp.data = new ArrayList<>(Arrays.asList(in));
	data = tmp;
    }

    public ScriptProperty(String name, String... in) {
	this(name);
	StringArrayData tmp = new StringArrayData();
	tmp.data = new ArrayList<>(Arrays.asList(in));
	data = tmp;
    }

    public ScriptProperty(String name, Float... in) {
	this(name);
	FloatArrayData tmp = new FloatArrayData();
	tmp.data = new ArrayList<>(Arrays.asList(in));
	data = tmp;
    }

    public ScriptProperty(String name, Boolean... in) {
	this(name);
	BoolArrayData tmp = new BoolArrayData();
	tmp.data = new ArrayList<>(Arrays.asList(in));
	data = tmp;
    }

    public ScriptProperty(String name, FormID... in) {
	this(name);
	FormArrayData tmp = new FormArrayData();
	ArrayList<FormIDData> list = new ArrayList<>(in.length);
	for (FormID id : in) {
	    list.add(new FormIDData(id));
	}
	tmp.data = list;
	data = tmp;
    }

    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	if (getPropertyType().equals(ScriptPropertyType.FormID)) {
	    out.add(((FormIDData) data).id);
	} else if (getPropertyType().equals(ScriptPropertyType.FormIDArr)) {
	    FormArrayData arr = (FormArrayData) data;
	    for (FormIDData id : arr.data) {
		out.add(id.id);
	    }
	}
	return out;
    }

    @Override
    final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	name.set(in.extractString(in.extractInt(2)));
	ScriptPropertyType type = ScriptPropertyType.value(in.extractInt(1));
	unknown = in.extractInt(1);
	if (SPGlobal.logMods){
	    logMod(srcMod, "VMAD", "    Property " + name + " with type " + type + ", unknown: " + unknown);
	}
	switch (type) {
	    case FormID:
		data = new FormIDData();
		break;
	    case String:
		data = new StringData();
		break;
	    case Integer:
		data = new IntData();
		break;
	    case Float:
		data = new FloatData();
		break;
	    case Boolean:
		data = new BooleanData();
		break;
	    case FormIDArr:
		data = new FormArrayData();
		break;
	    case StringArr:
		data = new StringArrayData();
		break;
	    case IntegerArr:
		data = new IntegerArrayData();
		break;
	    case FloatArr:
		data = new FloatArrayData();
		break;
	    case BooleanArr:
		data = new BoolArrayData();
		break;
	    default:
		if (logging()) {
		    logError("VMAD", "    Importing property with UNKNOWN TYPE!");
		}
                if (SPGlobal.logMods){
                    logMod(srcMod, "VMAD", "    Importing property with UNKNOWN TYPE!");
                }
		in.extractInts(1000);  // break extraction to exclude NPC from database
	}
	if (data != null) {
	    data.parseData(in, srcMod);
	}
	if (SPGlobal.logMods){
	    logMod(srcMod, "VMAD", "      Data: " + data.print());
	}
    }

    @Override
    void export(ModExporter out) throws IOException {
	name.export(out);
	out.write(getPropertyType().value, 1);
	out.write(unknown, 1);
	data.export(out);
    }

    @Override
    boolean isValid() {
	return name != null;
    }

    @Override
    public String toString() {
	return "VMADproperty " + name;
    }

    @Override
    public String print() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("VMAD");
    }

    @Override
    Record getNew() {
	return new ScriptProperty();
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    int getSizeLength() {
	return 0;
    }

    @Override
    int getFluffLength() {
	return 0;
    }

    @Override
    int getContentLength(ModExporter out) {
	int len = name.getTotalLength(out) + 2;
	return len + data.getContentLength();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ScriptProperty other = (ScriptProperty) obj;
	if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
	return hash;
    }

    public enum ScriptPropertyType {

	Unknown(0),
	FormID(1),
	String(2),
	Integer(3),
	Float(4),
	Boolean(5),
	FormIDArr(11),
	StringArr(12),
	IntegerArr(13),
	FloatArr(14),
	BooleanArr(15);
	int value;

	ScriptPropertyType(int value) {
	    this.value = value;
	}

	static ScriptPropertyType value(int value) {
	    for (ScriptPropertyType p : ScriptPropertyType.values()) {
		if (p.value == value) {
		    return p;
		}
	    }
	    return Unknown;
	}
    }

    // Data classes
    interface ScriptData {

	void parseData(LImport in, Mod srcMod);

	int getContentLength();

	void export(ModExporter out) throws IOException;

	ScriptPropertyType getType();

	String print();
    }

    class StringData implements ScriptData, Serializable {

	String data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    data = in.extractString(in.extractInt(2));
	}

	@Override
	public int getContentLength() {
	    return 2 + data.length();
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data.length(), 2);
	    out.write(data);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.String;
	}

	@Override
	public String print() {
	    return data;
	}
    }

    class IntData implements ScriptData, Serializable {

	int data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    data = in.extractInt(4);
	}

	@Override
	public int getContentLength() {
	    return 4;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.Integer;
	}

	@Override
	public String print() {
	    return Integer.toString(data);
	}
    }

    class BooleanData implements ScriptData, Serializable {

	boolean data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    data = in.extractBool(1);
	}

	@Override
	public int getContentLength() {
	    return 1;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    if (data) {
		out.write(1, 1);
	    } else {
		out.write(0, 1);
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.Boolean;
	}

	@Override
	public String print() {
	    return String.valueOf(data);
	}
    }

    class FormIDData implements ScriptData, Serializable {

	byte[] data;
	FormID id;

	FormIDData() {
	    data = new byte[4];
	    data[2] = -1;
	    data[3] = -1;
	}

	FormIDData(FormID in) {
	    this();
	    id = in;
	}

	@Override
	public void parseData(LImport in, Mod srcMod){
	    data = in.extract(4);
	    id = new FormID();
	    id.parseData(in, srcMod);
	}

	@Override
	public int getContentLength() {
	    return 8;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data, 4);
	    id.export(out);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.FormID;
	}

	@Override
	public String print() {
	    return id.toString();
	}
    }

    class FloatData implements ScriptData, Serializable {

	float data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    data = in.extractFloat();
	}

	@Override
	public int getContentLength() {
	    return 4;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.Float;
	}

	@Override
	public String print() {
	    return String.valueOf(data);
	}
    }

    class IntegerArrayData implements ScriptData, Serializable {

	ArrayList<Integer> data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    int size = in.extractInt(4);
	    data = new ArrayList<>(size);
	    for (int i = 0; i < size; i++) {
		data.add(in.extractInt(4));
	    }
	}

	@Override
	public int getContentLength() {
	    return data.size() * 4 + 4;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data.size());
	    for (Integer i : data) {
		out.write(i);
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.IntegerArr;
	}

	@Override
	public String print() {
	    String out = "";
	    for (Integer i : data) {
		out += i + " ";
	    }
	    return out;
	}
    }

    class StringArrayData implements ScriptData, Serializable {

	ArrayList<String> data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    int size = in.extractInt(4);
	    data = new ArrayList<>(size);
	    for (int i = 0; i < size; i++) {
		int stringSize = in.extractInt(2);
		data.add(in.extractString(stringSize));
	    }
	}

	@Override
	public int getContentLength() {
	    int out = data.size() * 2 + 4;
	    for (String s : data) {
		out += s.length();
	    }
	    return out;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data.size());
	    for (String i : data) {
		out.write(i.length(), 2);
		out.write(i);
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.StringArr;
	}

	@Override
	public String print() {
	    String out = "";
	    for (String i : data) {
		out += i + " ";
	    }
	    return out;
	}
    }

    class FormArrayData implements ScriptData, Serializable {

	ArrayList<FormIDData> data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    int size = in.extractInt(4);
	    data = new ArrayList<>(size);
	    for (int i = 0; i < size; i++) {
		FormIDData id = new FormIDData();
		id.parseData(in, srcMod);
		data.add(id);
	    }
	}

	@Override
	public int getContentLength() {
	    return data.size() * 8 + 4;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data.size());
	    for (FormIDData i : data) {
		i.export(out);
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.FormIDArr;
	}

	@Override
	public String print() {
	    String out = "";
	    for (FormIDData i : data) {
		out += i.print() + " ";
	    }
	    return out;
	}
    }

    class BoolArrayData implements ScriptData, Serializable {

	ArrayList<Boolean> data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    int size = in.extractInt(4);
	    data = new ArrayList<>(size);
	    for (int i = 0; i < size; i++) {
		data.add(in.extractBool(1));
	    }
	}

	@Override
	public int getContentLength() {
	    return data.size() + 4;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data.size());
	    for (Boolean i : data) {
		if (i) {
		    out.write(1, 1);
		} else {
		    out.write(0, 1);
		}
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.BooleanArr;
	}

	@Override
	public String print() {
	    String out = "";
	    for (Boolean i : data) {
		out += i + " ";
	    }
	    return out;
	}
    }

    class FloatArrayData implements ScriptData, Serializable {

	ArrayList<Float> data;

	@Override
	public void parseData(LImport in, Mod srcMod){
	    int size = in.extractInt(4);
	    data = new ArrayList<>(size);
	    for (int i = 0; i < size; i++) {
		data.add(in.extractFloat());
	    }
	}

	@Override
	public int getContentLength() {
	    return data.size() * 4 + 4;
	}

	@Override
	public void export(ModExporter out) throws IOException {
	    out.write(data.size());
	    for (Float i : data) {
		out.write(i);
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.FloatArr;
	}

	@Override
	public String print() {
	    String out = "";
	    for (Float i : data) {
		out += i + " ";
	    }
	    return out;
	}
    }

    // get/set
    public ScriptPropertyType getPropertyType() {
	return data.getType();
    }
}
