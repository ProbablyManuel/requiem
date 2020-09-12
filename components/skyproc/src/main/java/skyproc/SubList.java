/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.*;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A specialized collection of subrecords. Think of it as a special SkyProc
 * ArrayList used for subrecords.
 *
 * @param S The type of subrecord the group contains.
 * @author Justin Swanson
 */
class SubList<S extends SubRecord<T>, T> extends SubRecord<ArrayList<S>> implements Iterable<S> {

    ArrayList<T> collection = new ArrayList<>();
    S prototype;
    S last;
    boolean unique = false;

    SubList(S prototype_) {
	super();
	prototype = prototype_;
    }

    SubList(S prototype_, boolean unique) {
	this(prototype_);
	setUnique(unique);
    }

    SubList(SubList rhs) {
	super();
	prototype = (S) rhs.prototype;
	unique = rhs.unique;
	collection.addAll(rhs.collection);
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    boolean isValid() {
	return !collection.isEmpty();
    }

    public boolean contains(T s) {
	return collection.contains(s);
    }

    /**
     *
     * @param i Index of the item to retrieve.
     * @return The ith item.
     */
    public T get(int i) {
	return collection.get(i);
    }

    public boolean add(T item) {
	if (allow(item)) {
	    collection.add(item);
	    return true;
	} else {
	    return false;
	}
    }

    boolean allow(T item) {
	return !unique || !contains(item);
    }

    public boolean addAtIndex(T item, int i) {
	if (allow(item)) {
	    collection.add(i, item);
	    return true;
	} else {
	    return false;
	}
    }

    public boolean remove(T item) {
	return collection.remove(item);
    }

    /**
     * Removes an item based on its index in the list.
     *
     * @param i Index of the item to remove.
     */
    public void remove(int i) {
	collection.remove(i);
    }

    /**
     *
     * @return The number of items currently in the list.
     */
    public int size() {
	return collection.size();
    }

    public final void setUnique(boolean unique) {
	this.unique = unique;
    }

    public void sort() {
	TreeSet<T> sorter = new TreeSet<>();
	sorter.addAll(collection);
	collection.clear();
	collection.addAll(sorter);
    }

    public void clear() {
	collection.clear();
    }

    /**
     *
     * @return True if list is empty, and size == 0.
     */
    public boolean isEmpty() {
	return collection.isEmpty();
    }

    /**
     * This function will replace all records in the SubRecordList with the ones
     * given.<br><br> WARNING: All existing records will be lost.
     *
     * @param in ArrayList of records to replace the current ones.
     */
    public void setRecordsTo(ArrayList<T> in) {
	collection = in;
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	parseData(in, srcMod, getNextType(in));
    }

    void parseData(LImport in, Mod srcMod, String nextType) throws BadRecord, DataFormatException, BadParameter {
	if (nextType.equals(getType())) {
	    S newRecord = (S) prototype.getNew(getType());
	    last = newRecord;
	    newRecord.parseData(in, srcMod);
	    add(newRecord.translate());
	} else {
	    last.parseData(in, srcMod);
	}
    }

    @Override
    SubRecord getNew(String type) {
	return new SubList(this);
    }

    @Override
    int getContentLength(ModExporter out) {
	int length = 0;
	for (S s : translate()) {
	    length += s.getTotalLength(out);
	}
	return length;
    }

    @Override
    void export(ModExporter out) throws IOException {
	if (isValid()) {
	    for (S s : translate()) {
		s.export(out);
	    }
	}
    }

    ArrayList<T> toPublic() {
	return collection;
    }

    /**
     *
     * @return An iterator of all records in the SubRecordList.
     */
    @Override
    public Iterator<S> iterator() {
	return translate().iterator();
    }

    public void addAll(Collection<T> items) {
	collection.addAll(items);
    }
    
    @Override
    ArrayList<FormID> allFormIDs() {
	if (prototype.getClass().equals(SubForm.class)) {
	    return (ArrayList<FormID>) collection;
	}
	ArrayList<FormID> out = new ArrayList<>();
	for (S item : translate()) {
	    out.addAll(item.allFormIDs());
	}
	return out;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 89 * hash + Objects.hashCode(this.collection);
	return hash;
    }

    @Override
    public int getRecordLength(LImport in) {
	return prototype.getRecordLength(in);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o) {
	    return true;
	}
	if (o == null) {
	    return false;
	}
	if (!(o instanceof SubList)) {
	    return false;
	}
	SubList s = (SubList) o;
	return (Ln.equals(collection, s.collection, true));
    }

    @Override
    ArrayList<String> getTypes() {
	return prototype.getTypes();
    }

    @Override
    ArrayList<S> translate() {
	T trans = prototype.translate();
	if (trans != null && prototype.getClass().equals(trans.getClass())) {
	    return (ArrayList<S>) collection;
	}
	ArrayList<S> out = new ArrayList<>(collection.size());
	for (T t : collection) {
	    out.add((S) prototype.translate(t));
	}
	return out;
    }

    @Override
    SubRecord<ArrayList<S>> translate(ArrayList<S> in) {
	throw new UnsupportedOperationException("Not supported yet.");
    }
}
