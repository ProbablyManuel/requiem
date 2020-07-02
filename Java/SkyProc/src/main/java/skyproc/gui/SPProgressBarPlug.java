package skyproc.gui;

import java.util.ArrayList;
import lev.gui.LProgressBarInterface;

/**
 * A boundary class that lets SkyProcs importing interface with custom GUIs
 *
 * @author Justin Swanson
 */
public class SPProgressBarPlug {

    static final ArrayList<LProgressBarInterface> bars = new ArrayList<>();

    static {
	bars.add(new Placeholder());
    }

    /**
     * Adds a progress bar to the list of bars to be updated with SkyProc
     * import/export data.
     *
     * @param progressBar
     */
    public static void addProgressBar(LProgressBarInterface progressBar) {
	bars.add(progressBar);
	progressBar.setBar(getBar());
	progressBar.setMax(getMax());
	progressBar.pause(paused());
    }

    static class Placeholder implements LProgressBarInterface {

	int max = 0;
	int cur = 0;
	boolean paused = false;

	@Override
	public void setMax(int in) {
	    if (!paused) {
		max = in;
	    }
	}

	@Override
	public void setMax(int in, String status) {
	    setMax(in);
	}

	@Override
	public void setStatus(String status) {
	}

	@Override
	public void setStatusNumbered(int cur, int max, String status) {
	}

	@Override
	public void setStatusNumbered(String status) {
	}

	@Override
	public void incrementBar() {
	    if (!paused) {
		cur++;
	    }
	}

	@Override
	public void reset() {
	    if (!paused) {
		cur = 0;
	    }
	}

	@Override
	public void setBar(int in) {
	    if (!paused) {
		cur = in;
	    }
	}

	@Override
	public int getBar() {
	    return cur;
	}

	@Override
	public int getMax() {
	    return max;
	}

	@Override
	public void pause(boolean on) {
	    paused = on;
	}

	@Override
	public boolean paused() {
	    return paused;
	}

	@Override
	public void done() {
	    setBar(getMax());
	}
    }

    /**
     *
     * @param in Value to set as the max unit value of the progress bar.
     */
    public static void setMax(int in) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setMax(in);
	    }
	}
    }

    /**
     *
     * @param in Value to set as the max unit value of the progress bar.
     * @param status String to set as the status of the progress bar.
     */
    public static void setMax(int in, String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setMax(in, status);
	    }
	}
    }

    /**
     * @param status String to set as the status of the progress bar.
     */
    public static void setStatus(String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setStatus(status);
	    }
	}
    }

    /**
     * Updates the progress bar status text to display: <br> ([cur]/[max])
     * [status]
     *
     * @param cur
     * @param max
     * @param status
     */
    public static void setStatusNumbered(int cur, int max, String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setStatusNumbered(cur, max, status);
	    }
	}
    }

    /**
     *
     * Updates the progress bar status text to display the current step in the
     * form of: <br> ([cur]/[max]) [status]
     *
     * @param status
     */
    public static void setStatusNumbered(String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setStatusNumbered(status);
	    }
	}
    }

    /**
     * Increments the progress bar one unit.
     */
    public static void incrementBar() {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.incrementBar();
	    }
	}
    }

    /**
     * Resets the progress bar to zero of max.
     */
    public static void reset() {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.reset();
	    }
	}
    }

    /**
     *
     * @param in value to set the progress bar at.
     */
    public static void setBar(int in) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setBar(in);
	    }
	}
    }

    /**
     *
     * @return Current value of the bar
     */
    public static int getBar() {
	return bars.get(0).getBar();
    }

    /**
     *
     * @return Current max value of the bar
     */
    public static int getMax() {
	return bars.get(0).getMax();
    }

    /**
     * Block the progress bar from updating.
     * @param on
     */
    public static void pause(boolean on) {
	for (LProgressBarInterface p : bars) {
	    p.pause(on);
	}
    }

    /**
     *
     * @return Whether the progress bar is accepting updates.
     */
    public static boolean paused() {
	return bars.get(0).paused();
    }

    /**
     * Sets the current value to the max value (100%).
     */
    public static void done() {
	for (LProgressBarInterface p : bars) {
	    p.done();
	}
    }
}
