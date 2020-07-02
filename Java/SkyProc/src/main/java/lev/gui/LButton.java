/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import javax.swing.JButton;

/**
 * A customized JButton used by Leviathan in GUIs.
 *
 * @author Justin Swanson
 */
public class LButton extends LHelpComponent {

    /**
     *
     */
    protected JButton button;

    /**
     *
     * @param title
     */
    public LButton(String title) {
	super(title);
	button = new JButton();
	button.setText(title);
	button.setMargin(new Insets(0, 2, 0, 2));
	button.setSize(button.getPreferredSize());
	button.setFocusable(false);
	button.setVisible(true);
	button.setLocation(0, 0);
	super.setSize(button.getSize());
	setVisible(true);
	add(button);
    }

    /**
     *
     * @param title
     * @param size
     */
    public LButton(String title, Dimension size) {
	this(title);
	setSize(size);
    }

    /**
     *
     * @param title
     * @param x
     * @param y
     */
    public LButton(String title, int x, int y) {
	this(title);
	setSize(x, y);
    }

    /**
     *
     * @param title
     * @param size
     * @param location
     */
    public LButton(String title, Dimension size, Point location) {
	this(title);
	setSize(size);
	setLocation(location);
    }

    /**
     *
     * @param title
     * @param location
     */
    public LButton(String title, Point location) {
	this(title);
	setLocation(location);
    }

    /**
     *
     * @param size
     */
    @Override
    public final void setSize(Dimension size) {
	setSize(size.width, size.height);
    }

    /**
     *
     * @param x
     * @param y
     */
    @Override
    public final void setSize(int x, int y) {
	button.setSize(x, y);
	super.setSize(x, y);
    }

    /**
     *
     * @param l
     */
    public void addActionListener(ActionListener l) {
	button.addActionListener(l);
    }

    /**
     *
     * @param s
     */
    public void setActionCommand(String s) {
	button.setActionCommand(s);
    }

    /**
     *
     * @return
     */
    public JButton getSource() {
	return button;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean requestFocusInWindow() {
	return button.requestFocusInWindow();
    }

    /**
     *
     * @param arg0
     */
    @Override
    public synchronized void addMouseListener(MouseListener arg0) {
	button.addMouseListener(arg0);
    }

    /**
     *
     * @return
     */
    public String getText() {
	return button.getText();
    }

    /**
     *
     * @param in
     */
    public void setText(String in) {
	button.setText(in);
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    protected void addHelpHandler(boolean hoverListener) {
	if (hoverListener) {
	    button.addMouseListener(new LHelpComponent.HelpMouseHandler());
	}
	button.addActionListener(new LHelpComponent.HelpActionHandler());
    }

    @Override
    public void setFocusable(boolean focusable) {
	super.setFocusable(focusable);
	button.setFocusable(focusable);
    }

    @Override
    public void addFocusListener(FocusListener l) {
	button.addFocusListener(l);
    }

    /**
     *
     */
    public void clearActionHandlers() {
	ActionListener[] ls = button.getListeners(ActionListener.class);
	for (ActionListener a : ls) {
	    button.removeActionListener(a);
	}
    }
}
