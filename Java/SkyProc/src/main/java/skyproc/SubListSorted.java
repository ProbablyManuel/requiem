/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 *
 * @author Justin Swanson
 */
class SubListSorted<S extends SubRecord<T>, T> extends SubList<S, T> {

    TreeSet<T> sorter = new TreeSet<>();

    SubListSorted(S prototype_) {
        super(prototype_);
    }

    SubListSorted(SubListSorted rhs) {
	super(rhs);
	sorter.addAll(rhs.sorter);
    }

    @Override
    SubRecord getNew(String type) {
	return new SubListSorted(this);
    }

    @Override
    public T get(int i) {
        return (T) sorter.toArray()[i];
    }

    @Override
    public boolean add(T item) {
        sorter.add(item);
	return super.add(item);
    }

    @Override
    public boolean remove(T item) {
        sorter.remove(item);
        return super.remove(item);
    }

    @Override
    public void remove(int i) {
        sorter.remove(collection.get(i));
        super.remove(i);
    }

    @Override
    public void clear() {
        super.clear();
        sorter.clear();
    }

    @Override
    public void setRecordsTo(ArrayList<T> in) {
        super.setRecordsTo(in);
        sorter.clear();
        sorter.addAll(in);
    }

    @Override
    ArrayList<S> translate() {
	ArrayList<S> out = new ArrayList<>(sorter.size());
	for (T t : sorter) {
	    out.add((S) prototype.translate(t));
	}
	return out;
    }

    public Iterator<S> unsortedIterator() {
	return super.translate().iterator();
    }
}
