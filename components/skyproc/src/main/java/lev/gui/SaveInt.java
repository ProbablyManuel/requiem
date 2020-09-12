/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
class SaveInt extends Setting<Integer> {

    public SaveInt(String title_, Integer data_, Boolean[] extraFlags) {
        super(title_, data_, extraFlags);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void parse(String in) {
        data = Integer.valueOf(in);
    }

    @Override
    public Setting<Integer> copyOf() {
        SaveInt out = new SaveInt(title, data, extraFlags);
        out.tie = tie;
        return out;
    }

}
