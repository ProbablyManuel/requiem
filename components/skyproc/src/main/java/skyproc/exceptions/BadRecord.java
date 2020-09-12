/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

/**
 *
 * @author Justin Swanson
 */
public class BadRecord extends Exception {

    /**
     * 
     */
    public BadRecord() {
    }

    /**
     * 
     * @param msg
     */
    public BadRecord(String msg) {
        super(msg);
    }
}
