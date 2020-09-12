package skyproc;

import java.io.IOException;
import java.util.Objects;
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
class SubFlag extends SubRecordTyped {
    LFlags flags;

    SubFlag(String type_, int size) {
	super(type_);
	flags = new LFlags(size);
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	out.write(flags.export(), flags.length());
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	flags.set(in.getAllBytes());
	if (SPGlobal.logMods){
	    logMod(srcMod, toString(), "Setting " + toString() + " to : " + print());
	}
    }

    void set(int bit, boolean on) {
	flags.set(bit, on);
    }

    boolean is(int bit) {
	return flags.get(bit);
    }

    @Override
    public String print() {
	return flags.toString();
    }

    @Override
    SubRecord getNew(String type) {
	return new SubFlag(type, flags.length());
    }

    @Override
    public boolean isValid() {
	return flags != null;
    }

    @Override
    int getContentLength(ModExporter out) {
	return flags.length();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubFlag other = (SubFlag) obj;
	if (!Objects.equals(this.flags, other.flags)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 89 * hash + Objects.hashCode(this.flags);
	return hash;
    }
}
