/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import lev.Ln;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubFormArray extends SubRecordTyped implements Iterable<FormID> {

    ArrayList<FormID> IDs;

    public SubFormArray(String type_, int size) {
	super(type_);
	IDs = new ArrayList<>(size);
	for (int i = 0; i < size; i++) {
	    IDs.add(new FormID());
	}
    }

    @Override
    SubRecord getNew(String type) {
	return new SubFormArray(type, 0);
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	for (FormID ID : IDs) {
	    ID.export(out);
	}
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	int size = IDs.size();
	if (size != 0) {
	    for (int i = 0; i < size; i++) {
		IDs.get(i).parseData(in, srcMod);
		if (SPGlobal.logMods){
		    logMod(srcMod, toString(), "Setting " + toString() + " FormID[" + i + "]: " + IDs.get(i));
		}
	    }
	} else {
	    while (!in.isDone()) {
		FormID id = new FormID();
		id.parseData(in, srcMod);
		add(id);
                size++;
                if (SPGlobal.logMods){
		    logMod(srcMod, toString(), "Setting " + toString() + " FormID[" + size + "]: " + id);
		}
	    }
	}
    }

    void setIth(int i, byte[] in) {
    }

    int size() {
	return IDs.size();
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	return new ArrayList<>(IDs);
    }

    @Override
    boolean isValid() {
	for (FormID ID : IDs) {
	    if (!ID.isValid()) {
		return false;
	    }
	}
	return true;
    }

    @Override
    int getContentLength(ModExporter out) {
	return IDs.size() * 4;
    }

    @Override
    public Iterator<FormID> iterator() {
	return IDs.iterator();
    }

    public boolean remove(FormID id) {
	if (IDs.contains(id)) {
	    IDs.remove(id);
	    return true;
	} else {
	    return false;
	}
    }

    public void add(FormID id) {
	IDs.add(id);
    }

    public boolean containedIn(SubFormArray in) {
	for (FormID id : IDs) {
	    if (!in.IDs.contains(id)) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubFormArray other = (SubFormArray) obj;
	if (this.IDs != other.IDs && (this.IDs == null || !Ln.equals(this.IDs, other.IDs, true))) {
	    return false;
	}
	return true;
    }

    public void clear() {
	IDs.clear();
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 17 * hash + (this.IDs != null ? this.IDs.hashCode() : 0);
	return hash;
    }
    
    public boolean contains(FormID form){
        return IDs.contains(form);
    }
}
