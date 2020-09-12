/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.components;

import Reqtificator.FormIDStash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;
import skyproc.gui.SUMGUI;

/**This is the abstract base for all Components.
 * This abstract class defines a general-purpose constructor which defines all
 * the central quantities used by all individual components, e.g. logging and
 * internationalization helpers and often used mod-references.
 *
 * @author Ogerboss
 */
public abstract class Component {

    private final static Logger logger = LogManager.getLogger();

    final ModListing requiem_ml;
    protected final Mod requiem;

    protected final String contextRecord = "record";
    private final String context = "context";

    Component() {
        requiem_ml = FormIDStash.REQ;
        requiem = SPDatabase.getMod(requiem_ml);
    }

    void startCategory(int maxRecords, String category, String guiTitle) {
        SUMGUI.progress.setMax(maxRecords, guiTitle);
        SUMGUI.progress.setBar(0);
        ThreadContext.put(context, category);
        logger.info("beginning patch operations");
    }

    void updateProgressBar() {
        SUMGUI.progress.incrementBar();
    }

    void endCategory() {
        ThreadContext.remove(contextRecord);
        logger.info("finished patch operations");
        ThreadContext.remove(context);
    }

    public static String formatRecord(MajorRecord record) {
        if (record == null) {
            return "<null reference>";
        } else {
            return "[" + record.getType() +
                    "|" + record.getEDID() +
                    "|" + record.getFormStr() +
                    "|" + record.getModImportedFrom().print() +
                    "]";
        }
    }

    public static String formatRecordPretty(MajorRecord record) {
        if (record == null) {
            return "<null reference>";
        } else {
            return " \"" + record.getEDID() + "\" - " + record.getFormStr() +
                    " [type: " + record.getType() + ", last overwrite: " +
                    record.getModImportedFrom().print() + "]";
        }
    }
}
