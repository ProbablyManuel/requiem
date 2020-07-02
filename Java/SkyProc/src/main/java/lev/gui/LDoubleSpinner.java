/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.util.Map;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin Swanson
 */
public class LDoubleSpinner extends LUserSetting<Double> {

    /**
     *
     */
    protected JSpinner spinner;

    /**
     *
     * @param title
     * @param init
     * @param min
     * @param max
     * @param step
     * @param width
     */
    public LDoubleSpinner(String title, double init, double min, double max, double step, int width) {
        super(title);
        SpinnerModel model = new SpinnerNumberModel(init, min, max, step);
        spinner = new JSpinner(model);
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        textField.setAlignmentX(4.0F);
        textField.setColumns(width);
        spinner.setSize(spinner.getPreferredSize());
        add(spinner);
        setSize(spinner.getPreferredSize());
    }

    @Override
    protected void addUpdateHandlers() {
        spinner.addChangeListener(new LUserSetting.UpdateChangeHandler());
    }

    @Override
    public boolean revertTo(Map<Enum, Setting> m) {
        if (isTied().booleanValue()) {
            double cur = getValue();
            setValue(m.get(saveTie).getDouble());
            if (cur != getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Double getValue() {
        return (Double) spinner.getValue();
    }

    @Override
    protected void addHelpHandler(boolean hoverListener) {
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().addFocusListener(new LHelpComponent.HelpFocusHandler());
        if (hoverListener) {
            MouseListener m = new LHelpComponent.HelpMouseHandler();
            titleLabel.addMouseListener(m);
            editor.getTextField().addMouseListener(m);
            for (Component c : spinner.getComponents()) {
                c.addMouseListener(m);
            }
        }
    }

    /**
     *
     * @param in
     */
    public void setValue(int in) {
        spinner.setValue(Integer.valueOf(in));
    }

    /**
     *
     * @param in
     */
    public void setValue(double in) {
        spinner.setValue(Double.valueOf(in));
    }

    @Override
    public synchronized void addFocusListener(FocusListener arg0) {
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().addFocusListener(arg0);
    }

    /**
     *
     * @param c
     */
    public void addChangeListener(ChangeListener c) {
        spinner.addChangeListener(c);
    }

    @Override
    public void highlightChanged() {
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setBackground(new Color(224, 121, 147));
    }

    @Override
    public void clearHighlight() {
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setBackground(Color.WHITE);
    }
}