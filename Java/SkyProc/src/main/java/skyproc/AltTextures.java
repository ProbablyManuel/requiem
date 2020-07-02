/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class AltTextures extends SubRecordTyped {

    ArrayList<AltTexture> altTextures = new ArrayList<>();

    AltTextures(String t) {
	super(t);
    }

    @Override
    void export(ModExporter out) throws IOException {
	super.export(out);
	if (isValid()) {
	    out.write(altTextures.size());
	    for (AltTexture t : altTextures) {
		t.export(out);
	    }
	}
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, srcMod);
	int numTextures = in.extractInt(4);
	for (int i = 0; i < numTextures; i++) {
	    int strLength = Ln.arrayToInt(in.getInts(0, 4));
	    AltTexture newText = new AltTexture(new LShrinkArray(in.extract(12 + strLength)), srcMod);
	    altTextures.add(newText);
	    if (SPGlobal.logMods){
		logMod(srcMod, "", "New Texture Alt -- Name: " + newText.name + ", texture: " + newText.texture + ", index: " + newText.index);
	    }
	}
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>(altTextures.size());
	for (AltTexture t : altTextures) {
	    out.add(t.texture);
	}
	return out;
    }

    @Override
    SubRecord getNew(String type) {
	return new AltTextures(type);
    }

    @Override
    boolean isValid() {
	return !altTextures.isEmpty();
    }

    @Override
    int getContentLength(ModExporter out) {
	int len = 4;  // num Textures
	for (AltTexture t : altTextures) {
	    len += t.getTotalLength();
	}
	return len;
    }

    /**
     *
     * @param alts
     * @param rhsAlts
     * @return
     */
    public static boolean equal(ArrayList<AltTexture> alts, ArrayList<AltTexture> rhsAlts) {
	if (alts.size() != rhsAlts.size()) {
	    return false;
	}
	if (alts.isEmpty() && rhsAlts.isEmpty()) {
	    return true;
	}

	Set<AltTexture> altSet = new HashSet<>(alts);
	for (AltTexture t : rhsAlts) {
	    if (!altSet.contains(t)) {
		return false;
	    }
	}

	return true;
    }

    /**
     *
     */
    public static class AltTexture implements Serializable {

	String name;
	FormID texture = new FormID();
	int index;

	/**
	 * Creates a new AltTexture, which can be added to the ARMA to give it
	 * an alternate texture.
	 *
	 * @param name Name of the NiTriShape to apply this TXST to.
	 * @param txst FormID of the TXST to apply as the alt.
	 * @param index Index of the NiTriShape to apply this TXST to.
	 */
	public AltTexture(String name, FormID txst, int index) {
	    this.name = name;
	    this.texture = txst;
	    this.index = index;
	}

	AltTexture(LShrinkArray in, Mod srcMod) {
	    parseData(in, srcMod);
	}

	final void parseData(LShrinkArray in, Mod srcMod) {
	    int strLength = in.extractInt(4);
	    name = in.extractString(strLength);
	    texture.parseData(in, srcMod);
	    index = in.extractInt(4);
	}

	void export(ModExporter out) throws IOException {
	    out.write(name.length());
	    out.write(name);
	    texture.export(out);
	    out.write(index);
	}

	int getTotalLength() {
	    return name.length() + 12;
	}

	/**
	 *
	 * @param name String to set the AltTexture name to.
	 */
	public void setName(String name) {
	    this.name = name;
	}

	/**
	 *
	 * @return Name of the AltTexture.
	 */
	public String getName() {
	    return name;
	}

	/**
	 *
	 * @param txst FormID of the TXST to tie the AltTexture to.
	 */
	public void setTexture(FormID txst) {
	    texture = txst;
	}

	/**
	 *
	 * @return FormID of the TXST the AltTexture is tied to.
	 */
	public FormID getTexture() {
	    return texture;
	}

	/**
	 *
	 * @param index The NiTriShape index to assign.
	 */
	public void setIndex(int index) {
	    this.index = index;
	}

	/**
	 *
	 * @return The NiTriShape index assigned to the AltTexture.
	 */
	public int getIndex() {
	    return index;
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
	    final AltTexture other = (AltTexture) obj;
	    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
		return false;
	    }
	    if (this.index != other.index) {
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
	    int hash = 7;
	    hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
	    hash = 29 * hash + (this.texture != null ? this.texture.hashCode() : 0);
	    hash = 29 * hash + this.index;
	    return hash;
	}
    }
}
