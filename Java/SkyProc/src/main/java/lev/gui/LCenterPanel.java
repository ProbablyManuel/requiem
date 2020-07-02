/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Component;
import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public class LCenterPanel extends LPanel {

    /**
     *
     */
    protected ArrayList<Component> components = new ArrayList<>();

    @Override
    public void Add(Component input) {
	super.Add(input);
	components.add(input);
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	int height = 0;
	align(Align.Center);
	for (Component c : components) {
	    height += c.getHeight() + spacing;
	}

	last.y = y / 2 - height / 2;
	last.x = x / 2;

	for (Component c : components) {
	    setPlacement(c);
	}
    }
}
