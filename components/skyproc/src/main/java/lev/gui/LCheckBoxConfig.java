/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author Justin Swanson
 */
public class LCheckBoxConfig extends LUserSetting {

    /**
     *
     */
    protected LSpecialCheckBox cbox;
    /**
     *
     */
    protected LButton button;
    /**
     *
     */
    public static String buttonText = "Open";
    /**
     *
     */
    public static int spacing = 18;

    /**
     *
     * @param title_
     */
    protected LCheckBoxConfig (String title_) {
        super(title_);
    }

    /**
     *
     * @param title_
     * @param size
     * @param style
     * @param shade
     * @param help_
     * @param save_
     * @param setting
     */
    public LCheckBoxConfig(String title_, int size, int style, Color shade, LHelpPanel help_, LSaveFile save_, Enum setting) {
        super(title_);
        help = help_;
        save = save_;
        saveTie = setting;

        button = new LButton(buttonText);
        button.addActionListener(new UpdateHelpActionHandler());

        cbox = new LSpecialCheckBox(title_, new Font("Serif",size, style), shade, this);
        cbox.tie(setting, save_, help, false);
        button.setLocation(cbox.getWidth() + spacing, 0);

        add(button);
        add(cbox);
        setSize(cbox.getWidth() + button.getWidth() + spacing, cbox.getHeight());

    }

    /**
     *
     */
    @Override
    public void updateHelp() {
        this.requestFocus();
        SwingUtilities.invokeLater(
                new Runnable() {

                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        SwingUtilities.invokeLater(
                                                new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        updateHelpHelper();
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    private void updateHelpHelper() {
        super.updateHelp();
        if (help != null) {
            help.setDefaultPos();
            help.hideArrow();
        }
    }

    @Override
    protected void addUpdateHandlers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean revertTo(Map m) {
        return cbox.revertTo(m);
    }

    @Override
    public Object getValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param hoverHandler
     */
    @Override
    protected void addHelpHandler(boolean hoverHandler) {
    }

    /**
     *
     * @param in
     */
    public void setButtonOffset (int in) {
        button.setLocation(button.getX(), cbox.getY() + in);
        setSize(getWidth(), button.getHeight() + in);
    }

    /**
     *
     * @param in
     */
    public void setOffset(int in) {
        cbox.setOffset(in);
    }

    /**
     *
     */
    public void addShadow () {
        cbox.addShadow();
    }

    /**
     *
     * @return
     */
    public JComponent getSource() {
        return button.getSource();
    }

    /**
     *
     * @param a
     */
    public void addActionListener(ActionListener a) {
        button.addActionListener(a);
    }

    @Override
    public void highlightChanged() {
        cbox.highlightChanged();
    }

    @Override
    public void clearHighlight() {
        cbox.clearHighlight();
    }

    /**
     *
     */
    protected class LSpecialCheckBox extends LCheckBox {

        LHelpComponent forwardTo;

	/**
	 *
	 * @param text
	 * @param font
	 * @param shade
	 * @param forwardTo_
	 */
	public LSpecialCheckBox(String text, Font font, Color shade, LHelpComponent forwardTo_) {
            super(text, font, shade);
            forwardTo = forwardTo_;
        }

	/**
	 *
	 */
	@Override
        public void updateHelp() {
            forwardTo.updateHelp();
        }
    }

    /**
     *
     */
    public class UpdateHelpActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            SwingUtilities.invokeLater(
                    new Runnable() {

                        @Override
                        public void run() {
                            updateHelp();
                        }
                    });
        }
    }

}
