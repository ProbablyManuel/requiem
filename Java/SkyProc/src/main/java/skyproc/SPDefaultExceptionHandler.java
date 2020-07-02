/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package skyproc;

import javax.swing.JOptionPane;
import skyproc.exceptions.MissingMaster;
import skyproc.gui.SPPopups;
import skyproc.gui.SUMGUI;

/**
 *
 * @author Ogerboss
 */
public class SPDefaultExceptionHandler implements SPExeceptionHandler {

    private SPPopups popupHandler;

    public SPDefaultExceptionHandler() {
        this.popupHandler = new SPPopups();
    }
    
    @Override
    public void handleException(Exception error) {
        System.err.print(error);
        if (SPGlobal.logging()) {
            SPGlobal.logException(error);
        }
    }
    
    @Override
    public void handleCriticalException(Exception error) {
        System.err.print(error);
        if (SPGlobal.logging()) {
            SPGlobal.logException(error);
            SPGlobal.closeDebug();
        }
        if (error instanceof MissingMaster) {
            popupHandler.popup("One of your mods has missing masters!",
                    error.getMessage(), JOptionPane.ERROR_MESSAGE, 0);
        } else {
            popupHandler.popup("A fatal error has occured!", error.getMessage(), JOptionPane.ERROR_MESSAGE,
                    400);
        }
        SUMGUI.exitProgram(false, true);
    }
}
