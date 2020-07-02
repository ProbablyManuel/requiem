/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Reqtificator;

import Reqtificator.enums.ConfigSections;
import Reqtificator.exceptions.PatchingException;
import Reqtificator.exceptions.SetupException;
import Reqtificator.logging_and_gui.TextManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import skyproc.Mod;
import skyproc.ModListing;
import skyproc.SPDatabase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**This class extracts Reqtificator-specific data from the setup.
 * This class does two things when created: First, it parses the
 * Reqtificator.ini to identify the mods that qualify as visual templates.
 * Afterwards it goes through the imported plugins and checks all Requiem-
 * dependent plugins for REQ-Tags. The results are then stored in public fields.
 *
 * @author Ogerboss
 */
public class OldReqtificatorConfiguration {

    private final static Logger logger = LogManager.getLogger();
    private TextManager texts;

    /**Mods registered for Tempered Lists.
     */
    public final Set<ModListing> modsWithTemperedItems;
    /**Mods registered for Compact Leveled Lists.
     */
    public final Set<ModListing> modsWithCompactLists;
    /** Mods registered for ActorVariations.
     */
    public final Set<ModListing> modsWithActorVariations;

    /**The settings from the parsed configuration file.
     * This map contains the known sections of the config-file and maps them to
     * the set of mods the user has specified for this category.
     */
    public final Map<ConfigSections, Set<ModListing>> settings;

    /**
     * Creates a new ReqtificatorConfiguration instance. During setup, it will parse the
     * given file for mods that qualify as visual templates for automated visual
     * merging. Furthermore it will process the list of imported mods for
     * REQ-Tags in Requiem-patches to identify patches that qualify for special
     * treatment.
     *
     * @param configfile path to the file used to set up visual templates
     * @throws PatchingException if the Reqtificator.ini contains inconsistent
     * data or a mod has wrong REQ-Tags
     * @throws SetupException if the Reqtificator.ini cannot be read
     */
    public OldReqtificatorConfiguration(String configfile, TextManager texts)
            throws PatchingException, SetupException {
        this.texts = texts;
        modsWithActorVariations = new HashSet<>();
        modsWithTemperedItems = new HashSet<>();
        modsWithCompactLists = new HashSet<>();
        settings = new HashMap<>();
        settings.put(ConfigSections.NPCVisuals, new HashSet<>());
        settings.put(ConfigSections.RaceVisuals, new HashSet<>());
        readConfig(configfile);
        extractPatchTags();
    }

    /**Read the given configuration file.
     * This function will parse the given configuration file and store the
     * parsed user-input in the settings field.
     *
     * @param configfile the file to parse
     * @throws PatchingException if the file contains inconsistent data
     * @throws SetupException if the file is missing or could not be read from
     */
    private void readConfig(String configfile) throws PatchingException,
            SetupException {
        ArrayList<ModListing> imported = SPDatabase.getImportedModListings();
        try {
            FileReader fstream = new FileReader(configfile);
            BufferedReader reader = new BufferedReader(fstream);
            String line = reader.readLine();
            ConfigSections category = null;
            ThreadContext.put("context", "Configuration");
            logger.info("begin dump of Reqtificator.ini");
            while (line != null) {
                logger.info("|" + line);
                line = line.trim();
                if (line.length() == 0 || line.charAt(0) == '#') {
                    line = reader.readLine();
                    continue;
                } else if (line.charAt(0) == '[') {
                    try {
                        category = ConfigSections.valueOf(line.substring(1,
                                line.length() - 1));
                    } catch (EnumConstantNotPresentException |
                            IllegalArgumentException err) {
                        String message = texts.format(
                                "patch.configuration.unknown_category", line);
                        throw new PatchingException("unknown section", message,
                                err);
                    }
                } else {
                    ModListing overwrite = new ModListing(line);
                    if (!imported.contains(overwrite)) {
                        String message = texts.format(
                                "patch.configuration.mod_missing",
                                overwrite.print());
                        throw new PatchingException("missing mod", message);
                    }
                    settings.get(category).add(overwrite);
                }
                line = reader.readLine();
            }
            logger.info("finished dump of Reqtificator.ini");
            for (ModListing entry: settings.get(ConfigSections.NPCVisuals)) {
                logger.info("'" + entry.print() + "' registered as NPC visual template provide");
            }
            for (ModListing entry: settings.get(ConfigSections.RaceVisuals)) {
                logger.info("'" + entry.print() + "' registered as race visual template provide");
            }
            ThreadContext.remove("context");
        } catch (FileNotFoundException e) {
            throw new SetupException("Reqtificator.ini not found",
                    texts.format("patch.configuration.ini_not_found"), e);
        } catch (IOException e) {
            throw new SetupException("Reqtificator.ini not readable",
                    texts.format("patch.configuration.ini_io_error"), e);
        }
    }

    /**Extract the REQ-Tags from Requiem-dependent mods in the load order.
     * Iterate through the load order and identify Requiem-dependent mods that
     * specify REQ-Tags. The extracted tags are stored in the public fields
     * modsWithTemperedItems, modsWithCompactLists and modsWithActorVariations of the instance.
     *
     * @throws PatchingException if any of the mods specified a REQ-Tag pattern
     * that could not be parsed.
     */
    private void extractPatchTags() throws PatchingException {
        Pattern reqtag = Pattern.compile("<<(REQ:\"[^>\"]+\" ?;)?([^>]+)>>",
                Pattern.CASE_INSENSITIVE);
        modsWithActorVariations.add(FormIDStash.REQ);
        modsWithTemperedItems.add(FormIDStash.REQ);
        modsWithCompactLists.add(FormIDStash.REQ);
        for (Mod imported : SPDatabase.getImportedMods()) {
            if (imported.getMasters().contains(FormIDStash.REQ)) {
                Matcher match = reqtag.matcher(imported.getDescription());
                if (!match.find()) {
                    continue;
                }
                if (match.group(1) != null) {
                    logger.warn("DEPRECATED FEATURE USED: '" + imported.getName() +
                            "' specifies a REQ-TAG prefix. This is " +
                            "no longer used and will be removed in a future release.");
                }
                for (String command: match.group(2).split(";")) {
                    command = command.trim();
                    if (command.equalsIgnoreCase("REQ:MUTATE")) {
                        modsWithActorVariations.add(imported.getInfo());
                        logger.info("'" + imported.getName() + "' registered for Actor Variations");
                    } else if (command.equalsIgnoreCase("REQ:UNROLL")) {
                        modsWithCompactLists.add(imported.getInfo());
                        logger.info("'" + imported.getName()
                                + "' registered for Compact Leveled Lists");
                    } else if (command.equalsIgnoreCase("REQ:TEMPER")) {
                        modsWithTemperedItems.add(imported.getInfo());
                        logger.info("'" + imported.getName()
                                + "' registered for Tempered Item Lists");
                    } else {
                        String message = texts.format(
                                "patch.configuration.invalid_tags", imported.getName(),
                                command, texts.format("urls.req_tags", true));
                        throw new PatchingException("invalid tags", message);
                    }
                }
            }
        }
    }
}
