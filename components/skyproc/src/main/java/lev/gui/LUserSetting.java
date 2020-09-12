/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A GUI component that can be tied to a Setting in a SaveFile.
 * This facilitates automatic updating of the savefile when the GUI updates.
 * @param <T>
 * @author Justin Swanson
 */
public abstract class LUserSetting<T> extends LHelpComponent {

    /**
     *
     */
    protected LLabel titleLabel;

    /**
     * Creates a user setting component that doesn't use the label.
     * @param text
     */
    public LUserSetting(String text) {
	super(text);
    }

    /**
     * Creates a user setting component that uses the label.
     * @param text
     * @param label
     * @param shade
     */
    public LUserSetting(String text, Font label, Color shade) {
	this(text);
	titleLabel = new LLabel(text, label, shade);
	add(titleLabel);
    }

    /**
     * Ties this GUI component to a setting in a savefile.  Additionally, it links the GUI component
     * to update a help panel when focused.  Optionally it can update the help panel when hovered over too.
     * @param setting Setting to tie to
     * @param saveFile SaveFile to tie to
     * @param help_ HelpPanel to update
     * @param hoverListener Whether to update help panel on hover
     */
    public void tie(Enum setting, LSaveFile saveFile, LHelpPanel help_, boolean hoverListener) {
	tie(setting, saveFile);
	linkTo(setting, saveFile, help_, hoverListener);
    }

    /**
     * Ties this GUI component to a setting in a savefile.
     * @param setting Setting to tie to
     * @param saveFile SaveFile to tie to
     */
    public void tie(Enum setting, LSaveFile saveFile) {
	saveTie = setting;
	save = saveFile;
	save.tie(setting, this);
	revertTo(save.saveSettings);
	addUpdateHandlers();
    }

    /**
     * Abstract function that should add appropriate update handlers to the
     * appropriate components.
     */
    protected abstract void addUpdateHandlers();

    /**
     * Reverts the GUI component to to SaveFile instance specified.
     * @param m
     * @return
     */
    public abstract boolean revertTo(Map<Enum, Setting> m);

    /**
     *
     * @return
     */
    public Boolean isTied() {
	return (saveTie != null);
    }

    /**
     *
     * @return
     */
    public abstract T getValue();

    /**
     * Updates the savefile to the GUI's value.
     */
    public void update() {
	if (isTied()) {
	    save.set(saveTie, getValue());
	}
    }

    /**
     * Abstract function that should change some GUI component to symbolize
     * it is highlighted.
     */
    public abstract void highlightChanged();

    /**
     * Abstract function that should clear any GUI components of their highlighted state.
     */
    public abstract void clearHighlight();

    /**
     * Handler that will update the savefile to the GUI's value
     */
    public class UpdateHandler implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
	    update();
	}
    }

    /**
     * Handler that will update the savefile to the GUI's value
     */
    public class UpdateChangeHandler implements ChangeListener {

	@Override
	public void stateChanged(ChangeEvent event) {
	    update();
	}
    }

    /**
     * Handler that will update the savefile to the GUI's value
     */
    public class UpdateCaretHandler implements CaretListener {

	@Override
	public void caretUpdate(CaretEvent event) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    update();
		}
	    });

	}
    }

    @Override
    public String getName() {
	return title;
    }
}
