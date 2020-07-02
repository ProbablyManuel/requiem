/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import lev.gui.LButton;
import lev.gui.LLabel;
import lev.gui.LList;

/**
 *
 * @param <T>
 * @author Justin Swanson
 */
public class SPList<T extends Object> extends LList<T> {

    LLabel titleLabel;
    LButton remove;
    LButton accept;

    /**
     *
     * @param title
     * @param font
     * @param color
     */
    public SPList(String title, Font font, Color color) {
	super(title);
	this.titleLabel = new LLabel(title, font, color);
	this.titleLabel.addShadow();
	add(this.titleLabel);

	scroll.setLocation(0, this.titleLabel.getY() + this.titleLabel.getHeight() + 10);

	remove = new LButton("Remove Selected");
	remove.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		removeSelected();
	    }
	});
	Add(remove);

    }

    /**
     * 
     * @param m
     */
    @Override
    public void addMouseListener(MouseListener m) {
	super.addMouseListener(m);
	remove.addMouseListener(m);
	if (accept != null) {
	    accept.addMouseListener(m);
	}
    }

    /**
     * 
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	scroll.setSize(width, height - titleLabel.getHeight() - remove.getHeight() - 20);
	remove.setSize((scroll.getWidth() - spacing) / 2, remove.getHeight());
	if (accept != null) {
	    remove.putUnder(scroll, 0, 10);
	    accept.setSize(remove.getSize());
	    accept.putUnder(scroll, remove.getRight() + spacing, 10);
	} else {
	    remove.centerOn(scroll, scroll.getY() + scroll.getHeight() + 10);
	}
    }

    /**
     *
     * @param title
     * @param a
     */
    public void addEnterButton(String title, ActionListener a) {
	accept = new LButton(title);
	accept.addActionListener(a);
	Add(accept);
	remove.setLocation(0, remove.getY());
	setSize(getSize().width, getSize().height);
    }

    /**
     *
     * @param title
     * @param a
     */
    public void setRemoveButton(String title, ActionListener a) {
	remove.setText(title);
	remove.clearActionHandlers();
	remove.addActionListener(a);
    }

}
