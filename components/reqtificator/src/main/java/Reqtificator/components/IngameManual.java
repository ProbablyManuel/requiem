package Reqtificator.components;

import Reqtificator.FormIDStash;
import skyproc.FLST;
import skyproc.GRUP_TYPE;
import skyproc.Mod;

public class IngameManual {

    public static void patchIngameManual(Mod merger, Mod patch) {
        FLST pcHelp = (FLST) merger.getMajor(FormIDStash.formlistHelpTopcicsPC, GRUP_TYPE.FLST);
        FLST xboxHelp = (FLST) merger.getMajor(FormIDStash.formlistHelpTopcicsXbox, GRUP_TYPE.FLST);
        FLST requiemHelp = (FLST) merger.getMajor(FormIDStash.formlistHelpTopcicsRequiem, GRUP_TYPE.FLST);

        pcHelp.addAll(requiemHelp.getFormIDEntries());
        xboxHelp.addAll(requiemHelp.getFormIDEntries());
        patch.addRecord(pcHelp);
        patch.addRecord(xboxHelp);
    }
}
