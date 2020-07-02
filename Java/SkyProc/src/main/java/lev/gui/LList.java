/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.event.MouseListener;
import java.util.*;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @param <T>
 * @author Justin Swanson
 */
public class LList<T extends Object> extends LHelpComponent implements Iterable<T> {

    /**
     *
     */
    protected DefaultListModel<T> model;
    /**
     *
     */
    protected JList<T> list;
    /**
     *
     */
    protected JScrollPane scroll;
    /**
     *
     */
    protected boolean unique;
    /**
     *
     */
    protected Comparator compare;
    /**
     *
     */
    static protected int spacing = 15;

    /**
     *
     * @param title
     */
    public LList(String title) {
	super(title);

	model = new DefaultListModel();
	list = new JList(model);
	scroll = new JScrollPane(list);
	scroll.setVisible(true);
	add(scroll);
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	scroll.setSize(width, height);
    }

    /**
     *
     * @param o
     */
    public void addElement(T o) {
	if (!unique || !model.contains(o)) {
	    model.addElement(o);
	}
    }

    /**
     *
     * @param in
     */
    public void addElements(Collection<T> in) {
	for (T t : in) {
	    addElement(t);
	}
    }

    /**
     *
     * @return
     */
    public List<T> getSelectedElements() {
	return list.getSelectedValuesList();
    }

    /**
     *
     * @return
     */
    public T getSelectedElement() {
	return list.getSelectedValue();
    }

    /**
     *
     * @param index
     */
    public void setSelectedElement(int index) {
	list.setSelectedIndex(index);
    }

    /**
     *
     */
    public void removeSelected() {
	for (Object o : list.getSelectedValuesList()) {
	    model.removeElement(o);
	}
    }

    /**
     *
     * @return
     */
    public int numItems() {
	return model.size();
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
	return model.isEmpty();
    }

    /**
     *
     */
    public void clear() {
	model.clear();
    }

    /**
     *
     * @param on
     */
    public void setUnique(boolean on) {
	unique = on;
    }

    /**
     *
     * @param l
     */
    public void addListSelectionListener(ListSelectionListener l) {
	list.addListSelectionListener(l);
    }

    @Override
    public void addMouseListener(MouseListener m) {
	super.addMouseListener(m);
	scroll.addMouseListener(m);
	list.addMouseListener(m);
    }

    @Override
    public Iterator<T> iterator() {
	return getAll().iterator();
    }

    /**
     *
     */
    public void highlightChanged() {
	list.setBackground(new Color(224, 121, 147));
    }

    /**
     *
     */
    public void clearHighlight() {
	list.setBackground(Color.white);
    }

    /**
     *
     * @return
     */
    public ArrayList<T> getAll() {
	ArrayList<T> out = new ArrayList<T>(model.size());
	for (Object o : model.toArray()) {
	    out.add((T) o);
	}
	return out;
    }

    @Override
    protected void addHelpHandler(boolean hoverListener) {
	addMouseListener(new HelpMouseHandler());
    }
}
