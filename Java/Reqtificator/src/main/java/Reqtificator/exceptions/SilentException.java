/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.exceptions;

/**This exception indicates a user-requested termination.
 * This exception should be raised if an abnormal situation was discovered and
 * the user decided against using the automated default fix, thus terminating
 * the patch process. In contrast to other subclassses of the BaseException, it
 * will not display a popup-window.
 *
 * @author Ogerboss
 */
public class SilentException extends BaseException{

    /**Constructs a new SilentException.
     *
     * @param log short version for logfile (English)
     */
    public SilentException(String log) {
        super(log, "<NOT GIVEN>");
    }

    /**Constructs a new SilentException.
     *
     * @param log short version for logfile (English)
     * @param cause the underlying exception that caused this one
     */
    public SilentException(String log, Throwable cause) {
        super(log, "<NOT GIVEN>", cause);
    }
}
