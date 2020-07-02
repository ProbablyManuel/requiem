/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package skyproc;

/**
 *
 * @author Ogerboss
 */
public interface SPExeceptionHandler {
    
    /**handle a minor error, from which the patcher can recover on its own,
     * program execution should continue afterwards, use this for example to log
     * errors to the log-files or display a warning popup.
     * @param error the thrown exception
     */
    public void handleException(Exception error);
    
    /**handle a critical error, it is expected that this function will call
     * SUMGUI.exitProgram after dealing with the error.
     * @param error the responsible exception
     */
    public void handleCriticalException(Exception error);
}
