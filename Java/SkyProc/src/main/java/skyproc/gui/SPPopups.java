/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package skyproc.gui;

import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

/**
 *
 * @author Ogerboss
 */
public class SPPopups {
    
    private final JLabel template = new JLabel();

    public SPPopups() {
    }

    /**Shows a popup with the given window title, main message and message icon.
     * The message will wrapped in html-tags and interpreted accordingly. Please
     * escape any html control characters accordingly. Hyperlinks in the main
     * message will display a tooltip showing the real target address on 
     * mouse-over events, i.e. <a href="some ref">blabla</a> will display as 
     * "blabla" in the message and show "some ref" as mouse-over tooltip.
     * 
     * @param title the window title of the popup
     * @param message the main message, will be wrapped in html tags
     * @param messagetype a message type for the JOptionPane, see the 
     * documentation of this class for details about the allowed values
     * @param width the width of the popup in px, 0 means no fixed width
     */
    public void popup(String title, String message, int messagetype, int width) {
        Font font = template.getFont();
        StringBuilder style = new StringBuilder("font-family:" + font.getFamily() + ";");
        style.append("font-weight: normal;");
        style.append("font-size:").append(font.getSize()).append("pt;");
        if (width > 0) {
            style.append("width: ").append(width).append("px;");
        }
        JEditorPane content = new JEditorPane("text/html", 
            "<html><body style=\"" + style + "\">" + message + "</body></html>");
        ToolTipManager.sharedInstance().registerComponent(content);
        content.setEditable(false);
        content.addHyperlinkListener(new HyperManager(content));
        content.setBackground(template.getBackground());
        JOptionPane.showMessageDialog(null, content, title , messagetype);
    }
    
}
