package skyproc;

import java.io.Serializable;
import skyproc.SubStringPointer.Files;

/**
 * An extended major record that has a name. (FULL record)
 *
 * @author Justin Swanson
 */
public abstract class MajorRecordNamed extends MajorRecord implements Serializable {

    static final SubPrototype namedProto = new SubPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubStringPointer("FULL", Files.STRINGS));
	}
    };

    MajorRecordNamed() {
	super();
    }

    /**
     * Returns the in-game name of the Major Record.
     *
     * @return
     */
    public String getName() {
	return subRecords.getSubStringPointer("FULL").print();
    }

    /**
     * Sets the in-game name of the Major Record.
     *
     * @param in The string to set the in-game name to.
     */
    public void setName(String in) {
	subRecords.setSubStringPointer("FULL", in);
    }
}
