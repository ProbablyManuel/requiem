/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator;

import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.MajorRecord;
import skyproc.Mod;
import skyproc.ModListing;

/**
 * This class is simply a centralized connection of all special FormIDs that are
 * hardcoded into the patcher.
 * @author Ogerboss
 */
public class FormIDStash {
    public static final ModListing SKY = new ModListing("Skyrim.esm");
    public static final ModListing REQ = new ModListing("Requiem.esp");

    public static final FormID formlist_ME_races = new FormID("A3C6DD", REQ);
    public static final FormID formlist_GM_perks = new FormID("8F57EA", REQ);
    public static final FormID formlist_closedzones = new FormID("A46546", REQ);
    public static final FormID formlist_LLmerge_highpriority
            = new FormID("AD36E7", REQ);
    public static final FormID formlist_LLmerge_mediumpriority
            = new FormID("AD36E6", REQ);
    public static final FormID formlistHelpTopcicsPC = new FormID("000163", SKY);
    public static final FormID formlistHelpTopcicsXbox = new FormID("000165", SKY);
    public static final FormID formlistHelpTopcicsRequiem = new FormID("AD3A1A", REQ);

    public static final FormID spell_ME_base = new FormID("7E76F4", REQ);
    public static final FormID spell_ME_npc = new FormID("82CC14", REQ);

    public static final FormID PROTOTYPE_CONTAINER_LOCKED = new FormID("003E38", REQ);
    public static final FormID PROTOTYPE_DOOR_LOCKED = new FormID("AD38BA", REQ);
}
