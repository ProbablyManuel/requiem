/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin Swanson
 */
public class LSpinner extends LUserSetting<Integer> {

    /**
     *
     */
    protected JSpinner spinner;

    /**
     *
     * @param title
     * @param init
     * @param min
     * @param max
     * @param step
     * @param width
     */
    public LSpinner(String title, int init, int min, int max, int step, int width) {
	super(title);
	SpinnerModel model = new SpinnerNumberModel(init, min, max, step);
	spinner = new JSpinner(model);
	JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
	textField.setAlignmentX(JTextField.RIGHT);
	textField.setColumns(width);
	spinner.setSize(spinner.getPreferredSize());
	add(spinner);
	setSize(spinner.getPreferredSize());
    }

    @Override
    protected void addUpdateHandlers() {
	spinner.addChangeListener(new UpdateChangeHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    int cur = getValue();
	    setValue(m.get(saveTie).getInt());
	    if (cur != getValue()) {
		return false;
	    }
	}
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public Integer getValue() {
	return (Integer) spinner.getValue();
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    protected void addHelpHandler(boolean hoverListener) {
	JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
	editor.getTextField().addFocusListener(new HelpFocusHandler());
	if (hoverListener) {
	    MouseListener m = new HelpMouseHandler();
	    if (titleLabel != null) {
		titleLabel.addMouseListener(m);
	    }
	    editor.getTextField().addMouseListener(m);
	    for (Component c : spinner.getComponents()) {
		c.addMouseListener(m);
	    }
	}
    }

    /**
     *
     * @param in
     */
    public void setValue(int in) {
	spinner.setValue(in);
    }

    @Override
    public synchronized void addFocusListener(FocusListener arg0) {
	JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
	editor.getTextField().addFocusListener(arg0);
    }

    /**
     *
     * @param c
     */
    public void addChangeListener(ChangeListener c) {
	spinner.addChangeListener(c);
    }

    @Override
    public void highlightChanged() {
	(((JSpinner.DefaultEditor) spinner.getEditor()).getTextField()).setBackground(new Color(224, 121, 147));
    }

    @Override
    public void clearHighlight() {
	(((JSpinner.DefaultEditor) spinner.getEditor()).getTextField()).setBackground(Color.WHITE);
    }
}
