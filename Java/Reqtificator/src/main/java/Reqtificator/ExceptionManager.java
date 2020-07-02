/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator;

import Reqtificator.components.Component;
import Reqtificator.exceptions.PatchingException;
import Reqtificator.exceptions.SetupException;
import Reqtificator.exceptions.SilentException;
import Reqtificator.exceptions.UnexpectedException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import skyproc.ModListing;
import skyproc.SPDefaultExceptionHandler;
import skyproc.exceptions.BadMod;
import skyproc.exceptions.MissingMaster;
import skyproc.gui.SUMGUI;
import skyrim.requiem.exceptions.LoadOrderIssueDetectedException;
import skyrim.requiem.exceptions.ReqtificatorException;
import skyrim.requiem.exceptions.SetupRequirementFailedException;
import skyrim.requiem.gui.PopupTools;
import skyrim.requiem.gui.PopupTools.Companion.PopupType;
import skyrim.requiem.localization.TextFormatter;

/**
 * @author Ogerboss
 */
public class ExceptionManager extends SPDefaultExceptionHandler {

    private final static Logger logger = LogManager.getLogger();

    private final TextFormatter formatter;
    private final TextManager texts;
    private final PopupTools popupTools;

    /**
     * Create a new ExceptionManager.
     *
     * @param texts the TextManager instance to load GUI text from
     */
    public ExceptionManager(TextManager texts, TextFormatter formatter, PopupTools popupTools) {
        this.texts = texts;
        this.formatter = formatter;
        this.popupTools = popupTools;
    }

    @Override
    public void handleCriticalException(Exception error) {
        String title = texts.format("error_handling.popup_title");
        if (!(error instanceof UnexpectedException || error instanceof SilentException
                || error instanceof LoadOrderIssueDetectedException)) {
            logger.error("critical exception encountered", error);
        }
        if (error instanceof SilentException) {
            SilentException convert = (SilentException) error;
            logger.info("user canceled patch: " + convert.log);
        } else if (error instanceof LoadOrderIssueDetectedException) {
            LoadOrderIssueDetectedException convert = (LoadOrderIssueDetectedException) error;
            logger.info("user canceled patch: " + formatter.format(convert.getMessageTemplate()));
        } else if (error instanceof SetupException) {
            popupTools.showPopupMessage(title, ((SetupException) error).gui, PopupType.Error);
        } else if (error instanceof SetupRequirementFailedException) {
            SetupRequirementFailedException convert = (SetupRequirementFailedException) error;
            popupTools.showPopupMessage(title, formatter.format(convert.getMessageTemplate()), PopupType.Error);
        } else if (error instanceof ReqtificatorException) {
            ReqtificatorException convert = (ReqtificatorException) error;
            popupTools.showPopupMessage(title, formatter.format(convert.getMessageTemplate()), PopupType.Error);
        } else if (error instanceof PatchingException) {
            popupTools.showPopupMessage(title, ((PatchingException) error).gui, PopupType.Error);
        } else if (error instanceof UnexpectedException) {
            UnexpectedException convert = (UnexpectedException) error;
            String message = texts.format("error_handling.unexpected_error",
                    Component.formatRecordPretty(convert.failedRecord),
                    convert.getCause().toString(),
                    texts.format("urls.service_desk", true));
            popupTools.showPopupMessage(title, message, PopupType.Error);
        } else if (error instanceof MissingMaster) {
            MissingMaster convert = (MissingMaster) error;
            StringBuilder sB = new StringBuilder();
            for (ModListing master : convert.notinstalled) {
                sB.append(texts.format("error_handling.masters_missing",
                        master.print()));
            }
            for (ModListing master : convert.inactive) {
                sB.append(texts.format("error_handling.masters_inactive",
                        master.print()));
            }
            for (ModListing master : convert.loadedafter) {
                sB.append(texts.format("error_handling.masters_loadafter",
                        master.print()));
            }
            String message = texts.format("error_handling.masters_error",
                    convert.failedmod.print(), sB.toString());
            popupTools.showPopupMessage(title, message, PopupType.Error);
        } else if (error instanceof BadMod) {
            BadMod convert = (BadMod) error;
            String message = texts.format("error_handling.corrupt_mod",
                    convert.failedmod.print());
            popupTools.showPopupMessage(title, message, PopupType.Error);
        } else {
            String message = texts.format("error_handling.general_error",
                    texts.format("urls.service_desk", true),
                    error.toString());
            popupTools.showPopupMessage(title, message, PopupType.Error);
        }
        SUMGUI.exitProgram(false, true);
    }
}
