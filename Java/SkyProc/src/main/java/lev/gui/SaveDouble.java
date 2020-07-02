/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
public class SaveDouble extends Setting<Double> {

    /**
     *
     * @param title_
     * @param data_
     * @param extraFlags
     */
    public SaveDouble(String title_, Double data_, Boolean[] extraFlags) {
        super(title_, data_, extraFlags);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void parse(String in) {
        data = Double.valueOf(in);
    }

    @Override
    public Setting<Double> copyOf() {
        SaveDouble out = new SaveDouble(title, data, extraFlags);
        out.tie = tie;
        return out;
    }

}