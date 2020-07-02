/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.SubStringPointer.Files;

/**
 *
 * @author Justin Swanson
 */
public abstract class MajorRecordDescription extends MajorRecordNamed {

    static final SubPrototype descProto = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    SubStringPointer description = new SubStringPointer("DESC", Files.DLSTRINGS);
	    description.forceExport = true;
	    forceExport("DESC");
	    add(description);
	}
    };

    MajorRecordDescription() {
	super();
    }

    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if
     * empty.
     */
    public String getDescription() {
	return subRecords.getSubStringPointer("DESC").print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    public void setDescription(String description) {
	subRecords.setSubStringPointer("DESC", description);
    }
}
