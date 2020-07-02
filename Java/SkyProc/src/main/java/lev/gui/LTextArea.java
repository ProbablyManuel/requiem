/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.*;

/**
 * A customized Text Pane used by Leviathan for GUIs.
 *
 * @author Justin Swanson
 */
public class LTextArea extends LComponent {

    /**
     *
     */
    protected JScrollPane scroll;
    /**
     *
     */
    protected JTextArea area;
    /**
     *
     */
    protected Document doc;

    /**
     *
     * @param size_
     * @param c
     */
    public LTextArea(Dimension size_, Color c) {
	this(c);
	setSize(size_);
    }

    /**
     *
     * @param x
     * @param y
     * @param c
     */
    public LTextArea(int x, int y, Color c) {
	this(new Dimension(x, y), c);
    }

    /**
     *
     * @param c
     */
    public LTextArea(Color c) {
	area = new JTextArea();
	doc = area.getDocument();
	area.setOpaque(false);
	setVisible(true);
	setForeground(c);
	add(area);
    }

    @Override
    public void setSize(Dimension size) {
	setSize(size.width, size.height);
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	if (scroll == null) {
	    area.setSize(width, height);
	} else {
	    scroll.setSize(width, height);
	}
    }

    @Override
    public void setForeground(Color c) {
	area.setForeground(c);
    }

    /**
     *
     * @param in
     */
    public void setText(String in) {
	clearText();
	try {
	    doc.insertString(0, in, null);
	} catch (BadLocationException ex) {
	    badText();
	}
    }

    /**
     *
     * @return
     */
    public String getText() {
	try {
	    return doc.getText(0, doc.getLength());
	} catch (BadLocationException ex) {
	    return "ERROR";
	}
    }

    /**
     *
     */
    public void badText() {
	try {
	    doc.insertString(0, "Bad Error", null);
	} catch (BadLocationException ex) {
	    Logger.getLogger(LTextPane.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     *
     */
    public void clearText() {
	try {
	    doc.remove(0, doc.getLength());
	} catch (BadLocationException ex) {
	}
    }

    /**
     *
     * @param in
     */
    public void append(String in) {
	try {
	    doc.insertString(doc.getLength(), in, null);
	} catch (BadLocationException ex) {
	    badText();
	}
    }

    /**
     *
     * @param b
     */
    public void setOpaque(Boolean b) {
	area.setOpaque(b);
    }

    @Override
    public void setFont (Font f) {
	area.setFont(f);
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
	return doc.getLength() == 0;
    }

    /**
     *
     * @param c
     */
    @Override
    public void setBackground(Color c) {
	area.setBackground(c);
    }

    /**
     *
     * @param c
     */
    public void setCaretColor(Color c) {
	area.setCaretColor(c);
    }

    /**
     *
     * @param b
     */
    public void setLineWrap (boolean b) {
	area.setLineWrap(b);
    }

    /**
     *
     * @param b
     */
    public void setWrapStyleWord(boolean b) {
	area.setWrapStyleWord(b);
    }

    /**
     *
     */
    public void addScroll() {
	this.removeAll();
	scroll = new JScrollPane(area);
	scroll.setSize(area.getSize());
	scroll.setOpaque(false);
	scroll.getViewport().setOpaque(false);
	scroll.setBorder(null);
	scroll.setVisible(true);
	add(scroll);
    }

    /**
     *
     * @param b
     */
    public void setEditable(boolean b) {
	area.setEditable(b);
    }

    /**
     *
     * @param size
     */
    public void setFontSize(float size) {
	area.setFont(area.getFont().deriveFont(size));
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
	return area.getPreferredSize();
    }
}
