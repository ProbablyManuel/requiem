/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import lev.*;

/**
 *
 * @author Justin Swanson
 */
class Consistency {

    static ConsistencyVersion consistency = new ConsistencyV3();
    static String header = "Consistency";
    static private boolean cleaned = false;
    static boolean automaticExport = true;
    static boolean imported = false;
    static char[] badChars = {(char) 0x0D, (char) 0x0A};
    static String debugFolder = "Consistency/";

    static FormID getOldForm(String edid) {
	return consistency.getOldForm(edid);
    }

    static boolean requestID(FormID id) {
	return consistency.requestID(id);
    }

    static void cleanConsistency() {
	cleaned = true;
    }

    static boolean isCleaned() {
	return cleaned;
    }

    static void syncIDwithEDID(String edid, MajorRecord m) {
	FormID saved = Consistency.getOldForm(edid);
	if (saved != null) {
	    // If have an ID on record
	    if (SPGlobal.logging()) {
		SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Assigning FormID " + saved + " for EDID " + edid);
	    }
	    m.setForm(saved);
	} else if (m.getForm().isNull()) {
	    // If fresh record in need of new ID
	    FormID freshID = getNextID(m.srcMod);
	    Consistency.insert(edid, freshID);
	    m.setForm(freshID);
	    if (SPGlobal.logging()) {
		SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Assigning FRESH FormID " + freshID + " for EDID " + edid);
	    }
	}
    }

    static public FormID getNextID(Mod srcMod) {
	FormID possibleID = srcMod.getNextID();
	while (!Consistency.requestID(possibleID)) {
	    if (possibleID.isNull()) {
		if (!Consistency.cleaned) {
		    srcMod.resetNextIDcounter();
		    Consistency.cleanConsistency();
		    return getNextID(srcMod);
		} else {
		    SPGlobal.logError(srcMod.toString(), "Ran out of available formids.");
		    JOptionPane.showMessageDialog(null, "<html>The output patch ran out of available FormIDs.<br>"
			    + "Please contact Leviathan1753.</html>");
		    return null;
		}
	    }
	    possibleID = srcMod.getNextID();
	}
	return possibleID;
    }

    static public String edidFilter(String edid) {
	edid = edid.replaceAll(" ", "");
	for (char c : badChars) {
	    edid = edid.replaceAll(String.valueOf(c), "");
	}
	return edid;
    }

    static void getConsistencyFile() throws FileNotFoundException, IOException {
	consistency.getConsistencyFile();
    }

    static void clear() {
	imported = false;
	consistency.clear();
    }

    static void importConsistency(boolean globalOnly) {
	try {
	    if (SPGlobal.testing) {
		return;
	    }
	    consistency.importConsistency(globalOnly);
	    imported = true;
	} catch (Exception ex) {
	    SPGlobal.logException(ex);
	    JOptionPane.showMessageDialog(null, "<html>There was an error importing the consistency information.<br><br>"
		    + "This means your savegame has a good chance of having mismatched records.<br><br>"
		    + "It would be greatly appreciated if you sent the failed consistency file located in<br>"
		    + "'My Documents/My Games/Skyrim/Skyproc/' to Leviathan1753 for analysis."
		    + "<br><br> If this is the first time running the patch, please ignore this message.</html>");
	}
    }

    static boolean insert(String EDID, FormID id) {
	return consistency.insert(EDID, id);
    }

    static void export() throws IOException {
	consistency.export();
    }

    static boolean isImported() {
	return imported;
    }

    static enum LogTypes {

	CONSISTENCY,
	CONSISTENCY_IMPORT,;
    }

    abstract static class ConsistencyVersion {

	Set<FormID> set = new HashSet<>();
	LMergeMap<FormID, String> conflicts = new LMergeMap<>(false);

	abstract FormID getOldForm(String edid);

	boolean requestID(FormID id) {
	    return !set.contains(id);
	}

	abstract String getConsistencyFile() throws IOException;

	void clear() {
	    set.clear();
	}

	abstract boolean insert(String EDID, FormID id);

	boolean insert(String EDID, FormID id, Map<String, FormID> modEDIDlist) {

	    // If EDID is already logged, skip it.
	    if (!modEDIDlist.containsKey(EDID)) {
		if (set.contains(id)) {
		    // FormID conflict
		    String offendingEDID = "";
		    for (String item : modEDIDlist.keySet()) {
			if (modEDIDlist.get(item).equals(id)) {
			    offendingEDID = item;
			    break;
			}
		    }
		    conflicts.put(id, EDID);
		    conflicts.put(id, offendingEDID);
		    if (SPGlobal.logging()) {
			SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "!!!>>> Duplicate FormID warning.  ID: " + id + "  EDID: " + EDID + " and EDID2: " + offendingEDID);
			SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "!!!>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		    }
		}
		modEDIDlist.put(EDID, id);
		set.add(id);
		return true;
	    } else {
		return false;
	    }
	}

	abstract void export() throws IOException;

	abstract void remove(String edid);

	abstract boolean importConsistency(boolean globalOnly) throws IOException;

	void pruneConflicts() {
	    if (SPGlobal.logging()) {
		SPGlobal.newLog(debugFolder + "Conflict Pruning.txt");
		SPGlobal.log(header, "====================================");
		SPGlobal.log(header, "====== Pruning Conflicts ===========");
		SPGlobal.log(header, "====================================");
	    }
	    Mod global = SPGlobal.getGlobalPatch();
	    for (FormID id : conflicts.keySet()) {
		if (SPGlobal.logging()) {
		    SPGlobal.log(header, "------ Addressing " + id);
		}
		ArrayList<String> edidConflicts = conflicts.get(id);
		ArrayList<String> found = new ArrayList<>(2);
		for (String edid : edidConflicts) {
		    if (SPGlobal.logging()) {
			SPGlobal.log(header, "| For EDID: " + edid);
		    }
		    for (GRUP g : global.GRUPs.values()) {
			if (g.contains(edid)) {
			    found.add(edid);
			    if (SPGlobal.logging()) {
				SPGlobal.log(header, "|   Found!");
			    }
			    break;
			}
		    }
		}
		// If only one is being used, wipe the rest
		if (found.size() == 1) {
		    if (SPGlobal.logging()) {
			SPGlobal.log(header, "| Only one EDID is being used, pruning others.");
		    }
		    edidConflicts.remove(found.get(0));
		    for (String unusedEDID : edidConflicts) {
			consistency.remove(unusedEDID);
		    }
		}
		if (SPGlobal.logging()) {
		    SPGlobal.log(header, "-----------------------");
		}
	    }
	}
    }

    static class ConsistencyV2 extends ConsistencyVersion {

	Map<ModListing, Map<String, FormID>> storage = new HashMap<>();

	@Override
	FormID getOldForm(String edid) {
	    return storage.get(SPGlobal.getGlobalPatch().getInfo()).get(edid);
	}

	@Override
	String getConsistencyFile() throws FileNotFoundException, IOException {
	    File myDocs = SPGlobal.getSkyProcDocuments();
	    return myDocs.getPath() + "\\Consistency";
	}

	@Override
	void clear() {
	    super.clear();
	    storage.clear();
	}

	@Override
	boolean insert(String EDID, FormID id) {
	    Map<String, FormID> modEDIDlist = storage.get(id.master);
	    if (modEDIDlist == null) {
		modEDIDlist = new HashMap<>();
		storage.put(id.master, modEDIDlist);
	    }
	    return insert(EDID, id, modEDIDlist);
	}

	@Override
	void export() throws IOException {
	    if (SPGlobal.logging()) {
		SPGlobal.logMain(header, "Exporting Consistency file.");
	    }
	    if (SPGlobal.testing) {
		return;
	    }
	    pruneConflicts();
	    importConsistency(false);
	    try {
		getConsistencyFile();
		File tmp = new File(getConsistencyFile() + "Tmp");
		LOutChannel out = new LOutChannel(tmp);
		//Header
		out.write("SPC2");
		out.markLength(4);
		for (ModListing m : storage.keySet()) {
		    out.write(m.print().length(), 2);
		    out.write(m.print());
		    out.setPosMarker(4);
		}
		out.closeLength();
		for (ModListing m : storage.keySet()) {
		    out.fillPosMarker();
		    out.markLength(4);
		    Map<String, FormID> map = storage.get(m);
		    for (String edid : map.keySet()) {
			out.write(edid.length(), 2);
			out.write(edid);
			out.write(map.get(edid).getFormStr().substring(0, 6));
		    }
		    out.closeLength();
		}
		out.close();
		File f = new File(getConsistencyFile() + "V2");
		if (f.isFile()) {
		    f.delete();
		}
		tmp.renameTo(f);
		out.close();
	    } catch (IOException ex) {
		SPGlobal.logException(ex);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Error exporting Consistency file.");
		JOptionPane.showMessageDialog(null, "<html>There was an error exporting the consistency information.<br><br>"
			+ "This means your savegame has a good chance of having mismatched records the next<br>"
			+ "time you run the patcher.</html>");
	    }
	}

	@Override
	void remove(String edid) {
	    storage.get(SPGlobal.getGlobalPatch().getInfo()).remove(edid);
	}

	@Override
	boolean importConsistency(boolean globalOnly) throws IOException {
	    if (!imported) {
		storage.put(SPGlobal.getGlobalPatch().getInfo(), new HashMap<String, FormID>());
	    }
	    File f = new File(getConsistencyFile() + "V2");
	    if (!f.isFile()) {
		return false;
	    }
	    if (SPGlobal.logging()) {
		String name;
		if (globalOnly) {
		    name = debugFolder + "Import - V2 - Only Global.txt";
		} else {
		    name = debugFolder + "Import - V2 - Remaining.txt";
		}
		SPGlobal.newSpecialLog(LogTypes.CONSISTENCY_IMPORT, name);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "Importing v2 consistency file.");
	    }
	    LInChannel in = new LInChannel(f);
	    in.skip(4);
	    ModListing globalListing = SPGlobal.getGlobalPatch().getInfo();
	    Map<ModListing, Integer> headerMap = importHeader(in);
	    if (globalOnly) {
		Integer tmp = headerMap.get(globalListing);
		headerMap.clear();
		if (tmp != null) {
		    headerMap.put(globalListing, tmp);
		}
	    } else {
		headerMap.remove(globalListing);
	    }
	    for (ModListing m : headerMap.keySet()) {
		if (SPGlobal.logging()) {
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "===========================");
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "== Importing " + m);
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "===========================");
		}
		in.pos(headerMap.get(m));
		int length = in.extractInt(0, 4);
		LInChannel contentArr = new LInChannel(in, length);
		while (!contentArr.isDone()) {
		    int edidLen = in.extractInt(2);
		    String EDID = in.extractString(edidLen);
		    String FormStr = in.extractString(6);
		    FormID ID = new FormID(FormStr, m);
		    if (SPGlobal.logging()) {
			SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "  | EDID: " + EDID);
			SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "  | Form: " + FormStr);
			SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v2Import", "  |============================");
		    }
		    insert(EDID, ID);
		}
	    }
	    in.close();
	    v1import(globalOnly);
	    return true;
	}

	boolean v1import(boolean globalOnly) throws IOException {
	    File f = new File(getConsistencyFile());
	    if (!f.isFile()) {
		return false;
	    }
	    if (SPGlobal.logging()) {
		String name;
		if (globalOnly) {
		    name = debugFolder + "Import - V1 - Only Global.txt";
		} else {
		    name = debugFolder + "Import - V1 - Remaining.txt";
		}
		SPGlobal.newSpecialLog(LogTypes.CONSISTENCY_IMPORT, name);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v1Import", "Importing v1 consistency file.");
	    }
	    BufferedReader in = new BufferedReader(new FileReader(f));
	    while (in.ready()) {
		String EDID = in.readLine();
		String form = in.readLine();

		// Check to see if following line is actually a formid
		boolean fail = false;
		while (in.ready() && !form.toUpperCase().endsWith(".ESP") && !form.toUpperCase().endsWith(".ESM")) {
		    if (SPGlobal.logging()) {
			SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "v1Import", "  Read fail line: " + form);
		    }
		    form = in.readLine();
		    fail = true;
		}
		// If not, skip whole "pair"
		if (fail) {
		    continue;
		}

		FormID ID = new FormID(form);
		boolean isGlobal = ID.getMaster().equals(SPGlobal.getGlobalPatch().getInfo());
		if ((globalOnly && isGlobal) || (!globalOnly && !isGlobal)) {
		    if (insert(EDID, ID) && SPGlobal.logging()) {
			SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Consistency Import", "  Inserting " + form + " with " + EDID);
		    }
		}
	    }
	    in.close();
	    return true;
	}

	Map<ModListing, Integer> importHeader(LInChannel in) {
	    Map<ModListing, Integer> out = new HashMap<>();
	    int headerLength = in.extractInt(4);
	    LShrinkArray headerArr = new LShrinkArray(in.extract(headerLength));
	    while (!headerArr.isDone()) {
		int length = headerArr.extractInt(2);
		String name = headerArr.extractString(length);
		ModListing mod = new ModListing(name);
		Integer pos = headerArr.extractInt(0, 4);
		out.put(mod, pos);
	    }
	    return out;
	}
    }

    static class ConsistencyV3 extends ConsistencyVersion {

	Map<String, FormID> storage = new TreeMap<>();

	@Override
	FormID getOldForm(String edid) {
	    return storage.get(edid);
	}

	@Override
        String getConsistencyFile() throws IOException {
            return SPGlobal.getGlobalPatch().getName() + "_Consistency";
	}

	@Override
	boolean insert(String EDID, FormID id) {
	    return insert(EDID, id, storage);
	}

	@Override
	void export() throws IOException {
	    if (SPGlobal.logging()) {
		SPGlobal.logMain(header, "Exporting Consistency file.");
	    }
	    if (SPGlobal.testing) {
		return;
	    }
	    pruneConflicts();
	    try {
		getConsistencyFile();
		File tmp = new File(getConsistencyFile() + "Tmp");
		LOutChannel out = new LOutChannel(tmp);
		//Header
		out.write("SPC3");
		for (String edid : storage.keySet()) {
		    out.write(edid.length(), 2);
		    out.write(edid);
		    out.write(storage.get(edid).getInternal(true));
		}
		out.close();
		File f = new File(getConsistencyFile());
		if (f.isFile()) {
		    f.delete();
		}
		tmp.renameTo(f);
		out.close();
	    } catch (IOException ex) {
		SPGlobal.logException(ex);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY, header, "Error exporting Consistency file.");
		JOptionPane.showMessageDialog(null, "<html>There was an error exporting the consistency information.<br><br>"
			+ "This means your savegame has a good chance of having mismatched records the next<br>"
			+ "time you run the patcher.</html>");
	    }
	}

	@Override
	void remove(String edid) {
	    storage.remove(edid);
	}

	@Override
	boolean importConsistency(boolean globalOnly) throws IOException {
	    File f = new File(getConsistencyFile());
	    if (!f.isFile()) {
		upgrade();
		return false;
	    }
	    if (SPGlobal.logging()) {
		String name = debugFolder + "Import - V3.txt";
		SPGlobal.newSpecialLog(LogTypes.CONSISTENCY_IMPORT, name);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Import", "Importing v3 consistency file.");
	    }
	    Mod global = SPGlobal.getGlobalPatch();
	    LInChannel in = new LInChannel(f);
	    in.skip(4);
	    LShrinkArray arr = new LShrinkArray(in.extractAllBytes());
	    while (!arr.isDone()) {
		int edidLen = arr.extractInt(2);
		String EDID = arr.extractString(edidLen);
		byte[] bytes = arr.extract(4);
		FormID ID = new FormID();
		ID.setInternal(bytes, global);
		if (SPGlobal.logging()) {
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Import", "  | EDID: " + EDID);
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Import", "  | Form: " + ID);
		    SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Import", "  |============================");
		}
		insert(EDID, ID);
	    }
	    in.close();
	    upgrade();
	    return true;
	}

	void upgrade() throws IOException {
	    ConsistencyV2 v2 = new ConsistencyV2();
	    v2.importConsistency(true);
	    Map<String, FormID> map = v2.storage.get(SPGlobal.getGlobalPatch().getInfo());
	    if (SPGlobal.logging()) {
		String name = debugFolder + "Import - V3.txt";
		SPGlobal.newSpecialLog(LogTypes.CONSISTENCY_IMPORT, name);
		SPGlobal.logSpecial(LogTypes.CONSISTENCY_IMPORT, "Import", "Upgrading from v2 consistency.");
	    }
	    for (Entry<String, FormID> entry : map.entrySet()) {
		insert(entry.getKey(), entry.getValue());
	    }
	}
    }
}
