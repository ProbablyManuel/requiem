/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
class SaveBool extends Setting<Boolean> {

    public SaveBool(String title_, Boolean data_, Boolean[] extraFlags) {
        super(title_, data_, extraFlags);
    }

    @Override
    public String toString() {
        return Ln.convertBoolTo1(data);
    }

    @Override
    public void parse(String in) {
        data = Ln.toBool(in);
    }

    @Override
    public Setting<Boolean> copyOf() {
        SaveBool out = new SaveBool(title, data, extraFlags);
        out.tie = tie;
        return out;
    }

}
