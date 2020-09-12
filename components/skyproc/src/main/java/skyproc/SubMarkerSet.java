/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

class SubMarkerSet<T extends SubRecord> extends SubRecord {

    Map<String, T> set = new HashMap<>(2);
    ArrayList<String> markers;
    T prototype;
    boolean forceMarkers = false;
    static String loadedMarker;

    SubMarkerSet(T prototype) {
	super();
	this.prototype = prototype;
    }

    SubMarkerSet(T prototype, String... markers) {
	super();
	this.markers = new ArrayList<>(Arrays.asList(markers));
	this.prototype = prototype;
    }

    @Override
    void export(ModExporter out) throws IOException {
	for (String t : markers) {
	    if (set.containsKey(t)) {
		if (set.get(t).isValid()) {
		    SubData marker = new SubData(t);
		    marker.export(out);
		    set.get(t).export(out);
		    continue;
		}
	    }
	    if (forceMarkers) {
		SubData marker = new SubData(t);
		marker.export(out);
	    }
	}
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	String next = Record.getNextType(in);
	if (markers.contains(next)) {
            if (SPGlobal.logMods){
                logMod(srcMod, "", "Loaded Marker " + next);
            }
	    loadedMarker = next;
	} else {
	    if (!set.containsKey(loadedMarker)) {
		set.put(loadedMarker, (T) prototype.getNew(next));
	    }
	    set.get(loadedMarker).parseData(in, srcMod);
	}
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    SubRecord getNew(String t) {
	SubMarkerSet out = new SubMarkerSet(prototype);
	out.markers = markers;
	out.forceMarkers = forceMarkers;
	return out;
    }

    @Override
    int getContentLength(ModExporter out) {
	int len = 0;
	for (String t : markers) {
	    if (set.containsKey(t)) {
		if (set.get(t).isValid()) {
		    len += 6 + set.get(t).getTotalLength(out);
		    continue;
		}
	    }
	    if (forceMarkers) {
		len += 6;
	    }
	}
	return len;
    }

    public void clear() {
	set.clear();
    }

    public T get(String marker) {
	if (!set.containsKey(marker)) {
	    T newItem = (T) prototype.getNew();
	    set.put(marker, newItem);
	}
	return set.get(marker);
    }

    @Override
    boolean isValid() {
	return true;
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (T s : set.values()) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    ArrayList<String> getTypes() {
	ArrayList<String> out = new ArrayList<>(prototype.getTypes());
	out.addAll(markers);
	return out;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubMarkerSet<T> other = (SubMarkerSet<T>) obj;
	for (String marker : markers) {
	    if (!set.get(marker).equals(other.set.get(marker))) {
		return false;
	    }
	}
	return true;
    }
}
