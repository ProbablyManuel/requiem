/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author Justin Swanson
 */
abstract class SubPrototype implements Serializable {

    protected ArrayList<String> listExport = new ArrayList<>();
    protected Map<String, SubRecord> map = new HashMap<>(0);
    protected ArrayList<String> listExtensive = new ArrayList<>();
    protected Set<String> forceExport = new HashSet<>(0);

    public SubPrototype() {
	addRecords();
    }

    protected abstract void addRecords();

    public SubPrototype(SubPrototype in) {
	mergeIn(in);
	addRecords();
    }

    public void mergeIn(SubPrototype in) {
	for (String t : in.listExport) {
	    add(in.get(t));
	}
	forceExport.addAll(in.forceExport);
    }

    public final SubRecord add(SubRecord r) {
	for (String t : r.getTypes()) {
	    remove(t);
	    map.put(t, r);
	    listExtensive.add(t);
	}
	listExport.add(r.getType());
	return r;
    }

    public void before(SubRecord r, String type) {
	add(r);
	listExport.remove(r.getType());
	listExport.add(listExport.indexOf(type), r.getType());
    }

    public void after(SubRecord r, String type) {
	add(r);
	listExport.remove(r.getType());
	listExport.add(listExport.indexOf(type) + 1, r.getType());
    }

    public void reposition(String type) {
	type = get(type).getType();
	listExport.remove(type);
	listExport.add(type);
    }

    public void forceExport(String type) {
	forceExport.add(type);
    }

    public void remove(String type) {
	if (map.containsKey(type)) {
	    for (int i = 0; i < listExport.size(); i++) {
		if (listExport.get(i).equals(type)) {
		    listExport.remove(i);
		    break;
		}
	    }
	    for (int i = 0; i < listExtensive.size(); i++) {
		if (listExtensive.get(i).equals(type)) {
		    listExtensive.remove(i);
		    break;
		}
	    }
	    map.remove(type);
	    forceExport.remove(type);
	}
    }

    public boolean contains(String type) {
	return map.containsKey(type);
    }

    public SubRecord get(String type) {
	return map.get(type);
    }
}
