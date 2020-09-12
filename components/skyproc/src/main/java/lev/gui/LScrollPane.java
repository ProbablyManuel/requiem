/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Justin Swanson
 */
public class LScrollPane extends JScrollPane {

    /**
     *
     * @param c
     */
    public LScrollPane(Component c) {
	super(c);
	setBorder(BorderFactory.createEmptyBorder());
	setViewportBorder(BorderFactory.createEmptyBorder());
	scrollToTop();
    }

    @Override
    public void setOpaque(boolean arg0) {
	super.setOpaque(false);
	getViewport().setOpaque(false);
    }

    /**
     *
     */
    public final void scrollToTop() {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		getVerticalScrollBar().setValue(0);
	    }
	});
    }

    @Override
    public void setVisible(boolean on) {
	super.setVisible(on);
	if (on) {
	    scrollToTop();
	}
    }
}
