/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.swing.*;
import lev.gui.LButton;
import lev.gui.LUserSetting;
import lev.gui.Setting;
import skyproc.FormID;

/**
 *
 * @author Justin Swanson
 */
public class LFormIDPicker extends LUserSetting<FormID[]> {

    LButton add;
    LButton remove;
    JComboBox addPicker;
    DefaultListModel model;
    JList addedList;
    JScrollPane scroll;

    /**
     *
     * @param title
     * @param font
     * @param c
     */
    public LFormIDPicker(String title, Font font, Color c) {
	super(title, font, c);
	titleLabel.addShadow();
	addPicker = new JComboBox();
	addPicker.setSize(addPicker.getPreferredSize());
	addPicker.setLocation(0, titleLabel.getHeight() + 5);
	add(addPicker);
	add = new LButton("Add FormID  /\\");
	add.setLocation(0, addPicker.getY() + addPicker.getHeight() + 5);
	add.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		for (Object o : addPicker.getSelectedObjects()) {
		    if (!model.contains(o)) {
			model.addElement((FormID) o);
		    }
		}
	    }
	});
	add(add);
	remove = new LButton("\\/  Remove FormID");
	remove.setLocation(add.getWidth() + 5, add.getY());
	remove.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		for (Object o : addedList.getSelectedValuesList()) {
		    model.removeElement(o);
		}
	    }
	});
	add(remove);
	model = new DefaultListModel();
	addedList = new JList(model);
	scroll = new JScrollPane(addedList);
	scroll.setLocation(0, add.getY() + add.getHeight() + 5);
	scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	add(scroll);
	setSize(300, 150);
    }

    /**
     * 
     * @param arg0
     * @param arg1
     */
    @Override
    final public void setSize(int arg0, int arg1) {
	super.setSize(arg0, arg1);
//	titleLabel.setLocation(arg0 / 2 - titleLabel.getWidth() / 2, titleLabel.getY());
	addPicker.setSize(arg0, addPicker.getHeight());
	add.setSize((addPicker.getWidth() - 6) / 2, add.getHeight());
	remove.setSize(addPicker.getWidth() - add.getWidth() - 6, remove.getHeight());
	remove.setLocation(add.getX() + add.getWidth() + 5, remove.getY());
	scroll.setSize(arg0, arg1 - add.getY() - add.getHeight() - 5);
    }

    @Override
    protected void addUpdateHandlers() {
	add.addActionListener(new UpdateHandler());
	remove.addActionListener(new UpdateHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    FormID[] list = (FormID[]) m.get(saveTie).getData();
	    model.removeAllElements();
	    model.addElement(list);
	}
	return true;
    }

    @Override
    public FormID[] getValue() {
	FormID[] out = new FormID[addedList.getModel().getSize()];
	for (int i = 0; i < addedList.getModel().getSize(); i++) {
	    out[i] = (FormID) addedList.getModel().getElementAt(i);
	}
	return out;
    }

    @Override
    public void highlightChanged() {
    }

    @Override
    public void clearHighlight() {
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    public void addHelpHandler(boolean hoverListener) {
	FocusListener f = new HelpFocusHandler();
	addPicker.addFocusListener(f);
	addedList.addFocusListener(f);
	if (hoverListener) {
	    MouseListener l = new HelpMouseHandler();
	    addPicker.addMouseListener(l);
	    addedList.addMouseListener(l);
	    add.addMouseListener(l);
	    remove.addMouseListener(l);
	}
    }

    /**
     *
     * @param ids
     */
    public void load(ArrayList<FormID> ids) {
	for (FormID id : ids) {
	    addForm(id);
	}
    }

    /**
     *
     * @param ids
     */
    public void load(FormID[] ids) {
	for (FormID id : ids) {
	    addForm(id);
	}
    }

    /**
     *
     * @param id
     */
    public void addForm(FormID id) {
	model.addElement(add);
    }

    /**
     *
     * @param ids
     */
    public void load(String[][] ids) {
	ArrayList<FormID> out = new ArrayList<FormID>(ids.length);
	for (String[] id : ids) {
	    out.add(new FormID(id[0],id[1]));
	}
	load(out);
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getPickedIDs () {
	ArrayList<FormID> out = new ArrayList<FormID>(model.getSize());
	for (int i = 0 ; i < model.getSize() ; i++) {
	    out.add((FormID)model.getElementAt(i));
	}
	return out;
    }
}
