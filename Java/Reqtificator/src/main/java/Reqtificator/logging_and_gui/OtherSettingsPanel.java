/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reqtificator.logging_and_gui;

import Reqtificator.YourSaveFile;
import Reqtificator.YourSaveFile.Settings;
import lev.gui.LCheckBox;
import lev.gui.LLabel;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;
import skyproc.gui.SUMGUI;

public class OtherSettingsPanel extends SPSettingPanel {

    private final TextManager texts;
    private final YourSaveFile saveFile;

    public OtherSettingsPanel(SPMainMenuPanel parent_, TextManager texts, YourSaveFile saveFile) {
        super(parent_, "who knows?", GUIDesignStash.headerColor);
        this.texts = texts;
        this.saveFile = saveFile;
        header = new LLabel(texts.format("gui.settings.title"),
                header.getFont(), GUIDesignStash.headerColor);
    }

    @Override
    protected void initialize() {
        super.initialize();

        LCheckBox lchar_merge = new LCheckBox(
                texts.format("gui.settings.lchar_merge"),
                GUIDesignStash.settingsFont, GUIDesignStash.settingsColor);
        lchar_merge.tie(YourSaveFile.Settings.LCHAR_MERGE,
                saveFile, SUMGUI.helpPanel, true);
        lchar_merge.setOffset(2);
        lchar_merge.addShadow();
        setPlacement(lchar_merge);
        AddSetting(lchar_merge);

        LCheckBox litem_merge = new LCheckBox(
                texts.format("gui.settings.litem_merge"),
                GUIDesignStash.settingsFont, GUIDesignStash.settingsColor);
        litem_merge.tie(YourSaveFile.Settings.LITEM_MERGE,
                saveFile, SUMGUI.helpPanel, true);
        litem_merge.setOffset(2);
        litem_merge.addShadow();
        setPlacement(litem_merge);
        AddSetting(litem_merge);

        LCheckBox openCombatBoundaties = new LCheckBox(
                texts.format("gui.settings.open_encounter_zones"),
                GUIDesignStash.settingsFont, GUIDesignStash.settingsColor);
        openCombatBoundaties.tie(YourSaveFile.Settings.OpenEncounterZones,
                saveFile, SUMGUI.helpPanel, true);
        openCombatBoundaties.setOffset(2);
        openCombatBoundaties.addShadow();
        setPlacement(openCombatBoundaties);
        AddSetting(openCombatBoundaties);

        LCheckBox addallmasters = new LCheckBox(
                texts.format("gui.settings.all_masters"),
                GUIDesignStash.settingsFont, GUIDesignStash.settingsColor);
        addallmasters.tie(YourSaveFile.Settings.ADDALLMASTERS,
                saveFile, SUMGUI.helpPanel, true);
        addallmasters.setOffset(2);
        addallmasters.addShadow();
        setPlacement(addallmasters);
        AddSetting(addallmasters);

        LCheckBox debugLogging = new LCheckBox(
                texts.format("gui.settings.debug_logging"),
                GUIDesignStash.settingsFont, GUIDesignStash.settingsColor);
        debugLogging.tie(Settings.LOG_DEBUG,
                saveFile, SUMGUI.helpPanel, true);
        debugLogging.setOffset(2);
        debugLogging.addShadow();
        setPlacement(debugLogging);
        AddSetting(debugLogging);

        alignRight();
    }

}
