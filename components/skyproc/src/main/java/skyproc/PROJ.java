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
 *
 * @author Justin Swanson
 */
public class PROJ extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype PROJprototype = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(new Model());
	    add(new DestructionData());
	    add(new DATA());
	    add(SubString.getNew("NAM1", true));
	    add(new SubData("NAM2"));
	    add(new SubData("VNAM")); // SoundVolume
	}
    };
    static class DATA extends SubRecord {

	LFlags flags = new LFlags(2);
	LFlags projType = new LFlags(2);
	float gravity = 0;
	float speed = 0;
	float range = 0;   //1
	FormID light = new FormID();
	FormID muzzleLight = new FormID();
	float tracerChance = 0;
	float proximity = 0;  // 2
	float timer = 0;
	FormID explosionType = new FormID();
	FormID sound = new FormID();
	float muzzleFlashDuration = 0;  //3
	float fadeDuration = 0;
	float impactForce = 0;
	FormID explosionSound = new FormID();
	FormID disableSound = new FormID();  //4
	FormID defaultWeaponSource = new FormID();
	float coneSpread = 0;
	float collisionRadius = 0;
	float lifetime = 0; //5
	float relaunchInterval = 0;
	FormID decalData = new FormID();
	byte[] collisionLayer = new byte[4];

	DATA() {
	    super();
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>();
	    out.add(light);
	    out.add(muzzleLight);
	    out.add(explosionType);
	    out.add(sound);
	    out.add(explosionSound);
	    out.add(disableSound);
	    out.add(defaultWeaponSource);
	    out.add(decalData);
	    return out;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(flags.export(), 2);
	    out.write(projType.export(), 2);
	    out.write(gravity);
	    out.write(speed);
	    out.write(range);
	    light.export(out);
	    muzzleLight.export(out);
	    out.write(tracerChance);
	    out.write(proximity);
	    out.write(timer);
	    explosionType.export(out);
	    sound.export(out);
	    out.write(muzzleFlashDuration);
	    out.write(fadeDuration);
	    out.write(impactForce);
	    explosionSound.export(out);
	    disableSound.export(out);
	    defaultWeaponSource.export(out);
	    out.write(coneSpread);
	    out.write(collisionRadius);
	    out.write(lifetime);
	    out.write(relaunchInterval);
	    decalData.export(out);
	    out.write(collisionLayer);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    flags.set(in.extract(2));
	    projType.set(in.extract(2));
	    gravity = in.extractFloat();
	    speed = in.extractFloat();
	    range = in.extractFloat();   //16
	    light.parseData(in, srcMod);
	    muzzleLight.parseData(in, srcMod);
	    tracerChance = in.extractFloat();
	    proximity = in.extractFloat();  //32
	    timer = in.extractFloat();
	    explosionType.parseData(in, srcMod);
	    sound.parseData(in, srcMod);
	    muzzleFlashDuration = in.extractFloat();  //48
	    fadeDuration = in.extractFloat();
	    impactForce = in.extractFloat();
	    explosionSound.parseData(in, srcMod);
	    disableSound.parseData(in, srcMod);  //64
	    defaultWeaponSource.parseData(in, srcMod);
	    coneSpread = in.extractFloat();
	    collisionRadius = in.extractFloat();
	    lifetime = in.extractFloat(); // 80
	    relaunchInterval = in.extractFloat();
	    if (!in.isDone()) {
		decalData.parseData(in, srcMod);
	    }
	    if (!in.isDone()) {
		collisionLayer = in.extract(4);  // 92
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 92;
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
    public enum ProjectileFlag {

	/**
	 *
	 */
	Explosion(1),
	/**
	 *
	 */
	AltTrigger(2),
	/**
	 *
	 */
	MuzzleFlash(3),
	/**
	 *
	 */
	CanBeDisabled(5),
	/**
	 *
	 */
	CanBePickedUp(6),
	/**
	 *
	 */
	SuperSonic(7),
	/**
	 *
	 */
	CritPinsLimbs(8),
	/**
	 *
	 */
	PassThroughSmallTransparent(9),
	/**
	 *
	 */
	DisableCombatAimCorrection(10);
	int value;

	ProjectileFlag(int val) {
	    value = val;
	}
    }

    /**
     *
     */
    public enum ProjectileType {

	/**
	 *
	 */
	Missile, //1
	/**
	 *
	 */
	Lobber, //2
	/**
	 *
	 */
	Beam, //4
	/**
	 *
	 */
	Flame, //8
	/**
	 *
	 */
	Cone, //10
	/**
	 *
	 */
	Barrier, //20
	/**
	 *
	 */
	Arrow; //40

	static ProjectileType get(int value) {
	    switch (value) {
		case 1:
		    return Missile;
		case 2:
		    return Lobber;
		case 4:
		    return Beam;
		case 8:
		    return Flame;
		case 16:
		    return Cone;
		case 32:
		    return Barrier;
		default:
		    return Arrow;
	    }
	}
    }

    // Common Functions
    PROJ() {
	super();
	subRecords.setPrototype(PROJprototype);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("PROJ");
    }

    @Override
    Record getNew() {
	return new PROJ();
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

    /**
     *
     * @param filename
     */
    public void setEffectModel(String filename) {
	subRecords.setSubString("NAM1", filename);
    }

    /**
     *
     * @return
     */
    public String getEffectModel() {
	return subRecords.getSubString("NAM1").print();
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ProjectileFlag flag, boolean on) {
	getDATA().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ProjectileFlag flag) {
	return getDATA().flags.get(flag.value);
    }

    /**
     *
     * @param t
     */
    public void setProjType(ProjectileType t) {
	LFlags flags = getDATA().projType;
	flags.clear();
	flags.set(t.ordinal(), true);
    }

    /**
     *
     * @return
     */
    public ProjectileType getProjType() {
	return ProjectileType.values()[getDATA().projType.getFirstTrue()];
    }

    /**
     *
     * @param gravity
     */
    public void setGravity(float gravity) {
	getDATA().gravity = gravity;
    }

    /**
     *
     * @return
     */
    public float getGravity() {
	return getDATA().gravity;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(float speed) {
	getDATA().speed = speed;
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
	return getDATA().speed;
    }

    /**
     *
     * @param range
     */
    public void setRange(float range) {
	getDATA().range = range;
    }

    /**
     *
     * @return
     */
    public float getRange() {
	return getDATA().range;
    }

    /**
     *
     * @param light
     */
    public void setLight(FormID light) {
	getDATA().light = light;
    }

    /**
     *
     * @return
     */
    public FormID getLight() {
	return getDATA().light;
    }

    /**
     *
     * @param light
     */
    public void setMuzzleLight(FormID light) {
	getDATA().muzzleLight = light;
    }

    /**
     *
     * @return
     */
    public FormID getMuzzleLight() {
	return getDATA().muzzleLight;
    }

    /**
     *
     * @param chance
     */
    public void setTracerChance(float chance) {
	getDATA().tracerChance = chance;
    }

    /**
     *
     * @return
     */
    public float getTracerChance() {
	return getDATA().tracerChance;
    }

    /**
     *
     * @param proximity
     */
    public void setProximity(float proximity) {
	getDATA().proximity = proximity;
    }

    /**
     *
     * @return
     */
    public float getProximity() {
	return getDATA().proximity;
    }

    /**
     *
     * @param timer
     */
    public void setTimer(float timer) {
	getDATA().timer = timer;
    }

    /**
     *
     * @return
     */
    public float getTimer() {
	return getDATA().timer;
    }

    /**
     *
     * @param explType
     */
    public void setExplosionType(FormID explType) {
	getDATA().explosionType = explType;
    }

    /**
     *
     * @return
     */
    public FormID getExplosionType() {
	return getDATA().explosionType;
    }

    /**
     *
     * @param sound
     */
    public void setSound(FormID sound) {
	getDATA().sound = sound;
    }

    /**
     *
     * @return
     */
    public FormID getSound() {
	return getDATA().sound;
    }

    /**
     *
     * @param duration
     */
    public void setMuzzleFlashDuration(float duration) {
	getDATA().muzzleFlashDuration = duration;
    }

    /**
     *
     * @return
     */
    public float getMuzzleFlashDuration() {
	return getDATA().muzzleFlashDuration;
    }

    /**
     *
     * @param duration
     */
    public void setFadeDuration(float duration) {
	getDATA().fadeDuration = duration;
    }

    /**
     *
     * @return
     */
    public float getFadeDuration() {
	return getDATA().fadeDuration;
    }

    /**
     *
     * @param force
     */
    public void setImpactForce(float force) {
	getDATA().impactForce = force;
    }

    /**
     *
     * @return
     */
    public float getImpactForce() {
	return getDATA().impactForce;
    }

    /**
     *
     * @param sound
     */
    public void setExplosionSound(FormID sound) {
	getDATA().explosionSound = sound;
    }

    /**
     *
     * @return
     */
    public FormID getExplosionSound() {
	return getDATA().explosionSound;
    }

    /**
     *
     * @param sound
     */
    public void setDisableSound(FormID sound) {
	getDATA().disableSound = sound;
    }

    /**
     *
     * @return
     */
    public FormID getDisableSound() {
	return getDATA().disableSound;
    }

    /**
     *
     * @param weaponSource
     */
    public void setDefaultWeaponSource(FormID weaponSource) {
	getDATA().defaultWeaponSource = weaponSource;
    }

    /**
     *
     * @return
     */
    public FormID getDefaultWeaponSource() {
	return getDATA().defaultWeaponSource;
    }

    /**
     *
     * @param spread
     */
    public void setConeSpread(float spread) {
	getDATA().coneSpread = spread;
    }

    /**
     *
     * @return
     */
    public float getConeSpread() {
	return getDATA().coneSpread;
    }

    /**
     *
     * @param radius
     */
    public void setCollisionRadius(float radius) {
	getDATA().collisionRadius = radius;
    }

    /**
     *
     * @return
     */
    public float getCollisionRadius() {
	return getDATA().collisionRadius;
    }

    /**
     *
     * @param lifetime
     */
    public void setLifetime(float lifetime) {
	getDATA().lifetime = lifetime;
    }

    /**
     *
     * @return
     */
    public float getLifetime() {
	return getDATA().lifetime;
    }

    /**
     *
     * @param interval
     */
    public void setRelaunchInterval(float interval) {
	getDATA().relaunchInterval = interval;
    }

    /**
     *
     * @return
     */
    public float getRelaunchInterval() {
	return getDATA().relaunchInterval;
    }

    /**
     *
     * @param decal
     */
    public void setDecalData(FormID decal) {
	getDATA().decalData = decal;
    }

    /**
     *
     * @return
     */
    public FormID getDecalData() {
	return getDATA().decalData;
    }
    
    /**
     * 
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }
}
