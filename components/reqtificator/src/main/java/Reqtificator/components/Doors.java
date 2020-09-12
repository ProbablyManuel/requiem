/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Reqtificator.components;

import Reqtificator.FormIDStash;
import Reqtificator.exceptions.UnexpectedException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;

import java.util.List;

/**Updates door records for Requiem game play.
 * <ul>
 * <li>add a script that handles lockpicking by player and
 * followers</li></ul>
 *
 * @author ogerboss
 */
public class Doors extends Component {

    private final static Logger logger = LogManager.getLogger();

    final private TextManager texts;

    public Doors(TextManager texts) {
        super();
        this.texts = texts;
    }

    /**
     * Reqtify imported door records.
     *
     * @param merger the merged import mod with the armors to process
     * @param patch  the global patch for exporting modified records
     */
    public void reqtifyDoors(Mod merger, Mod patch) throws UnexpectedException {
        GRUP<DOOR> doors = merger.getDoors();
        int maxRecords = doors.numRecords();
        startCategory(maxRecords, "Doors",
                texts.format("patch.gui_titles.doors"));
        ThreadContext.push(FormIDStash.PROTOTYPE_DOOR_LOCKED.toString());
        DOOR template = (DOOR) merger.getMajor(
                FormIDStash.PROTOTYPE_DOOR_LOCKED, GRUP_TYPE.DOOR);
        ThreadContext.push(formatRecord(template));
        List<ScriptRef> prototypes = template.getScriptPackage().getScripts();
        ThreadContext.clearStack();
        for (DOOR door: doors) {
            ThreadContext.put(contextRecord, formatRecord(door));
            try {
                ScriptPackage myScripts = door.getScriptPackage();
                boolean edited = false;
                for (ScriptRef toAdd : prototypes) {
                    ThreadContext.push(toAdd.getName());
                    if (!myScripts.hasScript(toAdd)) {
                        myScripts.addScript(toAdd);
                        logger.debug("added script: {}",
                                toAdd.getName());
                        edited = true;
                    }
                    ThreadContext.pop();
                }
                if (edited) {
                    patch.addRecord(door);
                }
                updateProgressBar();
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage(),
                        ex.getLocalizedMessage(), door, ex);
            }

        }
        endCategory();
    }

}
