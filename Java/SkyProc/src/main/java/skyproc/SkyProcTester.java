/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import lev.Ln;
import lev.debug.LDebug;
import skyproc.exceptions.BadMod;
import skyproc.exceptions.BadRecord;
import skyproc.gui.SPDefaultGUI;
import skyproc.gui.SPProgressBarPlug;

/**
 *
 * @author Justin Swanson
 */
public class SkyProcTester {

    static ArrayList<FormID> badIDs;
//    static GRUP_TYPE[] types = {GRUP_TYPE.DIAL};
    static GRUP_TYPE[] types = GRUP_TYPE.values();
    static boolean streaming = false;
    static ArrayList<GRUP_TYPE> skip = new ArrayList<>(Arrays.asList(new GRUP_TYPE[]{GRUP_TYPE.BOOK}));

    /**
     * @param test
     */
    public static void runTests(int test) {
	setSkyProcGlobal();
	badIDs = new ArrayList<>();
	ModListing skyrim = new ModListing("Skyrim.esm");
	badIDs.add(new FormID("018A45", skyrim));  //RiverwoodZone
	badIDs.add(new FormID("00001E", skyrim));  //NoZoneZone
	SPGlobal.testing = true;
	SPDefaultGUI gui = new SPDefaultGUI("Tester Program", "A tester program meant to flex SkyProc.");
	try {
	    switch (test) {
		case 1:
		    validateAll();
		    break;
		case 2:
		    importTest();
		    break;
		case 3:
		    copyTest();
		    break;
	    }
	    gui.finished();
	} catch (Exception e) {
	    System.out.println("EXCEPTION THROWN");
	    gui.finished();
	    SPGlobal.logException(e);
	}
	LDebug.wrapUp();
    }

    private static class ModTestPackage 
    {
        ModListing main;
        ModListing[] importList;
        public ModTestPackage(String main, String ... list)
        {
            this.main = new ModListing(main);
            importList = new ModListing[list.length];
            for (int i = 0 ; i < list.length ; i++)
            {
                importList[i] = new ModListing(list[i]);
            }
        }
    }
    
    private static void validateAll() throws Exception {
	ModTestPackage[] mods = {
            new ModTestPackage("Skyrim.esm", "Skyrim.esm", "Update.esm"),
            new ModTestPackage("Dawnguard.esm", "Skyrim.esm", "Update.esm", "Dawnguard.esm"),
            new ModTestPackage("Dragonborn.esm", "Skyrim.esm", "Update.esm", "Dragonborn.esm")
	};
	SPGlobal.checkMissingMasters = false;
	for (ModTestPackage p : mods) {
	    if (!validate(p)) {
		break;
	    }
	}
	System.out.println("TESTING COMPLETE");
    }

    private static boolean validate(ModTestPackage p) throws Exception {

	SubStringPointer.shortNull = false;

	FormID.allIDs.clear();

	SPProgressBarPlug.reset();
	SPProgressBarPlug.setMax(types.length);

	boolean exportPass = true;
	boolean idPass = true;
	for (GRUP_TYPE g : types) {
	    if (!GRUP_TYPE.unfinished(g) && !GRUP_TYPE.internal(g) && !skip.contains(g)) {
                for(ModListing m : p.importList)
                {
                    SPImporter.importMod(m, SPGlobal.pathToData, g);
                }
		if (!test(g, p)) {
		    SPProgressBarPlug.setStatus("FAILED: " + g);
		    exportPass = false;
		    break;
		}
		SPProgressBarPlug.setStatus("Validating DONE");
		for (FormID id : FormID.allIDs) {
		    if (!id.isNull() && id.getMaster() == null && !badIDs.contains(id)) {
			System.out.println("A bad id: " + id);
			System.out.println("Some FormIDs were unstandardized!!");
			return false;
		    }
		}
		SPGlobal.reset();
	    }
	}

	SPGlobal.reset();
	return exportPass && idPass;
    }

    private static boolean test(GRUP_TYPE type, ModTestPackage p) throws IOException, BadRecord, BadMod {
	System.out.println("Testing " + type + " in " + p.main);
	SPProgressBarPlug.setStatus("Validating " + type);
	SPProgressBarPlug.pause(true);

	boolean passed = true;
	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.addAsOverrides(SPGlobal.getDB().getMod(p.main), type);
	// Test to see if stream has been prematurely imported
	if (SPGlobal.streamMode && type != GRUP_TYPE.NPC_) {
	    GRUP g = patch.GRUPs.get(type);
	    if (!g.listRecords.isEmpty()) {
		MajorRecord m = (MajorRecord) g.listRecords.get(0);
		if (m.subRecords.map.size() > 2) {
		    System.out.println("Premature streaming occured: " + m);
		    return false;
		}
	    }
	}
	// Remove known bad ids
	for (FormID f : badIDs) {
	    patch.remove(f);
	}
	patch.setAuthor("Leviathan1753");
        for (ModListing depend : p.importList)
        {
            patch.addMaster(depend);
        }
	try {
	    patch.export(new File(SPGlobal.pathToData + patch.getName()));
	} catch (BadRecord ex) {
	    SPGlobal.logException(ex);
	    System.out.println("Record Lengths were off.");
	}
	passed = passed && NiftyFunc.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);
	File validF = new File("Validation Files/" + type.toString() + "_" + p.main.printNoSuffix() + ".esp");
	if (validF.isFile()) {
	    passed = Ln.validateCompare(SPGlobal.pathToData + "Test.esp", validF.getPath(), 10) && passed;
	} else {
	    System.out.println("Didn't have a source file to validate bytes to.");
	}

	SPProgressBarPlug.pause(false);
	SPProgressBarPlug.incrementBar();
	return passed;
    }

    private static boolean copyTest() throws IOException, BadRecord {
	SPProgressBarPlug.pause(true);

	boolean passed = true;
	Mod merger = new Mod(new ModListing("tmpMerge.esp"));
	merger.addAsOverrides(SPGlobal.getDB());
	for (FormID f : badIDs) {
	    merger.remove(f);
	}

	Mod patch = new Mod(new ModListing("Test.esp"));
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.setAuthor("Leviathan1753");

	for (GRUP g : merger) {
	    for (Object o : g) {
		MajorRecord m = (MajorRecord) o;
		m.copyOf(patch);
	    }
	}

	patch.export(new File(SPGlobal.pathToData + patch.getName()));
	passed = passed && NiftyFunc.validateRecordLengths(SPGlobal.pathToData + "Test.esp", 10);

	SPProgressBarPlug.pause(false);
	SPProgressBarPlug.incrementBar();
	return passed;
    }

    /**
     *
     */
    public static void importTest() {
	try {
	    SPImporter importer = new SPImporter();
	    importer.importActiveMods();
	    Mod patch = new Mod(new ModListing("Test.esp"));
	    patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	    patch.addAsOverrides(SPGlobal.getDB());
	    patch.allFormIDs();
	} catch (Exception e) {
	    SPGlobal.logException(e);
	}
    }

    /**
     *
     */
    public static void parseEmbeddedScripts() {
	try {
	    EmbeddedScripts.generateEnums();
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }

    private static void setSkyProcGlobal() {
	SPGlobal.createGlobalLog();
	LDebug.timeElapsed = true;
	SPGlobal.streamMode = streaming;
	SPGlobal.logging(true);
	SPGlobal.setGlobalPatch(new Mod(new ModListing("Test", false)));
    }
}
