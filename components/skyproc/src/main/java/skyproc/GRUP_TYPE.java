package skyproc;

//NOTE: Any new grup registered here must also be registered in Mod.init()

/**
 * Types of GRUP records that skyproc can currently import.
 * @see SPImporter
 * @author Justin Swanson
 */
public enum GRUP_TYPE {

    /**
     * Game Settings
     */
    GMST,
    /**
     * Keywords
     */
    KYWD,
    /**
     * Texture Sets
     */
    TXST,
    /**
     *
     */
    GLOB,
    /**
     * Factions
     */
    FACT,
    /**
     *
     */
    HDPT,
    /**
     * Races
     */
    RACE,
    /**
     * Magic Effects
     */
    MGEF,
    /**
     *
     */
    ENCH,
    /**
     * Spells
     */
    SPEL,
    /**
     * Scrolls
     */
    SCRL,
    /**
     * Armors
     */
    ARMO,
    /**
     *
     */
    BOOK,
    /**
     *
     */
    CONT,
    /**
     * Ingredients
     */
    INGR,
    /**
     *
     */
    MISC,
    /**
     * Alchemy
     */
    ALCH,
    /**
     * Craftable Object
     */
    COBJ,
    /**
     * Projectile
     */
    PROJ,
    /**
     * 
     */
    STAT,
    /**
     * Weapons
     */
    WEAP,
    /**
     * Ammo
     */
    AMMO,
    /**
     * Non-Player Characters (Actors)
     */
    NPC_,
    /**
     * Leveled Lists
     */
    LVLN,
    /**
     *
     */
    LVLI,
    /**
     *
     */
    WTHR,
    /**
     *
     */
    DIAL,
    /**
     *
     */
    INFO,
    /**
     * Image Spaces
     */
    QUST,
    /**
     *
     */
    IMGS,
    /**
     * Form Lists
     */
    FLST,
    /**
     * Perks
     */
    PERK,
    /**
     * 
     */
    VTYP,
    /**
     *
     */
    AVIF,
    /**
     * Armatures
     */
    ARMA,
    /**
     *
     */
    ECZN,
    /**
     *
     */
    LGTM,
    /**
     *
     */
    DLBR,
    /**
     *
     */
    DLVW,
    /**
     *
     */
    OTFT,
    /**
     * Doors
     */
    DOOR;

    static boolean unfinished (GRUP_TYPE g) {
	switch (g) {
	    case LGTM:
		return true;
	    default:
		return false;
	}
    }

    static boolean internal (GRUP_TYPE g) {
	switch (g) {
	    case INFO:
		return true;
	    default:
		return false;
	}
    }

}