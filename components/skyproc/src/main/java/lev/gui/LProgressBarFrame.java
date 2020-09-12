/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import lev.gui.resources.LImages;

/**
 * A GUI window frame for LProgress bar. Used in SUMGUI.
 *
 * @author Justin Swanson
 */
public class LProgressBarFrame extends JFrame implements LProgressBarInterface {

    /**
     *
     */
    protected LProgressBar bar;
    /**
     *
     */
    protected LLabel title;
    /**
     *
     */
    protected Dimension correctLocation = new Dimension(0, 0);
    /**
     *
     */
    protected Dimension GUIsize = new Dimension(250, 100);
    /**
     *
     */
    protected LImagePane backgroundPanel;
    /**
     *
     */
    protected JFrame guiRef;
    /**
     * JFrame operation to execute when the window is closed.
     */

    /**
     *
     * @param header
     * @param headerC
     * @param footer
     * @param footerC
     */
    public LProgressBarFrame(final Font header, final Color headerC, final Font footer, final Color footerC) {
	bar = new LProgressBar(150, 15, footer, footerC);
	addComponents(header, headerC);
	moveToCorrectLocation();
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    final void addComponents(Font header, Color headerC) {
	backgroundPanel = new LImagePane(LImages.multipurpose());
	backgroundPanel.setVisible(true);
	super.add(backgroundPanel);

	setSize(GUIsize);
	setResizable(false);
	setLayout(null);

	title = new LLabel("Please wait...", header, headerC);
	setSize(250, 72);
	bar.setLocation(getWidth() / 2 - bar.getWidth() / 2, getHeight() / 2 - bar.getHeight() / 2 + 10);
	title.setLocation(getWidth() / 2 - title.getWidth() / 2, 2);

	setSize(250, 100);
	backgroundPanel.add(bar);
	backgroundPanel.add(title);
	setResizable(false);
    }

    /**
     * Sets the progress bar to exit the program when it is closed.
     */
    public void setExitOnClose() {
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void setMax(final int max, final String reason) {
	bar.setMax(max, reason);
    }

    @Override
    public void incrementBar() {
	bar.incrementBar();
    }

    /**
     * Sets the location to display relative to the GUI ref object assigned.
     *
     * @param x
     * @param y
     */
    public void setCorrectLocation(int x, int y) {
	correctLocation = new Dimension(x, y);
    }

    /**
     * Moves the progress bar window to the relatively correct location,
     * relative to the GUI ref object assigned.
     */
    public final void moveToCorrectLocation() {
	if (guiRef != null) {
	    Rectangle r = guiRef.getBounds();
	    setLocation(r.x + correctLocation.width, r.y + correctLocation.height);
	} else {
	    setLocation(correctLocation.width, correctLocation.height);
	}
    }

    @Override
    public void setStatus(final String input_) {
	bar.setStatus(input_);
    }

    /**
     * Opens and displays the progress bar frame.
     */
    public void open() {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		moveToCorrectLocation();
		setVisible(true);
	    }
	});
    }

    /**
     * Opens and displays the progress bar frame, and adds a listener that will
     * execute when the progress bar is done.
     *
     * @param c
     */
    public void open(ChangeListener c) {
	open();
	setDoneListener(c);
    }

    void setDoneListener(ChangeListener c) {
	bar.setDoneListener(c);
    }

    /**
     * Makes the progress bar invisible.
     */
    public void close() {
	setVisible(false);
    }

    /**
     * Sets the GUI ref object for relative positioning.
     *
     * @param ref
     */
    public void setGUIref(JFrame ref) {
	guiRef = ref;
    }

    @Override
    public void setMax(int in) {
	bar.setMax(in);
    }

    @Override
    public void reset() {
	bar.reset();
    }

    @Override
    public void setBar(int in) {
	bar.setBar(in);
    }

    @Override
    public int getBar() {
	return bar.getBar();
    }

    @Override
    public int getMax() {
	return bar.getMax();
    }

    @Override
    public void setStatusNumbered(int min, int max, String status) {
	bar.setStatusNumbered(min, max, status);
    }

    /**
     *
     * @param status
     */
    @Override
    public void setStatusNumbered(String status) {
	bar.setStatusNumbered(status);
    }

    @Override
    public void pause(boolean on) {
	bar.pause(on);
    }

    @Override
    public boolean paused() {
	return bar.paused();
    }

    @Override
    public void done() {
	bar.done();
    }

    @Override
    public Component add(Component c) {
	backgroundPanel.add(c);
	return c;
    }
}