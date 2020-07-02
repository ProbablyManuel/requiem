/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * GUI related nifty functions
 *
 * @author Justin Swanson
 */
public class Lg {

    /**
     *
     * @param originalImage Image to resize
     * @param size size to convert to
     * @return Resized image
     */
    static public BufferedImage resizeImage(BufferedImage originalImage, Dimension size) {
	int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
	BufferedImage resizedImage = new BufferedImage(size.width, size.height, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, size.width, size.height, null);
	g.dispose();

	return resizedImage;
    }

    /**
     *
     * @param originalImage Image to resize
     * @param size size to convert to
     * @return Resized image
     */
    static public BufferedImage resizeImageWithHint(BufferedImage originalImage, Dimension size) {
	if (originalImage.getWidth() == size.width && originalImage.getHeight() == size.height) {
	    return originalImage;
	}

	int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
	BufferedImage resizedImage = new BufferedImage(size.width, size.height, type);
	Graphics2D g = resizedImage.createGraphics();
	g.drawImage(originalImage, 0, 0, size.width, size.height, null);
	g.dispose();
	g.setComposite(AlphaComposite.Src);

	g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

	return resizedImage;
    }

    /**
     * This function returns the minimum dimensions to fit inside (maxx,maxy)
     * while retaining the aspect ratio of the original (x,y)
     *
     * @param x Original width
     * @param y Original height
     * @param maxX Max width
     * @param maxY Max height
     * @return New dimensions fitting inside limits, while retaining aspect
     * ratio.
     */
    static public Dimension calcSize(double x, double y, int maxX, int maxY) {
	if (maxX == 0) {
	    maxX = Integer.MAX_VALUE;
	}
	if (maxY == 0) {
	    maxY = Integer.MAX_VALUE;
	}
	double xMod = 1.0 * maxX / x;
	double yMod = 1.0 * maxY / y;
	double mod = (xMod <= yMod) ? xMod : yMod;
	if (mod < 1) {
	    x = x * mod;
	    y = y * mod;
	}
	return new Dimension((int) x, (int) y);
    }

    /**
     * Returns the height of the taller component.
     *
     * @param a
     * @param b
     * @return
     */
    static public int taller(Component a, Component b) {
	if (a.getHeight() > b.getHeight()) {
	    return a.getHeight();
	}
	return b.getHeight();
    }

    /**
     * Creates an Alpha Composite with the given transparency
     *
     * @param trans
     * @return
     */
    static public Composite getAlphaComposite(float trans) {
	return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, trans);
    }

    /**
     * Returns the spacing that should be given to the components.
     *
     * @param horiz Horizontal spacing vs vertical
     * @param allocated How much space the components should take up
     * @param cs the components to space
     * @return the spacing
     */
    static public int getSpacing(boolean horiz, int allocated, Component... cs) {
	for (Component c : cs) {
	    if (horiz) {
		allocated -= c.getWidth();
	    } else {
		allocated -= c.getHeight();
	    }
	}
	return allocated / (cs.length + 1);
    }

    static public JEditorPane getQuickHTMLPane(String str) {
	JEditorPane ep = new JEditorPane("text/html", str);

	// handle link events
	ep.addHyperlinkListener(new HyperlinkListener() {

	    @Override
	    public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
		    try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(e.getURL().toString()));
		    } catch (IOException ex) {
			Logger.getLogger(Lg.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    }
	});
	ep.setEditable(false);
	return ep;
    }
}
