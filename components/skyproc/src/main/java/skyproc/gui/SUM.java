/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import lev.gui.LSaveFile;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.ModListing;
import skyproc.SPExeceptionHandler;

/**
 * Interface that allows your SkyProc program to be automatically detected and loaded into SkyProc Unified Manager.
 * @author Justin Swanson
 */
public interface SUM {
    /**
     *
     * @return Name of the project/mod.
     */
    public String getName();
    /**
     * One of the major "danger zones" of SkyProc is the potential for compounding record duplications.<br><br>
     *
     * Please export an array of any record types that you duplicate and create an unbounded large number of.  This will be
     * used to quickly identify potential issues cross-SkyProc patchers.
     *
     * An example of the danger:<br>
     * A mod might duplicate each weapon in a load order for each enchantment, making (# of weapons X # of enchantments) new weapon records.<br>
     * This can add up to be a huge number of new records for the single patcher alone.  If you combine these new records with another SkyProc patcher
     * that duplicates weapons exponentially as well, then an absurdly large number of records could be created.
     * @return Array of the types that this patcher will be creating a large amount of new records of.
     */
    public GRUP_TYPE[] dangerousRecordReport();
    /**
     *
     * @return An array of the types of records that this patcher would like to import and have access to.
     */
    public GRUP_TYPE[] importRequests();
    /**
     *
     * @return Whether the standard GUI should start importing the mods as soon as it opens.
     */
    public boolean importAtStart();
    /**
     *
     * @return True if your patcher has a SPMainMenuPanel for a SUMGUI
     */
    public boolean hasStandardMenu();
    /**
     * Return a SPMainMenuPanel that has been customized with your patcher's contents.<br>
     * Customizing constitutes first creating your own SPSettingPanels extending classes in your project.<br>
     * In this function, you then SPMainMenuPanel.addMenu() on each of your SPSettingPanels you created.
     *
     * @see SPMainMenuPanel
     * @return A SPMainMenuPanel customized with your patcher's contents.
     */
    public SPMainMenuPanel getStandardMenu();
    /**
     * True if you have a custom made GUI.<br>
     * NOTE: The SPDefaultGUI counts as a custom GUI.
     * @return True if you have a custom made GUI.
     */
    public boolean hasCustomMenu();
    /**
     * Opens and displays the custom menu of your patcher and returns it.<br>
     * If you do not have a custom menu, simply put this line (after making sure hasCustomMenu() returns false):<br>
     * <i>throw new UnsupportedOperationException("Not supported yet.");</i>
     * @return
     */
    public JFrame openCustomMenu();
    /**
     *
     * @return True if you have a logo for your patcher.
     */
    public boolean hasLogo();
    /**
     * Returns a URL to the patcher's logo image.  <br><br>
     * An example line from Automatic Variants is:<br>
     * <i>return SettingsOther.class.getResource("AutoVarGUITitle.png");</i><br>
     * SettingsOther is simply a class that is in the same package as the png.
     * It can be any class.<br><br>
     * If you do not have a logo, simply put this line (after making sure hasLogo() returns false):<br>
     * <i>throw new UnsupportedOperationException("Not supported yet.");</i>
     *
     * @return URL to your patcher's logo.
     */
    public URL getLogo();

    /** Does this patcher supply a custom ExceptionHandler?
     *
     * @return True if it does
     */
    public boolean hasExceptionManager();
    
    /**If hasExceptionManager returns true, this function must return the 
     * exception manager SkyProc should use to handle any exception encountered
     * along the way.
     *
     * @return an instance of a class implementing SPExeceptionHandler
     */
    public SPExeceptionHandler getExceptionManager();
    
    /**
     * True if you have a savefile.<br>
     * You can create your own savefile and specify the settings in it by extending
     * the LSaveFile class.
     * @see LSaveFile
     * @return
     */
    public boolean hasSave();
    /**
     *
     * @return The singleton instance of your custom LSaveFile.
     */
    public LSaveFile getSave();
    /**
     *
     * @return The string representation of the current version of your patcher.
     */
    public String getVersion();
    /**
     *
     * @return The modlisting used for your export patch.
     */
    public ModListing getListing();
    /**
     * Create a new mod that will be your export patch and return it.  This includes
     * customizing the header flags as desired.
     * @return
     */
    public Mod getExportPatch();
    /**
     *
     * @return The preferred header color for your mod.
     */
    public Color getHeaderColor();
    /**
     * Custom code to determine if a patch is needed (in addition to the normal SUM patch needed rules).
     * @return Whether or not your program requires a patch.
     */
    public boolean needsPatching();
    /**
     *
     * @return A description of your patcher for display in SUM.
     */
    public String description();
    /**
     *
     * @return A list of ModListings of mods required to be present in order to
     * patch.  Program will stop and display error if any are missing.
     */
    public ArrayList<ModListing> requiredMods();
    /**
     * Code to run before GUI displays.  This code runs AFTER your save is loaded.
     * @throws Exception
     */
    public void onStart () throws Exception;
    /**
     * Code to run before program closes.
     * @param patchWasGenerated True if a patch was generated before calling this function.
     * @throws Exception
     */
    public void onExit(boolean patchWasGenerated) throws Exception;
    /**
     * This function should start the processing code of your patcher.<br>
     * Assume:<br>
     * 1. Mods have already been imported. Do not reimport the load order here.<br>
     * 2. ALWAYS reference SPGlobal.getGlobalPatch() when referring to the export patch.  Otherwise
     * SUM will not play nice with it.  In earlier portions of your code (in main() perhaps) you should set the global
     * patch to be your export patch.<br>
     * 3. Do not export the patch in this function.  Just process and add the desired records to the global patch.<br><br>
     *
     * To run your program you then do the following in your main() function:<br>
     * 1. Import desired mods.<br>
     * 2. Call this function.<br>
     * 3. Export the patch.<br><br>
     * Everything that you want done aside from importing/exporting needs to be inside this function or SUM won't do it.
     *
     * @throws Exception
     */
    public void runChangesToPatch() throws Exception ;
}
