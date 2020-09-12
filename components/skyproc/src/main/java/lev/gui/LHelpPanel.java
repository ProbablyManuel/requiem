/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import lev.gui.resources.LImages;

/**
 * A panel that displays help information.
 *
 * @author Justin Swanson
 */
public class LHelpPanel extends LPanel {

    /**
     *
     */
    protected LLabel setting;
    /**
     *
     */
    protected LTextPane help;
    /**
     *
     */
    protected Image arrow;
    /**
     *
     */
    protected int arrowx;
    /**
     *
     */
    protected int y;
    /**
     *
     */
    protected int textOffset = 0;
    /**
     *
     */
    protected LPanel bottomArea;
    /**
     *
     */
    protected boolean textVisible = true;
    /**
     *
     */
    protected boolean hideArrow = false;
    /**
     *
     */
    protected boolean following = true;

    /**
     *
     * @param bounds
     * @param titleFont
     * @param titleC
     * @param contentC
     * @param arrow
     * @param arrowX
     */
    public LHelpPanel(Rectangle bounds, Font titleFont, Color titleC, Color contentC, Image arrow, int arrowX) {
	y = - 100;
	arrowx = arrowX;
	this.arrow = arrow;

	setBounds(bounds);

	setting = new LLabel("  ", titleFont, titleC);
	setting.setLocation(17, 0);

	help = new LTextPane(new Dimension(getWidth() - 35, getHeight()), contentC);
	help.addScroll();
	help.setEditable(false);
	help.setLocation(35, 0);
	add(setting);
	add(help);
	setting.setVisible(textVisible);
	help.setVisible(textVisible);

	bottomArea = new LPanel();
	bottomArea.setSize((int) bounds.getWidth(), 190);
	bottomArea.setLocation(0, (int) bounds.getHeight() - bottomArea.getHeight());
	bottomArea.setVisible(false);
	add(bottomArea);

	setVisible(true);
    }

    @Override
    public void revalidate() {
	super.revalidate();
	if (bottomArea != null) {
	    bottomArea.revalidate();
	}
    }

    @Override
    public void repaint() {
	super.repaint();
	if (bottomArea != null) {
	    bottomArea.repaint();
	}
    }

    @Override
    public void paintComponent(Graphics g) {
	if (textVisible && !hideArrow && following && arrow != null) {
	    g.drawImage(arrow, arrowx, y - arrow.getHeight(null) / 2, null);
	}
    }

    /**
     *
     * @param c
     */
    public void setHeaderColor(Color c) {
	setting.setForeground(c);
    }

    /**
     *
     * @param f
     */
    public void setHeaderFont (Font f) {
	setting.setFont(f);
    }

    /**
     *
     * @param y
     */
    public void setDefaultY(int y) {
	spacing = y;
    }

    /**
     *
     * @param title_
     */
    public void setTitle(String title_) {
	setting.setText("   " + title_);
	setting.setSize(setting.getPreferredSize());
    }

    /**
     *
     * @param y
     */
    public void setTitleOffset(int y) {
	textOffset = y;
    }

    /**
     * Sets the X offset for the title and the help text.
     * @param title
     * @param helpText
     */
    public void setXOffsets(int title, int helpText) {
	setting.setLocation(title, setting.getY());
	help.setLocation(helpText, help.getY());
    }

    /**
     *
     * @param y_
     */
    public void setY(int y_) {
	if (y_ == -1 || !following) {
	    y = spacing;
	} else {
	    y = y_;
	}
	setting.setLocation(setting.getX(), y - setting.getHeight() / 2 + textOffset);
	help.setLocation(help.getX(), y + setting.getHeight() / 2 + textOffset);
	evalPositioning();
	repaint();
    }

    /**
     * Makes help section move to point at desired Component with a y offset.
     * @param c
     * @param offset
     */
    public void focusOn(Component c, int offset) {
	setY(c.getY() + c.getHeight() / 2 + offset);
    }

    /**
     * Moves the help section back to its default position.
     */
    public void setDefaultPos() {
	setY(-1);
    }

    /**
     *
     * @param text
     */
    public void setContent(String text) {
	hideArrow = false;
	help.setText(text);
	evalPositioning();
    }

    private void evalPositioning() {
	// Divide by two as setting + help are shifted up that much in positioning
	int min = getLimit() - spacing - setting.getHeight() / 2;
	if (min > help.getPreferredSize().height) {
	    min = help.getPreferredSize().height;
	}

	help.setSize(help.getWidth(), min);
	int helpReach = setting.getY() + setting.getHeight() + help.getHeight();
	if (helpReach > getLimit()) {
	    int move = helpReach - getLimit();
	    help.setLocation(help.getX(), help.getY() - move);
	    setting.setLocation(setting.getX(), setting.getY() - move);
	}
    }

    private int getLimit() {
	if (bottomArea != null && bottomArea.isVisible()) {
	    return getHeight() - bottomArea.getHeight();
	} else {
	    return getHeight() - 25;
	}
    }

    /**
     * Appends text to the content string.
     *
     * @param text
     */
    public void addContent(String text) {
	help.append(text);
	evalPositioning();
    }

    /**
     * Clears the bottom panel of the help area.
     */
    public void clearBottomArea() {
	bottomArea.removeAll();
	bottomArea.setVisible(false);
    }

    /**
     * Adds a component to the separate bottom area panel.
     *
     * @param c
     */
    public void addToBottomArea(Component c) {
	bottomArea.Add(c);
	bottomArea.setVisible(true);
    }

    /**
     *
     * @param y
     */
    public void setBottomAreaHeight(int y) {
	int limit = bottomArea.getY() + bottomArea.getHeight();
	bottomArea.setSize(bottomArea.getWidth(), y);
	bottomArea.setLocation(0, limit - bottomArea.getHeight());
    }

    /**
     *
     * @param on
     */
    public void setBottomAreaVisible(boolean on) {
	bottomArea.setVisible(on);
    }

    /**
     *
     * @param b
     */
    public void textVisible(Boolean b) {
	textVisible = b;
	setting.setVisible(b);
	help.setVisible(b);
	repaint();
    }

    /**
     *
     */
    public void hideArrow() {
	hideArrow = true;
    }

    /**
     *
     * @return
     */
    public Dimension getBottomSize() {
	return bottomArea.getSize();
    }

    /**
     * Hides the help panel and resets it for next time.
     */
    public void reset (){
	clearBottomArea();
	setTitle("");
	setContent("");
	hideArrow();
	repaint();
    }
}
