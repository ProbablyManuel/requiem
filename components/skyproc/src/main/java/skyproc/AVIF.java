/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 * Actor value records and perk trees.
 *
 * @author Justin Swanson
 */
public class AVIF extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype AVIFproto = new SubPrototype(MajorRecordDescription.descProto) {
	@Override
	protected void addRecords() {
	    add(SubString.getNew("ANAM", true));
	    add(new SubData("CNAM"));
	    add(new SubData("AVSK"));
	    add(new SubList<>(new PerkReference(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubForm("PNAM"));
		    forceExport("PNAM");
		    add(new SubInt("FNAM"));
		    add(new SubInt("XNAM"));
		    add(new SubInt("YNAM"));
		    add(new SubFloat("HNAM"));
		    add(new SubFloat("VNAM"));
		    add(new SubForm("SNAM"));
		    add(new SubList<>(new SubInt("CNAM")));
		    add(new SubInt("INAM"));
		}
	    })));
	}
    };

    /**
     * A structure that represents a perk in a perktree
     */
    public static final class PerkReference extends SubShellBulkType {

	PerkReference(SubPrototype proto) {
	    super(proto, false);
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	SubRecord getNew(String type) {
	    return new PerkReference(getPrototype());
	}

	@Override
	Record getNew() {
	    return new PerkReference(getPrototype());
	}

	/**
	 *
	 * @param id
	 */
	public void setPerk(FormID id) {
	    subRecords.setSubForm("PNAM", id);
	}

	/**
	 *
	 * @return
	 */
	public FormID getPerk() {
	    return subRecords.getSubForm("PNAM").getForm();
	}

	/**
	 *
	 * @param x
	 */
	public void setX(int x) {
	    subRecords.setSubInt("XNAM", x);
	}

	/**
	 *
	 * @return
	 */
	public int getX() {
	    return subRecords.getSubInt("XNAM").get();
	}

	/**
	 *
	 * @param y
	 */
	public void setY(int y) {
	    subRecords.setSubInt("YNAM", y);
	}

	/**
	 *
	 * @return
	 */
	public int getY() {
	    return subRecords.getSubInt("YNAM").get();
	}

	/**
	 *
	 * @param horiz
	 */
	public void setHorizontalPos(float horiz) {
	    subRecords.setSubFloat("HNAM", horiz);
	}

	/**
	 *
	 * @return
	 */
	public float getHorizontalPos() {
	    return subRecords.getSubFloat("HNAM").get();
	}

	/**
	 *
	 * @param vert
	 */
	public void setVerticalPos(float vert) {
	    subRecords.setSubFloat("VNAM", vert);
	}

	/**
	 *
	 * @return
	 */
	public float getVerticalPos() {
	    return subRecords.getSubFloat("VNAM").get();
	}

	/**
	 *
	 * @param skill
	 */
	public void setSkill(FormID skill) {
	    subRecords.setSubForm("SNAM", skill);
	}

	/**
	 *
	 * @return
	 */
	public FormID getSkill() {
	    return subRecords.getSubForm("SNAM").getForm();
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Integer> getPointers() {
	    return subRecords.getSubList("CNAM").toPublic();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly
	 * affect the record.
	 */
	public void clearPointers() {
	    subRecords.getSubList("CNAM").clear();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly
	 * affect the record.
	 * @param index
	 */
	public void addPointer(int index) {
	    subRecords.getSubList("CNAM").add(index);
	}

	/**
	 * @param ref
	 */
	public void addPointer(PerkReference ref) {
	    addPointer(ref.getIndex());
	}

	/**
	 *
	 * @param index
	 */
	public void setIndex(int index) {
	    subRecords.setSubInt("INAM", index);
	}

	/**
	 *
	 * @return
	 */
	public int getIndex() {
	    return subRecords.getSubInt("INAM").get();
	}
    }

    // Common Functions
    AVIF() {
	super();
	subRecords.setPrototype(AVIFproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("AVIF");
    }

    @Override
    Record getNew() {
	return new AVIF();
    }

    // Get/Set
    /**
     *
     * @param abbr
     */
    public void setAbbreviation(String abbr) {
	subRecords.setSubString("ANAM", abbr);
    }

    /**
     *
     * @return
     */
    public String getAbbreviation() {
	return subRecords.getSubString("ANAM").print();
    }

    /**
     *
     * @return
     */
    public ArrayList<PerkReference> getPerkReferences() {
	return subRecords.getSubList("PNAM").toPublic();
    }
}
