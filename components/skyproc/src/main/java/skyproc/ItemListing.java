/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class ItemListing extends SubShell {

    static SubPrototype itemListingProto = new SubPrototype(){

	@Override
	protected void addRecords() {
	    add(new SubFormInt("CNTO"));
	    add(new Owner());
	}
    };

    /**
     *
     * @param id
     * @param count
     */
    public ItemListing(FormID id, int count) {
	this();
	subRecords.setSubFormInt("CNTO", id, count);
    }

    /**
     *
     * @param id
     */
    public ItemListing(FormID id) {
	this(id, 1);
    }

    ItemListing() {
	super(itemListingProto);
    }

    /**
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ItemListing other = (ItemListing) obj;
	if (!getForm().equals(other.getForm())) {
	    return false;
	}
	return true;
    }

    /**
     * 
     * @return
     */
    @Override
    public int hashCode() {
	return getForm().hashCode();
    }

    @Override
    SubRecord getNew(String type) {
	return new ItemListing();
    }

    // Get/set
    /**
     *
     * @return
     */
    public FormID getForm() {
	return subRecords.getSubFormInt("CNTO").getForm();
    }

    /**
     *
     * @param id
     */
    public void setForm(FormID id) {
	subRecords.setSubFormInt("CNTO", id);
    }

    /**
     *
     * @return
     */
    public int getCount() {
	return subRecords.getSubFormInt("CNTO").getNum();
    }

    /**
     *
     * @param count
     */
    public void setCount(int count) {
	subRecords.setSubFormInt("CNTO", count);
    }
}
