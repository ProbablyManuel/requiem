/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.genenums;

/**
 * Enum representing the various skills associated with an NPC.
 */
public enum Skill {
    // DO NOT change the order of these.

    Unknown1,
    Unknown2,
    Unknown3,
    Unknown4,
    Unknown5,
    Unknown6,
    /**
     * One-handed skill of the NPC.
     */
    ONEHANDED,
    /**
     * Two-handed skill of the NPC.
     */
    TWOHANDED,
    /**
     * Marksman skill of the NPC.
     */
    MARKSMAN,
    /**
     * Block skill of the NPC.
     */
    BLOCK,
    /**
     * Smithing skill of the NPC.
     */
    SMITHING,
    /**
     * Heavy Armor skill of the NPC.
     */
    HEAVYARMOR,
    /**
     * Light Armor skill of the NPC.
     */
    LIGHTARMOR,
    /**
     * Pick Pocket skill of the NPC.
     */
    PICKPOCKET,
    /**
     * Lockpicking skill of the NPC.
     */
    LOCKPICKING,
    /**
     * Sneak skill of the NPC.
     */
    SNEAK,
    /**
     * Alchemy skill of the NPC.
     */
    ALCHEMY,
    /**
     * Speech Craft skill of the NPC.
     */
    SPEECHCRAFT,
    /**
     * Alteration skill of the NPC.
     */
    ALTERATION,
    /**
     * Conjuration skill of the NPC.
     */
    CONJURATION,
    /**
     * Destruction skill of the NPC.
     */
    DESTRUCTION,
    /**
     * Illusion skill of the NPC.
     */
    ILLUSION,
    /**
     * Restoration skill of the NPC.
     */
    RESTORATION,
    /**
     * Enchanting skill of the NPC.
     */
    ENCHANTING,
    /**
     * None placeholder.
     */
    NONE;
    
    /**
     *
     * @param in
     * @return
     */
    static public int value(Skill in) {
	if (in == NONE) {
	    return -1;
	} else {
	    return in.ordinal();
	}
    }
    
    /**
     *
     * @param in
     * @return
     */
    static public Skill value(int in) {
	if (in < Skill.NONE.ordinal() && in >= 0) {
	    return Skill.values()[in];
	} else {
	    return NONE;
	}
    }
    
    /**
     *
     * @param in
     * @return
     */
    static public int NPC_Value(Skill in) {
	if ((in.ordinal() > Skill.Unknown6.ordinal()) && (in.ordinal() < Skill.NONE.ordinal())) {
	    return in.ordinal() - Skill.ONEHANDED.ordinal();
	} else {
	    throw new IndexOutOfBoundsException("Skill " + in.name() + " is not an NPC_ DNAM entry");
	}
    }
    
    /**
     *
     * @param in
     * @return
     */
    static public Skill NPC_Value(int in) {
	if ((in >= 0) && (in < 18)) {
	    return Skill.values()[in+Skill.ONEHANDED.ordinal()];
	} else {
	    throw new IndexOutOfBoundsException(in + " is not an index of a skill in NPC_ DNAM");
	}
    }
    
    static public Skill[] NPC_Skills(){
        Skill[] values = Skill.values();
        int len = Skill.NONE.ordinal() - Skill.ONEHANDED.ordinal();
        Skill[] ret = new Skill[(len)];
        System.arraycopy(values, Skill.ONEHANDED.ordinal(), ret, 0, (Skill.NONE.ordinal() - Skill.ONEHANDED.ordinal()));
        return ret;
    }
}