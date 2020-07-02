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
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class LeveledEntry extends SubShell {
    
    static final SubPrototype LVLOproto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new LVLOin());
	    add(new Owner());
	}
    };

    /**
     *
     * @param id
     * @param level
     * @param count
     */
    public LeveledEntry(FormID id, int level, int count) {
	this();
	setForm(id);
	setLevel(level);
	setCount(count);
    }

    LeveledEntry() {
	super(LVLOproto);
    }

    @Override
    boolean isValid() {
	return subRecords.isAnyValid();
    }

    @Override
    SubRecord getNew(String type) {
	return new LeveledEntry();
    }

    static class LVLOin extends SubRecord {

	int level = 1;
	FormID entry = new FormID();
	int count = 1;

	LVLOin() {
	    super();
	}

	@Override
	SubRecord getNew(String type) {
	    return new LVLOin();
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 12;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(entry);
	    return out;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(level, 4);
	    entry.export(out);
	    out.write(count, 4);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    level = in.extractInt(4);
	    entry.parseData(in, srcMod);
	    count = in.extractInt(4);
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("LVLO");
	}
    }

    LVLOin getEntry() {
	return (LVLOin) subRecords.get("LVLO");
    }

    /**
     *
     * @return The level this entry is marked on the LVLN.
     */
    public int getLevel() {
	return getEntry().level;
    }

    /**
     *
     * @param in The level to mark the entry as on the LVLN.
     */
    final public void setLevel(int in) {
	getEntry().level = in;
    }

    /**
     *
     * @param in The number to set the spawn count to.
     */
    final public void setCount(int in) {
	getEntry().count = in;
    }

    /**
     *
     * @return The spawn counter.
     */
    public int getCount() {
	return getEntry().count;
    }

    /**
     *
     * @param id
     */
    final public void setForm(FormID id) {
	getEntry().entry = id;
    }

    /**
     *
     * @return
     */
    public FormID getForm() {
	return getEntry().entry;
    }
    
    /**
     *
     * @return condition of the entry, 1.0 is 100% health and 1.1 = 110%
     */
    public float getItemCondition() {
        Owner own = (Owner) subRecords.get("COED");
        float cond = own.getItemCondition();
        return (cond == 0.0f ? 1.0f : cond );
    }

    /**
     *
     * @param itemCondition the health of the item where 1.0 = 100% and 1.1 = 110%
     */
    public void setItemCondition(float itemCondition) {
        Owner own = (Owner) subRecords.get("COED");
        own.setItemCondition(itemCondition);
    }

}
