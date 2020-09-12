/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

/**
 *
 * @author Justin Swanson
 */
public class NotFound extends Exception {

    /**
     * 
     */
    public NotFound() {
    }

    /**
     * 
     * @param msg
     */
    public NotFound(String msg) {
        super(msg);
    }
}
