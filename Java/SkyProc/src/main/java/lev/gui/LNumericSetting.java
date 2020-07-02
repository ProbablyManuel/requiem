/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.FocusListener;
import java.util.Map;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin Swanson
 */
public class LNumericSetting extends LUserSetting<Integer> {

    /**
     *
     */
    protected LSpinner setting;

    /**
     *
     * @param text
     * @param font
     * @param c
     * @param min
     * @param max
     * @param step
     */
    public LNumericSetting(String text, Font font, Color c, int min, Integer max, int step) {
        super(text, font, c);
        setting = new LSpinner(text, min, min, max, step, Integer.toString(max).length());
        int spacing = 10;
        titleLabel.addShadow();
        if (titleLabel.getHeight() < setting.getHeight()) {
            titleLabel.setLocation(titleLabel.getX(), setting.getHeight() / 2 - titleLabel.getHeight() / 2);
            setSize(1, setting.getHeight());
        } else {
            setting.setLocation(setting.getX(), titleLabel.getHeight() / 2 - setting.getHeight() / 2);
            setSize(1, titleLabel.getHeight());
        }
        setting.setLocation(titleLabel.getWidth() + spacing, setting.getY());

        Add(setting);
        Add(titleLabel);

	setting.titleLabel = titleLabel;
        setSize(titleLabel.getWidth() + setting.getWidth() + spacing, getHeight());
        setLocation(getX() - getWidth(), getY());
        setVisible(true);
    }

    /**
     *
     * @param s
     */
    public void setValue(String s) {
        setValue(Integer.parseInt(s));
    }

    /**
     *
     * @param i
     */
    public void setValue(int i) {
        setting.setValue(i);
    }

    /**
     *
     * @param d
     */
    public void setValue(double d) {
        setValue(Double.toString(d));
    }

    @Override
    final public void tie(Enum s, LSaveFile save_, LHelpPanel help_, boolean hoverHandler) {
	setting.tie(s, save_, help_, hoverHandler);
    }

    /**
     *
     * @param s
     * @param save
     * @param help_
     * @param hoverListener
     */
    @Override
    public void linkTo(Enum s, LSaveFile save, LHelpPanel help_, boolean hoverListener) {
	setting.linkTo(s, save, help_, hoverListener);
    }

    @Override
    public void tie(Enum s, LSaveFile save_) {
	setting.tie(s, save_);
    }

    @Override
    public Integer getValue() {
        return (Integer) setting.getValue();
    }

    @Override
    public synchronized void addFocusListener(FocusListener arg0) {
	setting.addFocusListener(arg0);
    }

    /**
     *
     * @param c
     */
    public void addChangeListener(ChangeListener c) {
        setting.addChangeListener(c);
    }

    /**
     *
     * @param c
     */
    public void setColor(Color c) {
        titleLabel.setForeground(c);
    }

    @Override
    public void highlightChanged() {
	setting.highlightChanged();
    }

    @Override
    public void clearHighlight() {
	setting.clearHighlight();
    }

    @Override
    protected void addUpdateHandlers() {
	setting.addUpdateHandlers();
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	return setting.revertTo(m);
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    protected void addHelpHandler(boolean hoverListener) {
	setting.addHelpHandler(hoverListener);
    }

    @Override
    final public void setLocation(int x, int y) {
	super.setLocation(x, y);
	setting.helpYoffset = y;
    }

    @Override
    final public void setLocation(Point p) {
	super.setLocation(p);
	setting.helpYoffset = p.y;
    }
}