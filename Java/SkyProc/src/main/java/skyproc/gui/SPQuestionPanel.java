/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import lev.gui.LButton;
import lev.gui.LTextPane;

/**
 *
 * @author Justin Swanson
 */
public abstract class SPQuestionPanel extends SPSettingPanel {

    /**
     * Textbox with the question text to display.
     */
    protected LTextPane question;
    /**
     *
     */
    protected LButton cancelButton;
    /**
     * Panel to switch to when cancelling.
     */
    protected SPSettingPanel cancelPanel;
    /**
     *
     */
    protected LButton backButton;
    /**
     * Panel to switch to when pressing back.
     */
    protected SPSettingPanel backPanel;
    /**
     *
     */
    protected LButton nextButton;
    /**
     * Panel to switch to when pressing next.
     */
    protected SPSettingPanel nextPanel;

    /**
     *
     * @param title
     * @param parent_
     * @param headerColor
     * @param cancel Panel to open when cancel is pressed.
     * @param back Panel to open when back is pressed.
     */
    public SPQuestionPanel(SPMainMenuPanel parent_, String title, Color headerColor,
	    SPSettingPanel cancel, SPSettingPanel back) {
	super(parent_, title, headerColor);
	this.cancelPanel = cancel;
	this.backPanel = back;
    }

    @Override
    protected void initialize() {
	super.initialize();

	question = new LTextPane(settingsPanel.getWidth() - 20, 20, SUMGUI.light);
	question.setEditable(false);
	question.centerIn(settingsPanel, header.getBottom() + 10);
	settingsPanel.add(question);

	last = new Point(last.x, question.getBottom());

	cancelButton = new LButton("Cancel");
	cancelButton.setLocation(15, settingsPanel.getHeight() - cancelButton.getHeight() - 10);
	cancelButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (testCancel()) {
		    int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "Confirm Cancel",
			    JOptionPane.YES_NO_OPTION);
		    if (answer == JOptionPane.YES_OPTION) {
			onCancel();
			cancelPanel.open();
		    }
		}
	    }
	});
	settingsPanel.add(cancelButton);

	backButton = new LButton("Back");
	backButton.setLocation(settingsPanel.getWidth() / 2 - backButton.getWidth() / 2, settingsPanel.getHeight() - backButton.getHeight() - 10);
	backButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (testBack()) {
		    onBack();
		    backPanel.open();
		}
	    }
	});
	settingsPanel.add(backButton);

	nextButton = new LButton("Next");
	nextButton.setLocation(settingsPanel.getWidth() - nextButton.getWidth() - 15, settingsPanel.getHeight() - cancelButton.getHeight() - 10);
	nextButton.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (testNext()) {
		    onNext();
		    nextPanel.open();
		}
	    }
	});
	settingsPanel.add(nextButton);

	setCancel(cancelPanel);
	setBack(backPanel);
	setNext(nextPanel);
    }

    /**
     *
     * @param in Panel to switch to when cancelling.
     */
    public void setCancel(SPSettingPanel in) {
	cancelPanel = in;
	cancelButton.setVisible(in != null);
    }

    /**
     *
     * @param in Panel to switch to when pressing back.
     */
    public void setBack(SPSettingPanel in) {
	backPanel = in;
	backButton.setVisible(in != null);
    }

    /**
     *
     * @param in Panel to switch to when pressing next.
     */
    public void setNext(SPSettingPanel in) {
	nextPanel = in;
	nextButton.setVisible(in != null);
    }

    /**
     *
     * @return Test to see if pressing cancel should be allowed by user.
     */
    public boolean testCancel() {
	return true;
    }

    /**
     *
     * @return Test to see if pressing back should be allowed by user.
     */
    public boolean testBack() {
	return true;
    }

    /**
     *
     * @return Test to see if pressing next should be allowed by user.
     */
    public boolean testNext() {
	return true;
    }

    /**
     * Code to run when cancelling.
     */
    public void onCancel() {
    }

    /**
     * Code to run when pressing back.
     */
    public void onBack() {
    }

    /**
     * Code to run when pressing next.
     */
    public void onNext() {
    }

    /**
     *
     * @param f
     */
    public void setQuestionFont(Font f) {
	question.setFont(f);
    }

    /**
     *
     */
    public void setQuestionCentered() {
	question.setCentered();
    }

    /**
     *
     * @param c
     */
    public void setQuestionColor(Color c) {
	question.setForeground(c);
    }

    /**
     *
     * @param t
     */
    public void setQuestionText(String t) {
	question.setText(t);
	question.setSize(question.getWidth(), (int) question.getPreferredSize().getHeight());
	updateLast(question);
    }

    /**
     *
     * @return
     */
    public String getQuestionText() {
	return question.getText();
    }
}