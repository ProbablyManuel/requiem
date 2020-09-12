/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @author ogerboss
 */
public class DOOR extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype PROTOTYPE_DOOR = new SubPrototype(MajorRecordNamed.namedProto) {

        @Override
        protected void addRecords() {
            add(new ScriptPackage());
            add(new SubData("OBND", new byte[12]));
            reposition("FULL");
            add(new Model());
	    add(new DestructionData());
	    add(new SubForm("SNAM"));
            add(new SubForm("ANAM"));
            add(new SubForm("BNAM"));
            add(new SubFlag("FNAM", 1));
        }
    };

    /**
     * valid flags for doors, descriptions are taken from the Creation Kit Wiki
     */
    public enum DOOR_Flags {

        /**
         * undocumented
         */
        Automatic(1),
        /**
         * Door does not appear on map
         */
        Hidden(2),
        /**
         * Quest Targets will avoid using this door when choosing a path for the
         * compass. Also, the door will not be used by NPCs (or followers, or
         * fast travel?) for pathing, if there is any viable alternative. So
         * adding a min use teleport to another city into someone's basement
         * will not cause that basement to replace the highways as the preferred
         * and fastest route for NPC traders to travel between those cities.
         */
        MinimalUse(3),
        /**
         * Door slides open into the walls instead of opening forward or
         * backwards
         */
        Sliding(4),
        /**
         * While searching for the player, enemies will not open this door to
         * look
         */
        DoNotOpenInCombatSearch(5);
        int value;

        DOOR_Flags(int value) {
            this.value = value;
        }
    }

    // Common Functions
    DOOR() {
        super();
        subRecords.setPrototype(PROTOTYPE_DOOR);
    }

    @Override
    Record getNew() {
        return new DOOR();
    }

    @Override
    ArrayList<String> getTypes() {
        return Record.getTypeList("DOOR");
    }

    /**
     *
     * @param sound FormID of the sound to be used (Null form for no sound)
     */
    public void setOpenSound(FormID sound) {
        subRecords.setSubForm("SNAM", sound);
    }

    /**
     *
     * @return FormID of the assigned sound (null form if none is assigned)
     */
    public FormID getOpenSound() {
        return subRecords.getSubForm("SNAM").getForm();
    }

    /**
     *
     * @param sound FormID of the sound to be used (Null form for no sound)
     */
    public void setCloseSound(FormID sound) {
        subRecords.setSubForm("ANAM", sound);
    }

    /**
     *
     * @return FormID of the assigned sound (null form if none is assigned)
     */
    public FormID getCloseSound() {
        return subRecords.getSubForm("ANAM").getForm();
    }

    /**
     *
     * @param sound FormID of the sound to be used (Null form for no sound)
     */
    public void setLoopSound(FormID sound) {
        subRecords.setSubForm("BNAM", sound);
    }

    /**
     *
     * @return FormID of the assigned sound (null form if none is assigned)
     */
    public FormID getLoopSound() {
        return subRecords.getSubForm("BNAM").getForm();
    }

    /**
     *
     * @return model data
     */
    public Model getModelData() {
        return subRecords.getModel();
    }

    /**
     *
     * @return ScriptPackage of the DOOR
     */
    public ScriptPackage getScriptPackage() {
        return subRecords.getScripts();
    }

    /**
     * Get the value of the given flag.
     * @param flag the DOOR flag to query
     * @return boolean state of the flag
     */
    public boolean getFlag(DOOR_Flags flag) {
        return subRecords.getSubFlag("FNAM").flags.get(flag.value);
    }

    /**
     * Set the value of the given flag.
     * @param flag  the DOOR flag to set
     * @param value new value of the flag
     */
    public void setFlag(DOOR_Flags flag, boolean value) {
        subRecords.getSubFlag("FNAM").flags.set(flag.value, value);
    }

}
