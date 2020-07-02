/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;

/**
 *
 * @author Justin Swanson
 */
class SaveColor extends Setting<Color> {

    public SaveColor(String title_, Color data_, Boolean[] extraFlags) {
	super(title_, extraFlags);
	Integer[] color = new Integer[4];
	color[0] = data_.getRed();
	color[1] = data_.getBlue();
	color[2] = data_.getGreen();
	color[3] = data_.getAlpha();
	setTo(data_);
    }

    private SaveColor(String title_, Integer[] color, Boolean[] extraFlags) {
	super(title_, extraFlags);
	Color c = new Color(color[0], color[1], color[2], color[3]);
	setTo(c);
    }

    @Override
    public String toString() {
	String out = "";
	out += data.getRed() + "-";
	out += data.getGreen() + "-";
	out += data.getBlue() + "-";
	out += data.getAlpha();
	return out;
    }

    @Override
    public void parse(String in) {
	String[] split = in.split("-");
	Integer[] ints = new Integer[4];
	for (int i = 0; i < 4; i++) {
	    ints[i] = Integer.parseInt(split[i]);
	}
	data = new Color(ints[0], ints[1], ints[2], ints[3]);
    }

    @Override
    public Setting<Color> copyOf() {
	SaveColor out = new SaveColor(title, data, extraFlags);
	out.tie = tie;
	return out;
    }
}
