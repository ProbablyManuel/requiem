/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.components;

import Reqtificator.FormIDStash;
import Reqtificator.exceptions.PatchingException;
import Reqtificator.exceptions.UnexpectedException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.*;

import java.util.HashSet;

/**Processing class for EncounterZones.
 * This component will set all EncounterZones to have open borders, which means
 * that NPCs can pursue you beyond the boundaries of their home cell/location.
 * Optionally, specific EncounterZones can be excluded from this treatment, e.g.
 * Requiem itself excludes all Vampire lairs to prevent their inhabitants from
 * rushing into the sunlight. Another example are the zones of the Civil War
 * quests, which are excluded to improve the stability of these.
 * Patch authors can simply add their special EncounterZones to the FormList
 * that contains the Requiem-exceptions. The Reqtificator will resolve
 * conflicts between several patches on its own.
 *
 * @author Ogerboss
 */
public class EncounterZones extends Component{

    private final static Logger logger = LogManager.getLogger();

    final private TextManager texts;

    public EncounterZones(TextManager texts) {
        super();
        this.texts = texts;
    }

    /**Reqtify EncounterZones.
     *
     * @param merger merged version of the imported mods
     * @param patch the global patch to be created
     * @throws PatchingException if the exception list contains invalid data
     * @throws UnexpectedException wrapper around any unexpected errors
     */
    public void reqtifyZones(Mod merger, Mod patch, boolean openZones) throws PatchingException,
            UnexpectedException {
        HashSet<FormID> exceptions = new HashSet<>();
        FLST exceptionList = (FLST) merger.getMajor(
                FormIDStash.formlist_closedzones, GRUP_TYPE.FLST);
        String key = "patch.encounter_zones.wrong_type_in_exception_list";
        for (MajorRecord version: exceptionList.getRecordHistory()) {
            FLST overwrite = (FLST) version;
            for (FormID fid: overwrite.getFormIDEntries()) {
                if (merger.getMajor(fid, GRUP_TYPE.ECZN) == null) {
                    String msg = texts.format(key, fid.toString(),
                            version.getModImportedFrom().print(),
                            texts.format("urls.encounter_zones", true));
                    throw new PatchingException("invalid exception list entry",
                            msg);
                }
                exceptions.add(fid);
            }
        }
        if (openZones) {
            GRUP<ECZN> zones = merger.getEncounterZones();
            int maxRecords = zones.numRecords();
            startCategory(maxRecords, "Encounters",
                    texts.format("patch.gui_titles.encounter_zones"));
            for (ECZN zone : zones) {
                ThreadContext.put(contextRecord, formatRecord(zone));
                try {
                    if (exceptions.contains(zone.getForm())) {
                    } else {
                        zone.set(ECZN.ECZNFlags.DisableCombatBoundary, true);
                        patch.addRecord(zone);
                        logger.debug("opened combat boundary for NPCs");
                    }
                    updateProgressBar();
                } catch (Exception ex) {
                    throw new UnexpectedException(ex.getMessage(),
                            ex.getLocalizedMessage(), zone, ex);
                }

            }
        }
        endCategory();
    }

}
