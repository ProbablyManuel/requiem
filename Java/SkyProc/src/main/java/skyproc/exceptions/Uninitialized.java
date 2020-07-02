/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

/**
 *
 * @author Justin Swanson
 */
public class Uninitialized extends Exception {

    /**
     * 
     */
    public Uninitialized() {
    }

    /**
     * 
     * @param msg
     */
    public Uninitialized(String msg) {
        super(msg);
    }

}
