/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.event.*;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A GUI component that updates a help panel with text.
 *
 * @author Justin Swanson
 */
public abstract class LHelpComponent extends LComponent {

    /**
     * The target help panel.
     */
    protected LHelpPanel help = null;
    /**
     *
     */
    protected String helpPrefix = "";
    /**
     *
     */
    protected boolean followPos = true;
    /**
     *
     */
    protected Enum saveTie;
    /**
     *
     */
    protected LSaveFile save;
    /**
     * The title to put at the top of the help panel.
     */
    protected String title;
    /**
     * Amount to vertically offset the help text.
     */
    protected int helpYoffset = 0;

    /**
     *
     * @param title
     */
    public LHelpComponent(String title) {
	this.title = title;
    }

    /**
     * Adds a help handler to each GUI component that should trigger the help
     * panel to update.
     *
     * @param hoverListener
     */
    protected abstract void addHelpHandler(boolean hoverListener);

    /**
     * Updates the target help panel with this component's help info.
     */
    public void updateHelp() {
	if (help != null) {
	    help.setTitle(helpPrefix + title);
	    help.setContent(getHelp());
	    if (followPos) {
		help.focusOn(this, helpYoffset);
	    } else {
		help.setDefaultPos();
		help.hideArrow();
	    }
	}
    }

    /**
     *
     */
    public class HelpActionHandler implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent event) {
	    SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    updateHelp();
		}
	    });

	}
    }

    /**
     *
     */
    public class HelpFocusHandler implements FocusListener {

	@Override
	public void focusGained(FocusEvent event) {
	    SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    updateHelp();
		}
	    });

	}

	@Override
	public void focusLost(FocusEvent event) {
//            SwingUtilities.invokeLater(new Runnable() {
//
//                public void run() {
//                    if (help != null) {
//                        help.textVisible(false);
//                    }
//                }
//            });
	}
    }

    /**
     *
     */
    public class HelpMouseHandler implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	    updateHelp();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	    updateHelp();
	}
    }

    class HelpListHandler implements ListSelectionListener {

	@Override
	public void valueChanged(ListSelectionEvent e) {
	    SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    updateHelp();
		}
	    });
	}
    }

    class HelpChangeHandler implements ChangeListener {

	@Override
	public void stateChanged(ChangeEvent e) {
	    SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    updateHelp();
		}
	    });
	}
    }

    /**
     * Adds a prefix to the title.
     *
     * @param input
     */
    public void addHelpPrefix(String input) {
	helpPrefix = input + " ";
    }

    /**
     * Sets the target help panel, and sets the help info to the setting's
     * helpInfo in the savefile.
     *
     * @param setting
     * @param save
     * @param help_
     * @param hoverListener
     */
    public void linkTo(Enum setting, LSaveFile save, LHelpPanel help_, boolean hoverListener) {
	help = help_;
	this.save = save;
	this.saveTie = setting;
	if (hasHelp()) {
	    addHelpHandler(hoverListener);
	}
    }

    /**
     *
     * @return
     */
    public boolean hasHelp() {
	return getHelp() != null;
    }

    /**
     *
     * @return
     */
    public String getHelp() {
	return save.helpInfo.get(saveTie);
    }

    /**
     * Sets whether the helpPanel should vertically align with this component
     * when updating.
     *
     * @param on
     */
    public void setFollowPosition(boolean on) {
	followPos = on;
    }

    /**
     *
     * @return
     */
    public boolean isFollowingPosition() {
	return followPos;
    }
}
