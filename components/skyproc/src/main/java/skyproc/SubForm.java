/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A SubRecord containing a FormID at the start of its structure.
 *
 * @author Justin Swanson
 */
class SubForm extends SubRecordTyped<FormID> {

    FormID ID = new FormID();

    void copyForm(FormID in) {
	ID = new FormID(in);
    }

    /**
     *
     * @param id FormID to set the record's to.
     */
    public void setForm(FormID id) {
	ID = id;
    }

    /**
     *
     * @return The FormID string of the Major Record.
     */
    public String getFormStr() {
	return ID.getArrayStr(true);
    }

    /**
     *
     * @return The name of the mod from which this Major Record originates.
     */
    public ModListing getFormMaster() {
	return ID.getMaster();
    }

    FormID copyOfForm() {
	return new FormID(ID);
    }

    /**
     * Returns the FormID object of the Sub Record. Note that any changes made
     * to this FormID will be reflected in the Sub Record also.
     *
     * @return The FormID object of the Sub Record.
     */
    public FormID getForm() {
	return ID;
    }

    SubForm(String type_) {
	super(type_);
    }

    SubForm(String type, FormID form) {
	this(type);
	setForm(form);
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	ID.parseData(in, srcMod);
    }

    @Override
    public String toString() {
	if (isValid()) {
	    return ID.toString() + " - " + super.toString();
	} else {
	    return super.toString();
	}
    }

    @Override
    boolean isValid() {
	return ID.isValid();
    }

    @Override
    public String print() {
	return ID.getFormStr();
    }

    @Override
    boolean confirmLink() {
	if (SPGlobal.globalDatabase != null) {
	    return confirmLink(SPGlobal.globalDatabase);
	} else {
	    return true;
	}
    }

    boolean confirmLink(SPDatabase db) {
	return true;
    }

    @Override
    int getContentLength(ModExporter out) {
	return ID.getContentLength();
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	ID.export(out);
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubForm(type_);
    }

    /**
     * Takes the FormID into the equals calculations
     *
     * @param obj Another SubFormRecord
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
	final SubForm other = (SubForm) obj;
	if (this.ID != other.ID && (this.ID == null || !this.ID.equals(other.ID))) {
	    return false;
	}
	return true;
    }

    /**
     * Takes the FormID into the hashcode calculations
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 7;
	hash = 29 * hash + (this.ID != null ? this.ID.hashCode() : 0);
	return hash;
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>(1);
	out.add(ID);
	return out;
    }

    @Override
    FormID translate() {
	return ID;
    }

    @Override
    SubRecord<FormID> translate(FormID in) {
	return new SubForm(getType(), in);
    }
}
