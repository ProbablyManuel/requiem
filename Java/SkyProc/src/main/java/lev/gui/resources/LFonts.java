/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui.resources;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

/**
 * Collection of embedded fonts that can be used.
 *
 * @author Justin Swanson
 */
public class LFonts {

    /**
     *
     * @param size
     * @return
     */
    public static Font Typo3(float size) {
	return getFont("Typo3-Medium.ttf", size);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font Neuropol(float size){
	return getFont("NEUROPOL.ttf", size);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font OptimusPrinceps(float size) {
	return getFont("OptimusPrincepsSemiBold.ttf", size);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font Oleo(float size) {
	return getFont("OLEO.ttf", size);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font MyriadPro (float size) {
	return getFont("MyriadPro-Regular.ttf", size);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font MyriadProBold (float size) {
	return getFont("myriadwebpro-bold.ttf", size);
    }

    /**
     *
     * @param size
     * @return
     */
    public static Font ReasonSystem (float size) {
	return getFont("reasonSystem-Regular.ttf", size);
    }

    static Font getFont(String path, float size) {
	try {
	    Font font = Font.createFont(Font.TRUETYPE_FONT, LFonts.class.getResource(path).openStream());
	    return font.deriveFont(Font.PLAIN, size);
	} catch (IOException | FontFormatException ex) {
	}
	return new Font("Serif", 3, 3);
    }
}
