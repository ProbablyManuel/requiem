/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import lev.Ln;
import lev.gui.LSaveFile;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPPopups;
import skyproc.gui.SUM;
import skyproc.gui.SUMprogram;

/**
 *
 * @author Justin Swanson
 */
public class SUMMergerProgram implements SUM {

    @Override
    public String getName() {
	return "SUM Merger";
    }

    @Override
    public GRUP_TYPE[] dangerousRecordReport() {
	return new GRUP_TYPE[0];
    }

    @Override
    public GRUP_TYPE[] importRequests() {
	return new GRUP_TYPE[0];
    }

    @Override
    public boolean importAtStart() {
	return false;
    }

    @Override
    public boolean hasStandardMenu() {
	return false;
    }

    @Override
    public SPMainMenuPanel getStandardMenu() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCustomMenu() {
	return false;
    }

    @Override
    public JFrame openCustomMenu() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasLogo() {
	return true;
    }

    @Override
    public URL getLogo() {
	return SUMprogram.class.getResource("SUMprogram.png");
    }

    @Override
    public boolean hasSave() {
	return false;
    }

    @Override
    public LSaveFile getSave() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getVersion() {
	return "1.0";
    }

    @Override
    public ModListing getListing() {
	return new ModListing("SUM.esp");
    }

    @Override
    public Mod getExportPatch() {
	Mod patch = new Mod(getListing());
	patch.setFlag(Mod.Mod_Flags.STRING_TABLED, false);
	patch.setAuthor("Leviathan1753 and friends");
	return patch;
    }

    @Override
    public Color getHeaderColor() {
	return Color.BLACK;
    }

    @Override
    public boolean needsPatching() {
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String description() {
	return "Merges SkyProc patchers into one patch.";
    }

    @Override
    public ArrayList<ModListing> requiredMods() {
	return new ArrayList<>(0);
    }

    @Override
    public void onStart() throws Exception {
	SPGlobal.streamMode = false;
	SPGlobal.mergeMode = true;
    }

    @Override
    public void onExit(boolean patchWasGenerated) throws Exception {
    }

    @Override
    public void runChangesToPatch() throws Exception {
        SPGlobal.setAllModsAsMasters(false);
	Mod patch = SPGlobal.getGlobalPatch();

	// Get SUM patch names
	ArrayList<String> SUMpatchStrings = Ln.loadFileToStrings(SUMprogram.getSUMPatchList(), false);
	ArrayList<ModListing> SUMpatches = new ArrayList<>(SUMpatchStrings.size());
	for (String modStr : SUMpatchStrings) {
	    SUMpatches.add(new ModListing(modStr));
	}

	// Import SUM patches
	SPGlobal.checkMissingMasters = false;
	SPImporter.importMods(SUMpatches);
	Mod source = new Mod("MergerTemporary", false);
	source.addAsOverrides(SPGlobal.getDB());

	Map<FormID, FormID> origToNew = new HashMap<>();
	for (GRUP<MajorRecord> g : source) {
	    for (MajorRecord m : g.getRecords()) {
		if (SUMpatches.contains(m.getFormMaster())) {
		    // If record was created by SkyProc, make it originate from merger
		    MajorRecord copy = patch.makeCopy(m, m.getEDID());
		    origToNew.put(m.getForm(), copy.getForm());
		} else {
		    // If non-skyproc override record, just add it
		    patch.addRecord(m);
		}
	    }
	}

	// Replace all formID references to new IDs
	for (GRUP<MajorRecord> g : patch) {
	    for (MajorRecord m : g.getRecords()) {
		ArrayList<FormID> allFormIDs = m.allFormIDs();
		for (FormID id : allFormIDs) {
		    FormID newID = origToNew.get(id);
		    if (newID != null) {
			id.form = newID.form;
			id.master = newID.master;
		    }
		}
	    }
	}

	// Remove SkyProc patches from master list and delete their files
	ArrayList<Mod> add = new ArrayList<>(1);
	add.add(SPGlobal.getGlobalPatch());
	ArrayList<Mod> remove = new ArrayList<>();
	for (Mod m : SPGlobal.getDB()) {
	    patch.tes.getMasters().remove(m.getInfo());
	    File modFile = new File(SPGlobal.pathToData + m.getName());
	    if (modFile.exists()) {
		modFile.delete();
	    }
	    remove.add(m);
	}
	remove.remove(SPGlobal.getGlobalPatch());
	NiftyFunc.modifyPluginsTxt(add, remove);
    }

    @Override
    public boolean hasExceptionManager() {
        return false;
    }

    @Override
    public SPExeceptionHandler getExceptionManager() {
        throw new UnsupportedOperationException("Uses default class.");
    }

}
