/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.net.URL;
import lev.gui.LImagePane;
import lev.gui.LProgressBarFrame;
import skyproc.SPGlobal;

/**
 *
 * @author Justin Swanson
 */
public class SPProgressBarFrame extends LProgressBarFrame {
    
    /**
     * 
     * @param header
     * @param headerC
     * @param footer
     * @param footerC
     */
    public SPProgressBarFrame(final Font header, final Color headerC, final Font footer, final Color footerC) {
	super(header, headerC, footer, footerC);
	title.addShadow();
	bar.addShadow();
    }

    /**
     * 
     * @param logo URL of the logo image to display.
     */
    public void addLogo(URL logo) {
	try {
	    LImagePane logoPane = new LImagePane(logo);
	    logoPane.setMaxSize(getWidth() + 50, getHeight() + 50);
	    logoPane.setAlpha((float) 0.3);
	    logoPane.setLocation(getWidth() / 2 - logoPane.getWidth() / 2, getHeight() / 2 - logoPane.getHeight() / 2 - 10);
	    add(logoPane);
	} catch (IOException ex) {
	    SPGlobal.logException(ex);
	}
    }
}
