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
public class Model extends SubShell {

    static final SubPrototype modelPrototype = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(SubString.getNew("MODL", true));
	    add(new SubData("MODT"));
	    add(new SubData("MODB"));
	    add(new AltTextures("MODS"));
	    add(new SubData("MODD"));
	}
    };

    Model () {
	super(modelPrototype);
    }

    @Override
    SubRecord getNew(String type) {
	return new Model();
    }

    /**
     *
     * @param path
     */
    public void setFileName(String path) {
	subRecords.setSubString("MODL", path);
    }

    /**
     *
     * @return
     */
    public String getFileName() {
	return subRecords.getSubString("MODL").print();
    }

    /**
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return ((AltTextures) subRecords.get("MODS")).altTextures;
    }

    /**
     *
     * @param rhs Other MISC record.
     * @return true if:<br> Both sets are empty.<br> or <br> Each set contains
     * matching Alt Textures with the same name and TXST formID reference, in
     * the same corresponding indices.
     */
    public boolean equalAltTextures(Model rhs) {
	return AltTextures.equal(getAltTextures(), rhs.getAltTextures());
    }
}
