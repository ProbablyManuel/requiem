/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An arrayList that holds LMergable objects that have a special
 * merge function, that allows for adding the list without replacing the old.
 * @param <T> 
 * @author Justin Swanson
 */
public class LMergeList<T extends LMergable> implements Iterable<T> {

    ArrayList<T> list = new ArrayList<T>();

    /**
     * Adds element to the mergelist.  If element exists, then it will
     * be merged in with that element.
     * @param in
     */
    public void add(T in) {
        int index;
        if ((index = list.indexOf(in)) != -1) {
            list.get(index).mergeIn(in);
        } else {
            list.add(in);
        }
    }

    /**
     * 
     * @param in
     * @return
     */
    public boolean contains(T in) {
        return list.contains(in);
    }

    /**
     * 
     * @param in
     */
    public void remove(T in) {
        list.remove(in);
    }

    /**
     * 
     * @return
     */
    public int size () {
        return list.size();
    }

    /**
     * 
     * @param i
     * @return
     */
    public T get (int i) {
        return list.get(i);
    }

    /**
     * 
     * @return
     */
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

}
