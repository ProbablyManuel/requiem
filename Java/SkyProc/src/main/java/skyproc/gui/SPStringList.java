/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import lev.gui.LTextField;

/**
 *
 * @author Justin Swanson
 */
public class SPStringList extends SPList<String> {

    LTextField adder;

    /**
     *
     * @param title
     * @param font
     * @param color
     */
    public SPStringList(String title, Font font, Color color) {
	super(title, font, color);

	adder = new LTextField("Adder");
	adder.addEnterButton("Add", new ActionListener(){

	    @Override
	    public void actionPerformed(ActionEvent e) {
		addElement(adder.getText());
		adder.setText("");
	    }
	});
	adder.setLocation(0, this.titleLabel.getY() + this.titleLabel.getHeight() + 10);
	Add(adder);

	scroll.setLocation(scroll.getX(), adder.getBottom() + 10);
    }

    /**
     * 
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	adder.setSize(width, adder.getHeight());
	scroll.setSize(scroll.getWidth(), height - adder.getHeight() - remove.getHeight() - 52);
	if (accept != null) {
	    remove.putUnder(scroll, 0, 10);
	    accept.setSize(remove.getSize());
	    accept.putUnder(scroll, remove.getRight() + spacing, 10);
	} else {
	    remove.centerOn(scroll, scroll.getY() + scroll.getHeight() + 10);
	}
    }


}
