/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.interfaces;

import skyproc.KeywordSet;

/**
 *
 * @author ogerboss
 */
public interface KeywordRecord {

    /**
     * Get the keywords assigned to this record.
     *
     * @return a KeywordSet object to query and manipulate
     */
    abstract public KeywordSet getKeywordSet();

}
