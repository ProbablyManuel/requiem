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

/**
 * <ul>
 * <li>add a script that handles lockpicking by player and
 * followers</li></ul>
 */
public class Containers extends Component {

    private final static Logger logger = LogManager.getLogger();

    final private TextManager texts;

    public Containers(TextManager texts) {
        super();
        this.texts = texts;
    }

    /**
     * Reqtify imported container records.
     *
     * @param merger the merged import mod with the armors to process
     * @param patch  the global patch for exporting modified records
     */
    public void reqtifyContainers(Mod merger, Mod patch)
            throws UnexpectedException {
        GRUP<CONT> containers = merger.getContainers();
        int maxRecords = containers.numRecords();
        startCategory(maxRecords, "Containers",
                texts.format("patch.gui_titles.containers"));
        ThreadContext.push(FormIDStash.PROTOTYPE_CONTAINER_LOCKED.toString());
        CONT template = (CONT) merger.getMajor(
                FormIDStash.PROTOTYPE_CONTAINER_LOCKED, GRUP_TYPE.CONT);
        ThreadContext.push(formatRecord(template));
        List<ScriptRef> prototypes = template.getScriptPackage().getScripts();
        ThreadContext.clearStack();
        for (CONT container : containers) {
            ThreadContext.put(contextRecord, formatRecord(container));
            try {
                ScriptPackage myScripts = container.getScriptPackage();
                boolean edited = false;
                for (ScriptRef toAdd : prototypes) {
                    ThreadContext.push(toAdd.getName());
                    if (!myScripts.hasScript(toAdd)) {
                        myScripts.addScript(toAdd);
                        logger.debug("added script: {}", toAdd.getName());
                        edited = true;
                    }
                    ThreadContext.pop();
                }
                if (edited) {
                    patch.addRecord(container);
                }
                updateProgressBar();
            } catch (Exception ex) {
                throw new UnexpectedException(ex.getMessage(),
                        ex.getLocalizedMessage(), container, ex);
            }

        }
        endCategory();
    }

}
