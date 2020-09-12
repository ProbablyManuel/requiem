/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LImport;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A set of keywords associated with a major record.
 *
 * @author Justin Swanson
 */
public class KeywordSet extends SubRecord {

    private final static ArrayList<String> type = new ArrayList<>(Arrays.asList(new String[]{"KSIZ", "KWDA"}));
    SubData counter = new SubData("KSIZ", 0);
    SubFormArray keywords = new SubFormArray("KWDA", 0);

    KeywordSet() {
        super();
    }

    @Override
    SubRecord getNew(String type) {
        return new KeywordSet();
    }

    @Override
    boolean isValid() {
        return keywords.isValid();
    }

    @Override
    int getHeaderLength() {
        return 0;
    }

    @Override
    int getContentLength(ModExporter out) {
        return counter.getTotalLength(out)
                + keywords.getTotalLength(out);
    }

    @Override
    void export(ModExporter out) throws IOException {
        if (isValid()) {
            counter.setData(keywords.size(), 4);
            counter.export(out);
            keywords.export(out);
        }
    }

    @Override
    void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
        switch (getNextType(in)) {
            case "KSIZ":
                counter.parseData(in, srcMod);
//                keywords = new SubFormArray("KWDA", counter.toInt());
                break;
            case "KWDA":
                keywords.parseData(in, srcMod);
                break;
        }
    }

    @Override
    ArrayList<FormID> allFormIDs() {
        return keywords.IDs;
    }

    /**
     * Test if the keyword set contains the keyword with the given FormID
     *
     * @param keyword FormID of the keyword to check
     * @return whether this set contains the given keyword FormID
     */
    public boolean containsKeyword(FormID keyword) {
        return keywords.contains(keyword);
    }

    /**
     * Returns a COPY of the list of FormIDs associated with this keyword set.
     *
     * @return
     */
    public ArrayList<FormID> getKeywordRefs() {
        return new ArrayList<>(keywords.IDs);
    }

    /**
     * Adds a keyword to the list if it is not already in the list
     *
     * @param keywordRef A KYWD formID
     */
    public void addKeywordRef(FormID keywordRef) {
        if (!keywords.contains(keywordRef)) {
            keywords.add(keywordRef);
            //counter.modValue(1);
        }
    }

    /**
     * Removes a keyword to the list
     *
     * @param keywordRef A KYWD formID
     */
    public void removeKeywordRef(FormID keywordRef) {
        if (keywords.remove(keywordRef)) {
            //counter.modValue(-1);
        }
    }

    /**
     *
     */
    public void clearKeywordRefs() {
        keywords.clear();
        counter.setData(0, 4);
    }

    /**
     *
     * @param set
     * @return True if every keyword in this set is contained in the parameter's
     * set.
     */
    public boolean containedIn(KeywordSet set) {
        return keywords.containedIn(set.keywords);
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
        final KeywordSet other = (KeywordSet) obj;
        if (this.keywords != other.keywords && (this.keywords == null || !this.keywords.equals(other.keywords))) {
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
        int hash = 5;
        hash = 89 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        return hash;
    }

    @Override
    ArrayList<String> getTypes() {
        return type;
    }

}
