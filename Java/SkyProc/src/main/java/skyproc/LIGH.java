/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author pc tech
 */
public class LIGH extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype LIGHproto = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    add(new SubData("OBND", new byte[12]));
	    add(new Model());
	    add(new DestructionData());
	    reposition("FULL");
	    add(SubString.getNew("ICON", true));
	    add(SubString.getNew("MICO", true));
	    add(new DATA());
	    add(new SubFloat("FNAM"));
	    add(new SubForm("SNAM"));
	}
    };
    static class DATA extends SubRecord {

        int time = 0;
        int radius = 0;
        int red = 0;
        int blue = 0;
        int green = 0;
        int holder = 0;
        LFlags flags = new LFlags(4);
        float falloff = 0;
        float fov = 0;
        float nearclip;
        float period = 0;
        float intensity = 0;
        float movement = 0;
        int value = 0;
        float weight = 0;

        DATA() {
            super();
        }

        @Override
        void export(ModExporter out) throws IOException {
            super.export(out);
            out.write(time);
            out.write(radius);
            out.write(red, 1);
            out.write(blue, 1);
            out.write(green, 1);
            out.write(holder, 1);
            out.write(flags.export(), 4);
            out.write(falloff);
            out.write(fov);
            out.write(nearclip);
            out.write(period);
            out.write(intensity);
            out.write(movement);
            out.write(value);
            out.write(weight);
        }

        @Override
        void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in, srcMod);
            time = in.extractInt(4);
            radius = in.extractInt(4);
            red = in.extractInt(1);
            blue = in.extractInt(1);
            green = in.extractInt(1);
            holder = in.extractInt(1);
            flags.set(in.extract(4));
            falloff = in.extractFloat();
            fov = in.extractFloat();
            nearclip = in.extractFloat();
            period = in.extractFloat();
            intensity = in.extractFloat();
            movement = in.extractFloat();
            value = in.extractInt(4);
            weight = in.extractFloat();
        }

        @Override
        SubRecord getNew(String type) {
            return new DATA();
        }

        @Override
        int getContentLength(ModExporter out) {
            return 48;
        }

        @Override
        ArrayList<FormID> allFormIDs() {
            ArrayList<FormID> out = new ArrayList<>(0);
            return out;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final DATA other = (DATA) obj;
            if (this.time != other.time) {
                return false;
            }
            if (this.radius != other.radius) {
                return false;
            }
            if (this.red != other.red) {
                return false;
            }
            if (this.blue != other.blue) {
                return false;
            }
            if (this.green != other.green) {
                return false;
            }
            if (this.holder != other.holder) {
                return false;
            }
            if (!Objects.equals(this.flags, other.flags)) {
                return false;
            }
            if (Float.floatToIntBits(this.falloff) != Float.floatToIntBits(other.falloff)) {
                return false;
            }
            if (Float.floatToIntBits(this.fov) != Float.floatToIntBits(other.fov)) {
                return false;
            }
            if (Float.floatToIntBits(this.nearclip) != Float.floatToIntBits(other.nearclip)) {
                return false;
            }
            if (Float.floatToIntBits(this.period) != Float.floatToIntBits(other.period)) {
                return false;
            }
            if (Float.floatToIntBits(this.intensity) != Float.floatToIntBits(other.intensity)) {
                return false;
            }
            if (Float.floatToIntBits(this.movement) != Float.floatToIntBits(other.movement)) {
                return false;
            }
            if (this.value != other.value) {
                return false;
            }
            if (Float.floatToIntBits(this.weight) != Float.floatToIntBits(other.weight)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 89 * hash + this.time;
            hash = 89 * hash + this.radius;
            hash = 89 * hash + this.red;
            hash = 89 * hash + this.blue;
            hash = 89 * hash + this.green;
            hash = 89 * hash + this.holder;
            hash = 89 * hash + Objects.hashCode(this.flags);
            hash = 89 * hash + Float.floatToIntBits(this.falloff);
            hash = 89 * hash + Float.floatToIntBits(this.fov);
            hash = 89 * hash + Float.floatToIntBits(this.nearclip);
            hash = 89 * hash + Float.floatToIntBits(this.period);
            hash = 89 * hash + Float.floatToIntBits(this.intensity);
            hash = 89 * hash + Float.floatToIntBits(this.movement);
            hash = 89 * hash + this.value;
            hash = 89 * hash + Float.floatToIntBits(this.weight);
            return hash;
        }

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    // Enums
    /**
     *
     */
    public enum LIGHFlag {

        /**
         *
         */
        Dynamic,
        /**
         *
         */
        CanBeCarried,
        /**
         *
         */
        Negative,
        /**
         *
         */
        Flicker,
        /**
         *
         */
        Unknown,
        /**
         *
         */
        OffByDefault,
        /**
         *
         */
        FlickerSlow,
        /**
         *
         */
        Pulse,
        /**
         *
         */
        PulseSlow,
        /**
         *
         */
        Spotlight,
        /**
         *
         */
        ShadowSpotlight,
        /**
         *
         */
        ShadowHemisphere,
        /**
         *
         */
        ShadowOmnidirectional,
        /**
         *
         */
        PortalStrict;
    }

    // Common Functions
    LIGH() {
        super();
        subRecords.setPrototype(LIGHproto);
    }

    @Override
    Record getNew() {
        return new LIGH();
    }

    @Override
    ArrayList<String> getTypes() {
        return Record.getTypeList("LIGH");
    }

    //Get/Set
    /**
     * @deprecated use getModelData()
     * @param path
     */
    public void setModel(String path) {
	subRecords.getModel().setFileName(path);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModel() {
	return subRecords.getModel().getFileName();
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(LIGHFlag flag, boolean on) {
        getDATA().flags.set(flag.ordinal() + 1, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(LIGHFlag flag) {
        return getDATA().flags.get(flag.ordinal() + 1);
    }

    /**
     *
     * @param gold
     */
    public void setValue(int gold) {
        getDATA().value = gold;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return getDATA().value;
    }

    /**
     *
     * @return
     */
    public int getBlue() {
        return getDATA().blue;
    }

    /**
     *
     * @param blue
     */
    public void setBlue(int blue) {
        this.getDATA().blue = blue;
    }

    /**
     *
     * @return
     */
    public float getFalloff() {
        return getDATA().falloff;
    }

    /**
     *
     * @param falloff
     */
    public void setFalloff(float falloff) {
        this.getDATA().falloff = falloff;
    }

    /**
     *
     * @return
     */
    public float getFov() {
        return getDATA().fov;
    }

    /**
     *
     * @param fov
     */
    public void setFov(float fov) {
        this.getDATA().fov = fov;
    }

    /**
     *
     * @return
     */
    public int getGreen() {
        return getDATA().green;
    }

    /**
     *
     * @param green
     */
    public void setGreen(int green) {
        this.getDATA().green = green;
    }

    /**
     *
     * @return
     */
    public float getIntensity() {
        return getDATA().intensity;
    }

    /**
     *
     * @param intensity
     */
    public void setIntensity(float intensity) {
        this.getDATA().intensity = intensity;
    }

    /**
     *
     * @return
     */
    public float getMovement() {
        return getDATA().movement;
    }

    /**
     *
     * @param movement
     */
    public void setMovement(float movement) {
        this.getDATA().movement = movement;
    }

    /**
     *
     * @return
     */
    public float getNearclip() {
        return getDATA().nearclip;
    }

    /**
     *
     * @param nearclip
     */
    public void setNearclip(float nearclip) {
        this.getDATA().nearclip = nearclip;
    }

    /**
     *
     * @return
     */
    public float getPeriod() {
        return getDATA().period;
    }

    /**
     *
     * @param period
     */
    public void setPeriod(float period) {
        this.getDATA().period = period;
    }

    /**
     *
     * @return
     */
    public int getRadius() {
        return getDATA().radius;
    }

    /**
     *
     * @param radius
     */
    public void setRadius(int radius) {
        this.getDATA().radius = radius;
    }

    /**
     *
     * @return
     */
    public int getRed() {
        return getDATA().red;
    }

    /**
     *
     * @param red
     */
    public void setRed(int red) {
        this.getDATA().red = red;
    }

    /**
     *
     * @return
     */
    public int getTime() {
        return getDATA().time;
    }

    /**
     *
     * @param time
     */
    public void setTime(int time) {
        this.getDATA().time = time;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
        return getDATA().weight;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
        this.getDATA().weight = weight;
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