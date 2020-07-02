/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

import java.util.ArrayList;
import skyproc.ModListing;

/**
 *
 * @author Justin Swanson
 */
public class MissingMaster extends Exception {

    public final ArrayList<ModListing> notinstalled;
    public final ArrayList<ModListing> inactive;
    public final ArrayList<ModListing> loadedafter;
    public final ModListing failedmod;

    /**Create a new Missing master exception
     *
     * @param message short summary of what went wrong
     * @param mod the mod with the missing masters
     * @param notinstalled all required mods that are not installed at all
     * @param inactive required mods that are installed but not in the loadorder
     * @param loadedafter required mods loaded after their dependency
     */
    public MissingMaster(String message, ModListing mod,
            ArrayList<ModListing> notinstalled, ArrayList<ModListing> inactive,
            ArrayList<ModListing> loadedafter) {
        super(message);
        this.notinstalled = notinstalled;
        this.inactive = inactive;
        this.loadedafter = loadedafter;
        this.failedmod = mod;
    }
}
