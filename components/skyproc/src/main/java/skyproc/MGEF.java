/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import lev.LFlags;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import skyproc.genenums.ActorValue;
import skyproc.genenums.CastType;
import skyproc.genenums.DeliveryType;
import skyproc.genenums.SoundVolume;
import skyproc.interfaces.KeywordRecord;

/**
 * A Magic Effect record
 *
 * @author Justin Swanson
 */
public class MGEF extends MajorRecordDescription implements KeywordRecord {

    // Static prototypes and definitions
    static final SubPrototype MGEFproto = new SubPrototype(MajorRecordDescription.descProto) {

        @Override
        protected void addRecords() {
            add(new ScriptPackage());
            reposition("FULL");
            add(new SubForm("MDOB"));
            add(new KeywordSet());
            add(new DATA());
            add(new SNDD());
            SubStringPointer dnam = new SubStringPointer("DNAM", SubStringPointer.Files.STRINGS);
            dnam.forceExport = true;
            add(dnam);
            forceExport("DNAM");
            remove("DESC");
            add(new SubForm("ESCE"));
            add(new SubList<>(new Condition()));
            add(new SubData("OBND"));
        }
    };

    static class DATA extends SubRecord {

        LFlags flags = new LFlags(4);
        float baseCost = 0;
        FormID relatedID = new FormID();
        ActorValue skillType = ActorValue.NONE;
        ActorValue resistanceAV = ActorValue.NONE;
        //byte[] unknown = {0x00, 0x00, 0x00, (byte) 0x80};
        int counterEffectCount = 0;
        FormID lightID = new FormID();
        float taperWeight = 0;
        FormID hitShader = new FormID();
        FormID enchantShader = new FormID();
        int skillLevel = 0;
        int area = 0;
        float castingTime = 0;
        float taperCurve = 0;
        float taperDuration = 0;
        float secondAVWeight = 0;
        int effectType = 0;
        ActorValue primaryAV = ActorValue.Health;
        FormID projectileID = new FormID();
        FormID explosionID = new FormID();
        CastType castType = CastType.ConstantEffect;
        DeliveryType deliveryType = DeliveryType.Self;
        ActorValue secondAV = ActorValue.NONE;
        FormID castingArt = new FormID();
        FormID hitEffectArt = new FormID();
        FormID impactData = new FormID();
        float skillUsageMult = 0;
        FormID dualCastID = new FormID();
        float dualCastScale = 1;
        FormID enchantArtID = new FormID();
        int nullData = 0;
        int nullData2 = 0;
        FormID equipAbility = new FormID();
        FormID imageSpaceModID = new FormID();
        FormID perkID = new FormID();
        SoundVolume vol = SoundVolume.Normal;
        float scriptAIDataScore = 0;
        float scriptAIDataDelayTime = 0;

        DATA() {
            super();
        }

        @Override
        void export(ModExporter out) throws IOException {
            super.export(out);
            out.write(flags.export(), 4);
            out.write(baseCost);
            relatedID.export(out);
//            if (skillType == ActorValue.NONE){
//                out.write(-1); // 4 bytes on disk
//            } else {
            out.write(ActorValue.value(skillType));
//            }
//            if (resistanceAV == ActorValue.NONE){
//                out.write(-1); // 4 bytes on disk
//            } else {
            out.write(ActorValue.value(resistanceAV));
//            }
            out.write(counterEffectCount);//unknown, 4);
            lightID.export(out);
            out.write(taperWeight);
            hitShader.export(out);
            enchantShader.export(out);
            out.write(skillLevel);
            out.write(area);
            out.write(castingTime);
            out.write(taperCurve);
            out.write(taperDuration);
            out.write(secondAVWeight);
            out.write(effectType);
//            if (primaryAV == ActorValue.NONE){
//                out.write(-1); // 4 bytes on disk
//            } else {
            out.write(ActorValue.value(primaryAV));
//            }
            projectileID.export(out);
            explosionID.export(out);
            out.write(castType.ordinal());
            out.write(deliveryType.ordinal());
//            if (secondAV == ActorValue.NONE){
//                out.write(-1); // 4 bytes on disk
//            } else {
            out.write(ActorValue.value(secondAV));
//            }
            castingArt.export(out);
            hitEffectArt.export(out);
            impactData.export(out);
            out.write(skillUsageMult);
            dualCastID.export(out);
            out.write(dualCastScale);
            enchantArtID.export(out);
            out.write(nullData, 4);
            out.write(nullData2, 4);
            equipAbility.export(out);
            imageSpaceModID.export(out);
            perkID.export(out);
            out.write(vol.ordinal());
            out.write(scriptAIDataScore);
            out.write(scriptAIDataDelayTime);
        }

        @Override
        void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in, srcMod);
            flags.set(in.extract(4));
            baseCost = in.extractFloat();
            relatedID.parseData(in, srcMod);
            skillType = ActorValue.value(in.extractInt(4));
            resistanceAV = ActorValue.value(in.extractInt(4));
            counterEffectCount = in.extractInt(4);//unknown = in.extract(4);
            lightID.parseData(in, srcMod);
            taperWeight = in.extractFloat();
            hitShader.parseData(in, srcMod);
            enchantShader.parseData(in, srcMod);
            skillLevel = in.extractInt(4);
            area = in.extractInt(4);
            castingTime = in.extractFloat();
            taperCurve = in.extractFloat();
            taperDuration = in.extractFloat();
            secondAVWeight = in.extractFloat();
            effectType = in.extractInt(4);
            primaryAV = ActorValue.value(in.extractInt(4));
            projectileID.parseData(in, srcMod);
            explosionID.parseData(in, srcMod);
            castType = CastType.values()[in.extractInt(4)];
            deliveryType = DeliveryType.values()[in.extractInt(4)];
            secondAV = ActorValue.value(in.extractInt(4));
            castingArt.parseData(in, srcMod);
            hitEffectArt.parseData(in, srcMod);
            impactData.parseData(in, srcMod);
            skillUsageMult = in.extractFloat();
            dualCastID.parseData(in, srcMod);
            dualCastScale = in.extractFloat();
            enchantArtID.parseData(in, srcMod);
            nullData = in.extractInt(4);
            nullData2 = in.extractInt(4);
            equipAbility.parseData(in, srcMod);
            imageSpaceModID.parseData(in, srcMod);
            perkID.parseData(in, srcMod);
            vol = SoundVolume.values()[in.extractInt(4)];
            scriptAIDataScore = in.extractFloat();
            scriptAIDataDelayTime = in.extractFloat();
            if (SPGlobal.logMods) {
                logMod(srcMod, "", "DATA:");
                logMod(srcMod, "", "  Flags: " + flags);
                logMod(srcMod, "", "  Base Cost: " + baseCost);
                logMod(srcMod, "", "  Related ID: " + relatedID);
                logMod(srcMod, "", "  skillType: " + skillType);
                logMod(srcMod, "", "  resistanceAV: " + resistanceAV);
                logMod(srcMod, "", "  Light: " + lightID);
                logMod(srcMod, "", "  Taper Weight: " + taperWeight);
                logMod(srcMod, "", "  Hit Shader: " + hitShader);
                logMod(srcMod, "", "  Enchant Shader: " + enchantShader);
                logMod(srcMod, "", "  Skill Level: " + skillLevel);
                logMod(srcMod, "", "  Area: " + area);
                logMod(srcMod, "", "  Casting Time: " + castingTime);
                logMod(srcMod, "", "  Taper Curve: " + taperCurve);
                logMod(srcMod, "", "  Taper Duration: " + taperDuration);
                logMod(srcMod, "", "  second AV weight: " + secondAVWeight);
                logMod(srcMod, "", "  Effect Type: " + effectType);
                logMod(srcMod, "", "  Primary AV: " + primaryAV);
                logMod(srcMod, "", "  Projectile : " + projectileID);
                logMod(srcMod, "", "  Explosion: " + explosionID);
                logMod(srcMod, "", "  Cast Type: " + castType);
                logMod(srcMod, "", "  Delivery Type: " + deliveryType);
                logMod(srcMod, "", "  Second AV: " + secondAV);
                logMod(srcMod, "", "  Casting Art: " + castingArt);
                logMod(srcMod, "", "  Hit Effect Art: " + hitEffectArt);
                logMod(srcMod, "", "  Impact Data: " + impactData);
                logMod(srcMod, "", "  Skill Usage Mult: " + skillUsageMult);
                logMod(srcMod, "", "  Dual Cast ID: " + dualCastID);
                logMod(srcMod, "", "  Dual Cast Scale: " + dualCastScale);
                logMod(srcMod, "", "  Enchant Art: " + enchantArtID);
                logMod(srcMod, "", "  Equip Ability: " + equipAbility);
                logMod(srcMod, "", "  Image Space Mod ID: " + imageSpaceModID);
                logMod(srcMod, "", "  Perk: " + perkID);
                logMod(srcMod, "", "  Volume: " + vol);
                logMod(srcMod, "", "  Script AI Data Score: " + scriptAIDataScore);
                logMod(srcMod, "", "  Script AI Data Delay Time: " + scriptAIDataDelayTime);
            }
        }

        @Override
        SubRecord getNew(String type) {
            return new DATA();
        }

        @Override
        boolean isValid() {
            return true;
        }

        @Override
        int getContentLength(ModExporter out) {
            return 152;
        }

        @Override
        ArrayList<FormID> allFormIDs() {
            ArrayList<FormID> out = new ArrayList<>(14);
            out.add(relatedID);
            out.add(lightID);
            out.add(hitShader);
            out.add(enchantShader);
            out.add(projectileID);
            out.add(explosionID);
            out.add(castingArt);
            out.add(hitEffectArt);
            out.add(impactData);
            out.add(dualCastID);
            out.add(enchantArtID);
            out.add(equipAbility);
            out.add(imageSpaceModID);
            out.add(perkID);
            return out;
        }

        @Override
        ArrayList<String> getTypes() {
            return Record.getTypeList("DATA");
        }
    }

    static class SNDD extends SubRecord {

        ArrayList<Sound> sounds = new ArrayList<>();

        SNDD() {
            super();
        }

        @Override
        void export(ModExporter out) throws IOException {
            super.export(out);
            for (Sound s : sounds) {
                out.write(s.sound.ordinal());
                s.soundID.export(out);
            }
        }

        @Override
        void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in, srcMod);
            while (!in.isDone()) {
                Sound sound = new Sound();
                sound.sound = SoundData.values()[in.extractInt(4)];
                sound.soundID.parseData(in, srcMod);
                sounds.add(sound);
            }
        }

        @Override
        SubRecord getNew(String type) {
            return new SNDD();
        }

        @Override
        boolean isValid() {
            return !sounds.isEmpty();
        }

        @Override
        int getContentLength(ModExporter out) {
            return 8 * sounds.size();
        }

        @Override
        ArrayList<FormID> allFormIDs() {
            ArrayList<FormID> out = new ArrayList<>();
            for (Sound s : sounds) {
                out.add(s.soundID);
            }
            return out;
        }

        @Override
        ArrayList<String> getTypes() {
            return Record.getTypeList("SNDD");
        }

    }

    public static class Sound {

        SoundData sound;
        FormID soundID = new FormID();

        private Sound() {
        }

        /**
         *
         * @param sound
         * @param soundID
         */
        public Sound(SoundData sound, FormID soundID) {
            this.sound = sound;
            this.soundID = soundID;
        }

        /**
         *
         * @return
         */
        public SoundData getSoundData() {
            return sound;
        }

        /**
         *
         * @param sound
         */
        public void setSoundData(SoundData sound) {
            this.sound = sound;
        }

        /**
         *
         * @return
         */
        public FormID getSoundID() {
            return soundID;
        }

        /**
         *
         * @param soundID
         */
        public void setSoundID(FormID soundID) {
            this.soundID = soundID;
        }

        /**
         *
         * @return
         */
        @Override
        public String toString() {
            return "Sound [sound=" + sound + ", soundID=" + soundID + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((sound == null) ? 0 : sound.hashCode());
            result = prime * result
                    + ((soundID == null) ? 0 : soundID.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Sound other = (Sound) obj;
            if (sound != other.sound) {
                return false;
            }
            if (soundID == null) {
                if (other.soundID != null) {
                    return false;
                }
            } else if (!soundID.equals(other.soundID)) {
                return false;
            }
            return true;
        }

    }

    /**
     *
     * @return
     */
    SNDD getSNDD() {
        return (SNDD) subRecords.get("SNDD");
    }

    /**
     *
     * @return Returns the actual ArrayList, not a copy.
     */
    public ArrayList<Sound> getSounds() {
        return getSNDD().sounds;
    }

    /**
     * Replaces all previous sounds
     *
     * @param sounds
     */
    public void setSounds(ArrayList<Sound> sounds) {
        getSNDD().sounds = sounds;
    }

    /**
     *
     * @param sound
     */
    public void addSound(Sound sound) {
        getSNDD().sounds.add(sound);
    }

    /**
     *
     * @param sound
     */
    public void removeSound(Sound sound) {
        getSNDD().sounds.remove(sound);
    }

    // Enums
    /**
     *
     */
    public enum SpellEffectFlag {

        /**
         *
         */
        Hostile(0),
        /**
         *
         */
        Recover(1),
        /**
         *
         */
        Detrimental(2),
        /**
         *
         */
        SnapToNavmesh(3),
        /**
         *
         */
        NoHitEvent(4),
        /**
         *
         */
        Unknown_6(5),
        /**
         *
         */
        Unknown_7(6),
        /**
         *
         */
        Unknown_8(7),
        /**
         *
         */
        DispellEffects(8),
        /**
         *
         */
        NoDuration(9),
        /**
         *
         */
        NoMagnitude(10),
        /**
         *
         */
        NoArea(11),
        /**
         *
         */
        FXPersist(12),
        /**
         *
         */
        Unknown_14(13),
        /**
         *
         */
        GoryVisual(14),
        /**
         *
         */
        HideInUI(15),
        /**
         *
         */
        Unknown_17(16),
        /**
         *
         */
        NoRecast(17),
        /**
         *
         */
        Unknown_19(18),
        /**
         *
         */
        Unknown_20(19),
        /**
         *
         */
        Unknown_21(20),
        /**
         *
         */
        PowerAffectsMagnitude(21),
        /**
         *
         */
        PowerAffectsDuration(22),
        /**
         *
         */
        Unknown_24(23),
        /**
         *
         */
        Unknown_25(24),
        /**
         *
         */
        Unknown_26(25),
        /**
         *
         */
        Painless(26),
        /**
         *
         */
        NoHitEffect(27),
        /**
         *
         */
        NoDeathDispel(28),
        /*
         *
         */
        Unknown_30(29),
        /**
         *
         */
        Unknown_31(30),
        /**
         *
         */
        Unknown_32(31);

        int value;

        SpellEffectFlag(int value) {
            this.value = value;
        }
    }

    /**
     *
     */
    public enum SoundData {

        /**
         *
         */
        SheathDraw,
        /**
         *
         */
        Charge,
        /**
         *
         */
        Ready,
        /**
         *
         */
        Release,
        /**
         *
         */
        ConcentrationCastLoop,
        /**
         *
         */
        OnHit
    }

    // Common Functions
    /**
     *
     * @param edid EDID to give the new record. Make sure it is unique.
     * @param name
     */
    public MGEF(String edid, String name) {
        this();
        originateFromPatch(edid);
        this.setName(name);
    }

    MGEF() {
        super();
        subRecords.setPrototype(MGEFproto);
    }

    @Override
    ArrayList<String> getTypes() {
        return Record.getTypeList("MGEF");
    }

    @Override
    Record getNew() {
        return new MGEF();
    }

    // Get/Set
    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if
     * empty.
     */
    public KeywordSet getKeywordSet() {
        return subRecords.getKeywords();
    }

    @Override
    public String getDescription() {
        return subRecords.getSubStringPointer("DNAM").print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    @Override
    public void setDescription(String description) {
        subRecords.setSubStringPointer("DNAM", description);
    }

    DATA getDATA() {
        return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(SpellEffectFlag flag, boolean on) {
        getDATA().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(SpellEffectFlag flag) {
        return getDATA().flags.get(flag.value);
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
        return subRecords.getScripts();
    }

    /**
     *
     * @param value
     */
    public void setBaseCost(float value) {
        getDATA().baseCost = value;
    }

    /**
     *
     * @return
     */
    public float getBaseCost() {
        return getDATA().baseCost;
    }

    /**
     *
     * @param id
     */
    public void setRelatedID(FormID id) {
        getDATA().relatedID = id;
    }

    /**
     *
     * @return
     */
    public FormID getRelatedID() {
        return getDATA().relatedID;
    }

    /**
     *
     * @param val
     */
    public void setSkillType(ActorValue val) {
        getDATA().skillType = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getSkillType() {
        return getDATA().skillType;
    }

    /**
     *
     * @param val
     */
    public void setResistanceAV(ActorValue val) {
        getDATA().resistanceAV = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getResistanceAV() {
        return getDATA().resistanceAV;
    }

    /**
     *
     * @param counterEffectCount
     */
    public void setCounterEffectCount(int counterEffectCount) {
        getDATA().counterEffectCount = counterEffectCount;
    }

    /**
     *
     * @return
     */
    public int getCounterEffectCount() {
        return getDATA().counterEffectCount;
    }

    /**
     *
     * @param light
     */
    public void setLight(FormID light) {
        getDATA().lightID = light;
    }

    /**
     *
     * @return
     */
    public FormID getLight() {
        return getDATA().lightID;
    }

    /**
     *
     * @param value
     */
    public void setTaperWeight(float value) {
        getDATA().taperWeight = value;
    }

    /**
     *
     * @return
     */
    public float getTaperWeight() {
        return getDATA().taperWeight;
    }

    /**
     *
     * @param hitShader
     */
    public void setHitShader(FormID hitShader) {
        getDATA().hitShader = hitShader;
    }

    /**
     *
     * @return
     */
    public FormID getHitShader() {
        return getDATA().hitShader;
    }

    /**
     *
     * @param enchantShader
     */
    public void setEnchantShader(FormID enchantShader) {
        getDATA().enchantShader = enchantShader;
    }

    /**
     *
     * @return
     */
    public FormID getEnchantShader() {
        return getDATA().enchantShader;
    }

    /**
     *
     * @param level
     */
    public void setSkillLevel(int level) {
        getDATA().skillLevel = level;
    }

    /**
     *
     * @return
     */
    public int getSkillLevel() {
        return getDATA().skillLevel;
    }

    /**
     *
     * @param area
     */
    public void setArea(int area) {
        getDATA().area = area;
    }

    /**
     *
     * @return
     */
    public int getArea() {
        return getDATA().area;
    }

    /**
     *
     * @param value
     */
    public void setCastingTime(float value) {
        getDATA().castingTime = value;
    }

    /**
     *
     * @return
     */
    public float getCastingTime() {
        return getDATA().castingTime;
    }

    /**
     *
     * @param value
     */
    public void setTaperCurve(float value) {
        getDATA().taperCurve = value;
    }

    /**
     *
     * @return
     */
    public float getTaperCurve() {
        return getDATA().taperCurve;
    }

    /**
     *
     * @param value
     */
    public void setTaperDuration(float value) {
        getDATA().taperDuration = value;
    }

    /**
     *
     * @return
     */
    public float getTaperDuration() {
        return getDATA().taperDuration;
    }

    /**
     *
     * @param value
     */
    public void setSecondAVWeight(float value) {
        getDATA().secondAVWeight = value;
    }

    /**
     *
     * @return
     */
    public float getSecondAVWeight() {
        return getDATA().secondAVWeight;
    }

    /**
     *
     * @param value
     */
    public void setEffectType(int value) {
        getDATA().effectType = value;
    }

    /**
     *
     * @return
     */
    public float getEffectType() {
        return getDATA().effectType;
    }

    /**
     *
     * @param val
     */
    public void setPrimaryAV(ActorValue val) {
        getDATA().primaryAV = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getPrimaryAV() {
        return getDATA().primaryAV;
    }

    /**
     *
     * @param id
     */
    public void setProjectile(FormID id) {
        getDATA().projectileID = id;
    }

    /**
     *
     * @return
     */
    public FormID getProjectile() {
        return getDATA().projectileID;
    }

    /**
     *
     * @param id
     */
    public void setExplosion(FormID id) {
        getDATA().explosionID = id;
    }

    /**
     *
     * @return
     */
    public FormID getExplosion() {
        return getDATA().explosionID;
    }

    /**
     *
     * @param cast
     */
    public void setCastType(CastType cast) {
        getDATA().castType = cast;
    }

    /**
     *
     * @return
     */
    public CastType getCastType() {
        return getDATA().castType;
    }

    /**
     *
     * @param del
     */
    public void setDeliveryType(DeliveryType del) {
        getDATA().deliveryType = del;
    }

    /**
     *
     * @return
     */
    public DeliveryType getDeliveryType() {
        return getDATA().deliveryType;
    }

    /**
     *
     * @param val
     */
    public void setSecondAV(ActorValue val) {
        getDATA().secondAV = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getSecondAV() {
        return getDATA().secondAV;
    }

    /**
     *
     * @param art
     */
    public void setCastingArt(FormID art) {
        getDATA().castingArt = art;
    }

    /**
     *
     * @return
     */
    public FormID getCastingArt() {
        return getDATA().castingArt;
    }

    /**
     *
     * @param art
     */
    public void setHitEffectArt(FormID art) {
        getDATA().hitEffectArt = art;
    }

    /**
     *
     * @return
     */
    public FormID getHitEffectArt() {
        return getDATA().hitEffectArt;
    }

    /**
     *
     * @param data
     */
    public void setImpactData(FormID data) {
        getDATA().impactData = data;
    }

    /**
     *
     * @return
     */
    public FormID getImpactData() {
        return getDATA().impactData;
    }

    /**
     *
     * @param mult
     */
    public void setSkillUsageMult(float mult) {
        getDATA().skillUsageMult = mult;
    }

    /**
     *
     * @return
     */
    public float getSkillUsageMult() {
        return getDATA().skillUsageMult;
    }

    /**
     *
     * @param id
     */
    public void setDualCast(FormID id) {
        getDATA().dualCastID = id;
    }

    /**
     *
     * @return
     */
    public FormID getDualCast() {
        return getDATA().dualCastID;
    }

    /**
     *
     * @param scale
     */
    public void setDualCastScale(float scale) {
        getDATA().dualCastScale = scale;
    }

    /**
     *
     * @return
     */
    public float getDualCastScale() {
        return getDATA().dualCastScale;
    }

    /**
     *
     * @param art
     */
    public void setEnchantArt(FormID art) {
        getDATA().enchantArtID = art;
    }

    /**
     *
     * @return
     */
    public FormID getEnchantArt() {
        return getDATA().enchantArtID;
    }

    /**
     *
     * @param id
     */
    public void setEquipAbility(FormID id) {
        getDATA().equipAbility = id;
    }

    /**
     *
     * @return
     */
    public FormID getEquipAbility() {
        return getDATA().equipAbility;
    }

    /**
     *
     * @param id
     */
    public void setImageSpaceMod(FormID id) {
        getDATA().imageSpaceModID = id;
    }

    /**
     *
     * @return
     */
    public FormID getImageSpaceMod() {
        return getDATA().imageSpaceModID;
    }

    /**
     *
     * @param id
     */
    public void setPerk(FormID id) {
        getDATA().perkID = id;
    }

    /**
     *
     * @return
     */
    public FormID getPerk() {
        return getDATA().perkID;
    }

    /**
     *
     * @param vol
     */
    public void setSoundVolume(SoundVolume vol) {
        getDATA().vol = vol;
    }

    /**
     *
     * @return
     */
    public SoundVolume getSoundVolume() {
        return getDATA().vol;
    }

    /**
     *
     * @param score
     */
    public void setScriptAIDataScore(float score) {
        getDATA().scriptAIDataScore = score;
    }

    /**
     *
     * @return
     */
    public float getScriptAIDataScore() {
        return getDATA().scriptAIDataScore;
    }

    /**
     *
     * @param score
     */
    public void setScriptAIDataTime(float score) {
        getDATA().scriptAIDataDelayTime = score;
    }

    /**
     *
     * @return
     */
    public float getScriptAIDataTime() {
        return getDATA().scriptAIDataDelayTime;
    }

    /**
     *
     * @return
     */
    public ArrayList<Condition> getConditions() {
        return subRecords.getSubList("CTDA").toPublic();
    }

    /**
     *
     * @param c
     */
    public void addCondition(Condition c) {
        subRecords.getSubList("CTDA").add(c);
    }

    /**
     *
     * @param c
     */
    public void removeCondition(Condition c) {
        subRecords.getSubList("CTDA").remove(c);
    }

}
