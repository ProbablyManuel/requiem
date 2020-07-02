/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import lev.LOutFile;

/**
 *
 * @author Justin Swanson
 */
class ModExporter extends LOutFile {

    private Mod exportMod;
    private Mod srcMod;
    private MajorRecord srcMajor;

    ModExporter (File path, Mod mod) throws FileNotFoundException {
	super(path);
	exportMod = mod;
    }

    public Mod getExportMod() {
	return exportMod;
    }

    public void setSourceMod (Mod srcMod) {
	this.srcMod = srcMod;
    }

    public Mod getSourceMod () {
	return srcMod;
    }

    public void setSourceMajor(MajorRecord src) {
	srcMajor = src;
    }

    public MajorRecord getSourceMajor() {
	return srcMajor;
    }
}
