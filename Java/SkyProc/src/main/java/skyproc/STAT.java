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
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class STAT extends MajorRecord {

    static final SubPrototype STATprototype = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubData("OBND", new byte[12]));
	    add(new Model());
	    add(new DNAM());
	    add(new SubData("MNAM"));
	}
    };
    static class DNAM extends SubRecordTyped {

	float angle = 0;
	FormID id = new FormID();

	DNAM () {
	    super("DNAM");
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(angle);
	    id.export(out);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    angle = in.extractFloat();
	    id.parseData(in, srcMod);
	}

	@Override
	ArrayList allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(id);
	    return out;
	}

	@Override
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 8;
	}

	@Override
	boolean subRecordEquals(SubRecord subRecord) {
	    return equals(subRecord);
	}

	@Override
	public int hashCode() {
	    int hash = 5;
	    hash = 31 * hash + Float.floatToIntBits(this.angle);
	    hash = 31 * hash + Objects.hashCode(this.id);
	    return hash;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
		return false;
	    }
	    if (getClass() != obj.getClass()) {
		return false;
	    }
	    final DNAM other = (DNAM) obj;
	    if (Float.floatToIntBits(this.angle) != Float.floatToIntBits(other.angle)) {
		return false;
	    }
	    if (!Objects.equals(this.id, other.id)) {
		return false;
	    }
	    return true;
	}
    }

    // Common Functions
    STAT() {
	super();
	subRecords.setPrototype(STATprototype);
    }

    /**
     *
     * @param edid
     */
    public STAT(String edid) {
	this();
	originateFromPatch(edid);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("STAT");
    }

    @Override
    Record getNew() {
	return new STAT();
    }

    /**
     *
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
    }

    /**
     *
     * @return
     */
    public float getMaxAngle() {
	return getDNAM().angle;
    }

    /**
     *
     * @param angle
     */
    public void setMaxAngle(float angle) {
	getDNAM().angle = angle;
    }

    /**
     *
     * @param id
     */
    public void setMaterial(FormID id) {
	getDNAM().id = id;
    }

    /**
     *
     * @return
     */
    public FormID getMaterial() {
	return getDNAM().id;
    }
}
