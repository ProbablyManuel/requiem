/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import lev.gui.resources.LFonts;

/**
 *
 * @author Justin Swanson
 */
public class LProgressBar extends LComponent implements LProgressBarInterface {

    /**
     *
     */
    protected JProgressBar bar;
    /**
     *
     */
    protected boolean pause;
    /**
     *
     */
    protected LLabel status;
    /**
     *
     */
    protected boolean centered = true;
    LCheckBox done = new LCheckBox("", LFonts.Typo3(1), Color.BLACK);

    /**
     *
     * @param width
     * @param height
     * @param footerF
     * @param footerC
     */
    public LProgressBar(final int width, final int height, final Font footerF, final Color footerC) {
	bar = new JProgressBar(0, 100);
	bar.setSize(width, height);
	bar.setLocation(bar.getWidth() / 2, 0);
	bar.setStringPainted(true);
	bar.setVisible(true);

	status = new LLabel(". . .", footerF, footerC);
	status.setLocation(bar.getX() + bar.getWidth() / 2 - status.getWidth() / 2, bar.getY() + bar.getHeight() + 10);

	setSize(bar.getWidth() * 2, status.getY() + status.getHeight());
	add(bar);
	add(status);
	setVisible(true);
    }

    @Override
    public void setSize(int x, int y) {
	super.setSize(x, y);
	bar.setLocation(bar.getWidth() / 2, 0);
    }

    /**
     *
     * @param centered
     */
    public void setCentered(boolean centered) {
	this.centered = centered;
	status.setLocation(bar.getX(), status.getY());
    }

    @Override
    public void setMax(final int max, final String reason) {
	setMax(max);
	setStatus(reason);
    }

    /**
     *
     */
    public void addShadow() {
	status.addShadow();
    }

    @Override
    public void incrementBar() {
	if (!pause) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    bar.setValue((bar.getValue()) + 1);
		}
	    });
	}
    }

    /**
     *
     * @param y
     */
    public void setStatusOffset(int y) {
	status.setLocation(status.getX(), status.getY() + y);
    }

    @Override
    public void setStatus(final String input_) {
	if (!pause) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    int x = status.getX() + status.getWidth() / 2;
		    status.setText(input_);
		    if (centered) {
			status.setLocation(x - status.getWidth() / 2, status.getY());
		    }
		}
	    });
	}
    }

    /**
     *
     * @param c
     */
    public void setDoneListener(ChangeListener c) {
	done = new LCheckBox("", done.getFont(), Color.BLACK);
	done.addChangeListener(c);
    }

    @Override
    public void setMax(final int in) {
	if (!pause) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    bar.setValue(0);
		    bar.setMaximum(in);
		}
	    });
	}
    }

    @Override
    public void reset() {
	if (!pause) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    bar.setValue(0);
		}
	    });
	}
    }

    @Override
    public void setBar(final int in) {
	if (!pause) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    bar.setValue(in);
		}
	    });
	}
    }

    @Override
    public int getBar() {
	return bar.getValue();
    }

    @Override
    public int getMax() {
	return bar.getMaximum();
    }

    @Override
    public void setStatusNumbered(int min, int max, String status) {
	if (!pause) {
	    setStatus("(" + min + "/" + max + ") " + status);
	}
    }

    /**
     *
     * @param status
     */
    @Override
    public void setStatusNumbered(final String status) {
	if (!pause) {
	    SwingUtilities.invokeLater(new Runnable() {

		@Override
		public void run() {
		    setStatus("(" + (getBar() + 1) + "/" + getMax() + ") " + status);
		}
	    });
	}
    }

    /**
     *
     * @param label
     */
    public void setStatusLabel(LLabel label) {
	status.setVisible(false);
	status = label;
    }

    @Override
    public void pause(boolean on) {
	pause = on;
    }

    @Override
    public boolean paused() {
	return pause;
    }

    @Override
    public void done() {
	setBar(getMax());
	done.setSelected(true);
    }
}