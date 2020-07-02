/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Collection of embedded images to be used in GUIs.
 *
 * @author Justin Swanson
 */
public class LImages {

    /**
     *
     * @return The multipurpose background image used in DefaultGUI.
     */
    public static BufferedImage multipurpose() {
	return create(LImages.class.getResource("multipurpose.png"));
    }

    /**
     * Returns an arrow graphic pointing left or right.
     *
     * @param leftArrow
     * @param dark 
     * @return
     */
    public static BufferedImage arrow(boolean leftArrow, boolean dark) {
	if (leftArrow) {
	    if (dark) {
		return create(LImages.class.getResource("ArrowLeftDark.png"));
	    } else {
		return create(LImages.class.getResource("ArrowLeft.png"));
	    }
	} else {
	    if (dark) {
		return create(LImages.class.getResource("ArrowRightDark.png"));
	    } else {
		return create(LImages.class.getResource("ArrowRight.png"));
	    }
	}
    }

    static BufferedImage create(URL image) {
	try {
	    return ImageIO.read(image);
	} catch (IOException ex) {
	}
	return null;
    }
}
