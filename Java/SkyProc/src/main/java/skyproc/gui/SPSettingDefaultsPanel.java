/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import lev.gui.LButton;
import lev.gui.LSaveFile;
import lev.gui.LUserSetting;

/**
 *
 * @author Justin Swanson
 */
public class SPSettingDefaultsPanel extends SPSettingPanel {

    LSaveFile saveFile;

    /**
     * Button at the bottom center column that will iterate over each item in
     * the settings list and revert them to the default settings of the savefile
     * field.
     */
    protected LButton defaults = new LButton("Set to Default");
    /**
     * Button at the bottom center column that will iterate over each item in
     * the settings list and revert them to the last saved settings of the
     * savefile field.
     */
    protected LButton save = new LButton("Revert to Saved");

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     * @param saveFile 
     */
    public SPSettingDefaultsPanel(SPMainMenuPanel parent_, String title, Color headerColor, LSaveFile saveFile) {
	super(parent_, title, headerColor);
	this.saveFile = saveFile;
    }

    @Override
    protected void initialize() {
	super.initialize();

	defaults.setLocation(getSpacing(defaults, save, true));
	defaults.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent event) {
		if (saveFile != null) {
		    for (LUserSetting s : settings) {
			saveFile.revertToDefault(s);
		    }
		    update();
		}
	    }
	});
	defaults.addMouseListener(new MouseListener() {

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		if (saveFile != null) {
		    saveFile.peekDefaults();
		    update();
		}
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		if (saveFile != null) {
		    saveFile.clearPeek();
		    update();
		}
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
	    }
	});

	save.setLocation(getSpacing(defaults, save, false));
	save.addMouseListener(new MouseListener() {

	    @Override
	    public void mousePressed(MouseEvent e) {
	    }

	    @Override
	    public void mouseReleased(MouseEvent e) {
	    }

	    @Override
	    public void mouseEntered(MouseEvent e) {
		if (saveFile != null) {
		    saveFile.peekSaved();
		    update();
		}
	    }

	    @Override
	    public void mouseExited(MouseEvent e) {
		if (saveFile != null) {
		    saveFile.clearPeek();
		    update();
		}
	    }

	    @Override
	    public void mouseClicked(MouseEvent e) {
	    }
	});
	save.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent event) {
		if (saveFile != null) {
		    for (LUserSetting s : settings) {
			saveFile.revertToSaved(s);
		    }
		    update();
		}
	    }
	});

	settingsPanel.add(defaults);
	settingsPanel.add(save);
    }
}
