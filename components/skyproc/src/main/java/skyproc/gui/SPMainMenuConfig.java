/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import lev.gui.LButton;
import lev.gui.LCheckBoxConfig;
import lev.gui.LComponent;
import lev.gui.LLabel;
import lev.gui.LSaveFile;

/**
 * An main menu GUI component that is used in SUMGUI.
 *
 * @author Justin Swanson
 */
public class SPMainMenuConfig extends LCheckBoxConfig {

    static int size = 20;

    /**
     * Creates a main menu GUI line tied to a savefile.
     *
     * @param title_ Text to display
     * @param checkbox Whether to include a checkbox
     * @param color Color to display
     * @param location Location of the component
     * @param saveFile Savefile to tie to
     * @param setting Setting to tie to
     */
    public SPMainMenuConfig(LLabel title_, boolean checkbox, Color color, Point location, LSaveFile saveFile, Enum setting) {
	super(title_.getText());
	boolean saveField = saveFile != null && setting != null;

	button = new LButton(buttonText);

	help = SUMGUI.helpPanel;
	if (saveField) {
	    save = saveFile;
	    saveTie = setting;
	    linkTo(saveTie, save, help, true);
	    button.addActionListener(new UpdateHelpActionHandler());
	}

	Font font = title_.getFont().deriveFont(Font.PLAIN, size);

	LComponent using;
	if (checkbox) {
	    cbox = new LSpecialCheckBox(title, font, color, this);
	    cbox.setOffset(5);
	    cbox.tie(setting, save, help, false);
	    cbox.setFocusable(false);
	    using = cbox;
	    add(cbox);
	} else {
	    titleLabel = new LLabel(title, font, color);
	    using = titleLabel;
	    add(titleLabel);
	}

	button.setLocation(using.getWidth() + spacing, using.getHeight() / 2 - button.getHeight() / 2);
	add(button);

	setLocation(location.x - button.getWidth() - using.getWidth() - spacing, location.y);
	setSize(using.getWidth() + button.getWidth() + spacing, using.getHeight());

	if (button.getY() < 0) {
	    button.setLocation(button.getX(), 0);
	    setSize(getWidth(), button.getHeight());
	    using.setLocation(using.getX(), button.getHeight() / 2 - using.getHeight() / 2);
	}
    }
}
