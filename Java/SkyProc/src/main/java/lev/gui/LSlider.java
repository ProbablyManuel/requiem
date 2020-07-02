/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin Swanson
 */
public class LSlider extends LUserSetting<Integer> {

    /**
     *
     */
    protected JSlider slider;

    /**
     *
     * @param title
     * @param font
     * @param c
     * @param min
     * @param max
     * @param cur
     */
    public LSlider(String title, Font font, Color c, int min, int max, int cur) {
	super(title, font, c);
	slider = new JSlider(JSlider.HORIZONTAL, min, max, cur);
	slider.setSize(100, 16);
	slider.setLocation(titleLabel.getRight() + 5, 0);
	slider.setVisible(true);
	add(slider);

	setSize(titleLabel.getWidth() + 100, Lg.taller(slider, titleLabel));
    }

    @Override
    public void setSize(int x, int y) {
	slider.setSize(x - titleLabel.getWidth() - 5, slider.getHeight());
	titleLabel.setLocation(titleLabel.getX(), y / 2 - titleLabel.getHeight() / 2);
	slider.setLocation(slider.getX(), y / 2 - slider.getHeight() / 2);
	super.setSize(x, y);
    }

    @Override
    protected void addUpdateHandlers() {
	slider.addChangeListener(new UpdateChangeHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    int cur = slider.getValue();
	    slider.setValue(m.get(saveTie).getInt());
	    if (cur != slider.getValue()) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public Integer getValue() {
	return slider.getValue();
    }

    @Override
    public void highlightChanged() {
    }

    @Override
    public void clearHighlight() {
    }

    /**
     *
     * @return
     */
    @Override
    public int getCenter() {
	return titleLabel.getRight() + 3;
    }

    @Override
    protected void addHelpHandler(boolean hoverListener) {
	slider.addMouseListener(new HelpMouseHandler());
    }

    /**
     *
     * @param c
     */
    public void addChangeListener(ChangeListener c) {
	slider.addChangeListener(c);
    }
}
