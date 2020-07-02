/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import lev.LOutFile;
import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
class SubRecordsCopied extends SubRecords {

    SubRecords orig;

    SubRecordsCopied(SubRecords rhs) {
	super();
	orig = rhs;
    }

    @Override
    public Iterator<SubRecord> iterator() {
	ArrayList<SubRecord> iter = new ArrayList<>();
	for (String t : orig.getTopLevelTypes()) {
	    if (contains(t)) {
		iter.add(get(t));
	    }
	}
	return iter.iterator();
    }

    @Override
    protected void export(ModExporter out) throws IOException {
	for (SubRecord s : iteratorNoCopy()) {
	    s.export(out);
	}
    }

    @Override
    public int length(ModExporter out) {
	int length = 0;
	for (SubRecord s : iteratorNoCopy()) {
	    length += s.getTotalLength(out);
	}
	return length;
    }

    public ArrayList<SubRecord> iteratorNoCopy() {
	ArrayList<SubRecord> out = new ArrayList<>();
	for (String t : orig.getTopLevelTypes()) {
	    if (contains(t)) {
		SubRecord s;
		if (map.containsKey(t)) {
		    s = map.get(t);
		} else {
		    s = orig.get(t);
		}
		if (shouldExport(s)) {
		    out.add(s);
		}
	    }
	}
	return out;
    }

    @Override
    public boolean contains(String t) {
	return orig.contains(t) || map.containsKey(t);
    }

    @Override
    public SubRecord get(String in) {
	if (!map.containsKey(in)) {
	    SubRecord s = (SubRecord) Ln.deepCopy(orig.get(in));
	    for (String t : s.getTypes()) {
		map.put(t, s);
	    }
	}
	return map.get(in);
    }

    @Override
    public ArrayList<String> getTypes() {
	return orig.getTypes();
    }

    @Override
    public ArrayList<String> getTopLevelTypes() {
	return orig.getTopLevelTypes();
    }

    @Override
    public ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>();
	for (SubRecord s : iteratorNoCopy()) {
	    out.addAll(s.allFormIDs());
	}
	return out;
    }

    @Override
    public SubPrototype getPrototype() {
	return orig.getPrototype();
    }
}
