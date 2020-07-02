/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

/**
 *
 * @author Justin Swanson
 */
public class BadParameter extends Exception {

    /**
     * 
     */
    public BadParameter() {
    }

    /**
     * 
     * @param msg
     */
    public BadParameter(String msg) {
        super(msg);
    }
}