/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public class CONT extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype CONTprototype = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(new Model());
	    add(new SubListCounted<>("COCT", 4, new ItemListing()));
	    add(new DestructionData());
	    add(new SubData("DATA"));
	    add(new SubForm("SNAM"));
	    add(new SubForm("QNAM"));
	}
    };

    // Common Functions
    CONT() {
	super();
	subRecords.setPrototype(CONTprototype);
    }

    @Override
    Record getNew() {
	return new CONT();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("CONT");
    }

    // Get/Set

    /**
     * @deprecated use getModelData()
     * @param path
     */
    public void setModel(String path) {
	subRecords.getModel().setFileName(path);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModel() {
	return subRecords.getModel().getFileName();
    }

    /**
     * @deprecated use getModelData()
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return subRecords.getModel().getAltTextures();
    }

    /**
     * 
     * @param itemReference
     * @param count
     * @return
     */
    public boolean addItem(FormID itemReference, int count) {
	return subRecords.getSubList("CNTO").add(new ItemListing(itemReference, count));
    }

    /**
     * 
     * @param item
     * @return
     */
    public boolean addItem(ItemListing item) {
	return subRecords.getSubList("CNTO").add(item);
    }

    /**
     * 
     * @param itemReference
     * @return
     */
    public boolean removeItem(FormID itemReference) {
	return subRecords.getSubList("CNTO").remove(new ItemListing(itemReference));
    }

    /**
     * 
     */
    public void clearItems() {
	subRecords.getSubList("CNTO").clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<ItemListing> getItems() {
	return subRecords.getSubList("CNTO").toPublic();
    }
    
    public ArrayList<FormID> getItemForms() {
	ArrayList<ItemListing> items = getItems();
	ArrayList<FormID> out = new ArrayList<>(items.size());
	for (ItemListing item : items) {
	    out.add(item.getForm());
	}
	return out;
    }

    /**
     *
     * @param target
     * @param replacement
     * @return
     */
    final public int replace(MajorRecord target, MajorRecord replacement) {
	int out = 0;
	FormID targetF = target.getForm();
	FormID replaceF = replacement.getForm();
	for (ItemListing item : getItems()) {
	    if (item.getForm().equals(targetF)) {
		out++;
		item.setForm(replaceF);
	    }
	}
	return out;
    }

    /**
     *
     * @param sound
     */
    public void setOpenSound(FormID sound) {
	subRecords.setSubForm("SNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getOpenSound() {
	return subRecords.getSubForm("SNAM").getForm();
    }

    /**
     *
     * @param sound
     */
    public void setCloseSound(FormID sound) {
	subRecords.setSubForm("QNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getCloseSound() {
	return subRecords.getSubForm("QNAM").getForm();
    }
    
    /**
     * 
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }
    
    /**
     * 
     * @return ScriptPackage of the CONT
     */
    public ScriptPackage getScriptPackage() {
        return subRecords.getScripts();
    }
}
