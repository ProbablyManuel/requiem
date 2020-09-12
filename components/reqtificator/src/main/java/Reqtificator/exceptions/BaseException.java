/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.exceptions;

/**Abstract base class for all Reqtificator-specific exceptions.
 *
 * @author Ogerboss
 */
abstract class BaseException extends Exception {

    /**The lengthy reason, explaining the details of the issue to the user.
     * This string will be in the user's chosen language if a translation
     * is available.
     */
    public final String gui;
    /**Short reason, for compact error message representations in the log.
     * Will always be in English.
     */
    public final String log;

    /**Constructs a new BaseException.
     *
     * @param log short version for logfile (English)
     * @param gui the detailed message for the GUI (user's language)
     */
    BaseException(String log, String gui) {
        super(log);
        this.log = log;
        this.gui = gui;
    }

    /**Constructs a new BaseException with a cause.
     *
     * @param log short version for logfile (English)
     * @param gui the detailed message for the GUI (user's language)
     * @param cause the underlying exception that caused this one
     */
    BaseException(String log, String gui, Throwable cause) {
        super(log, cause);
        this.log = log;
        this.gui = gui;
    }
}
