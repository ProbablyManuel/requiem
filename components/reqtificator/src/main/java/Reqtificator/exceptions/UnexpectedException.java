/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator.exceptions;

import Reqtificator.logging_and_gui.UnexpectedExceptionMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.MajorRecord;

/**
 * The UnexpectedException indicates that an exception occured which has not
 * been anticipated and is thus does not receive any error handling.
 *
 * @author Ogerboss
 */
public class UnexpectedException extends BaseException {
    private final static Logger logger = LogManager.getLogger();

    public MajorRecord failedRecord;

    /**
     * Constructs a new UnexpectedException.
     *
     * @param log          short version for logfile (English)
     * @param gui          the detailed message for the GUI (user's language)
     * @param failedRecord the record that triggered this exception
     * @param cause        the underlying exception that caused this one
     */
    public UnexpectedException(String log, String gui, MajorRecord failedRecord,
                               Throwable cause) {
        super(log, gui, cause);
        this.failedRecord = failedRecord;
        logger.error(new UnexpectedExceptionMessage(failedRecord, ThreadContext.getImmutableStack(), cause));
    }
}
