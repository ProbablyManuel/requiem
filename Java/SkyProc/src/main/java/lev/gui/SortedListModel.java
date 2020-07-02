/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.*;
import javax.swing.AbstractListModel;

/**
 *
 * @param <T>
 * @author Justin Swanson
 */
public class SortedListModel<T extends Object> extends AbstractListModel {

    // Define a SortedSet
    /**
     *
     */
    protected SortedSet<T> model;

    /**
     *
     */
    public SortedListModel() {
	// Create a TreeSet
	// Store it in SortedSet variable
	model = new TreeSet<>();
    }

    // ListModel methods
    @Override
    public int getSize() {
	// Return the model size
	return model.size();
    }

    @Override
    public T getElementAt(int index) {
	// Return the appropriate element
	return (T) model.toArray()[index];
    }

    // Other methods
    /**
     *
     * @param element
     */
    public void add(T element) {
	if (model.add(element)) {
	    fireContentsChanged(this, 0, getSize());
	}
    }

    /**
     *
     * @param elements
     */
    public void addAll(T elements[]) {
	Collection c = Arrays.asList(elements);
	model.addAll(c);
	fireContentsChanged(this, 0, getSize());
    }

    /**
     *
     */
    public void clear() {
	model.clear();
	fireContentsChanged(this, 0, getSize());
    }

    /**
     *
     * @param element
     * @return
     */
    public boolean contains(T element) {
	return model.contains(element);
    }

    /**
     *
     * @return
     */
    public T firstElement() {
	// Return the appropriate element
	return model.first();
    }

    /**
     *
     * @return
     */
    public Iterator iterator() {
	return model.iterator();
    }

    /**
     *
     * @return
     */
    public T lastElement() {
	// Return the appropriate element
	return model.last();
    }

    /**
     *
     * @param element
     * @return
     */
    public boolean removeElement(T element) {
	boolean removed = model.remove(element);
	if (removed) {
	    fireContentsChanged(this, 0, getSize());
	}
	return removed;
    }
}