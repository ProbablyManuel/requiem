/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JTextField;

/**
 *
 * @author Justin Swanson
 */
public class LTextField extends LUserSetting<String> {

    /**
     *
     */
    protected JTextField field;
    /**
     *
     */
    protected LButton enterButton;

    /**
     *
     * @param title_
     * @param font
     * @param shade
     */
    public LTextField(String title_, Font font, Color shade) {
	super(title_, font, shade);
	init();
    }

    /**
     *
     * @param title_
     */
    public LTextField(String title_) {
	super(title_);
	init();
    }

    final void init() {
	field = new JTextField();
	add(field);
	if (titleLabel != null) {
	    titleLabel.addShadow();
	    field.setLocation(0, titleLabel.getHeight() + 5);
	    setSize(275, 50);
	} else {
	    setSize(275, 22);
	}
	field.setVisible(true);
	setVisible(true);
    }

    @Override
    final public void setSize(int x, int y) {
	super.setSize(x, y);
	if (titleLabel != null) {
	    field.setSize(x, y - titleLabel.getHeight() - 5);
	} else {
	    field.setSize(x, y);
	}
	if (enterButton != null) {
	    field.setSize(x - enterButton.getWidth() - 10, field.getHeight());
	    enterButton.setLocation(field.getX() + field.getWidth() + 10, field.getY());
	    enterButton.setSize(enterButton.getWidth(), field.getHeight());
	}
    }

    /**
     *
     * @param a
     */
    public void addActionListener(ActionListener a) {
	field.addActionListener(a);
    }

    @Override
    protected void addUpdateHandlers() {
	field.addActionListener(new UpdateHandler());
    }

    /**
     *
     * @param s
     */
    public void setText(String s) {
	field.setText(s);
    }

    /**
     *
     * @return
     */
    public String getText() {
	return field.getText();
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    String cur = field.getText();
	    field.setText(m.get(saveTie).getStr());
	    if (!cur.equals(field.getText())) {
		return false;
	    }
	}
	return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getValue() {
	return field.getText();
    }

    @Override
    public void highlightChanged() {
	field.setBackground(new Color(224, 121, 147));
    }

    @Override
    public void clearHighlight() {
	field.setBackground(Color.white);
    }

    /**
     *
     * @param hoverListener
     */
    @Override
    public void addHelpHandler(boolean hoverListener) {
	field.addFocusListener(new HelpFocusHandler());
	if (hoverListener) {
	    field.addMouseListener(new HelpMouseHandler());
	}
    }

    /**
     * Adds an enter button with the desired listener.
     * @param label
     * @param done
     */
    public void addEnterButton(String label, ActionListener done) {
	enterButton = new LButton(label);
	enterButton.addActionListener(done);
	add(enterButton);
	setSize(getSize().width, getSize().height);
    }
}
