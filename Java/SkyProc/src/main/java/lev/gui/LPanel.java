/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 *
 * @author Justin Swanson
 */
public class LPanel extends JPanel implements Scrollable {

    /**
     * Reference to the position of the last-added setting or component added
     * using Add() or AddSetting()
     */
    protected Point last;
    /**
     * Spacing to be used between settings
     */
    protected int spacing = 12;
    /**
     *
     */
    protected Align align = Align.Left;

    /**
     *
     */
    public LPanel() {
	setLayout(null);
	super.setSize(1, 1);
	setOpaque(false);
	setBorder(BorderFactory.createEmptyBorder());
	super.setVisible(true);
	last = new Point(spacing, 0);
    }

    /**
     *
     * @param r
     */
    public LPanel(Rectangle r) {
	this();
	setBounds(r);
    }

    /**
     *
     * @param x
     * @param y
     */
    public LPanel(int x, int y) {
	this();
	setLocation(x, y);
    }

    /**
     *
     * @param input
     */
    public void Add(Component input) {
	input.setVisible(true);
	add(input);
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
	return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	return 100;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
	return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
	return false;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
	return new Dimension(300, 300);
    }

    /**
     *
     * @param size
     */
    public void remeasure(Dimension size) {
	setSize(size);
    }

    /**
     * Creates an alpha composite with the given transparency.
     * @param alpha
     * @return
     */
    protected static AlphaComposite makeAlphaComposite(float alpha) {
	return (AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    }

    /**
     * Sets the placement relative to the last component added.
     *
     * @param c
     * @return The point the component was placed
     */
    public Point setPlacement(Component c) {
	return setPlacement(c, last.x, last.y);
    }

    /**
     * Sets the placement to (x,y)
     *
     * @param c
     * @param x
     * @param y
     * @return The point the component was placed
     */
    public Point setPlacement(Component c, int x, int y) {
	switch (align) {
	    case Right:
		c.setLocation(x - c.getWidth(), y + spacing);
		break;
	    case Left:
		c.setLocation(x, y + spacing);
		break;
	    case Center:
		if (c instanceof LComponent) {
		    LComponent lc = (LComponent) c;
		    c.setLocation(lc.getCenter(), y + spacing);
		} else {
		    c.setLocation(x - c.getWidth() / 2, y + spacing);
		}
		break;
	}
	updateLast(c);
	return last;
    }

    /**
     * Sets placement of the component and then adds it.
     * @param c
     */
    public void placeAdd(Component c) {
	setPlacement(c);
	Add(c);
    }

    /**
     *
     * @param align
     */
    public void align(Align align) {
	this.align = align;
    }

    /**
     * Updates last GUI component tracker to be focused on c. For use with
     * setPlacement()
     *
     * @param c Component to set as the last GUI component placed.
     */
    public void updateLast(Component c) {
	last = new Point(last.x, c.getY() + c.getHeight());
    }

    /**
     *
     */
    public enum Align {

	/**
	 *
	 */
	Left,
	/**
	 *
	 */
	Center,
	/**
	 *
	 */
	Right;
    }
}