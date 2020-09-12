/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reqtificator;

import Reqtificator.logging_and_gui.TextManager;
import skyproc.SkyProcSave;

/**
 *
 * @author Justin Swanson
 */
public class YourSaveFile extends SkyProcSave {
    private final TextManager texts;

    public YourSaveFile(TextManager texts) {
        this.texts = texts;
    }

    @Override
    protected void initSettings() {
	//  The Setting,	    The default value,	    Whether or not it changing means a new patch should be made
	    Add(Settings.LCHAR_MERGE, false, true);
        Add(Settings.LITEM_MERGE, false, true);
        Add(Settings.ADDALLMASTERS, true, true);
        Add(Settings.OpenEncounterZones, true, true);
        Add(Settings.LOG_DEBUG, false, true);
    }

    @Override
    protected void initHelp() {
        helpInfo.put(Settings.LCHAR_MERGE, texts.format("gui.settings.info_lchar_merge"));
        helpInfo.put(Settings.LITEM_MERGE, texts.format("gui.settings.info_litem_merge"));
        helpInfo.put(Settings.ADDALLMASTERS, texts.format("gui.settings.info_all_masters"));
        helpInfo.put(Settings.OpenEncounterZones, texts.format("gui.settings.info_open_encounter_zones"));
        helpInfo.put(Settings.LOG_DEBUG, texts.format("gui.settings.info_debug_logging"));
        helpInfo.put(Settings.OTHER_SETTINGS, texts.format("gui.settings.info_other_settings"));
    }

    // Note that some settings just have help info, and no actual values in
    // initSettings().
    public enum Settings {
	    LCHAR_MERGE,
        LITEM_MERGE,
	    OTHER_SETTINGS,
        OpenEncounterZones,
        ADDALLMASTERS,
        LOG_DEBUG
    }
}
