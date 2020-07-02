/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.ScriptProperty.FloatArrayData;
import skyproc.ScriptProperty.FloatData;
import skyproc.ScriptProperty.FormArrayData;
import skyproc.ScriptProperty.FormIDData;
import skyproc.ScriptProperty.IntData;
import skyproc.ScriptProperty.IntegerArrayData;
import skyproc.ScriptProperty.StringArrayData;
import skyproc.ScriptProperty.StringData;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A script reference to be added/found in a major record's ScriptPackage
 * object.
 *
 * @author Justin Swanson
 */
public class ScriptRef extends Record implements Iterable<String> {

    StringNonNull name = new StringNonNull();
    int unknown = 0;
    Map<String, ScriptProperty> properties = new HashMap<>();

    ScriptRef() {
    }

    /**
     * Creates a script reference with the given name. Adding this to a major
     * record's ScriptPackage will attach the script to the record. Of course,
     * there must be a script with that name in the Data/Scripts/ folder for it
     * to actually do anything in-game.
     *
     * @param name
     */
    public ScriptRef(String name) {
	this.name.set(name);
    }

    ScriptRef(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	parseData(in, srcMod);
    }

    @Override
    final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	name.set(in.extractString(in.extractInt(2)));
	unknown = in.extractInt(1);
	int propertyCount = in.extractInt(2);
	if (SPGlobal.logMods){
	    logMod(srcMod, "VMAD", "  Script " + name.toString() + " with " + propertyCount + " properties. Unknown: " + unknown);
	}
        for (int i = 0; i < propertyCount; i++) {
            ScriptProperty newprop = new ScriptProperty(in, srcMod);
            properties.put(newprop.name.data.toLowerCase(), newprop);
	}
    }

    @Override
    void export(ModExporter out) throws IOException {
	name.export(out);
	out.write(unknown, 1);
	out.write(properties.size(), 2);
        for (ScriptProperty p : properties.values()) {
	    p.export(out);
	}
    }

    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
        for (ScriptProperty s : properties.values()) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    boolean isValid() {
	return name != null;
    }

    @Override
    public String toString() {
	return print();
    }

    @Override
    public String print() {
	return name.toString();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("VMAD");
    }

    @Override
    Record getNew() {
	return new ScriptRef();
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
	int len = name.getTotalLength(out) + 3;
        for (ScriptProperty p : properties.values()) {
	    len += p.getTotalLength(out);
	}
	return len;
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
	final ScriptRef other = (ScriptRef) obj;
	if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
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
	hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
	return hash;
    }

    /**
     *
     * @return Iterator over the Property names in the script.
     */
    @Override
    public Iterator<String> iterator() {
	return getProperties().iterator();
    }

    // Get/set
    /**
     * Sets the name of the script. This MUST match the name of the script file
     * you wish to attach.
     *
     * @param name
     */
    public void setName(String name) {
	this.name.set(name);
    }

    /**
     * Gets the name of the script
     *
     * @return
     */
    public String getName() {
	return name.print();
    }

    /**
     *
     * @return A copy of all the property names in the script.
     */
    public List<String> getProperties() {
        return properties.keySet().stream().sorted().
                collect(Collectors.toList());
    }

    /**
     * Removes a property with the given name from the script, if one exists.
     *
     * @param propertyName
     */
    public void removeProperty(String propertyName) {
        properties.remove(propertyName.toLowerCase());
    }

    /**
     * Get the data of the given property as float.
     *
     * @param propertyName the name of the property
     * @return the float data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public float getPropertyFloat(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return ((FloatData) prop.data).data;
    }

    /**
     * Get the data of the given property as float array. The returned array
     * list is a clone of the original one, i.e. you cannot use it to modify the
     * parameters of this property.
     *
     * @param propertyName the name of the property
     * @return the float data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public ArrayList<Float> getPropertyFloatArray(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return (ArrayList<Float>) ((FloatArrayData) prop.data).data.clone();
    }

    /**
     * Get the data of the given property as integer.
     *
     * @param propertyName the name of the property
     * @return the integer data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public int getPropertyInt(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return ((IntData) prop.data).data;
    }

    /**
     * Get the data of the given property as integer array. The returned array
     * list is a clone of the original one, i.e. you cannot use it to modify the
     * parameters of this property.
     *
     * @param propertyName the name of the property
     * @return the float data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public ArrayList<Integer> getPropertyIntArray(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return (ArrayList<Integer>) ((IntegerArrayData) prop.data).data.clone();
    }

    /**
     * Get the data of the given property as string.
     *
     * @param propertyName the name of the property
     * @return the string data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public String getPropertyString(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return ((StringData) prop.data).data;
    }

    /**
     * Get the data of the given property as string array. The returned array
     * list is a clone of the original one, i.e. you cannot use it to modify the
     * parameters of this property.
     *
     * @param propertyName the name of the property
     * @return the float data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public ArrayList<String> getPropertyStringArray(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return (ArrayList<String>) ((StringArrayData) prop.data).data.clone();
    }

    /**
     * Get the data of the given property as FormID.
     *
     * @param propertyName the name of the property
     * @return the string data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public FormID getPropertyFormID(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        return ((FormIDData) prop.data).id;
    }

    /**
     * Get the data of the given property as FormID array. The returned array
     * list is a clone of the original one, i.e. you cannot use it to modify the
     * parameters of this property.
     *
     * @param propertyName the name of the property
     * @return the float data of the property
     * @throws ClassCastException if the property is of a different type
     */
    public ArrayList<FormID> getPropertyFormIDArray(String propertyName) {
        ScriptProperty prop = properties.get(propertyName.toLowerCase());
        ArrayList<FormID> retval = new ArrayList<>();
        for (FormIDData entry : ((FormArrayData) prop.data).data) {
            retval.add(entry.id);
        }
        return retval;
    }

    /**
     * Adds a boolean property to the script. If the property name is already
     * present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param booleanProperty Boolean value to assign to property
     */
    public void setProperty(String propertyName, boolean booleanProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, booleanProperty));
    }

    /**
     * Adds an integer property to the script. If the property name is already
     * present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param integerProperty Integer value to assign to property
     */
    public void setProperty(String propertyName, int integerProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, integerProperty));
    }

    /**
     * Adds a FormID property to the script. If the property name is already
     * present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param idProperty FormID value to assign to property
     */
    public void setProperty(String propertyName, FormID idProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, idProperty));
    }

    /**
     * Adds a float property to the script. If the property name is already
     * present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    public void setProperty(String propertyName, float floatProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, floatProperty));
    }

    /**
     * Adds a String property to the script. If the property name is already
     * present, the previous data will be overwritten.
     *
     * @param propertyName   Property name to add
     * @param stringProperty String value to assign to property
     */
    public void setProperty(String propertyName, String stringProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, stringProperty));
    }

    /**
     * Adds a float array property to the script. If the property name is
     * already present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param floatProperty Float array to assign to property
     */
    public void setProperty(String propertyName, Float... floatProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, floatProperty));
    }

    /**
     * Adds a int array property to the script. If the property name is already
     * present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param intProperty Int array to assign to property
     */
    public void setProperty(String propertyName, Integer... intProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, intProperty));
    }

    /**
     * Adds a boolean array property to the script. If the property name is
     * already present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param boolProperty
     */
    public void setProperty(String propertyName, Boolean... boolProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, boolProperty));
    }

    /**
     * Adds a string array property to the script. If the property name is
     * already present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param stringProperty String array to assign to property
     */
    public void setProperty(String propertyName, String... stringProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, stringProperty));
    }

    /**
     * Adds a FormID array property to the script. If the property name is
     * already present, the previous data will be overwritten.
     *
     * @param propertyName Property name to add
     * @param formProperty FormID array to assign to property
     */
    public void setProperty(String propertyName, FormID... formProperty) {
        properties.put(propertyName.toLowerCase(),
                new ScriptProperty(propertyName, formProperty));
    }
}