/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import lev.gui.LSaveFile;

/**
 *
 * @author Justin Swanson
 */
public abstract class SkyProcSave extends LSaveFile {
    /**
     *
     */
    public SkyProcSave () {
	super(SPGlobal.pathToInternalFiles + "/Savefile");
    }
}
