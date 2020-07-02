/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Justin Swanson
 */
public class LHTMLPane extends LEditorPane {

    /**
     *
     */
    public LHTMLPane () {
	super();
	pane.setContentType("text/html");
    }

    /**
     *
     * @return
     */
    public HTMLDocument getDocument() {
	return (HTMLDocument) pane.getDocument();
    }

    /**
     *
     * @return
     */
    public StyleSheet getStyleSheet() {
	return getDocument().getStyleSheet();
    }

    /**
     *
     */
    public void transparentBackground() {
	pane.setBackground(new Color(255, 255, 255, 0)) ;
    }
}
