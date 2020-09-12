/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 * Interface that SkyProc expects and uses for progress bars.<br>
 * To get progress bar updates from SkyProc importing/exporting, create your own
 * progress bar GUI, and implement this interface.  Then set SPProgressBarPlug
 * to your GUI instance.
 * @author Justin Swanson
 */
public interface LProgressBarInterface {
    /**
     *
     * @param in Value to set as the max unit value of the progress bar.
     */
    void setMax(int in);
    /**
     *
     * @param in Value to set as the max unit value of the progress bar.
     * @param status String to set as the status of the progress bar.
     */
    void setMax(int in, String status);
    /**
     *
     * @param status String to set as the status of the progress bar.
     */
    void setStatus(String status);
    /**
     * Updates the progress bar status text to display: <br>
     * ([cur]/[max]) [status]
     * @param cur
     * @param max
     * @param status
     */
    void setStatusNumbered(int cur, int max, String status);
    /**
     * Updates the progress bar status text to display the current step in the form of: <br>
     * ([cur]/[max]) [status]
     * @param status
     */
    void setStatusNumbered(String status);
    /**
     * Increments the progress bar one unit.
     */
    void incrementBar();
    /**
     * Resets the progress bar to zero of max.
     */
    void reset();
    /**
     *
     * @param in value to set the progress bar at.
     */
    void setBar(int in);
    /**
     *
     * @return Current value of the bar
     */
    int getBar();
    /**
     *
     * @return Current max value of the bar
     */
    int getMax();
    /**
     * Block the progress bar from updating.
     * @param on
     */
    void pause(boolean on);
    /**
     *
     * @return Whether the progress bar is accepting updates.
     */
    boolean paused ();
    /**
     * Sets the current value to the max value (100%).
     */
    void done();
}
