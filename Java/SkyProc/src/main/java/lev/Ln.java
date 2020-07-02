package lev;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.util.Map.Entry;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Justin Swanson
 */
public class Ln {

    /**
     *
     * @return My documents folder for the system.
     */
    public static File getMyDocuments() {
		return FileSystemView.getFileSystemView().getDefaultDirectory();
    }

    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     *
     * @param orig Object to copy
     * @return A deep copy of the object, completely separate from the original.
     */
    public static Object deepCopy(Object orig) {
	Object obj = null;
	try {
	    // Write the object out to a byte array
	    FastByteArrayOutputStream fbos =
		    new FastByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(fbos);
	    out.writeObject(orig);
	    out.flush();
	    out.close();

	    // Retrieve an input stream from the byte array and read
	    // a copy of the object back in.
	    ObjectInputStream in =
		    new ObjectInputStream(fbos.getInputStream());
	    obj = in.readObject();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	}
	return obj;
    }

    private static String space(Boolean left, Boolean concat, int spaces, char c, String... input) {
	String output = "";
	for (String x : input) {
	    output = output + x;
	}

	if (spaces < 4) {
	    spaces = 4;
	}

	if (concat && output.length() > spaces) {
	    output = output.substring(0, spaces / 2 - 2) + "..."
		    + output.substring(output.length() - (spaces / 2 - 1));
	}

	spaces = spaces - output.length();
	for (int i = 0; i < spaces; i++) {
	    if (left) {
		output = c + output;
	    } else {
		output = output + c;
	    }
	}

	return output;
    }

    /**
     * Takes the input and adds the desired amount of spaces to get the end
     * result of being "spaces" wide.
     *
     * @param enforce Whether to shrink the input to enforce the spaces size, or
     * let it bleed over to print all of the input
     * @param spaces Width desired including spaces + input.
     * @param c Character to print to achieve desired width.
     * @param input Input to print.
     * @return Final spaced string.
     */
    public static String spaceLeft(Boolean enforce, int spaces, char c, String... input) {
	return space(true, enforce, spaces, c, input);
    }

    /**
     * Takes the input and adds the desired amount of spaces to get the end
     * result of being "spaces" wide. Input is aligned left, so that the spaces
     * are on the right side.
     *
     * @param spaces Width desired including spaces + input.
     * @param c Character to print to achieve desired width.
     * @param input Input to print.
     * @return Final spaced string.
     */
    public static String spaceRight(int spaces, char c, String... input) {
	return space(false, false, spaces, c, input);
    }

    /**
     * Centers the input inside desired width.
     *
     * @param spaces Width desired.
     * @param c Character to fill to the width
     * @param input Input to center.
     * @return Final string
     */
    public static String center(int spaces, char c, String... input) {
	String output = "";
	for (String x : input) {
	    output = output + x;
	}
	int leftSpaces = (spaces - output.length()) / 2;
	for (int i = 0; i < leftSpaces; i++) {
	    output = c + output;
	}
	spaces = spaces - output.length();
	for (int i = 0; i < spaces; i++) {
	    output = output + c;
	}
	return output;
    }

    /**
     * Reverse characters in the string
     *
     * @param input
     * @return
     */
    public static String reverse(String input) {
	return String.copyValueOf(reverse(input.toCharArray()));
    }

    /**
     * Reverses characters in a char array
     *
     * @param input
     * @return
     */
    public static char[] reverse(char[] input) {
	char[] output = new char[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    /**
     * Reverses integers in an int array
     *
     * @param input
     * @return
     */
    public static int[] reverse(int[] input) {
	int[] output = new int[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    /**
     * Reverses bytes in a byte array
     *
     * @param input
     * @return
     */
    public static byte[] reverse(byte[] input) {
	byte[] output = new byte[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    /**
     * Removes comments, and trims whitespace
     *
     * @param line
     * @param comment
     * @return
     */
    public static String cleanLine(String line, String comment) {
	//Shave off comments
	int commentIndex = line.indexOf(comment);
	if (-1 != commentIndex) {
	    line = line.substring(0, commentIndex);
	}

	//Remove whitespace
	line = line.trim();

	return line;
    }

    /**
     * Removes the directory and everything inside of it.
     *
     * @param directory
     * @return
     */
    public static boolean deleteDirectory(File directory) {

	if (directory == null) {
	    return false;
	}
	if (!directory.exists()) {
	    return true;
	}
	if (!directory.isDirectory()) {
	    return false;
	}

	String[] list = directory.list();

	// Some JVMs return null for File.list() when the
	// directory is empty.
	if (list != null) {
	    for (int i = 0; i < list.length; i++) {
		File entry = new File(directory, list[i]);

		if (entry.isDirectory()) {
		    if (!deleteDirectory(entry)) {
			return false;
		    }
		} else {
		    if (!entry.delete()) {
			return false;
		    }
		}
	    }
	}

	return directory.delete();
    }

    /**
     * Makes the directories associated with a file.
     *
     * @param file
     */
    public static void makeDirs(File file) {
	makeDirs(file.getPath());
    }

    /**
     * Tests whether two array lists have equal contents.
     *
     * @param lhs
     * @param rhs
     * @param ordered Whether the contents have to be in order to count as
     * "equal"
     * @return
     */
    public static boolean equals(ArrayList lhs, ArrayList rhs, boolean ordered) {
	if (lhs == null && rhs == null) {
	    return true;
	} else if (lhs == null || rhs == null) {
	    return false;
	}

	if (lhs.size() != rhs.size()) {
	    return false;
	}

	if (ordered) {
	    for (int i = 0; i < lhs.size(); i++) {
		if (!lhs.get(i).equals(rhs.get(i))) {
		    return false;
		}
	    }
	} else {
	    ArrayList tmp = new ArrayList(rhs);
	    for (Object o : lhs) {
		tmp.remove(o);
	    }
	    if (!tmp.isEmpty()) {
		return false;
	    }
	}

	return true;
    }

    /**
     * Makes the directories associated with a file.
     *
     * @param file
     */
    public static void makeDirs(String file) {
	int index1 = file.lastIndexOf("/");
	int index2;
	if ((index2 = file.lastIndexOf("\\")) > index1) {
	    index1 = index2;
	}
	if (index1 == -1) {
	    return;
	}
	file = file.substring(0, index1);
	File f = new File(file);
	if (!f.exists()) {
	    f.mkdirs();
	}
    }

    /**
     * Moves a file from one directory to another. (eraseOldDirs is not
     * implemented yet)
     *
     * @param src Source file
     * @param dest Destination file
     * @param eraseOldDirs Whether to erase old empty directories.
     * @return The destination file
     */
    public static boolean moveFile(File src, File dest, boolean eraseOldDirs) {
	makeDirs(dest);
	if (!src.renameTo(dest)) {
	    return false;
	}
	if (eraseOldDirs) {
	    while ((src = src.getParentFile()) != null) {
		if (src.isDirectory() && src.listFiles().length == 0) {
		    src.delete();
		} else {
		    break;
		}
	    }
	}
	return true;
    }

    /**
     * Recursively returns all files inside of a directory and its
     * subdirectories.<br> Has min/max depths to exclude undesired levels.
     *
     * @param src Folder to recursively search
     * @param minDepth Min depth to start adding file to the list
     * @param maxDepth Max depth to add files to the list
     * @param addDirs Include directories as files in the list, followed by
     * their contents.
     * @return List of all files inside the folder and subfolders within the
     * depth parameters.
     */
    public static ArrayList<File> generateFileList(File src, int minDepth, int maxDepth, boolean addDirs) {
	ArrayList<File> out = new ArrayList<>();
	if (src.isDirectory()) {
	    for (File f : src.listFiles()) {
		if (minDepth <= 0 && (f.isFile() || addDirs)) {
		    out.add(f);
		}
		if (f.isDirectory() && maxDepth != 0) {
		    out.addAll(generateFileList(f, minDepth - 1, maxDepth - 1, addDirs));
		}
	    }
	}
	return out;
    }

    /**
     * Recursively returns all files inside of a directory and its
     * subdirectories.
     *
     * @param src Folder to recursively search
     * @param addDirs Include directories as files in the list, followed by
     * their contents.
     * @return List of all files inside the folder and subfolders within the
     * depth parameters.
     */
    public static ArrayList<File> generateFileList(File src, boolean addDirs) {
	return generateFileList(src, -1, -1, addDirs);
    }

    /**
     * Converts an int array to its string equivalent.
     *
     * @param input
     * @return
     */
    public static String arrayToString(int[] input) {
	String output = "";
	for (int i = 0; i < input.length; i++) {
	    output = output + (char) input[i];
	}
	return output;
    }

    /**
     * Converts a byte array to its string equivalent.
     *
     * @param input
     * @return
     */
    public static String arrayToString(byte[] input) {
	String output = "";
	for (int i = 0; i < input.length; i++) {
	    if (input[i] != 0) {
		output = output + (char) input[i];
	    }
	}
	return output;
    }

    /**
     * Returns true if input is a file (case insensitive check)
     *
     * @param test File to check if exists
     * @return true if input is a file (case insensitive check).
     */
    public static boolean isFileCaseInsensitive(File test) {
	return !getFilepathCaseInsensitive(test).getPath().equals("");
    }

    /**
     * Returns an uppercase version of the test path if it exists.
     *
     * @param test
     * @return Uppercase version of the test file.
     */
    public static File getFilepathCaseInsensitive(File test) {
	File dir = null;
	int index = test.getPath().lastIndexOf('\\');
	if (index != -1) {
	    dir = new File(test.getPath().substring(0, index));
	} else {
	    dir = new File("");
	}

	if (dir.isDirectory()) {
	    for (File file : dir.listFiles()) {
		if (test.getName().toUpperCase().equals(file.getName().toUpperCase())) {
		    return file;
		}
	    }
	}
	return new File("");
    }

    /**
     * Converts int array to a single int, assuming little endian.
     *
     * @param input
     * @return
     */
    public static int arrayToInt(int[] input) {
	return (int) arrayToLong(input);
    }

    /**
     * Converts int array to a single long, assuming little endian.
     *
     * @param input
     * @return
     */
    public static long arrayToLong(int[] input) {
	int multiplier = 1;
	long output = 0;
	for (int i = 0; i < input.length; i++) {
	    output += (int) input[i] * multiplier;
	    multiplier *= 256;
	}
	return output;
    }

    /**
     * Converts byte array to a single int, assuming little endian.
     *
     * @param input
     * @return
     */
    public static int arrayToInt(byte[] input) {
	int out = 0;
	for (int i = input.length - 1; i >= 0; i--) {
	    out |= input[i] & 0xFF;
	    if (i != 0) {
		out <<= 8;
	    }
	}
	return out;
    }

    /**
     * Converts byte array to a single long, assuming little endian.
     *
     * @param input
     * @return
     */
    public static long arrayToLong(byte[] input) {
	long out = 0;
	for (int i = input.length - 1; i >= 0; i--) {
	    out |= input[i] & 0xFF;
	    if (i != 0) {
		out <<= 8;
	    }
	}
	return out;
    }

    /**
     * Prints the integer representation of each index in the array.
     *
     * @param input
     * @return
     */
    public static String arrayPrintInts(int[] input) {
	String output = "";
	for (int i : input) {
	    output = output + Integer.toString(i) + " ";
	}
	return output;
    }

    /**
     * Prints the hex string of the input (assuming its one byte)
     *
     * @param input
     * @return
     */
    public static String printHex(long input) {
	if (input < 16) {
	    return "0" + Long.toHexString(input).toUpperCase();
	} else {
	    return Long.toHexString(input).toUpperCase();
	}
    }

    /**
     * Prints the hex string of the input (assuming its one byte)
     *
     * @param input
     * @return
     */
    public static String printHex(int input) {
	return printHex((long) input);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input integer to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @param minLength Will add filler zeros to achieve min length
     * @return Final string.
     */
    public static String printHex(int input, Boolean space, Boolean reverse, int minLength) {
	return printHex(Ln.toIntArray(input, minLength), space, reverse);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input array to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @return Final string.
     */
    public static String printHex(byte[] input, Boolean space, Boolean reverse) {
	return printHex(toIntArray(input), space, reverse);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input array to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @return Final string.
     */
    public static String printHex(Integer[] input, Boolean space, Boolean reverse) {
	return printHex(toIntArray(input), space, reverse);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input array to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @return Final string.
     */
    public static String printHex(int[] input, Boolean space, Boolean reverse) {
	String output = "";
	for (int i : input) {
	    if (reverse) {
		if (space) {
		    output = " " + output;
		}
		output = printHex(i) + output;
	    } else {
		output = output + printHex(i);
		if (space) {
		    output = output + " ";
		}
	    }
	}
	return output;
    }

    /**
     * Prints input in the form of: "([integer representation]->[hex
     * representation])"
     *
     * @param input Number to print
     * @return Final string
     */
    public static String prettyPrintHex(int input) {
	return prettyPrintHex((long) input);
    }

    /**
     * Prints input in the form of: "([integer representation]->[hex
     * representation])"
     *
     * @param input Number to print
     * @return Final string
     */
    public static String prettyPrintHex(long input) {
	return "{" + Long.toString(input) + "->0x" + printHex(input) + "}";
    }

    /**
     * Prints a double in digit form, no scientific notation.
     *
     * @param in Double to print.
     * @param length number of characters to print.
     * @return Final double print string.
     */
    public static String printDouble(double in, int length) {
	String out = String.valueOf(in);
	if (out.length() > length) {
	    String suffix = "";
	    if (out.contains("E")) {
		suffix = out.substring(out.indexOf('E'));
	    }
	    return out.substring(0, length) + suffix;
	} else {
	    return out;
	}
    }

    /**
     * Returns the file extension string. Eg "Hello.jpg" returns "jpg"
     *
     * @param f
     * @return
     */
    public static String getFileType(File f) {
	if (f.getName().indexOf(".") != -1) {
	    return f.getName().substring(f.getName().lastIndexOf(".") + 1);
	} else {
	    return "";
	}
    }

    /**
     * True if file extension is equal to input, case insensitive.
     *
     * @param f
     * @param fileType
     * @return
     */
    public static boolean isFileType(File f, String fileType) {
	return getFileType(f).equalsIgnoreCase(fileType);
    }

    /**
     * Removes all instances of the remove string from the input string.
     *
     * @param input Source string
     * @param remove String to remove
     * @return
     */
    public static String removeFromStr(String input, String remove) {

	String[] split = input.split(remove);

	String output = "";
	for (String s : split) {
	    output = output + s;
	}
	return output;
    }

    /**
     * Inserts a string inside the input string at the given index.
     *
     * @param input Source string
     * @param insert String to insert
     * @param location index to insert at.
     * @return
     */
    public static String insertInStr(String input, String insert, int location) {
	if (location <= 0) {
	    return insert + input;
	} else if (location >= input.length()) {
	    return input + insert;
	} else {
	    return input.substring(0, location - 1) + insert + input.substring(location);
	}
    }

    /**
     * Converts "true" to "1" and "false" to "0"
     *
     * @param input
     * @return
     */
    public static String convertBoolTo1(String input) {
	if (input.equals("true")) {
	    return "1";
	} else if (input.equals("false")) {
	    return "0";
	} else {
	    return input;
	}
    }

    /**
     * Converts boolean to "1" or "0"
     *
     * @param input
     * @return
     */
    public static String convertBoolTo1(Boolean input) {
	return convertBoolTo1(input.toString());
    }

    /**
     *
     * @param input
     * @return True if string equals "1" or "true"
     */
    public static Boolean toBool(String input) {
	if (input.equals("1") || input.equals("true")) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Make directories to file path if they don't exist.<br> Deletes old file
     * if delete is on.
     *
     * @param f
     * @param delete
     * @return
     */
    public static File setupFile(File f, Boolean delete) {

	File parent = new File(f.getParent());
	if (parent != null && !parent.isDirectory()) {
	    f.mkdirs();
	}

	if (delete && (f.isFile() || f.isDirectory())) {
	    f.delete();
	}

	return f;
    }

    /**
     * Make directories to file path if they don't exist.<br> Deletes old file
     * if delete is on.
     *
     * @param s
     * @param delete
     * @return
     */
    public static File setupFile(String s, Boolean delete) {
	return setupFile(new File(s), delete);
    }

    /**
     * Expands or minimizes all nodes in a GUI tree.
     *
     * @param tree
     * @param expand
     */
    public static void expandAll(JTree tree, boolean expand) {
	// Traverse tree from root
	expandAll(tree, expand, 0, Integer.MAX_VALUE);
    }

    private static void expandAll(JTree tree, boolean expand, int depth, int maxDepth) {
	TreeNode root = (TreeNode) tree.getModel().getRoot();
	expandAll(tree, new TreePath(root), expand, depth, maxDepth);
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand, int depth, int maxDepth) {
	// Traverse children
	TreeNode node = (TreeNode) parent.getLastPathComponent();
	if (node.getChildCount() >= 0) {
	    for (Enumeration e = node.children(); e.hasMoreElements();) {
		TreeNode n = (TreeNode) e.nextElement();
		TreePath path = parent.pathByAddingChild(n);
		expandAll(tree, path, expand, depth + 1, maxDepth);
	    }
	}

	// Expansion or collapse must be done bottom-up
	if (expand) {
	    if (depth < maxDepth) {
		tree.expandPath(parent);
	    }
	} else {
	    tree.collapsePath(parent);
	}
    }

    /**
     *
     * @param tree
     * @param depth
     */
    public static void expandToDepth(JTree tree, int depth) {
	expandAll(tree, false);
	expandAll(tree, true, 0, depth);
    }

    /**
     * Parses a hex string.<br> Viable formats:<br> 1) "0123"<br> 2) "01 23"<br>
     * 3) "0x01 0x23"
     *
     * @param hex Hex string to parse.
     * @param min Minimum length, will be filled with zeros to fill.
     * @param reverse Reverse the bytes
     * @return Parsed hex string in a byte array.
     */
    public static byte[] parseHexString(String hex, int min, boolean reverse) {
	if (reverse) {
	    return Ln.reverse(parseHexString(hex, min));
	} else {
	    return parseHexString(hex, min);
	}
    }

    /**
     * Parses a hex string.<br> Viable formats:<br> 1) "0123"<br> 2) "01 23"<br>
     * 3) "0x01 0x23"
     *
     * @param hex Hex string to parse.
     * @return Parsed hex string in a byte array.
     */
    public static byte[] parseHexString(String hex) {
	return parseHexString(hex, 0);
    }

    /**
     * Parses a hex string.<br> Viable formats:<br> 1) "0123"<br> 2) "01 23"<br>
     * 3) "0x01 0x23"
     *
     * @param hex Hex string to parse.
     * @param min Minimum length, will be filled with zeros to fill.
     * @return Parsed hex string in a byte array.
     */
    public static byte[] parseHexString(String hex, int min) {
	byte[] tempOutput = new byte[1000];
	int counter = 0;

	// Cut out 0x and whitespace
	String tmpHex = "";
	Scanner scan = new Scanner(hex);
	String next;
	while (scan.hasNext()) {
	    next = scan.next();
	    if (next.indexOf('x') != -1) {
		next = next.substring(next.indexOf('x') + 1);
	    }
	    tmpHex += next;
	}
	hex = tmpHex;

	//Make even length
	if (hex.length() % 2 == 1) {
	    hex = "0" + hex;
	}
	//Increase to minimum length
	while (hex.length() < min * 2) {
	    hex = "00" + hex;
	}

	//Load and convert
	for (int i = 0; i < hex.length(); i = i + 2) {
	    tempOutput[counter++] = (byte) (int) Integer.valueOf(hex.substring(i, i + 2), 16);
	}
	byte[] output = new byte[counter];
	System.arraycopy(tempOutput, 0, output, 0, counter);
	return output;
    }

    /**
     * Converts a byte to an unsigned integer.
     *
     * @param in
     * @return
     */
    public static int bToUInt(byte in) {
	return 0x000000FF & (int) in;
    }

    /**
     * Converts to int array
     *
     * @param in
     * @return
     */
    public static int[] toIntArray(byte[] in) {
	int[] out = new int[in.length];
	for (int i = 0; i < in.length; i++) {
	    out[i] = Ln.bToUInt(in[i]);
	}
	return out;
    }

    private static int[] toIntArray(Integer[] in) {
	int[] out = new int[in.length];
	for (int i = 0; i < in.length; i++) {
	    out[i] = ((int) in[i]);
	}
	return out;
    }

    /**
     *
     * @param input
     * @return
     */
    public static int[] toIntArray(String input) {
	int[] output = new int[input.length()];
	for (int i = 0; i < input.length(); i++) {
	    output[i] = (int) input.charAt(i);
	}
	return output;
    }

    /**
     *
     * @param input
     * @return
     */
    public static int[] toIntArray(int input) {
	return toIntArray((long) input);
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static int[] toIntArray(int input, int minLength, int maxLength) {
	return toIntArray((long) input, minLength, maxLength);
    }

    /**
     *
     * @param input
     * @param minLength
     * @return
     */
    public static int[] toIntArray(int input, int minLength) {
	return toIntArray((long) input, minLength, 0);
    }

    /**
     *
     * @param input
     * @return
     */
    public static int[] toIntArray(long input) {
	return toIntArray(input, 0, 0);
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static int[] toIntArray(long input, int minLength, int maxLength) {
	if (maxLength == 0) {
	    maxLength = 16;
	}
	int[] tmp = new int[maxLength];

	int counter = 0;
	for (int i = 0; i < tmp.length && input != 0; i++) {
	    tmp[i] = (int) (input % 256);
	    input = input / 256;
	    counter++;
	}
	if (counter < minLength) {
	    counter = minLength;
	} else if (counter == 0) {
	    return new int[1];
	}

	int[] output = new int[counter];
	System.arraycopy(tmp, 0, output, 0, counter);

	return output;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(int[] input) {
	byte[] out = new byte[input.length];
	for (int i = 0; i < input.length; i++) {
	    out[i] = (byte) input[i];
	}
	return out;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(int input) {
	return new byte[]{
		    (byte) (input), (byte) (input >>> 8),
		    (byte) (input >>> 16), (byte) (input >>> 24)};
    }

    /**
     *
     * @param input
     * @param size
     * @return
     */
    public static byte[] toByteArray(int input, int size) {
	byte[] out = new byte[size];
	for (int i = 0; i < size && i < 4; i++) {
	    out[i] = (byte) (input >>> (8 * i));
	}
	return out;
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static byte[] toByteArray(int input, int minLength, int maxLength) {
//	if (minLength == 4 && maxLength == 4) {
//	    return toByteArray(input);
//	} else {
	return toByteArray((long) input, minLength, maxLength);
//	}
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static byte[] toByteArray(long input, int minLength, int maxLength) {
	if (maxLength == 0) {
	    maxLength = 16;
	}
	byte[] tmp = new byte[maxLength];

	int counter = 0;
	for (int i = 0; i < tmp.length && input != 0; i++) {
	    tmp[i] = (byte) (input & 0xff);
	    input = input >> 8;
	    counter++;
	}
	if (counter < minLength) {
	    counter = minLength;
	} else if (counter == 0) {
	    return new byte[0];
	}

	byte[] output = new byte[counter];
	System.arraycopy(tmp, 0, output, 0, counter);

	return output;
    }

    /**
     * Converts string to a byte array with no null terminator.
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(String input) {
	byte[] output = new byte[input.length()];
	for (int i = 0; i < input.length(); i++) {
	    output[i] = (byte) input.charAt(i);
	}
	return output;
    }

    /**
     * Print nanoseconds to a m:s format.
     *
     * @param nanoseconds
     * @return
     */
    public static String nanoTimeString(long nanoseconds) {
	int seconds = (int) (nanoseconds * Math.pow(10, -3));
	int min = seconds / 60;
	seconds = seconds % 50;
	return min + "m:" + seconds + "s";
    }

    /**
     * Replaces the suffix with the desired suffix.
     *
     * @param input
     * @param type
     * @return
     */
    public static String changeFileTypeTo(String input, String type) {
	return input.substring(0, input.lastIndexOf(".") + 1) + type;
    }

    /**
     * Returns the greatest common denominator.
     *
     * @param a
     * @param b
     * @return
     */
    public static int gcd(int a, int b) {
	// Euclidean algorithm
	int t;
	while (b != 0) {
	    t = b;
	    b = a % b;
	    a = t;
	}
	return a;
    }

    public static int gcd(int ... nums) {
	if (nums.length == 1) {
	    return nums[0];
	} else if (nums.length == 2) {
	    return gcd(nums[0], nums[1]);
	} else {
	    int[] rest = Arrays.copyOfRange(nums, 1, nums.length);
	    return gcd(nums[0], gcd(rest));
	}
    }

    /**
     * Returns least common multiple
     *
     * @param a
     * @param b
     * @return
     */
    public static int lcm(int a, int b) {
	return (a * b / gcd(a, b));
    }

    /**
     * Returns least common multiple
     *
     * @param nums
     * @return
     */
    public static int lcmm(int... nums) {
	if (nums.length == 1) {
	    return nums[0];
	} else if (nums.length == 2) {
	    return lcm(nums[0], nums[1]);
	} else {
	    int[] rest = Arrays.copyOfRange(nums, 1, nums.length);
	    return lcm(nums[0], lcmm(rest));
	}
    }

    /**
     * Converts byte number to megabyte number
     *
     * @param numBytes
     * @return
     */
    public static long toMB(long numBytes) {
	return numBytes / 1048576;
    }

    /**
     * Converts byte number to kilobyte number
     *
     * @param numBytes
     * @return
     */
    public static long toKB(long numBytes) {
	return numBytes / 1024;
    }

    /**
     * Asks the user to locate a file, and saves the location for next time. On
     * second runthrough, the user will not be asked, and the backup file will
     * be used instead.
     *
     * @param fileMessageToAskUserFor
     * @param backupFileLocation
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File manualFindFile(String fileMessageToAskUserFor, File backupFileLocation) throws FileNotFoundException, IOException {
	// Check for save file
	if (backupFileLocation.isFile()) {
	    BufferedReader pluginLocation = new BufferedReader(new FileReader(backupFileLocation));
	    File savedFile = new File(pluginLocation.readLine());
	    pluginLocation.close();
	    if (savedFile.isFile()) {
		return savedFile;
	    }
	}

	// Open dialog box

	JOptionPane.showMessageDialog(null, "The application is having trouble locating: " + fileMessageToAskUserFor + "\n"
		+ "Please locate this yourself.");

	JFileChooser fd = new JFileChooser(".");
	int returnVal = fd.showOpenDialog(null);
	File fileLocation = null;
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    // Save file location
	    fileLocation = fd.getSelectedFile();
	    BufferedWriter pluginLocation = new BufferedWriter(new FileWriter(backupFileLocation));
	    pluginLocation.write(fileLocation.getPath());
	    pluginLocation.close();
	}
	return fileLocation;
    }

    /**
     * Opens a file dialog for user to pick files.
     *
     * @return Files user picked.
     */
    public static File[] fileDialog() {
	JFileChooser fd = new JFileChooser(".");
	fd.setMultiSelectionEnabled(true);
	File[] fileLocation = new File[0];
	if (fd.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	    // Save file location
	    fileLocation = fd.getSelectedFiles();
	}
	return fileLocation;
    }

    /**
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
	if (!destFile.exists()) {
	    destFile.createNewFile();
	}

	FileChannel source = null;
	FileChannel destination = null;
	try {
	    source = new FileInputStream(sourceFile).getChannel();
	    destination = new FileOutputStream(destFile).getChannel();
	    destination.transferFrom(source, 0, source.size());
	} finally {
	    if (source != null) {
		source.close();
	    }
	    if (destination != null) {
		destination.close();
	    }
	}
    }

    /**
     *
     * @param sourceFile
     * @param destDir
     * @throws IOException
     */
    public static void copyFileToDir(File sourceFile, File destDir) throws IOException {
	File to = new File(destDir.getPath() + "\\" + sourceFile.getName());
	copyFile(sourceFile, to);
    }

    /**
     * A simple comparison function that compares two files byte by byte, and
     * reports the positions of any differences (eg. file1[i] != file2[i]).
     *
     * @param testFile File to test to keyFile.
     * @param keyFile Validation file to be used as a desired example.
     * @param numErrorsToPrint Number of differences to print.
     * @return True if files matched with NO differences.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean validateCompare(File testFile, File keyFile, int numErrorsToPrint) throws FileNotFoundException, IOException {
	boolean print = numErrorsToPrint != 0;
	if (keyFile.isFile() && testFile.isFile()) {

	    LInChannel keyIn = new LInChannel(keyFile);
	    LInChannel testIn = new LInChannel(testFile);

	    if (numErrorsToPrint == 0 && keyIn.available() != testIn.available()) {
		keyIn.close();
		testIn.close();
		return false;
	    }

	    byte[] keyArray = keyIn.extract(0, keyIn.available());
	    byte[] testArray = testIn.extract(0, testIn.available());
	    keyIn.close();
	    testIn.close();

	    Boolean passed = true;
	    for (int i = 0; i < keyArray.length && i < testArray.length; i++) {
		if (keyArray[i] != testArray[i]) {
		    if (print) {
			System.out.println("Files differed at " + Ln.prettyPrintHex(i));
		    }
		    passed = false;
		    if (--numErrorsToPrint == 0) {
			break;
		    }
		}
	    }
	    if (passed) {
		if (print) {
		    System.out.println("Files matched.");
		}
		return true;
	    } else {
		if (print) {
		    System.out.println("Files did NOT match.");
		}
		return false;
	    }
	}
	if (print) {
	    System.out.println("Validator could not locate both files (" + testFile + "," + keyFile + ")");
	}
	return false;
    }

    /**
     * A simple comparison function that compares two files byte by byte, and
     * reports the positions of any differences (eg. file1[i] != file2[i]).
     *
     * @param testFile File to test to keyFile.
     * @param keyFile Validation file to be used as a desired example.
     * @param numErrorsToPrint Number of differences to print.
     * @return True if files matched with NO differences.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static boolean validateCompare(String testFile, String keyFile, int numErrorsToPrint) throws FileNotFoundException, IOException {
	return validateCompare(new File(testFile), new File(keyFile), numErrorsToPrint);
    }

    /**
     *
     * @param parent
     * @param child
     * @return True if child is a descendant of parent
     */
    public static boolean isDescendant(TreePath parent, TreePath child) {
	int count1 = parent.getPathCount();
	int count2 = child.getPathCount();
	if (count1 <= count2) {
	    return false;
	}
	while (count1 != count2) {
	    parent = parent.getParentPath();
	    count1--;
	}
	return parent.equals(child);
    }

    /**
     * Returns a string representation of the tree's expansion state. To be used
     * to revert back to it later.
     *
     * @param tree
     * @param row
     * @return
     */
    public static String getExpansionState(JTree tree, int row) {
	TreePath rowPath = tree.getPathForRow(row);
	StringBuilder buf = new StringBuilder();
	int rowCount = tree.getRowCount();
	for (int i = row; i < rowCount; i++) {
	    TreePath path = tree.getPathForRow(i);
	    if (i == row || isDescendant(path, rowPath)) {
		if (tree.isExpanded(path)) {
		    buf.append(",").append(String.valueOf(i - row));
		}
	    } else {
		break;
	    }
	}
	return buf.toString();
    }

    /**
     * Restores a tree's expansion state to a previously saved string
     * representation.
     *
     * @param tree
     * @param row
     * @param expansionState
     */
    public static void restoreExpanstionState(JTree tree, int row, String expansionState) {
	StringTokenizer stok = new StringTokenizer(expansionState, ",");
	while (stok.hasMoreTokens()) {
	    int token = row + Integer.parseInt(stok.nextToken());
	    tree.expandRow(token);
	}
    }

    /**
     * A list of all the class files in a jar.
     *
     * @param jarPath
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static ArrayList<String> getClasses(File jarPath) throws FileNotFoundException, IOException {
	JarEntry jarEntry;
	JarInputStream jarFile = new JarInputStream(new FileInputStream(jarPath));
	ArrayList<String> out = new ArrayList<>();
	String name;
	while ((jarEntry = jarFile.getNextJarEntry()) != null) {
	    name = jarEntry.getName();
	    if (name.endsWith(".class") && !name.contains("$")) {
		name = name.substring(0, name.indexOf(".class"));
		name = name.replaceAll("/", ".");
		out.add(name);
	    }
	}
	return out;
    }

    /**
     * loads all classes in a jar. Optionally can skip any that throw
     * exceptions.
     *
     * @param jarPath
     * @param skipBad
     * @return
     * @throws MalformedURLException
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ArrayList<Class> loadClasses(File jarPath, boolean skipBad) throws MalformedURLException, FileNotFoundException, IOException, ClassNotFoundException {
	ArrayList<String> classPaths = getClasses(jarPath);
	ArrayList<Class> out = new ArrayList<>(classPaths.size());
	ClassLoader loader = new URLClassLoader(new URL[]{jarPath.toURI().toURL()});
	for (String s : classPaths) {
	    try {
    		out.add(loader.loadClass(s));
	    } catch (Throwable ex) {
		if (!skipBad) {
		    throw ex;
		}
	    }
	}
	return out;
    }

    /**
     * Exports contents of a stream to a string.
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String convertStreamToStr(InputStream is) throws IOException {

	if (is != null) {
	    Writer writer = new StringWriter();

	    char[] buffer = new char[1024];
	    try {
		Reader reader = new BufferedReader(new InputStreamReader(is,
			"UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
		    writer.write(buffer, 0, n);
		}
	    } finally {
		is.close();
	    }
	    return writer.toString();
	} else {
	    return "";
	}
    }


    /**
     * Opens a file and loads in each line.
     *
     * @param f
     * @param toUpper
     * @return An ArrayList of all the text lines in the file.
     * @throws IOException
     */
    public static ArrayList<String> loadFileToStrings(File f, boolean toUpper) throws IOException {
	ArrayList out = new ArrayList<>();
	BufferedReader in;
	in = new BufferedReader(new FileReader(f));
	String line;
	while ((line = in.readLine()) != null) {
	    if (toUpper) {
		line = line.toUpperCase();
	    }
	    out.add(line);
	}
	in.close();
	return out;
    }

    /**
     * Opens a file and loads in each line.
     *
     * @param path
     * @param toUpper
     * @return An ArrayList of all the text lines in the file.
     * @throws IOException
     */
    public static ArrayList<String> loadFileToStrings(String path, boolean toUpper) throws IOException {
	return loadFileToStrings(new File(path), toUpper);
    }

    /**
     *
     * @param path
     * @param strs
     * @throws IOException
     */
    public static void writeStringsToFile(String path, ArrayList<String> strs) throws IOException {
	File file = new File(path);
	Ln.makeDirs(file);
	BufferedWriter out = new BufferedWriter(new FileWriter(file));
	for (String s : strs) {
	    out.write(s + "\n");
	}
	out.close();
    }

    /**
     * Makes every item in the ArrayList uppercase
     *
     * @param in
     * @return
     */
    public static ArrayList<String> toUpper(ArrayList<String> in) {
	for (int i = 0; i < in.size(); i++) {
	    in.set(i, in.get(i).toUpperCase());
	}
	return in;
    }

    /**
     *
     * @param in
     * @return
     */
    public static String[] toUpper (String[] in) {
	for (int i = 0 ; i < in.length ; i++) {
	    in[i] = in[i].toUpperCase();
	}
	return in;
    }

    /**
     *
     * @param arr
     * @param target
     * @return
     */
    public static String get(String[] arr, String target) {
	target = target.toUpperCase();
	for (String s : arr) {
	    if (target.equals(s.toUpperCase())) {
		return s;
	    }
	}
	return null;
    }

    /**
     * Checks string to see if it contains any of the keywords in the arraylist.
     *
     * @param target
     * @param keywords
     * @return
     */
    public static boolean hasAnyKeywords(String target, ArrayList<String> keywords) {
	target = target.toUpperCase();
	for (String s : keywords) {
	    if (target.contains(s.toUpperCase())) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Replaces "\\" with "/"
     *
     * @param filePath
     * @return
     */
    public static String standardizeFilePath(String filePath) {
	return filePath.replace("\\", "/");
    }

    /**
     * Returns the first index that has a string containing s.
     * @param list
     * @param s
     * @return
     */
    public static int indexOfContains(ArrayList<String> list, String s) {
	for (int i = 0 ; i < list.size() ; i++) {
	    if (list.get(i).contains(s)) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * True if list has a string that contains s.
     * @param list
     * @param s
     * @return
     */
    public static boolean contains(ArrayList<String> list, String s) {
	return -1 != indexOfContains(list, s);
    }
    
    public static boolean containsIgnoreCase(ArrayList<String> list, String s) {
        return -1 != indexOfContains(Ln.toUpper(new ArrayList<>(list)), s.toUpperCase());
    }

    /**
     * True if list has a string equaling s, ignoring case.
     * @param list
     * @param s
     * @return
     */
    public static boolean containsEqualsIgnoreCase(ArrayList<String> list, String s) {
	return -1 != indexOfIgnoreCase(list, s);
    }

    /**
     * A contains() check that's case insensitive.
     *
     * @param list
     * @param s
     * @return
     */
    public static int indexOfIgnoreCase(ArrayList<String> list, String s) {
	for (int i = 0 ; i < list.size() ; i++) {
	    if (list.get(i).equalsIgnoreCase(s)) {
		return i;
	    }
	}
	return -1;
    }

    /**
     *
     * @param list
     * @param s
     * @return
     */
    public static boolean removeIgnoreCase(ArrayList<String> list, String s) {
	int index = indexOfIgnoreCase(list, s);
	if (index != -1) {
	    list.remove(index);
	    return true;
	}
	return false;
    }

    /**
     * Gets a string containing N number of String s.
     * @param n
     * @param s
     * @return
     */
    public static String getNAmount(int n, String s) {
	String out = "";
	for (int i = 0; i < n; i++) {
	    out += s;
	}
	return out;
    }
}
