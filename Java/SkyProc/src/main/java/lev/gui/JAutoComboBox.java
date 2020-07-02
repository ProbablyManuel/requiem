/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.event.ItemEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * Creates a combo box that auto fills from a given list.
 */
public class JAutoComboBox extends JComboBox {

    private JAutoComboBox.AutoTextFieldEditor autoTextFieldEditor;
    private boolean isFired;

    private class AutoTextFieldEditor extends BasicComboBoxEditor {

	private JAutoTextField getAutoTextFieldEditor() {
	    return (JAutoTextField) editor;
	}

	AutoTextFieldEditor(java.util.List list) {
	    editor = new JAutoTextField(list, JAutoComboBox.this);
	}
    }

    /**
     *
     * @param list
     */
    public JAutoComboBox(java.util.List list) {
	isFired = false;
	autoTextFieldEditor = new JAutoComboBox.AutoTextFieldEditor(list);
	setEditable(true);
	setModel(new DefaultComboBoxModel(list.toArray()) {
	    protected void fireContentsChanged(Object obj, int i, int j) {
		if (!isFired) {
		    super.fireContentsChanged(obj, i, j);
		}
	    }
	});
	setEditor(autoTextFieldEditor);
    }

    /**
     *
     * @return
     */
    public boolean isCaseSensitive() {
	return autoTextFieldEditor.getAutoTextFieldEditor().isCaseSensitive();
    }

    /**
     *
     * @param flag
     */
    public void setCaseSensitive(boolean flag) {
	autoTextFieldEditor.getAutoTextFieldEditor().setCaseSensitive(flag);
    }

    /**
     *
     * @return
     */
    public boolean isStrict() {
	return autoTextFieldEditor.getAutoTextFieldEditor().isStrict();
    }

    /**
     *
     * @param flag
     */
    public void setStrict(boolean flag) {
	autoTextFieldEditor.getAutoTextFieldEditor().setStrict(flag);
    }

    /**
     *
     * @return
     */
    public java.util.List getDataList() {
	return autoTextFieldEditor.getAutoTextFieldEditor().getDataList();
    }

    /**
     *
     * @param list
     */
    public void setDataList(java.util.List list) {
	autoTextFieldEditor.getAutoTextFieldEditor().setDataList(list);
	setModel(new DefaultComboBoxModel(list.toArray()));
    }

    void setSelectedValue(Object obj) {
	if (isFired) {
	    return;
	} else {
	    isFired = true;
	    setSelectedItem(obj);
	    fireItemStateChanged(new ItemEvent(this, 701, selectedItemReminder,
		    1));
	    isFired = false;
	    return;
	}
    }

    protected void fireActionEvent() {
	if (!isFired) {
	    super.fireActionEvent();
	}
    }
}