package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Image Space major record. Used for various lighting settings.
 *
 * @author Plutoman101
 */
public class IMGS extends MajorRecord {

    // Static prototypes and definitions
    static final SubPrototype IMGSproto = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubData("ENAM"));
	    add(new HNAM());
	    add(new CNAM());
	    add(new TNAM());
	    add(new DNAM());
	}
    };

    static final class HNAM extends SubRecord {

	private float eyeAdaptSpeed = 0;
	private float bloomRadius = 0;
	private float bloomThreshold = 0;
	private float bloomScale = 0;
	private float targetLum1 = 0;
	private float targetLum2 = 0;
	private float sunlightScale = 0;
	private float skyScale = 0;
	private float eyeAdaptStrength = 0;
	private boolean valid = true;

	HNAM() {
	    super();
	    valid = false;
	}

	@Override
	SubRecord getNew(String type) {
	    return new HNAM();
	}

	@Override
	final void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);

	    eyeAdaptSpeed = in.extractFloat();
	    bloomRadius = in.extractFloat();
	    bloomThreshold = in.extractFloat();
	    bloomScale = in.extractFloat();
	    targetLum1 = in.extractFloat();
	    targetLum2 = in.extractFloat();
	    sunlightScale = in.extractFloat();
	    skyScale = in.extractFloat();
	    eyeAdaptStrength = in.extractFloat();

	    if (SPGlobal.logMods){
		logMod(srcMod, "", "HNAM record: ");
		logMod(srcMod, "", "  " + "Eye Adapt Speed: " + eyeAdaptSpeed + ", Bloom Radius: " + bloomRadius);
		logMod(srcMod, "", "  " + "Bloom Threshold: " + bloomThreshold + ", Bloom Scale: " + bloomScale + ", Target Lum #1: " + targetLum1);
		logMod(srcMod, "", "  " + "Target Lum #2: " + targetLum2 + ", Sunlight Scale: " + sunlightScale);
		logMod(srcMod, "", "  " + "Sky Scale: " + skyScale + ", Eye Adapt Strength: " + eyeAdaptStrength);
	    }

	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    if (isValid()) {
		out.write(eyeAdaptSpeed);
		out.write(bloomRadius);
		out.write(bloomThreshold);
		out.write(bloomScale);
		out.write(targetLum1);
		out.write(targetLum2);
		out.write(sunlightScale);
		out.write(skyScale);
		out.write(eyeAdaptStrength);
	    }
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (isValid()) {
		return 36;
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("HNAM");
	}
    }

    static final class CNAM extends SubRecord {

	private float saturation = 0;
	private float brightness = 0;
	private float contrast = 0;
	private boolean valid = true;

	public CNAM() {
	    super();
	    valid = false;
	}

	@Override
	SubRecord getNew(String type) {
	    return new CNAM();
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);

	    saturation = in.extractFloat();
	    brightness = in.extractFloat();
	    contrast = in.extractFloat();

	    if (SPGlobal.logMods){
		logMod(srcMod, "", "CNAM record: ");
		logMod(srcMod, "", "  " + "Saturation: " + saturation + ", Brightness: " + brightness + ", Contrast: " + contrast);
	    }

	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    if (isValid()) {
		out.write(saturation);
		out.write(brightness);
		out.write(contrast);
	    }
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (isValid()) {
		return 12;
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("CNAM");
	}
    }

    static final class TNAM extends SubRecord {

	private float red = 0;
	private float green = 0;
	private float blue = 0;
	private float alpha = 0;
	private boolean valid = true;

	public TNAM() {
	    super();
	    valid = false;
	}

	@Override
	SubRecord getNew(String type) {
	    return new TNAM();
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);

	    alpha = in.extractFloat();
	    red = in.extractFloat();
	    green = in.extractFloat();
	    blue = in.extractFloat();

	    if (SPGlobal.logMods){
		logMod(srcMod, "", "TNAM record: RWX Format");
		logMod(srcMod, "", "  " + "Red: " + red + ", Green: " + green);
		logMod(srcMod, "", "  " + "Blue: " + blue + ", Alpha: " + alpha);
	    }

	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    if (isValid()) {
		out.write(alpha);
		out.write(red);
		out.write(green);
		out.write(blue);
	    }
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (isValid()) {
		return 16;
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("TNAM");
	}
    }

    static final class DNAM extends SubRecord {

	float DOFstrength = 0;
	float DOFdistance = 0;
	float DOFrange = 0;
	byte[] unknown;
	boolean valid = false;

	public DNAM() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    if (isValid()) {
		out.write(DOFstrength);
		out.write(DOFdistance);
		out.write(DOFrange);
		if (unknown != null) {
		    out.write(unknown, 4);
		}
	    }
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    DOFstrength = in.extractFloat();
	    DOFdistance = in.extractFloat();
	    DOFrange = in.extractFloat();
	    if (in.available() >= 4) {
		unknown = in.extract(4);
	    }
	    valid = true;
	}

	@Override
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (isValid()) {
		if (unknown != null) {
		    return 16;
		} else {
		    return 12;
		}
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DNAM");
	}
    }

    // Common Functions
    /**
     * Creates a new IMGS record.
     */
    public IMGS() {
	super();
	subRecords.setPrototype(IMGSproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("IMGS");
    }

    @Override
    Record getNew() {
	return new IMGS();
    }

    // Get/Set
    HNAM getHNAM() {
	return (HNAM) subRecords.get("HNAM");
    }

    CNAM getCNAM() {
	return (CNAM) subRecords.get("CNAM");
    }

    TNAM getTNAM() {
	return (TNAM) subRecords.get("TNAM");
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
    }

    /**
     *
     * @return
     */
    public float getEyeAdaptSpeed() {
	return getHNAM().eyeAdaptSpeed;
    }

    /**
     *
     * @return
     */
    public float getBloomRadius() {
	return getHNAM().bloomRadius;
    }

    /**
     *
     * @return
     */
    public float getBloomThreshold() {
	return getHNAM().bloomThreshold;
    }

    /**
     *
     * @return
     */
    public float getBloomScale() {
	return getHNAM().bloomScale;
    }

    /**
     *
     * @return
     */
    public float getTargetLum1() {
	return getHNAM().targetLum1;
    }

    /**
     *
     * @return
     */
    public float getTargetLum2() {
	return getHNAM().targetLum2;
    }

    /**
     *
     * @return
     */
    public float getSunlightScale() {
	return getHNAM().sunlightScale;
    }

    /**
     *
     * @return
     */
    public float getSkyScale() {
	return getHNAM().skyScale;
    }

    /**
     *
     * @return
     */
    public float getEyeAdaptStrength() {
	return getHNAM().eyeAdaptStrength;
    }

    /**
     *
     * @param in
     */
    public void setEyeAdaptSpeed(float in) {
	getHNAM().eyeAdaptSpeed = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomRadius(float in) {
	getHNAM().bloomRadius = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomThreshold(float in) {
	getHNAM().bloomThreshold = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomScale(float in) {
	getHNAM().bloomScale = in;
    }

    /**
     *
     * @param in
     */
    public void setTargetLum1(float in) {
	getHNAM().targetLum1 = in;
    }

    /**
     *
     * @param in
     */
    public void setTargetLum2(float in) {
	getHNAM().targetLum2 = in;
    }

    /**
     *
     * @param in
     */
    public void setSunlightScale(float in) {
	getHNAM().sunlightScale = in;
    }

    /**
     *
     * @param in
     */
    public void setSkyScale(float in) {
	getHNAM().skyScale = in;
    }

    /**
     *
     * @param in
     */
    public void setEyeAdaptStrength(float in) {
	getHNAM().eyeAdaptStrength = in;
    }

    /**
     *
     * @return
     */
    public float getSaturation() {
	return getCNAM().saturation;
    }

    /**
     *
     * @return
     */
    public float getBrightness() {
	return getCNAM().brightness;
    }

    /**
     *
     * @return
     */
    public float getContrast() {
	return getCNAM().contrast;
    }

    /**
     *
     * @return
     */
    public float getRed() {
	return getTNAM().red;
    }

    /**
     *
     * @return
     */
    public float getBlue() {
	return getTNAM().blue;
    }

    /**
     *
     * @return
     */
    public float getGreen() {
	return getTNAM().green;
    }

    /**
     *
     * @return
     */
    public float getAlpha() {
	return getTNAM().alpha;
    }

    /**
     *
     * @param in
     */
    public void setSaturation(float in) {
	getCNAM().saturation = in;
    }

    /**
     *
     * @param in
     */
    public void setBrightness(float in) {
	getCNAM().brightness = in;
    }

    /**
     *
     * @param in
     */
    public void setContrast(float in) {
	getCNAM().contrast = in;
    }

    /**
     *
     * @param in
     */
    public void setRed(float in) {
	getTNAM().red = in;
    }

    /**
     *
     * @param in
     */
    public void setBlue(float in) {
	getTNAM().blue = in;
    }

    /**
     *
     * @param in
     */
    public void setGreen(float in) {
	getTNAM().green = in;
    }

    /**
     *
     * @param in
     */
    public void setAlpha(float in) {
	getTNAM().alpha = in;
    }

    /**
     *
     * @param in
     */
    public void setDOFstrength(float in) {
	getDNAM().DOFstrength = in;
    }

    /**
     *
     * @return
     */
    public float getDOFstrength() {
	return getDNAM().DOFstrength;
    }

    /**
     *
     * @param in
     */
    public void setDOFdistance(float in) {
	getDNAM().DOFdistance = in;
    }

    /**
     *
     * @return
     */
    public float getDOFdistance() {
	return getDNAM().DOFdistance;
    }

    /**
     *
     * @param in
     */
    public void setDOFrange(float in) {
	getDNAM().DOFrange = in;
    }

    /**
     *
     * @return
     */
    public float getDOFrange() {
	return getDNAM().DOFrange;
    }
}