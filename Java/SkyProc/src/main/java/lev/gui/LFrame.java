/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.JFrame;

/**
 *
 * @author Justin Swanson
 */
public class LFrame extends JFrame {

    /**
     *
     */
    protected LImagePane background;
    private static int marginX = 16;
    private static int marginY = 38;
    private boolean init = false;

    /**
     *
     * @param title
     */
    public LFrame (String title) {
	super(title);
	setLayout(null);
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	background = new LImagePane();
	this.getContentPane().add(background);
    }

    /**
     * Initialize the LFrame's GUI contents.  Will only be called the first time open() is called.
     */
    protected void init() {
    }

    /**
     *
     */
    public void open() {
	if (!init) {
	    init();
	    init = true;
	}
	setVisible(true);
    }

    @Override
    public void setBackground(Color c) {
	getContentPane().setBackground(c);
    }

    /**
     *
     * @return
     */
    public Dimension getRealSize() {
	return new Dimension(getRealWidth(), getRealHeight());
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setRealSize(int x, int y) {
	setSize(x + marginX, y + marginY);
    }

    /**
     *
     * @param size
     */
    public void setRealSize(Dimension size) {
	setRealSize(size.width, size.height);
    }

    /**
     *
     * @return
     */
    public int getRealWidth() {
	return getWidth() - marginX;
    }

    /**
     *
     * @return
     */
    public int getRealHeight () {
	return getHeight() - marginY;
    }

    /**
     *
     * @return
     */
    public Point defaultLocation() {
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int y = 5;
	int x = screen.width / 2 - getWidth() / 2;
	if (x < 0) {
	    x = 0;
	}
	return new Point(x, y);
    }

    /**
     *
     * @return
     */
    public Point centerScreen() {
	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	int y = screen.height / 2 - getHeight() / 2;
	int x = screen.width / 2 - getWidth() / 2;
	if (x < 0) {
	    x = 0;
	}
	if (y < 0) {
	    y = 0;
	}
	return new Point(x, y);
    }

    /**
     * Remeasures the internal contents to match the current size of the frame.
     */
    public void remeasure () {
	background.setMaxSize(getRealWidth(), 0);
    }
}
