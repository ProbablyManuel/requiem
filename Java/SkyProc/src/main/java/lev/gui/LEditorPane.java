/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.View;

/**
 *
 * @author Justin Swanson
 */
public class LEditorPane extends LComponent {

    /**
     *
     */
    protected JEditorPane pane;

    /**
     *
     */
    public LEditorPane() {
	pane = new JEditorPane();
	pane.setEditable(false);
	pane.setVisible(true);
	setVisible(true);
	add(pane);
    }

    /**
     *
     * @param h
     */
    public void addHyperLinkListener(HyperlinkListener h) {
	pane.addHyperlinkListener(h);
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
     * @return
     */
    public String getText() {
	return pane.getText();
    }

    /**
     *
     * @param s
     */
    public void setText(String s) {
	pane.setText(s);
    }

    @Override
    public void setSize(Dimension size) {
	setSize(size.width, size.height);
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	pane.setSize(x, y);
    }

    /**
     *
     * @param x
     */
    public void setSize(int x) {
	setSize(x, getHeight(x));
    }

    @Override
    public void setFont(Font f) {
	pane.setFont(f);
    }

    /**
     *
     */
    public void honorDisplayProperties() {
	pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    }

    @Override
    public Dimension getPreferredSize() {
	return pane.getPreferredSize();
    }

    /**
     *
     * @param maxWidth
     */
    public void compactContent(int maxWidth) {
	setSize(maxWidth);
	if (getPreferredSize().width < getWidth()) {
	    setSize(getPreferredSize().width, getHeight());
	}
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

    /**
     *
     * @param height
     * @return
     */
    public int getWidth(int height) {
	View v = pane.getUI().getRootView(pane);
	v.setSize(Integer.MAX_VALUE, height);
	int prefWidth = (int) v.getPreferredSpan(View.X_AXIS);
	return prefWidth;
    }

    /**
     *
     * @param on
     */
    public void setOpaque(boolean on) {
	pane.setOpaque(on);
    }

    @Override
    public void setEnabled(boolean on) {
	pane.setEnabled(on);
    }
}