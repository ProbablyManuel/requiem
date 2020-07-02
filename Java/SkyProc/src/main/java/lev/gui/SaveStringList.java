/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public class SaveStringList extends Setting<ArrayList<String>> {

    static String delimiter = "<#>";

    /**
     *
     * @param title_
     * @param data_
     * @param extraFlags
     */
    public SaveStringList(String title_, ArrayList<String> data_, Boolean[] extraFlags) {
	super(title_, data_, extraFlags);
    }

    @Override
    public String toString() {
	String out = "";
	int num = 0;
	for (String f : data) {
	    if (!f.equals("")) {
		out += f + delimiter + "\n";
		num++;
	    }
	}
	return num + "\n" + out;
    }

    @Override
    public void parse(String in) {
	String[] split = in.split(delimiter);
	data = new ArrayList<>(split.length);
	for (int i = 0; i < split.length; i++) {
	    split[i] = split[i].trim();
	    if (!"".equals(split[i])) {
		data.add(split[i]);
	    }
	}
    }

    @Override
    public Setting<ArrayList<String>> copyOf() {
	SaveStringList out = new SaveStringList(title, data, extraFlags);
	out.data = new ArrayList<>(data);
	out.tie = tie;
	return out;
    }
}