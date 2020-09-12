/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.exceptions;

import skyproc.ModListing;

/**
 *
 * @author Justin Swanson
 */
public class BadMod extends Exception {

    public final ModListing failedmod;

    /**
     * Create a new BadMod exception to indicate that importing the specified
     * mod has failed.
     * @param msg a message about the error
     * @param cause the underlying exception that triggered the error, if any
     * @param failedmod the name of the mod
     */
    public BadMod(String msg, Throwable cause, ModListing failedmod) {
        super(msg, cause);
        this.failedmod = failedmod;
    }
}