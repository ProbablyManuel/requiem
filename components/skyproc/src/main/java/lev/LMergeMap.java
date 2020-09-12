/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.util.*;
import java.util.Map.Entry;

/**
 * A map which has values which are ArrayLists of type V.
 * @param <K>
 * @param <V>
 * @author Justin Swanson
 */
public class LMergeMap<K, V> implements Iterable<V> {

    Map<K, ArrayList<V>> map;
    boolean sorted;
    boolean unique;
    
    /**
     *
     * @param sorted Whether to use TreeMap as the container.
     */
    public LMergeMap(Boolean sorted) {
	this(sorted, true);
    }

    /**
     *
     * @param sorted Whether to use TreeMap as the container.
     * @param unique Whether to check for object uniqueness before adding to the list.
     */
    public LMergeMap(Boolean sorted, Boolean unique) {
	if (sorted) {
	    map = new TreeMap<K, ArrayList<V>>();
	} else {
	    map = new HashMap<K, ArrayList<V>>();
	}
	this.sorted = sorted;
	this.unique = unique;
    }

    /**
     *
     */
    public void clear() {
	map.clear();
    }

    /**
     *
     * @param in
     */
    public void addAll (LMergeMap<K,V> in) {
	for (K k : in.keySet()) {
	    map.put(k, in.get(k));
	}
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean containsKey(K key) {
	return map.containsKey(key);
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean containsValue(V value) {
	for (ArrayList<V> vals : map.values()) {
	    for (V v : vals) {
		if (v.equals(value)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     *
     * @return
     */
    public Set<Entry<K, ArrayList<V>>> entrySet() {
	return map.entrySet();
    }

    /**
     *
     * @param key
     * @return
     */
    public ArrayList<V> get(K key) {
	return map.get(key);
    }

    /**
     *
     * @return
     */
    public int hashcode() {
	return map.hashCode();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
	return map.isEmpty();
    }

    /**
     *
     * @return
     */
    public Set<K> keySet() {
	return map.keySet();
    }

    /**
     *
     * @param key
     * @param value
     */
    public void put(K key, V value) {
	if (!map.containsKey(key)) {
	    map.put(key, new ArrayList<V>());
	}
	if (!unique || !map.get(key).contains(value)) {
	    map.get(key).add(value);
	}
    }

    /**
     *
     * @param key
     * @param value
     */
    public void put(K key, ArrayList<V> value) {
	if (!map.containsKey(key)) {
	    map.put(key, value);
	} else {
	    for (V v : value) {
		put(key, v);
	    }
	}
    }

    /**
     *
     * @param key
     */
    public void remove(K key) {
	if (map.containsKey(key)) {
	    map.get(key).clear();
	    map.remove(key);
	}
    }

    /**
     *
     * @return
     */
    public int size() {
	return map.size();
    }

    /**
     *
     * @return Number of real values in the MergeMap; Not the number of Key<>ArrayList combinations
     */
    public int numVals() {
	int sum = 0;
	for (ArrayList<V> vals : map.values()) {
	    sum += vals.size();
	}
	return sum;
    }

    /**
     *
     * @return
     */
    public Collection<ArrayList<V>> values() {
	return map.values();
    }

    /**
     *
     * @return All values in the mergemap.
     */
    public ArrayList<V> valuesFlat() {
	ArrayList<V> out = new ArrayList<>();
	for (ArrayList<V> vals : map.values()) {
	    out.addAll(vals);
	}
	return out;
    }

    /**
     *
     * @return A map with the first value of each key.
     */
    public Map<K,V> flatten() {
	Map<K,V> out;
	if (sorted) {
	    out = new TreeMap<K,V>();
	} else {
	    out = new HashMap<K,V>();
	}
	for (K key : map.keySet()) {
	    ArrayList<V> val = map.get(key);
	    if (val.size() > 0) {
		out.put(key, val.get(0));
	    }
	}
	return out;
    }

    /**
     * Flips the mergemap so keys become values.
     * @return
     */
    public LMergeMap<V, K> flip() {
	LMergeMap<V, K> flip = new LMergeMap<V, K>(sorted, unique);
	for (K key : map.keySet()) {
	    for (V val : map.get(key)) {
		flip.put(val, key);
	    }
	}
	return flip;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> print() {
	ArrayList<String> out = new ArrayList<>();
	for (K key : map.keySet()) {
	    out.add(key.toString());
	    for (V vals : get(key)) {
		out.add("   " + vals.toString());
	    }
	}
	return out;
    }

    @Override
    public Iterator<V> iterator() {
	return valuesFlat().iterator();
    }

}
