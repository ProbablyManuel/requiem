/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;

/**
 *
 * @author Justin Swanson
 */
public class LMenuItem extends LHelpComponent {

    /**
     *
     */
    protected JMenuItem menuItem;

    /**
     *
     * @param title_
     */
    public LMenuItem (String title_) {
	super(title_);
	menuItem = new JMenuItem(title_);
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    protected void addHelpHandler(boolean hoverListener) {
	menuItem.addMouseListener(new HelpMouseHandler());
    }

    /**
     *
     * @return
     */
    public JMenuItem getItem() {
	return menuItem;
    }

    /**
     *
     * @param a
     */
    public void addActionListener (ActionListener a) {
	menuItem.addActionListener(a);
    }

    @Override
    public void addMouseListener (MouseListener m) {
	menuItem.addMouseListener(m);
    }

    @Override
    public boolean isVisible() {
	return menuItem.isVisible();
    }

    @Override
    public void setVisible(boolean b) {
	menuItem.setVisible(b);
    }


}

