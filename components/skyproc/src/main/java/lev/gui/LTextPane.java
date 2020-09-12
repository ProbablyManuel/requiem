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
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 * A customized Text Pane used by Leviathan for GUIs.
 *
 * @author Justin Swanson
 */
public class LTextPane extends LComponent {

    /**
     *
     */
    protected JScrollPane scroll;
    /**
     *
     */
    protected JTextPane pane;
    /**
     *
     */
    protected Document doc;

    /**
     *
     * @param c
     */
    public LTextPane(Color c) {
	this();
	setForeground(c);
    }

    /**
     *
     */
    public LTextPane() {
	pane = new JTextPane();
	doc = pane.getDocument();
	pane.setOpaque(false);
	setVisible(true);
	add(pane);
    }

    /**
     *
     * @param x
     * @param y
     * @param c
     */
    public LTextPane(int x, int y, Color c) {
	this(c);
	setSize(x,y);
    }

    /**
     *
     * @param size
     * @param c
     */
    public LTextPane(Dimension size, Color c) {
	this(c);
	setSize(size);
    }

    @Override
    public void setSize(Dimension size) {
	setSize(size.width, size.height);
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	if (scroll == null) {
	    pane.setSize(width, height);
	} else {
	    scroll.setSize(width, height);
	}
    }

    /**
     *
     * @param x
     */
    public void setSize(int x) {
	setSize(x, getHeight(x));
    }

    /**
     *
     * @param width
     * @return
     */
    public int getHeight(int width) {
	View v = pane.getUI().getRootView(pane);
	v.setSize(width, Integer.MAX_VALUE);
	int preferredHeight = (int) v.getPreferredSpan(View.Y_AXIS);
	return preferredHeight;
    }

    @Override
    public void setForeground(Color c) {
	pane.setForeground(c);
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
	pane.setOpaque(b);
    }

    /**
     * Makes the Text Pane center aligned
     */
    public void setCentered() {
	StyledDocument doc = pane.getStyledDocument();
	SimpleAttributeSet center = new SimpleAttributeSet();
	StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
	doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    @Override
    public void setFont (Font f) {
	pane.setFont(f);
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
	pane.setBackground(c);
    }

    /**
     *
     * @param c
     */
    public void setCaretColor(Color c) {
	pane.setCaretColor(c);
    }

    /**
     *
     */
    public void centerText() {
	StyledDocument doc = pane.getStyledDocument();
	SimpleAttributeSet center = new SimpleAttributeSet();
	StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
	doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    /**
     *
     * @param s
     */
    public void setContentType(String s) {
	pane.setContentType(s);
    }

    /**
     *
     */
    public void addScroll() {
	this.removeAll();
	scroll = new JScrollPane(pane);
	scroll.setSize(pane.getSize());
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
	pane.setEditable(b);
    }

    /**
     *
     * @param size
     */
    public void setFontSize(float size) {
	pane.setFont(pane.getFont().deriveFont(size));
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
	return pane.getPreferredSize();
    }
}
