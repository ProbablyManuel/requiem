/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Reqtificator.logging_and_gui;

import lev.gui.LLabel;
import lev.gui.LTextPane;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SPSettingPanel;

/**
 *
 * @author Justin Swanson
 */
public class WelcomePanel extends SPSettingPanel {

    /**Container for localized texts.
     */
    private final TextManager texts;

//    LTextPane introText;

    public WelcomePanel(SPMainMenuPanel parent_, TextManager texts) {
	super(parent_, "who knows?", GUIDesignStash.headerColor);
        this.texts = texts;
        header = new LLabel(texts.format("gui.general.name"),
				header.getFont(), GUIDesignStash.headerColor);
    }

    @Override
    protected void initialize() {
	super.initialize();

	LTextPane introText = new LTextPane(settingsPanel.getWidth() - 40, 400,
                GUIDesignStash.settingsColor);
	introText.setText(texts.format("gui.general.description"));
	introText.setEditable(false);
	introText.setFont(GUIDesignStash.settingsFont);
	introText.setCentered();
	setPlacement(introText);
	Add(introText);

	alignRight();
    }
}
