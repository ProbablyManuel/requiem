/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 * A class that manages importing/exporting of save files, as well as providing
 * methods for LUserSetting GUI components to automatically update tied
 * settings.<br><br>
 *
 * NOTE: <br> The in-game parameters currently have no effect at the moment.
 * <br><br>
 *
 * To use it:<br> 1) Create an enum class defining a name for each setting you
 * want. 2) Extend LSaveFile with your own class that defines its own init
 * functions using the setting enum you created.
 *
 * @author Justin Swanson
 */
public abstract class LSaveFile {

    /**
     *
     */
    protected File location;
    /**
     * List containing default, save, temp, and current setting maps.
     */
    protected ArrayList<Map<Enum, Setting>> maps = new ArrayList<>();
    /**
     * Stores the default values for each setting.
     */
    protected Map<Enum, Setting> defaultSettings = new TreeMap<>();
    /**
     * Stores the previously saved settings of the current end user.
     */
    protected Map<Enum, Setting> saveSettings = new TreeMap<>();
    /**
     * Stores the current settings displayed on the GUI.
     */
    protected Map<Enum, Setting> curSettings = new TreeMap<>();
    /**
     *
     */
    protected Map<Enum, Setting> cancelSave = new TreeMap<>();
    /**
     *
     */
    protected Map<Enum, Setting> peekSave = new TreeMap<>();
    /**
     * Map containing the help text associated with settings in the saveFile.
     */
    protected Map<Enum, String> helpInfo = new TreeMap<>();
    /**
     *
     */
    protected boolean initialized = false;

    /**
     * Ties the LUserSetting to the Enum key
     *
     * @param s Enum key to tie to
     * @param c Setting to tie with
     */
    public void tie(Enum s, LUserSetting c) {
	for (Map<Enum, Setting> e : maps) {
	    if (e.containsKey(s)) {
		e.get(s).tie(c);
	    }
	}
    }

    /**
     *
     */
    private LSaveFile() {
	maps.add(defaultSettings);
	maps.add(saveSettings);
	maps.add(curSettings);
	maps.add(peekSave);
    }

    /**
     *
     * @param location
     */
    public LSaveFile(File location) {
	this();
	this.location = location;
    }

    /**
     *
     * @param location
     */
    public LSaveFile(String location) {
	this(new File(location));
    }

    /**
     * Call this function at the start of your program to signal the savefile to
     * load its settings and prep for use.
     */
    public void init() {
	if (!initialized) {
	    initSettings();
	    initHelp();
	    readInSettings();
	    initialized = true;
	}
    }

    /**
     * An abstract function that should contain Add() calls that define each
     * setting in the saveFile and their default values.
     */
    protected abstract void initSettings();

    /**
     * A function that loads the help map with help text for any settings that
     * you desire.
     */
    protected abstract void initHelp();

    void readInSettings() {
	File f = new File(location.getPath());
	if (f.isFile()) {
	    try {
		BufferedReader input = new BufferedReader(new FileReader(f));
		String version = input.readLine();  //title
		if (version.contains("Version")) {
		    String inStr;
		    String settingTitle;
		    while (input.ready()) {
			inStr = input.readLine().trim();
			if (inStr.equals("")) {
			    continue;
			}
			settingTitle = inStr.substring(0, inStr.indexOf(": "));
			inStr = inStr.substring(inStr.indexOf(": ") + 2);
			for (Enum s : saveSettings.keySet()) {
			    if (saveSettings.containsKey(s)) {
				if (saveSettings.get(s).getTitle().equals(settingTitle)) {
				    // Multiline setting
				    if (saveSettings.get(s).getClass() == SaveStringList.class) {
					int num = Integer.valueOf(inStr.trim());
					inStr = "";
					for (int i = 0; i < num; i++) {
					    inStr += input.readLine();
					}
				    }
				    saveSettings.get(s).readSetting(inStr);
				    curSettings.get(s).readSetting(inStr);
				    break;
				}
			    }
			}
		    }
		} else {
		    readInSettingsV1(input);
		}

	    } catch (Exception e) {
		JOptionPane.showMessageDialog(null, "Error in reading in save file. Reverting to default settings.");
		initSettings();
		initHelp();
		initialized = true;
	    }
	}
    }

    void readInSettingsV1(BufferedReader input) throws IOException {
	String inStr;
	String settingTitle;
	while (input.ready()) {
	    inStr = input.readLine().trim();
	    if (inStr.equals("")) {
		continue;
	    }
	    settingTitle = inStr.substring(4, inStr.indexOf(" to "));
	    inStr = inStr.substring(inStr.indexOf(" to ") + 4);
	    for (Enum s : saveSettings.keySet()) {
		if (saveSettings.containsKey(s)) {
		    if (saveSettings.get(s).getTitle().equals(settingTitle)) {
			// Multiline setting
			if (saveSettings.get(s).getClass() == SaveStringList.class) {
			    int num = Integer.valueOf(inStr.trim());
			    inStr = "";
			    for (int i = 0; i < num; i++) {
				inStr += input.readLine();
			    }
			}
			saveSettings.get(s).readSetting(inStr);
			curSettings.get(s).readSetting(inStr);
		    }
		}
	    }
	}
    }

    /**
     * Tells the savefile to write its values to the disk. Should be called as
     * the program is ending.
     */
    public void saveToFile() {

	File f = location;
	File dir = f.getParentFile();
	if (!dir.isDirectory()) {
	    dir.mkdirs();
	}
	if (f.isFile()) {
	    f.delete();
	}

	try {
	    BufferedWriter output = new BufferedWriter(new FileWriter(f));
	    output.write("### Savefile used for the application.  Version: 2\n");
	    for (Enum s : curSettings.keySet()) {
		if (!curSettings.get(s).get().equals("")) {
		    curSettings.get(s).write(output);
		} else {
		    defaultSettings.get(s).write(output);
		}
	    }
	    output.close();
	} catch (java.io.IOException e) {
	    JOptionPane.showMessageDialog(null, "The application couldn't open the save file output stream.  Your settings were not saved.");
	}
    }

    void Add(Enum type, Setting s) {
	for (Map<Enum, Setting> m : maps) {
	    m.put(type, s.copyOf());
	}
    }

    /**
     * Adds a setting of type boolean.
     *
     * @param type Enum to be associated with.
     * @param extraFlags
     * @param b Default value to assign the setting.
     */
    public void Add(Enum type, Boolean b, Boolean... extraFlags) {
	Add(type, new SaveBool(type.toString(), b, extraFlags));
    }

    /**
     * Adds a setting of type string.
     *
     * @param type Enum to be associated with.
     * @param s Default value to assign the setting.
     * @param extraFlags
     */
    public void Add(Enum type, String s, Boolean... extraFlags) {
	Add(type, new SaveString(type.toString(), s, extraFlags));
    }

    /**
     * Adds a setting of type integer.
     *
     * @param type Enum to be associated with.
     * @param extraFlags
     * @param i Default value to assign the setting.
     */
    public void Add(Enum type, Integer i, Boolean... extraFlags) {
	Add(type, new SaveInt(type.toString(), i, extraFlags));
    }

    /**
     * Adds a setting of type enum.
     *
     * @param type Enum to be associated with.
     * @param e
     * @param extraFlags
     */
    public void Add(Enum type, Enum e, Boolean... extraFlags) {
	Add(type, new SaveEnum(type.toString(), e, extraFlags));
    }

    /**
     * Adds a setting of type integer.
     *
     * @param type Enum to be associated with.
     * @param strs
     * @param extraFlags
     */
    public void Add(Enum type, ArrayList<String> strs, Boolean... extraFlags) {
	Add(type, new SaveStringList(type.toString(), strs, extraFlags));
    }

    /**
     * Adds a setting of type float.
     *
     * @param type Enum to be associated with.
     * @param f Default value to assign the setting.
     * @param extraFlags
     */
    public void Add(Enum type, Float f, Boolean... extraFlags) {
	Add(type, new SaveFloat(type.toString(), f, extraFlags));
    }

    /**
     * Adds a setting of type Color.
     *
     * @param type Enum to be associated with.
     * @param c
     * @param extraFlags
     */
    public void Add(Enum type, Color c, Boolean... extraFlags) {
	Add(type, new SaveColor(type.toString(), c, extraFlags));
    }

    /**
     * Adds a setting of type double.
     *
     * @param type Enum to be associated with.
     * @param d Default value to assign the setting.
     * @param extraFlags
     */
    public void Add(Enum type, Double d, Boolean... extraFlags) {
	Add(type, new SaveDouble(type.toString(), d, extraFlags));
    }

    /**
     * Copies one map of settings to another. For reverting current settings to
     * default, for example.
     *
     * @param from
     * @param to
     */
    public static void copyTo(Map<Enum, Setting> from, Map<Enum, Setting> to) {
	to.clear();
	for (Enum s : from.keySet()) {
	    to.put(s, from.get(s).copyOf());
	}
    }

    /**
     * Makes the savefile reacquire the settings from any tied GUI components.
     */
    public void updateCurToGUI() {
	for (Enum s : curSettings.keySet()) {
	    updateCurToGUI(s);
	}
    }

    /**
     *Makes the savefile reaquire the settings for the specific enum.
     * @param s Enum to update
     */
    public void updateCurToGUI(Enum s) {
	curSettings.get(s).set();
    }

    /**
     * Reverts the tied GUI to display the current settings.
     */
    public void updateGUItoCur() {
	revertTo(curSettings);
    }

    void set(Enum setting, Object in) {
	curSettings.get(setting).setTo(in);
    }

    /**
     * Makes the savefile's GUI ties display saved settings, and highlights ones
     * that have changed.
     */
    public void peekSaved() {
	peek(saveSettings);
    }

    /**
     * Makes the savefile's GUI ties display default settings, and highlights
     * ones that have changed.
     */
    public void peekDefaults() {
	peek(defaultSettings);
    }

    /**
     * Clears any "peeked" states, reverts all GUI components to the "current"
     * settings, and clears any highlighting.
     */
    public void clearPeek() {
	for (Setting s : curSettings.values()) {
	    if (s.tie != null) {
		s.tie.revertTo(peekSave);
		s.tie.clearHighlight();
	    }
	}
	updateCurToGUI();
    }

    void peek(Map<Enum, Setting> in) {
	copyTo(curSettings, peekSave);
	for (Setting s : curSettings.values()) {
	    if (s.tie != null && !s.tie.revertTo(in)) {
		s.tie.highlightChanged();
	    }
	}
	updateCurToGUI();
    }

    /**
     * Saves the current settings to a temporary cancel backup save
     */
    public void saveToCancelSave() {
	copyTo(curSettings, cancelSave);
    }

    /**
     * Reverts the current settings to the backup cancel save
     */
    public void revertToCancel() {
	copyTo(cancelSave, curSettings);
	updateGUItoCur();
    }

    /**
     * Reverts the current settings to the given map.
     * @param in
     */
    public void revertTo(Map<Enum, Setting> in) {
	for (Setting s : curSettings.values()) {
	    if (s.tie != null) {
		s.tie.revertTo(in);
	    }
	}
    }

    /**
     * Reverts a GUI component to the saved setting
     *
     * @param s
     */
    public void revertToSaved(LUserSetting s) {
	revertTo(saveSettings, s);
    }

    /**
     * Reverts a GUI component to the default setting
     *
     * @param s
     */
    public void revertToDefault(LUserSetting s) {
	revertTo(defaultSettings, s);
    }

    /**
     * Reverts the setting to its saved state
     *
     * @param setting
     */
    public void revertToSaved(Enum setting) {
	revertTo(saveSettings, curSettings.get(setting).tie);
    }

    /**
     * Reverts the setting to its default state
     *
     * @param setting
     */
    public void revertToDefault(Enum setting) {
	revertTo(defaultSettings, curSettings.get(setting).tie);
    }

    void revertTo(Map<Enum, Setting> in, LUserSetting s) {
	s.revertTo(in);
	copyTo(in, peekSave);
    }

    /**
     * Checks the flag of each setting at the given index, and returns the AND logic.
     * @param index
     * @return
     */
    public boolean checkFlagAnd(int index) {
	ArrayList<Setting> modified = getModifiedSettings();
	for (Setting s : modified) {
	    if (!s.extraFlags[index]) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Checks the flag of each setting at the given index, and returns the OR logic.
     * @param index
     * @return
     */
    public boolean checkFlagOr(int index) {
	ArrayList<Setting> modified = getModifiedSettings();
	for (Setting s : modified) {
	    if (s.extraFlags[index]) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Checks the flag of given setting at the given index.
     * @param s
     * @param index
     * @return
     */
    public boolean checkFlag(Enum s, int index) {
	return curSettings.get(s).extraFlags[index];
    }

    /**
     *
     * @return
     */
    public ArrayList<Setting> getModifiedSettings() {
	return getDiff(saveSettings, curSettings);
    }

    /**
     *
     * @param lhs
     * @param rhs
     * @return
     */
    public ArrayList<Setting> getDiff(Map<Enum, Setting> lhs, Map<Enum, Setting> rhs) {
	ArrayList<Setting> out = new ArrayList<>();
	for (Enum e : lhs.keySet()) {
	    if (!lhs.get(e).equals(rhs.get(e))) {
		out.add(rhs.get(e));
	    }
	}
	return out;
    }

    /**
     * Returns the value of the setting, and assumes it's a string value.
     *
     * @param s
     * @return
     */
    public String getStr(Enum s) {
	return curSettings.get(s).getStr();
    }

    /**
     * Returns the value of the setting, and assumes it's an int value.
     *
     * @param s
     * @return
     */
    public Integer getInt(Enum s) {
	return curSettings.get(s).getInt();
    }

    /**
     * Returns the value of the setting, and assumes it's a boolean value.
     *
     * @param s
     * @return
     */
    public Boolean getBool(Enum s) {
	return curSettings.get(s).getBool();
    }

    /**
     *
     * @param s
     * @return
     */
    public Color getColor(Enum s) {
	return curSettings.get(s).getColor();
    }

    /**
     *
     * @param s
     * @return
     */
    public Enum getEnum(Enum s) {
	return curSettings.get(s).getEnum();
    }

    /**
     *
     * @param s
     * @return
     */
    public float getFloat(Enum s) {
	return curSettings.get(s).getFloat();
    }

    /**
     * Returns the value of the setting, and assumes it's a boolean value.
     *
     * @param s
     * @return
     */
    public ArrayList<String> getStrings(Enum s) {
	return new ArrayList<>(curSettings.get(s).getStrings());
    }

    /**
     *
     * @param e
     * @param s
     */
    public void setStr(Enum e, String s) {
	curSettings.get(e).setTo(s);
    }

    /**
     *
     * @param e
     * @param i
     */
    public void setInt(Enum e, int i) {
	curSettings.get(e).setTo(i);
    }

    /**
     *
     * @param e
     * @param c
     */
    public void setColor(Enum e, Color c) {
	curSettings.get(e).setTo(c);
    }

    /**
     *
     * @param e
     * @param b
     */
    public void setBool(Enum e, boolean b) {
	curSettings.get(e).setTo(b);
    }

    /**
     *
     * @param e
     * @param strs
     */
    public void setStrings(Enum e, ArrayList<String> strs) {
	curSettings.get(e).setTo(strs);
    }

    /**
     *
     * @param e
     * @param s
     */
    public void addString(Enum e, String s) {
	curSettings.get(e).getStrings().add(s);
    }

    /**
     *
     * @param e
     * @param s
     */
    public void removeString(Enum e, String s) {
	curSettings.get(e).getStrings().remove(s);
    }
}
