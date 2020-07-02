/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Alchemy Records
 *
 * @author Justin Swanson
 */
public class ALCH extends MagicItem {

    // Static prototypes and definitions
    static final SubPrototype ALCHproto = new SubPrototype(MagicItem.magicItemProto) {

        @Override
        protected void addRecords() {
            remove("DESC");
            add(new Model());
            add(new DestructionData());
            add(SubString.getNew("ICON", true));
            add(SubString.getNew("MICO", true));
            add(new SubForm("YNAM"));
            add(new SubForm("ZNAM"));
            add(new SubForm("ETYP"));
            add(new SubFloat("DATA"));
            add(new ENIT());
            reposition("EFID");
        }
    };

    static final class ENIT extends SubRecord {

        int value = 0;
        LFlags flags = new LFlags(4);
        FormID addiction = new FormID();
        byte[] addictionChance = new byte[4];
        FormID useSound = new FormID();

        ENIT() {
            super();
        }

        @Override
        void export(ModExporter out) throws IOException {
            super.export(out);
            out.write(value);
            out.write(flags.export());
            addiction.export(out);
            out.write(addictionChance, 4);
            useSound.export(out);
        }

        @Override
        void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in, srcMod);
            value = in.extractInt(4);
            flags.set(in.extract(4));
            addiction.parseData(in, srcMod);
            addictionChance = in.extract(4);
            useSound.parseData(in, srcMod);
        }

        @Override
        ArrayList<FormID> allFormIDs() {
            ArrayList<FormID> out = new ArrayList<>(2);
            out.add(addiction);
            out.add(useSound);
            return out;
        }

        @Override
        SubRecord getNew(String type) {
            return new ENIT();
        }

        @Override
        int getContentLength(ModExporter out) {
            return 20;
        }

        @Override
        ArrayList<String> getTypes() {
            return Record.getTypeList("ENIT");
        }
    }

    // Enums
    /**
     *
     */
    public enum ALCHFlag {

        /**
         *
         */
        ManualCalc(0),
        /**
         *
         */
        Food(1),
        /**
         *
         */
        Medicine(16),
        /**
         *
         */
        Poison(17);
        int value;

        ALCHFlag(int in) {
            value = in;
        }
    }

    // Common Functions
    ALCH() {
        super();
        subRecords.setPrototype(ALCHproto);
    }

    @Override
    ArrayList<String> getTypes() {
        return Record.getTypeList("ALCH");
    }

    @Override
    Record getNew() {
        return new ALCH();
    }

    // Get / set
    /**
     * @deprecated use getModelData()
     * @param groundModel
     */
    public void setModel(String groundModel) {
        subRecords.getModel().setFileName(groundModel);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModel() {
        return subRecords.getModel().getFileName();
    }

    /**
     *
     * @param pickupSound
     */
    public void setPickupSound(FormID pickupSound) {
        subRecords.setSubForm("YNAM", pickupSound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
        return subRecords.getSubForm("YNAM").getForm();
    }

    /**
     *
     * @param dropSound
     */
    public void setDropSound(FormID dropSound) {
        subRecords.setSubForm("ZNAM", dropSound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
        return subRecords.getSubForm("ZNAM").getForm();
    }

    ENIT getEnit() {
        return (ENIT) subRecords.get("ENIT");
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
        getEnit().value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return getEnit().value;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ALCHFlag flag, boolean on) {
        getEnit().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ALCHFlag flag) {
        return getEnit().flags.get(flag.value);
    }

    /**
     *
     * @param addiction
     */
    public void setAddiction(FormID addiction) {
        getEnit().addiction = addiction;
    }

    /**
     *
     * @return
     */
    public FormID getAddiction() {
        return getEnit().addiction;
    }

    /**
     *
     * @param useSound
     */
    public void setUseSound(FormID useSound) {
        getEnit().useSound = useSound;
    }

    /**
     *
     * @return
     */
    public FormID getUseSound() {
        return getEnit().useSound;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
        subRecords.setSubFloat("DATA", weight);
    }

    /**
     *
     * @return
     */
    public float getWeight() {
        return subRecords.getSubFloat("DATA").get();
    }

    /**
     *
     * @param filename
     */
    public void setInventoryIcon(String filename) {
        subRecords.setSubString("ICON", filename);
    }

    /**
     *
     * @return
     */
    public String getInventoryIcon() {
        return subRecords.getSubString("ICON").print();
    }

    /**
     *
     * @param filename
     */
    public void setMessageIcon(String filename) {
        subRecords.setSubString("MICO", filename);
    }

    /**
     *
     * @return
     */
    public String getMessageIcon() {
        return subRecords.getSubString("MICO").print();
    }

    /**
     *
     * @param equipType
     */
    public void setEquipType(FormID equipType) {
        subRecords.setSubForm("ETYP", equipType);
    }

    /**
     *
     * @return
     */
    public FormID getEquipType() {
        return subRecords.getSubForm("ETYP").getForm();
    }

    /**
     * @deprecated use getModelData()
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
        return subRecords.getModel().getAltTextures();
    }

    /**
     *
     * @return
     */
    public Model getModelData() {
        return subRecords.getModel();
    }
}
