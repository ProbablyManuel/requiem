/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.debug;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lev.Ln;

/**
 * A debug log package that offers several locations and options for outputting
 * debug information.
 *
 * @author Justin Swanson
 */
public class LLogger {

    protected String debugPath;
    protected LDebug main;
    protected LDebug synced;
    protected boolean syncing = false;
    protected LDebug asynced;
    protected boolean logging = true;
    protected boolean loggingSync = true;
    protected boolean loggingAsync = true;
    protected static boolean mainLogSwitch = true;
    protected Map<Enum, LDebug> special = new HashMap<>();

    /**
     *
     * @param debugPath Path to create a LLogger debug package.
     */
    public LLogger(String debugPath) {
	this.debugPath = debugPath;
	Ln.deleteDirectory(new File(debugPath));
	main = new LDebug(debugPath + "=--Debug Overview--=.txt", 50);
	asynced = new LDebug(debugPath + "Asynchronous log.txt", 50);
	synced = new LDebug();
    }

    /**
     * Adds a special log tied to the Enum key, that can be exported to by using
     * that key.
     *
     * @param key Key to add the special log under
     * @param filePath Path to give the special log.
     */
    public void addSpecial(Enum key, String filePath) {
	if (special.containsKey(key)) {
	    special.get(key).closeDebugFile();
	}
	special.put(key, new LDebug(debugPath + filePath, 50));
    }

    /**
     * A function that will log messages to the synchronized log if the syncing
     * flag is on. Otherwise, it will output to the asynchronized log.
     *
     * @param header
     * @param log
     */
    public void logSync(String header, String... log) {
	if (loggingSync() || syncing && loggingAsync()) {
	    if (syncing) {
		synced.w(header, log);
	    } else {
		log(header, log);
	    }
	}
    }

    /**
     * Creates a new sync log in the desired location.
     *
     * @param filePath
     */
    public void newSyncLog(String filePath) {
	if (logging()) {
	    synced.openDebug(debugPath + filePath, 50);
	}
    }

    /**
     * Turn the synchronized logging on/off. If this is on, all logSync() output
     * will go to the synchronized log; if off, the messages will go to the
     * asynchronous log instead.
     *
     * @param on
     */
    public void sync(boolean on) {
	syncing = on;
    }

    /**
     *
     * @return Whether the LLogger is current output to the sync log.
     */
    public boolean sync() {
	return syncing;
    }

    /**
     * Outputs a message to the main debug overview log.
     *
     * @param header
     * @param log
     */
    public void logMain(String header, String... log) {
	main.w(header, log);
	main.flushDebug();
    }

    /**
     * Logs an error message on both to the sync log and to the main overview
     * log.
     *
     * @param header
     * @param log
     */
    public void logError(String header, String... log) {
	LDebug logger = sync() ? synced : asynced;
	logMain("ERROR", " /=== ERROR!");
	logMain("ERROR", "(==== File: " + logger.getOpenPath());
	logMain("ERROR", " \\=== Line: " + logger.line());
	String message = "  \\== Message: ";
	for (String s : log) {
	    message += s;
	}
	logMain("ERROR", message);
	logMain("ERROR", "   \\========================================>");
	logSync(header, log);
	flush();
    }

    /**
     *
     * @return Whether the LLogger is on/off.
     */
    public boolean logging() {
	return logging && mainLogSwitch;
    }

    /**
     * Turns the LLogger on/off.
     *
     * @param on
     */
    public void logging(Boolean on) {
	logging = on;
    }

    /**
     *
     * @return Whether the LLogger's sync log is on/off.
     */
    public boolean loggingSync() {
	return loggingSync && logging && mainLogSwitch;
    }

    /**
     * Turns the LLogger sync log on/off.
     *
     * @param on
     */
    public void loggingSync(Boolean on) {
	loggingSync = on;
    }

    /**
     *
     * @return Whether the LLogger's async log is on/off.
     */
    public boolean loggingAsync() {
	return loggingAsync && logging && mainLogSwitch;
    }

    /**
     * Turns the LLogger async log on/off.
     * @param on
     */
    public void loggingAsync(Boolean on) {
	loggingAsync = on;
    }

    void logException(String dump) {
	main.writeException(dump);
    }

    /**
     * Used for printing exception stack data to logs.
     *
     * @param e Exception to print.
     */
    public void logException(Throwable e) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw, true);
	e.printStackTrace(pw);
	pw.flush();
	sw.flush();
	logSync("EXCEPTION", sw.toString());
	logException(sw.toString());
    }

    /**
     * A global switch that allows/blocks all LLoggers to output.
     *
     * @param in
     */
    public void setAllLogging(boolean in) {
	mainLogSwitch = in;
    }

    /**
     * Forces all logs to export their buffers.
     */
    public void flush() {
	if (logging()) {
	    main.flushDebug();
	    asynced.flushDebug();
	    synced.flushDebug();
	    for (LDebug d : special.values()) {
		d.flushDebug();
	    }
	}
    }

    /**
     * Logs to a special log based on the given enum. You must create these
     * special logs ahead of time.
     *
     * @param e Enum key to log to.
     * @param header
     * @param log
     */
    public void logSpecial(Enum e, String header, String... log) {
	if (logging()) {
	    LDebug spec = special.get(e);
	    if (spec != null) {
		spec.w(header, log);
	    }
	}
    }

    /**
     * Logs to the asynchronous log.
     *
     * @param header
     * @param log
     */
    public void log(String header, String... log) {
	if (loggingAsync()) {
	    asynced.w(header, log);
	}
    }

    /**
     * Creates a new asynchronous log.
     *
     * @param filePath
     */
    public void newLog(String filePath) {
	asynced.openDebug(debugPath + filePath, 50);
    }

    /**
     * Closes all associated Debug files.
     */
    public void close() {
	for (LDebug d : allDebugs()) {
	    d.closeDebugFile();
	}
    }
    
    public ArrayList<LDebug> allDebugs() {
	ArrayList<LDebug> out = new ArrayList<>(special.values());
	out.add(main);
	out.add(asynced);
	out.add(synced);
	return out;
    }
}
