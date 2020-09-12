/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * A customized JLabel that is used by Leviathan for GUIs.
 * @author Justin Swanson
 */
public class LLabel extends LComponent {

    /**
     *
     */
    protected JLabel mainText;
    /**
     *
     */
    protected JLabel shadow;
    /**
     *
     */
    protected JLabel shadow2;
    /**
     *
     */
    protected JLabel shadow3;
    /**
     *
     */
    protected int shadowSpacing = 2;

    /**
     *
     * @param text
     * @param font
     * @param c
     */
    public LLabel(String text, Font font, Color c) {
	mainText = new JLabel(text);
	mainText.setFont(font);
	mainText.setForeground(c);
	mainText.setSize(mainText.getPreferredSize());
	mainText.setFocusable(false);
	mainText.setVisible(true);
	add(mainText);
	setSize(mainText.getPreferredSize());
    }

    /**
     * Adds three copies of the primary label that are black and placed to
     * mimic a shadow behind the primary label.
     */
    public void addShadow() {
	shadow = copyLabel(mainText);
	shadow.setForeground(Color.BLACK);
	shadow2 = copyLabel(shadow);
	shadow3 = copyLabel(shadow);
	shadow.setLocation(shadowSpacing, shadowSpacing);
	shadow2.setLocation(0, shadowSpacing);
	shadow3.setLocation(shadowSpacing, 0);
	add(shadow);
	add(shadow2);
	add(shadow3);
	setSize(getWidth() + shadowSpacing, getHeight() + shadowSpacing);
    }

    private JLabel copyLabel (JLabel input) {
	JLabel output = new JLabel(input.getText());
	output.setFont(input.getFont());
	output.setForeground(input.getForeground());
	output.setSize(output.getPreferredSize());
	output.setVisible(true);
	return output;
    }

    /**
     *
     * @return
     */
    public String getText () {
	return mainText.getText();
    }

    /**
     *
     * @param input
     */
    public void setText (String input) {
	mainText.setText(input);
	mainText.setSize(mainText.getPreferredSize());
	if (shadow != null) {
	    shadow.setText(input);
	    shadow2.setText(input);
	    shadow3.setText(input);
	    shadow.setSize(shadow.getPreferredSize());
	    shadow2.setSize(shadow2.getPreferredSize());
	    shadow3.setSize(shadow3.getPreferredSize());
	}
	setSize(getPreferredSize());
    }

    /**
     *
     * @param c
     */
    public void setFontColor (Color c) {
        mainText.setForeground(c);
    }

    /**
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize () {
	if (shadow == null)
	    return mainText.getSize();
	else
	    return new Dimension (mainText.getWidth() + shadowSpacing, mainText.getHeight() + shadowSpacing);
    }

    /**
     *
     * @param c
     */
    @Override
    public void setForeground(Color c) {
        mainText.setForeground(c);
        mainText.repaint();
    }

    @Override
    public Font getFont() {
	return mainText.getFont();
    }

    @Override
    public void setFont(Font font) {
	mainText.setFont(font);
	if (shadow != null) {
	    shadow.setFont(font);
	    shadow2.setFont(font);
	    shadow3.setFont(font);
	}
    }

}
