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

/**
 *
 * @author Justin Swanson
 */
public class DestructionData extends SubShell {

    static SubPrototype destructionProto = new SubPrototype() {

	@Override
	protected void addRecords() {
	    add(new SubData("DEST"));
	    add(new SubList<>(new SubShell(new SubPrototype() {

		@Override
		protected void addRecords() {
		    add(new DSTD());
		    add(SubString.getNew("DMDL", true));
		    add(new AltTextures("DMDS"));
		    add(new SubData("DSTF"));
		}
	    })));
	}
    };

    static class DSTD extends SubRecordTyped {

	byte healthPct = 0;
	byte index = 0;
	byte modelDmgStage = 0;
	LFlags flags = new LFlags(1);
	int selfDmgPerSec = 0;
	FormID explosion = new FormID();
	FormID debree = new FormID();
	int debreeCount = 0;

	DSTD() {
	    super("DSTD");
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(healthPct);
	    out.write(index);
	    out.write(modelDmgStage);
	    out.write(flags.export());
	    out.write(selfDmgPerSec);
	    explosion.export(out);
	    debree.export(out);
	    out.write(debreeCount);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    healthPct = in.extract(1)[0];
	    index = in.extract(1)[0];
	    modelDmgStage = in.extract(1)[0];
	    flags.set(in.extract(1));
	    selfDmgPerSec = in.extractInt(4);
	    explosion.parseData(in, srcMod);
	    debree.parseData(in, srcMod);
	    debreeCount = in.extractInt(4);
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(2);
	    out.add(explosion);
	    out.add(debree);
	    return out;
	}

	@Override
	SubRecord getNew(String type) {
	    return new DSTD();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 20;
	}
    }

    DestructionData() {
	super(destructionProto);
    }

    @Override
    SubRecord getNew(String type) {
	return new DestructionData();
    }

    @Override
    boolean isValid() {
	return true;
    }
}
