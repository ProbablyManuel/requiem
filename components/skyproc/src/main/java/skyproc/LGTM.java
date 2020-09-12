/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Kurtis
 */
public class LGTM extends MajorRecord {

    static final SubPrototype LGTMproto = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new DATA());
	    add(new DALC());
	}
    };

    LGTM() {
	super();
	subRecords.setPrototype(LGTMproto);
    }

    @Override
    Record getNew() {
	return new LGTM();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("LGTM");
    }

    static Color extractColor(LImport in) {
	return new Color(in.extractInt(1),
		in.extractInt(1),
		in.extractInt(1),
		in.extractInt(1));
    }

    static void writeColor(LOutFile out, Color c) throws IOException {
	out.write(c.getRed(), 1);
	out.write(c.getGreen(), 1);
	out.write(c.getBlue(), 1);
	out.write(c.getAlpha(), 1);
    }

    static class DATA extends SubRecord {

	Color amb = new Color(0, 0, 0, 0);
	Color dir = new Color(0, 0, 0, 0);
	Color fog = new Color(0, 0, 0, 0);
	float fogNear = 0;
	float fogFar = 0;
	int dirRotationXY = 0;
	int dirRotationZ = 0;
	float dirFade = 0;
	float fogClipDist = 0;
	float fogPower = 0;
	byte[] unknown;
	Color fogFarColor = new Color(0, 0, 0, 0);
	float fogMax = 0;
	float lightFadeStart = 0;
	float lightFadeEnd = 0;
	byte[] unknown2 = new byte[4];

	DATA() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    writeColor(out, amb);
	    writeColor(out, dir);
	    writeColor(out, fog);
	    out.write(fogNear);
	    out.write(fogFar);
	    out.write(dirRotationXY, 4);
	    out.write(dirRotationZ, 4);
	    out.write(dirFade);
	    out.write(fogClipDist);
	    out.write(fogPower);
	    out.write(unknown, 32);
	    writeColor(out, fogFarColor);
	    out.write(fogMax);
	    out.write(lightFadeStart);
	    out.write(lightFadeEnd);
	    out.write(unknown2, 4);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    amb = extractColor(in);
	    dir = extractColor(in);
	    fog = extractColor(in);
	    fogNear = in.extractFloat();
	    fogFar = in.extractFloat();
	    dirRotationXY = in.extractInt(4);
	    dirRotationZ = in.extractInt(4);
	    dirFade = in.extractFloat();
	    fogClipDist = in.extractFloat();
	    fogPower = in.extractFloat();
	    unknown = in.extract(32);
	    fogFarColor = extractColor(in);
	    fogMax = in.extractFloat();
	    lightFadeStart = in.extractFloat();
	    lightFadeEnd = in.extractFloat();
	    unknown2 = in.extract(4);
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
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(0);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    static class DALC extends SubRecord {

	Color dirAmbXpos = new Color(0, 0, 0, 0);
	Color dirAmbXneg = new Color(0, 0, 0, 0);
	Color dirAmbYpos = new Color(0, 0, 0, 0);
	Color dirAmbYneg = new Color(0, 0, 0, 0);
	Color dirAmbZpos = new Color(0, 0, 0, 0);
	Color dirAmbZneg = new Color(0, 0, 0, 0);
	Color specular = new Color(0, 0, 0, 0);
	float fresnelPower = 0;

	DALC() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    writeColor(out, dirAmbXpos);
	    writeColor(out, dirAmbXneg);
	    writeColor(out, dirAmbYpos);
	    writeColor(out, dirAmbYneg);
	    writeColor(out, dirAmbZpos);
	    writeColor(out, dirAmbZneg);
	    writeColor(out, specular);
	    out.write(fresnelPower);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    dirAmbXpos = extractColor(in);
	    dirAmbXneg = extractColor(in);
	    dirAmbYpos = extractColor(in);
	    dirAmbYneg = extractColor(in);
	    dirAmbZpos = extractColor(in);
	    dirAmbZneg = extractColor(in);
	    specular = extractColor(in);
	    fresnelPower = in.extractFloat();
	}

	@Override
	SubRecord getNew(String type) {
	    return new DALC();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 32;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(0);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DALC");
	}
    }
}
