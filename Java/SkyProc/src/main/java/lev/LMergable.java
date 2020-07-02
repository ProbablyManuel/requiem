/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

/**
 *
 * @author Justin Swanson
 */
public interface LMergable {
    /**
     * Function to handle merging in an element that is equal.
     * @param in
     */
    public void mergeIn(LMergable in);
}
