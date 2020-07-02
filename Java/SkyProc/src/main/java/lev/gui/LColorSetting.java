/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JColorChooser;

/**
 *
 * @author Justin Swanson
 */
public class LColorSetting extends LUserSetting<Color> {

    /**
     *
     */
    protected Rectangle box = new Rectangle(0, 0, 12, 12);
    /**
     *
     */
    protected Rectangle boxOutline;
    /**
     *
     */
    protected Color color;
    /**
     *
     */
    protected ArrayList<Runnable> actionListeners = new ArrayList<>(0);

    /**
     *
     * @param text
     * @param font
     * @param fontColor
     * @param pickerStart
     */
    public LColorSetting(String text, Font font, Color fontColor, Color pickerStart) {
	super(text, font, fontColor);
	color = pickerStart;

	int spacing = 5;
	int outline = 2;

	boxOutline = new Rectangle(0, 0, box.width + 2 * outline, box.height + 2 * outline);

	boxOutline.x = titleLabel.getWidth() + spacing;
	box.x = boxOutline.x + outline;
	boxOutline.y = titleLabel.getHeight() / 2 - boxOutline.height / 2;
	box.y = boxOutline.y + outline;

	int y = titleLabel.getHeight();
	if (y < boxOutline.height) {
	    y = boxOutline.height;
	}
	setSize(boxOutline.x + boxOutline.width, y);
	setOutline(true);
    }

    @Override
    protected void addUpdateHandlers() {
	addMouseListener(new ColorPickHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
	if (isTied()) {
	    Color cur = color;
	    color = m.get(saveTie).getColor();
	    repaint();
	    if (cur != color) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public Color getValue() {
	return color;
    }

    @Override
    public void highlightChanged() {
    }

    @Override
    public void clearHighlight() {
    }

    final void setOutline(boolean small) {
	int size;
	if (small) {
	    size = 1;
	} else {
	    size = 2;
	}
	boxOutline.x = box.x - size;
	boxOutline.y = box.y - size;
	boxOutline.width = box.width + size * 2;
	boxOutline.height = boxOutline.width;
    }

    /**
     *
     * @return
     */
    @Override
    public int getCenter() {
	return titleLabel.getRight() + 3;
    }

    @Override
    protected void addHelpHandler(boolean hoverListener) {
	addMouseListener(new HelpMouseHandler());
    }

    void pickNewColor() {
	Color newColor = JColorChooser.showDialog(this,
		"Choose New Color",
		getBackground());
	if (newColor != null) {
	    color = newColor;
	    update();
	}
    }

    /**
     *
     * @param r
     */
    public void addActionListener(Runnable r) {
	actionListeners.add(r);
    }

    class ColorPickHandler implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
	    pickNewColor();
	    for (Runnable r : actionListeners) {
		r.run();
	    }
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	    setOutline(false);
	    repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
	    setOutline(true);
	    repaint();
	}
    }

    @Override
    public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g.create();
	g2.setPaint(Color.BLACK);
	g2.fill(boxOutline);
	boxOutline.x++;
	boxOutline.y++;
	g2.fill(boxOutline);
	boxOutline.x--;
	boxOutline.y--;
	g2.setPaint(color);
	g2.fill(box);
	super.paint(g2);
    }
}
