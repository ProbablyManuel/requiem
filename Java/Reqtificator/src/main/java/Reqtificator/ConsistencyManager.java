/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator;

import Reqtificator.exceptions.SetupException;
import Reqtificator.exceptions.SilentException;
import Reqtificator.logging_and_gui.TextManager;
import skyproc.ModListing;
import skyproc.SPDatabase;
import skyproc.SPGlobal;
import skyrim.requiem.fptools.Option;
import skyrim.requiem.gui.PopupTools;
import skyrim.requiem.gui.PopupTools.Companion.PopupType;
import skyrim.requiem.localization.Translatable;
import skyrim.requiem.localization.TextReference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 * A management class for assigning FormIDs in a consistent way.
 * This helper
 * class takes care of managing SkyProc's internal consistency files and tries
 * to prevent the user from accidentally deleting mandatory auxiliary files to
 * continue existing save games when updating Requiem.
 *
 * @author Ogerboss
 */
public class ConsistencyManager {

    private TextManager texts;
    private final PopupTools popupManager;
    private final File backuppath;
    private final String consistencyFileName;
    private final String MetaFileName = "ConsistencyMetaData.txt";

    private enum UserReaction implements Translatable {
        Cancel {
            @Override
            public TextReference getText() {
                return new TextReference("gui.consistency.cancel");
            }
        },
        FreshInstall {
            @Override
            public TextReference getText() {
                return new TextReference("gui.consistency.fresh");
            }
        };
    }

    /**
     * Create a new ConsistencyManager.
     * <p>
     * In addition to loading the required strings, this constructor will also
     * create the backup directory in My Documents/Skyrim if it doesn't exist
     * yet.
     *
     * @throws SetupException if the My Documents/Skyrim folder was not readable
     */
    public ConsistencyManager(TextManager texts, PopupTools popupManager, String consistencyFileName)
            throws SetupException {
        this.texts = texts;
        this.popupManager = popupManager;
        this.consistencyFileName = consistencyFileName;
        try {
            backuppath = new File(SPGlobal.getMyDocumentsSkyrimFolder(),
                    "Requiem/");
            if (!backuppath.exists()) {
                Files.createDirectory(backuppath.toPath());
            }
        } catch (IOException e) {
            String message = texts.format(
                    "patch.consistency.my_documents_not_found",
                    "My Documents/Skyrim", e.toString());
            throw new SetupException("My Documents not found", message, e);
        }
    }

    /**
     * Find the Consistency file to use and warn user if it is missing.
     * <p>
     * If the default location doesn't contain any usable consistency file, the
     * user is informed about the situation and whether a backup has been found.
     * The user can then either abort the process to recover the backup or start
     * from scratch to patch for a new game.
     *
     * @throws SilentException if default location provides no match and the
     *                         user decides to abort the patch generation
     */
    public void checkConsistencyFile() throws SilentException {
        File standard = new File(".", consistencyFileName);
        File backup = new File(backuppath, consistencyFileName);

        if (!standard.exists()) {
            TextReference message;
            UserReaction defaultChoice;
            if (backup.exists()) {
                message = new TextReference("patch.consistency.backup_found", standard.getAbsolutePath(),
                        backup.getAbsolutePath());
                defaultChoice = UserReaction.Cancel;
            } else {
                message = new TextReference("patch.consistency.no_backup", consistencyFileName);
                defaultChoice = UserReaction.FreshInstall;
            }
            Option<UserReaction> choice = popupManager.showPopupQuestion(
                    new TextReference("patch.consistency.backup_title"),
                    message,
                    UserReaction.values(),
                    defaultChoice,
                    PopupType.Warning);
            if (choice.getOrElse(() -> UserReaction.Cancel).equals(UserReaction.Cancel)) {
                throw new SilentException("aborted to recover backup consistency file");
            }
        }
    }


    /**
     * Write consistency file meta data and update external backups.
     *
     * @throws SetupException if any file operation fails
     */
    public void backupConsistencyData() throws SetupException {
        File tempMeta = new File(MessageFormat.format("{0}_tmp", MetaFileName));
        File fileMeta = new File(MetaFileName);

        writeTempMetaFile(tempMeta);
        try {
            Files.move(tempMeta.toPath(), fileMeta.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            String message = texts.format("patch.consistency.not_writable",
                    texts.format("urls.security_settings", true),
                    fileMeta.getAbsolutePath());
            throw new SetupException("cannot write consistency file", message,
                    e);
        }
        updateBackups();
    }

    private void writeTempMetaFile(File temppath)
            throws SetupException {
        try (FileWriter fstream = new FileWriter(temppath);
             BufferedWriter writer = new BufferedWriter(fstream)) {
            String end = System.getProperty("line.separator");
            writer.write("This file contains the meta data for the " +
                    "consistency file used by the Reqtificator, " +
                    "Requiem's SkyProc Patcher." + end);
            writer.write("It is intended to help you to recover the correct " +
                    "backup file if you accidentally deleted the " +
                    "original version." + end);
            writer.write(MessageFormat.format("Creation Date: " +
                            "{0,date,full} {0,time,full}{1}",
                    Calendar.getInstance().getTime(), end));
            writer.write("Imported Load Order this Consistency File was " +
                    "based on:" + end);
            for (ModListing mod : SPDatabase.getImportedModListings()) {
                writer.write(MessageFormat.format(
                        "ModIndex: {0,number,###} - {1}{2}",
                        SPDatabase.modIndex(mod), mod, end));
            }
        } catch (IOException e) {
            String message = texts.format("patch.consistency.not_writable",
                    texts.format("urls.security_settings", true),
                    temppath.getAbsolutePath());
            throw new SetupException("consistency metafile not writable",
                    message, e);
        }
    }

    private void updateBackups()
            throws SetupException {
        File metaFile = new File(backuppath, MetaFileName + "_5");
        File consistencyFile = new File(backuppath, consistencyFileName + "_5");

        File copySource = new File(".");
        File copyTarget = new File(".");
        try {
            for (int i = 4; i >= 2; i--) {
                copyTarget = metaFile;
                copySource = new File(backuppath,
                        MetaFileName + "_" + i);
                if (copySource.exists()) {
                    Files.move(copySource.toPath(), copyTarget.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                metaFile = copySource;

                copyTarget = consistencyFile;
                copySource = new File(backuppath,
                        consistencyFileName + "_" + i);
                if (copySource.exists()) {
                    Files.move(copySource.toPath(), copyTarget.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                consistencyFile = copySource;
            }

            copyTarget = metaFile;
            copySource = new File(backuppath, MetaFileName);
            if (copySource.exists()) {
                Files.move(copySource.toPath(), copyTarget.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            metaFile = copySource;

            copyTarget = consistencyFile;
            copySource = new File(backuppath, consistencyFileName);
            if (copySource.exists()) {
                Files.move(copySource.toPath(), copyTarget.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            consistencyFile = copySource;

            copyTarget = consistencyFile;
            copySource = new File(consistencyFileName);
            Files.copy(copySource.toPath(), copyTarget.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            copyTarget = metaFile;
            copySource = new File(MetaFileName);
            Files.copy(copySource.toPath(), copyTarget.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            String message = texts.format("patch.consistency.copy_failure",
                    texts.format("urls.security_settings", true),
                    copySource.getAbsolutePath(), copyTarget.getAbsolutePath());
            throw new SetupException("backup file rotation failed", message, e);
        }
    }

}