/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package skyproc.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;

/**
 *
 * @author Ogerboss
 */
public class HyperManager implements HyperlinkListener {
    private final JEditorPane content;

    public HyperManager(JEditorPane source) {
        content = source;
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
            Element elem = e.getSourceElement();
            if (elem != null) {
                AttributeSet attr = elem.getAttributes();
                AttributeSet a = (AttributeSet) attr.getAttribute(HTML.Tag.A);
                if (a != null) {
                    content.setToolTipText((String) a.getAttribute(HTML.Attribute.HREF));
                }
            }
        } else if (e.getEventType() == HyperlinkEvent.EventType.EXITED) {
            content.setToolTipText("");
        } else if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            try {
                Desktop.getDesktop().browse(e.getURL().toURI());
            } catch (URISyntaxException | IOException ex) {
            }
        }
    }
    
}
