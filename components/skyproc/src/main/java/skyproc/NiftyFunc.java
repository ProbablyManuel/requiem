/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lev.LInChannel;
import lev.Ln;
import skyproc.gui.SUMGUI;

/**
 * A class to hold many common/useful functions.
 *
 * @author Justin Swanson
 */
public class NiftyFunc {

    static String[] validationSkip = {"DIAL"};
    static String recordLengths = "Record Lengths";
    static Map<FormID, MajorRecord> deepSubrecordCopyDB = new HashMap<>();

    /**
     * A common way to attach scripts to NPCs that normally cannot have scripts
     * attached<br> (Any NPC that is referenced by a LVLN)<br> is to give a
     * racial spell to them that has a magic effect that has the desired
     * script.<br><br> This function streamlines the process and gives you a
     * SPEL/MGEF setup that will attach the desired script.<br> Simply give this
     * SPEL to the NPC's race.<br><br> NOTE: Attaching a script attachment spell
     * to an NPCs race will affect ALL NPCs with that same race.<br> If you do
     * not want this, then consider using genSafeScriptAttachingRace().
     *
     * @param script Script to have the SPEL attach
     * @param uniqueID A unique string to differentiate the records from any
     * other SkyProc user's setups.<br> (using your mod's name is usually
     * sufficient)
     * @return The generated SPEL record that can be attached to any RACE to
     * have it attach the desired script.
     */
    public static SPEL genScriptAttachingSpel(ScriptRef script, String uniqueID) {
        String name = "SP_" + uniqueID + "_" + script.name.data + "_attacher";
        MGEF mgef = new MGEF(name + "_MGEF", name + "_MGEF");
        mgef.getScriptPackage().addScript(script);
        mgef.set(MGEF.SpellEffectFlag.HideInUI, true);
        SPEL spel = new SPEL(name + "_SPEL");
        spel.setSpellType(SPEL.SPELType.Ability);
        spel.addMagicEffect(mgef);
        return spel;
    }

    /**
     * A common way to attach scripts to NPCs that normally cannot have scripts
     * attached<br> (Any NPC that is referenced by a LVLN)<br> is to give a
     * racial spell to them that has a magic effect that has the desired
     * script.<br><br> This function streamlines the process and gives you a
     * duplicate race which will attach the desired script.<br> You can then set
     * it to be the target NPCs race. Since it is a duplicate, it will only
     * affect NPCs you explicitly attach it to, and not ALL NPCs that shared the
     * original race. <br> It is a full duplicate and will retain any settings
     * of the original race.
     *
     * @param script Script to have the SPEL attach
     * @param uniqueID A unique string to differentiate the records from any
     * other SkyProc user's setups.<br> (using your mod's name is usually
     * sufficient)
     * @param raceToDup Original race that you wish to duplicate.
     * @return A duplicate of the input race, with the only difference being it
     * has a script attachment racial spell.
     */
    public static RACE genSafeScriptAttachingRace(ScriptRef script, RACE raceToDup, String uniqueID) {
        SPEL attachmentSpel = genScriptAttachingSpel(script, uniqueID);
        RACE attachmentRace = (RACE) SPGlobal.getGlobalPatch().makeCopy(raceToDup);
        attachmentRace.addSpell(attachmentSpel.getForm());
        return attachmentRace;
    }

    /**
     * Checks the given template flag "chains" to see if the NPC is templated to
     * a Leveled List at any point. If it is, that Leveled List is returned;
     * Null if not.
     *
     * @param npc NPC formID to investigate.
     * @param templateFlagsToCheck Template flags to consider.
     * @return LVLN that it is templated to, or null.
     */
    public static LVLN isTemplatedToLList(FormID npc, NPC_.TemplateFlag... templateFlagsToCheck) {
        return isTemplatedToLList(npc, templateFlagsToCheck, 0);
    }

    /**
     * Checks the given template flag "chains" to see if the NPC is templated to
     * a Leveled List at any point. If it is, that Leveled List is returned;
     * Null if not.
     *
     * @param npc NPC to investigate.
     * @param templateFlagsToCheck Template flags to consider.
     * @return LVLN that it is templated to, or null.
     */
    public static LVLN isTemplatedToLList(NPC_ npc, NPC_.TemplateFlag... templateFlagsToCheck) {
        return isTemplatedToLList(npc.getForm(), templateFlagsToCheck);
    }

    static LVLN isTemplatedToLList(FormID npc, NPC_.TemplateFlag[] templateFlagsToCheck, int depth) {
        if (depth > 100) {
            return null; // avoid circular template overflows
        }

        if (templateFlagsToCheck.length == 0) {
            templateFlagsToCheck = NPC_.TemplateFlag.values();
        }

        NPC_ npcSrc = (NPC_) SPDatabase.getMajor(npc, GRUP_TYPE.NPC_);

        if (npcSrc != null && !npcSrc.getTemplate().equals(FormID.NULL)) {
            boolean hasTargetTemplate = false;
            for (NPC_.TemplateFlag flag : templateFlagsToCheck) {
                if (npcSrc.get(flag)) {
                    hasTargetTemplate = true;
                    break;
                }
            }
            if (!hasTargetTemplate) {
                return null;
            }

            NPC_ templateN = (NPC_) SPDatabase.getMajor(npcSrc.getTemplate(), GRUP_TYPE.NPC_);
            if (templateN != null) { // If template is an NPC, recursively chain the check
                return isTemplatedToLList(templateN.getForm(), templateFlagsToCheck, depth + 1);
            } else {
                return (LVLN) SPDatabase.getMajor(npcSrc.getTemplate(), GRUP_TYPE.LVLN);
            }
        }
        return null;
    }

    /**
     * Makes a new quest that starts immediately in-game, that has this script
     * attached to it.
     *
     * @param script The script to add to the quest.
     * @return
     */
    public static QUST makeScriptQuest(ScriptRef script) {
        QUST quest = new QUST(script.getName() + "_qust");
        quest.getScriptPackage().addScript(script);
        quest.setName(script.getName() + " Quest");
        return quest;
    }

    /**
     * A function that starts a new java program with more memory. Use this to
     * allocate more memory for your SkyProc program by simply putting the name
     * of your jar file as the jarpath. This function will automatically open a
     * second instance of your program, giving it more memory, and close the
     * first program if the second is opened properly.
     *
     * @param startingMem Memory to start the new program with.
     * @param maxMem Max amount of memory to allow the new program to use.
     * @param jarPath Path to the jar file to open. Usually, just put the name
     * of your jar.
     * @param args Any special main function args you want to give to the second
     * program.
     * @throws IOException
     * @throws InterruptedException
     */
    public static void allocateMoreMemory(String startingMem, String maxMem, String jarPath, String... args) throws IOException, InterruptedException {
        String[] argsInternal = new String[args.length + 5];
        argsInternal[0] = "java";
        argsInternal[1] = "-jar";
        argsInternal[2] = "-Xms" + startingMem;
        argsInternal[3] = "-Xmx" + maxMem;
        argsInternal[4] = jarPath;
        for (int i = 5; i < args.length + 5; i++) {
            argsInternal[i] = args[i - 5];
        }
        ProcessBuilder proc = new ProcessBuilder(argsInternal);
        Process start = proc.start();
        InputStream shellIn = start.getInputStream();
        int exitStatus = start.waitFor();
        String response = Ln.convertStreamToStr(shellIn);
        if (exitStatus != 0) {
            JOptionPane.showMessageDialog(null, "Error allocating " + maxMem + " of memory:\n"
                    + response
                    + "\nMemory defaulted to lowest levels.  Please lower your\n"
                    + "allocated memory in Other Settings and start the program again.");
        } else {
            System.exit(0);
        }
    }

    /**
     * Replaces all "naughty" characters with "" or "_"
     *
     * @param origEDID
     * @return
     */
    public static String EDIDtrimmer(String origEDID) {
        origEDID = origEDID.replaceAll(" ", "");
        origEDID = origEDID.replaceAll(":", "_");
        origEDID = origEDID.replaceAll("-", "_");
        return origEDID;
    }

    /**
     * Converts a string version to a unique number<br> Only supports
     * XX.XX.XX.XX with numbers for X
     *
     * @param version
     * @return
     */
    public static int versionToNum(String version) {
        String tmp = "";
        for (int i = 0; i < version.length(); i++) {
            if (Character.isDigit(version.charAt(i))
                    || version.charAt(i) == '.') {
                tmp += version.charAt(i);
            } else {
                break;
            }
        }
        version = tmp;
        String[] split = version.split("\\.");
        int out = 0;
        for (int i = 0; i < split.length && i < 4; i++) {
            int next = Integer.valueOf(split[i]) * 1000000;
            if (i != 0) {
                next /= Math.pow(100, i);
            }
            out += next;
        }
        return out;
    }

    /**
     * Reads in the file and confirms that all GRUPs and Major Records have
     * correct lengths. It does not explicitly check subrecord lengths, but due
     * to the recursive nature of SkyProc, these will be implicitly checked as
     * well by confirming Major Record length.
     *
     * This will bug out if strings inside records contain major record types
     * (ex. "SomeEDIDforMGEF", would fail because of "MGEF")
     *
     * @param testFile File to test.
     * @param numErrorsToPrint Number of error messages to print before
     * stopping.
     * @return True if the file had correct record lengths.
     */
    public static boolean validateRecordLengths(File testFile, int numErrorsToPrint) {
        return validateRecordLengths(testFile.getPath(), numErrorsToPrint);
    }

    /**
     * Reads in the file and confirms that all GRUPs and Major Records have
     * correct lengths. It does not explicitly check subrecord lengths, but due
     * to the recursive nature of SkyProc, these will be implicitly checked as
     * well by confirming Major Record length.
     *
     * This will bug out if strings inside records contain major record types
     * (ex. "SomeEDIDforMGEF", would fail because of "MGEF")
     *
     * @param testFilePath Path to the file to test.
     * @param numErrorsToPrint Number of error messages to print before
     * stopping.
     * @return True if the file had correct record lengths.
     */
    public static boolean validateRecordLengths(String testFilePath, int numErrorsToPrint) {
        boolean correct = true;
        int numErrors = 0;
        ArrayList<String> skip = new ArrayList<>(Arrays.asList(validationSkip));
        try {
            File file = new File(testFilePath);
            if (file.isFile()) {
                SPGlobal.log("Validate", "Target file exists: " + file);
            } else {
                SPGlobal.log("Validate", "Target file does NOT exist: " + file);
            }
            LInChannel input = new LInChannel(testFilePath);

            correct = testHeaderLength(input);

            String inputStr;
            //Test GRUPs
            String majorRecordType = "NULL";
            int grupLength = 0;
            long grupPos = input.pos();
            int length;
            long start = 0;
            String EDID = "";
            Map<Integer, String> formids = new HashMap<>();
            Map<Integer, String> dupIds = new HashMap<>();
            while (input.available() >= 4 && (numErrors < numErrorsToPrint || numErrorsToPrint == 0)) {

                inputStr = input.extractString(0, 4);
                if (inputStr.equals("GRUP")) {
                    long inputPos = input.pos();
                    if (inputPos - grupPos - 4 != grupLength) {
                        SPGlobal.logError(recordLengths, "GRUP " + majorRecordType + " is wrong. (" + Ln.prettyPrintHex(grupPos) + ")");
                        numErrors++;
                        correct = false;
                    }
                    grupPos = input.pos() - 4;
                    grupLength = input.extractInt(0, 4);
                    majorRecordType = input.extractString(0, 4);
                    if (skip.contains(majorRecordType)) {
                        input.skip(grupLength - 12);
                    } else {
                        input.skip(12);
                    }
                } else if (inputStr.equals(majorRecordType)) {
                    start = input.pos() - 4;
                    length = input.extractInt(0, 4);
                    input.skip(4);
                    int formID = input.extractInt(4);
                    input.skip(8); 
                    String subRecordType = input.extractString(0, 4);
                    if (subRecordType.equalsIgnoreCase("EDID")) {
                        int edidLength = input.extractInt(0, 2);
                        EDID = input.extractString(0, edidLength - 1);
                        input.skip(length - 6 - EDID.length()); // 4 from subrecord type 'EDID' + 2 from length of EDID subrecord
                        if (formids.containsKey(formID)) {
                            dupIds.put(formID, EDID);
                        } else {
                            formids.put(formID, EDID);
                        }
                    } else {
                        EDID = "No EDID subrecord";
                        input.skip(length - 4); // 4 from subrecord type
                    }
                } else {
                    SPGlobal.logError(recordLengths, "Major Record: " + majorRecordType + " | " + EDID + " is wrong. (" + Ln.prettyPrintHex(start) + ")");
                    numErrors++;
                    correct = false;
                }
            }

            if (!dupIds.isEmpty()) {
                SPGlobal.logError(recordLengths, "Duplicate FormIDs: ");
                for (int id : dupIds.keySet()) {
                    SPGlobal.logError(recordLengths, Ln.printHex(id) + ", EDIDS: " + dupIds.get(id) + ", and " + formids.get(id));
                }
                correct = false;
            }

            input.close();

        } catch (FileNotFoundException ex) {
            SPGlobal.logError(recordLengths, "File could not be found.");
            SPGlobal.logException(ex);
        } catch (IOException ex) {
            SPGlobal.logError(recordLengths, "File I/O error.");
            SPGlobal.logException(ex);
        }

        if (correct) {
            SPGlobal.log(recordLengths, "Validated.");
        } else {
            SPGlobal.logError(recordLengths, "NOT Validated.");
        }
        return correct;
    }

    static boolean testHeaderLength(LInChannel input) throws IOException {
        // Check header
        String inputStr;
        boolean correct = true;
        int length = input.extractInt(4, 4);
        input.skip(length + 16);
        if (input.available() > 0) {
            // Next should be a GRUP
            inputStr = input.extractString(0, 4);
            if (!"GRUP".equals(inputStr)) {
                SPGlobal.logError("Mod Header", "Header length is wrong.");
                correct = false;
            }
            input.skip(-4);
        } else if (input.available() < 0) {
            SPGlobal.logError("Mod Header", "Header length is wrong.");
            correct = false;
        } else {
            SPGlobal.logError("Mod Header", "File header was correct, but there were no GRUPS.  Validated.");
            return true;
        }
        return correct;
    }

    /**
     * Starts a process as if from the command line, with the given working
     * directory.
     *
     * @param directory
     * @param args
     * @return
     */
    static public boolean startProcess(File directory, String... args) {
        try {
            ProcessBuilder proc = new ProcessBuilder(args);
            if (directory != null) {
                proc.directory(directory);
            }
            Process start = proc.start();
            InputStream shellIn = start.getInputStream();
            int exitStatus = start.waitFor();
            String response = Ln.convertStreamToStr(shellIn);
            if (exitStatus != 0) {
                String tmp = "";
                for (String arg : args) {
                    tmp += " " + arg;
                }
                SPGlobal.logError("StartProcess", "Process with args " + tmp + " Failed to run: " + response);
                return false;
            }
        } catch (IOException | InterruptedException ex) {
            SPGlobal.logException(ex);
            return false;
        }
        return true;
    }

    /**
     * Starts a process as if from the command line.
     *
     * @param args
     * @return
     */
    static public boolean startProcess(String... args) {
        return startProcess(null, args);
    }

    /**
     * Trims or expands a string (with _) to have exactly four characters.
     *
     * @param in
     * @return
     */
    static public String trimToFour(String in) {
        if (in.length() > 4) {
            return in.substring(0, 3);
        } else if (in.length() < 4) {
            return Ln.spaceRight(4, '_', in);
        } else {
            return in;
        }
    }

    /**
     * Replaces formIDs in arraylist.
     *
     * @param src Target arraylist to look in
     * @param target FormID to replace
     * @param with FormIDs to substitute in place of target
     * @return Number of targets replaced
     */
    static public int replaceAll(ArrayList<FormID> src, FormID target, FormID... with) {
        ArrayList<FormID> tmp = new ArrayList<>(src);
        int numChanges = 0;
        for (int i = tmp.size() - 1; i >= 0; i--) {
            if (tmp.get(i).equals(target)) {
                numChanges++;
                src.remove(i);
                for (FormID id : with) {
                    src.add(i, id);
                }
            }
        }
        return numChanges;
    }

    static public int replaceAll(ArrayList<FormID> src, FormID target, ArrayList<FormID> with) {
        return replaceAll(src, target, with.toArray(new FormID[0]));
    }

    static public Map<FormID, Integer> replaceIDs(ArrayList<FormID> src, Map<FormID, FormID> replacements) {
        Map<FormID, Integer> out = new HashMap<>(replacements.size());
        for (FormID id : replacements.keySet()) {
            out.put(id, 0);
        }
        for (FormID id : src) {
            FormID replace = replacements.get(id);
            if (replace != null) {
                out.put(replace, out.get(replace) + 1);
                id.setTo(id);
            }
        }
        return out;
    }

    static public Map<FormID, Integer> replaceMajors(ArrayList<FormID> src, Map<FormID, MajorRecord> replacements) {
        Map<FormID, Integer> out = new HashMap<>(replacements.size());
        for (FormID id : replacements.keySet()) {
            out.put(id, new Integer(0));
        }
        for (FormID id : src) {
            MajorRecord replace = replacements.get(id);
            if (replace != null) {
                Integer i = out.get(id);
                out.put(id, i + 1);
                id.setTo(id);
            }
        }
        return out;
    }

    /**
     * Creates empty files for non existent mods.
     *
     * @param mods
     * @throws IOException
     */
    public static void setupMissingPatchFiles(ArrayList<Mod> mods) throws IOException {
        setupMissingPatchFiles(mods.toArray(new Mod[0]));
    }

    /**
     * Creates empty files for non existent mods.
     *
     * @param mods
     * @throws IOException
     */
    public static void setupMissingPatchFiles(Mod... mods) throws IOException {
        // Handle non-existant patchers
        for (Mod newPatcher : mods) {
            File path = new File(SPGlobal.pathToData + newPatcher.getName());
            // Export tmp patch as a placeholder
            if (!path.isFile()) {
                BufferedWriter placeholder = new BufferedWriter(new FileWriter(SPGlobal.pathToData + newPatcher.getName()));
                placeholder.close();
            }
        }
    }

    /**
     * Adds and removes desired mods from the plugins list.
     *
     * @param add Mods to add if they don't already exist on the list
     * @param remove Mods to remove if they do exist.
     * @throws IOException
     */
    public static void modifyPluginsTxt(ArrayList<Mod> add, ArrayList<Mod> remove) throws IOException {
        //Read in plugins.txt
        ArrayList<String> pluginsLines = Ln.loadFileToStrings(SPGlobal.getPluginsTxt(), false);

        // Remove unwanted mods
        if (remove != null) {
            for (Mod r : remove) {
                Ln.removeIgnoreCase(pluginsLines, r.getName());
            }
        }

        // Find missing lines on plugins.txt
        if (add != null) {
            for (Mod newPatcher : add) {
                if (!Ln.containsEqualsIgnoreCase(pluginsLines, newPatcher.getName())) {
                    // Add listing to plugins.txt
                    pluginsLines.add(newPatcher.getName());
                }
            }
        }

        // Write out new plugins.txt
        BufferedWriter pluginsOut = new BufferedWriter(new FileWriter(SPGlobal.getPluginsTxt()));
        for (String line : pluginsLines) {
            pluginsOut.write(line + "\n");
        }

        pluginsOut.close();
    }

    /**
     * Adds a mod to the plugins list if it doesn't exist.
     *
     * @param add
     * @throws IOException
     */
    public static void modifyPluginsTxt(Mod add) throws IOException {
        ArrayList<Mod> addL = new ArrayList<>(1);
        addL.add(add);
        modifyPluginsTxt(addL, null);
    }

    /**
     * Copies each major record from the target mod that is referenced in the
     * major record. This makes the major record "self contained" from the
     * target mod.
     *
     * @param in
     * @param targetMod
     * @return
     */
    public static ArrayList<MajorRecord> deepCopySubRecords(MajorRecord in, ModListing targetMod) {
        ArrayList<MajorRecord> out = new ArrayList<>();
        ArrayList<FormID> allIDs = in.allFormIDs();
        allIDs.remove(in.getForm());
        for (FormID id : allIDs) {
            MajorRecord m = SPDatabase.getMajor(id);
            if (m != null
                    && (id.getMaster().equals(targetMod) // From target mod
                    || !SPDatabase.getMod(id.getMaster()).contains(id) // Or missing "insert"
                    )) {
                MajorRecord copy = deepSubrecordCopyDB.get(id);
                if (copy == null) {
                    String edid = m.getEDID();
                    if (!m.getType().equals("KYWD") && !m.getType().equals("GMST")) {
                        edid += "_deepCopy";
                    }
                    copy = m.copy(edid);
                    deepSubrecordCopyDB.put(id, copy);
                    deepCopySubRecords(copy, targetMod);
                }
                id.setTo(copy.getForm());
                out.add(copy);
            }
        }
        return out;
    }

    /**
     * Checks global path for duplicate record. If none is found, in is
     * returned. If a duplicate is found, in is removed from the global patch
     * and the duplicate is returned.<br><br>
     *
     * deepEquals is used to determine if two records are equal.
     *
     * @param in Major Record to check for duplicates.
     * @return A record from global patch that is unique.
     */
    public static MajorRecord mergeDuplicate(MajorRecord in) {
        GRUP_TYPE g = GRUP_TYPE.valueOf(in.getType());
        ArrayList<MajorRecord> grup = new ArrayList<>(SPGlobal.getGlobalPatch().getGRUPs().get(g).getRecords());
        grup.remove(in);
        for (MajorRecord existing : grup) {
            if (in.deepEquals(existing)) {
                SPGlobal.getGlobalPatch().remove(in.getForm());
                if (SPGlobal.logging()) {
                    SPGlobal.log("NiftyFunc", in + " duplicate of " + existing);
                }
                return existing;
            }
        }
        return in;
    }
    
    public static void logMem(String header){
        SPGlobal.logMain(header, "Used Memory: " + Ln.toMB((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) + "MB");
    }
}
