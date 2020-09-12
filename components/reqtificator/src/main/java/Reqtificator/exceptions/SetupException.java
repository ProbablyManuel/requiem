/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.exceptions;

/**The SetupException indicates a problem with the environment of the patcher.
 * This exception type is raised whenever an external operation failed. In most
 * cases these are related to I/O operations, file permissions, installation
 * mistakes and operations that are access-restricted by the operating system,
 * like the creation of symbolic links.
 *
 * @author Ogerboss
 */
public class SetupException extends BaseException {

    /**Constructs a new SetupException.
     *
     * @param log short version for logfile (English)
     * @param gui the detailed message for the GUI (user's language)
     */
    public SetupException(String log, String gui) {
        super(log, gui);
    }

    /**Constructs a new SetupException.
     *
     * @param log short version for logfile (English)
     * @param gui the detailed message for the GUI (user's language)
     * @param cause the underlying exception that caused this one
     */
    public SetupException(String log, String gui, Throwable cause) {
        super(log, gui, cause);
    }
}
