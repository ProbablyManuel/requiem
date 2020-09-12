/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import lev.gui.*;
import lev.gui.resources.LFonts;

/**
 *
 * @author Justin Swanson
 */
public abstract class SPSettingPanel extends LPanel {

    /**
     * Reference to the Main Menu parent GUI object
     */
    protected SPMainMenuPanel parent;
    private int rightMost = 0;
    /**
     * The top label
     */
    protected LLabel header;
    /**
     * List of all the setting components added with AddSetting()
     */
    protected ArrayList<LUserSetting> settings = new ArrayList<>();
    /**
     * Reference to the panel in the center column
     */
    protected LScrollPane scroll;
    /**
     *
     */
    protected LPanel settingsPanel;
    private ArrayList<Component> components = new ArrayList<>();
    /**
     * Flag to symbolize Panel has been initialized and the components have been
     * created and added.
     */
    protected boolean initialized = false;

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     */
    public SPSettingPanel(SPMainMenuPanel parent_, String title, Color headerColor) {
	super(SUMGUI.fullDimensions);
	parent = parent_;
	header = new LLabel(title, SUMGUI.SUMmainFont, headerColor);
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
	return header.getText();
    }

    /**
     * Function that creates all components and adds them to the GUI using Add()
     * or AddSetting().<br><br> It should look like this:<br> <i>if
     * (super.initialize()) {<br> <br> //... Your initializing code ...<br> <br>
     * return true;<br> } else {<br> return false;<br> }<br>
     *
     */
    protected void initialize() {

	settingsPanel = new LPanel(SUMGUI.middleDimensions);
	settingsPanel.add(header);

	scroll = new LScrollPane(settingsPanel);
	scroll.setLocation(SUMGUI.middleDimensions.x, SUMGUI.middleDimensions.y);
	scroll.setSize(SUMGUI.middleDimensions.width, SUMGUI.middleDimensions.height);
	add(scroll);

	header.addShadow();
	header.setLocation(settingsPanel.getWidth() / 2 - header.getWidth() / 2, 20);
	settingsPanel.setPreferredSize(scroll.getSize());

	last = new Point(settingsPanel.getWidth(), 65);

	initialized = true;
	setVisible(true);
    }

    /**
     * Function that will be called after the Defaults and Saved buttons are
     * pressed.<br> You may override it and add your own functionality.
     */
    protected void update() {
    }

    /**
     * Gets the spacing that should be given to two buttons.
     * @param in1 Button on left
     * @param in2 Button on right
     * @param left To return spacing for left button?
     * @return Spacing to be given target button.
     */
    protected Point getSpacing(LButton in1, LButton in2, boolean left) {
	int spacing = (settingsPanel.getWidth() - in1.getWidth() - in2.getWidth()) / 3;
	if (left) {
	    return new Point(spacing, settingsPanel.getHeight() - in1.getHeight() - 15);
	} else {
	    return new Point(in1.getX() + in1.getWidth() + spacing, settingsPanel.getHeight() - in2.getHeight() - 15);
	}
    }

    /**
     * Adds a non-setting component to the panel and adds it to the components
     * list.
     *
     * @param c
     */
    @Override
    public final void Add(Component c) {
	components.add(c);
	settingsPanel.Add(c);
	int range = c.getY() + c.getHeight();
	if (range > settingsPanel.getPreferredSize().getHeight()) {
	    settingsPanel.setPreferredSize(new Dimension(settingsPanel.getPreferredSize().width, range));
	}
    }

    /**
     * Adds a non-setting component to the panel and adds it to the settings AND
     * components list.
     *
     * @param c
     */
    public void AddSetting(LUserSetting c) {
	Add(c);
	settings.add(c);
    }

    /**
     * Function that opens, initializes if needed, and displays the settings
     * panel.
     */
    public void open() {
	parent.open();
	if (!initialized) {
	    initialize();
	}
	SUMGUI.helpPanel.reset();
	parent.openPanel(this);
	onOpen(parent);
	parent.revalidate();
    }

    /**
     *
     * @return An ActionListener that will open this panel.
     */
    public ActionListener getOpenHandler() {

	return new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent event) {
		SwingUtilities.invokeLater(
			new Runnable() {

			    @Override
			    public void run() {
				open();
			    }
			});
	    }
	};
    }

    /**
     * An empty function that can be overwritten to provide special directives
     * to the open command.
     *
     * @param parent
     */
    public void onOpen(SPMainMenuPanel parent) {
    }

    void close (SPMainMenuPanel parent) {
	setVisible(false);
	onClose(parent);
    }

    /**
     * Code to run when panel is closing.
     * @param parent Main menu panel stems from.
     */
    public void onClose(SPMainMenuPanel parent) {
    }

    @Override
    public Point setPlacement(Component c, int x, int y) {
	c.setLocation(x / 2 - c.getWidth() / 2, y + spacing);
	if (c.getX() + c.getWidth() > rightMost) {
	    rightMost = c.getX() + c.getWidth();
	}
	updateLast(c);
	return last;
    }

    /**
     * Aligns each component to the right, as you would expect from a word
     * processor's "align right".
     */



    public void alignRight() {
	for (Component c : components) {
	    c.setLocation(rightMost - c.getWidth(), c.getY());
	}
    }
}
