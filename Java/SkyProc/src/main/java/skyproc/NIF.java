/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import lev.LInChannel;
import lev.LPair;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;

/**
 * An object that parses and represents a NIF object. Can be queried for its
 * contents. NOTE: This is a rough/incomplete object. Expect deficiencies.
 *
 * @author Justin Swanson
 */
public class NIF {

    private static String header = "NIF";
    String fileName;
    int numBlocks;
    ArrayList<String> blockTypes;
    ArrayList<Node> nodes;
    long headerOffset;

    /**
     * Loads in a nif file from the specified file. Exceptions can be thrown if
     * the file specified is not a nif file, or malformed.
     *
     * @param f File/path to load nif from.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BadParameter If the nif file is malformed (by SkyProc's
     * standards)
     */
    public NIF(File f) throws FileNotFoundException, IOException, BadParameter {
	LInChannel in = new LInChannel(f);
	fileName = f.getPath();
	parseData(new LShrinkArray(in.extractByteBuffer(0, in.available())));
    }

    /**
     * Creates a NIF object from the given ShrinkArray. Throws exceptions if the
     * data is not a proper nif file.
     *
     * @param filename Name to give the NIF object.
     * @param in Nif data to parse and load into the NIF object.
     * @throws BadParameter If the data given to parse is malformed (by
     * SkyProc's standards)
     */
    public NIF(String filename, LShrinkArray in) throws BadParameter {
	this.fileName = filename;
	parseData(in);
    }

    final void parseData(LShrinkArray in) throws BadParameter {
	loadHeader(in);
    }

    void loadHeader(LShrinkArray in) throws BadParameter {

	//Gamebryo header
	if (SPGlobal.debugNIFimport) {
	    SPGlobal.logSync(header, "Loading nif file");
	}
	if (!in.getString(20).equals("Gamebryo File Format")) {
	    byte first = in.extract(1)[0];
	    if (!in.extractString((int) first, 20).equals("Gamebryo File Format")) {
		throw new BadParameter(fileName + " was not a NIF file.");
	    }
	}

	in.extractLine();

	//BlockTypes
	numBlocks = in.extractInt(9, 4);
	if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Num Blocks: " + numBlocks);
	}
	in.skip(in.extractInt(4, 1)); // Author name
	in.skip(in.extractInt(1)); // Export Info 1
	in.skip(in.extractInt(1)); // Export Info 2
	int numBlockTypes = in.extractInt(2);
	if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Num Block Types: " + numBlockTypes);
	}
	blockTypes = new ArrayList<>(numBlockTypes);
	for (int i = 0; i < numBlockTypes; i++) {
	    String blockType = in.extractString(in.extractInt(4));
	    blockTypes.add(blockType);
	    if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
		SPGlobal.logSync(header, "  Added block type[" + i + "]: " + blockType);
	    }
	}


	//Blocks list
	if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Block Type list: ");
	}
	nodes = new ArrayList<>(numBlocks);
	for (int i = 0; i < numBlocks; i++) {
	    int type = in.extractInt(2);
	    Node n = new Node(NodeType.SPvalueOf(blockTypes.get(type)));
	    n.number = i;
	    nodes.add(n);
	    if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
		SPGlobal.logSync(header, "  Block list[" + i + "] has block type: " + type + ", " + blockTypes.get(type));
	    }
	}

	//Block lengths
	for (int i = 0; i < numBlocks; i++) {
	    nodes.get(i).size = in.extractInt(4);
	}

	if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Block headers: ");
	    for (int i = 0; i < numBlocks; i++) {
		SPGlobal.logSync(header, "  [" + i + "]: " + nodes.get(i).type + ", length: " + Ln.prettyPrintHex(nodes.get(i).size));
	    }
	}

	//Strings
	if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
	    SPGlobal.logSync(header, "Block Titles: ");
	}
	int numStrings = in.extractInt(4);
	in.skip(4); // max Length string
	ArrayList<String> strings = new ArrayList<>(numStrings);
	for (int i = 0; i < numStrings; i++) {
	    strings.add(in.extractString(in.extractInt(4)));
	}
	in.skip(4); // unknown int

	for (int i = 0; i < numBlocks; i++) {
	    nodes.get(i).data = new LShrinkArray(in, nodes.get(i).size);
	    in.skip(nodes.get(i).size);
	}

	//Set titles
	for (int i = 0; i < numBlocks; i++) {
	    NodeType type = nodes.get(i).type;
	    if (type == NodeType.NINODE
		    || type == NodeType.NITRISHAPE
		    || type == NodeType.BSINVMARKER
		    || type == NodeType.BSBEHAVIORGRAPHEXTRADATA) {
		Node n = nodes.get(i);
		int stringIndex = n.data.getInts(0, 4)[0];
		n.title = strings.get(stringIndex);
		if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
		    SPGlobal.log(header, "  [" + i + "]: " + nodes.get(i).type + ", string: " + nodes.get(i).title);
		}
	    }
	}
    }

    /**
     * A single Node and its data in the nif file.
     */
    public class Node {

	/**
	 * Title assigned to the node.
	 */
	public String title;
	/**
	 * Type of node.
	 */
	public NodeType type;
	int size;
	/**
	 * Raw data contained in the node.
	 */
	public LShrinkArray data;
	/**
	 * Node index in the nif
	 */
	public int number;

	Node(NodeType n) {
	    type = n;
	}

	Node(Node in) {
	    this.title = in.title;
	    this.type = in.type;
	    this.size = in.size;
	    this.data = new LShrinkArray(in.data);
	    this.number = in.number;
	}
    }

    /**
     *
     */
    public enum NodeType {

	/**
	 *
	 */
	NINODE,
	/**
	 *
	 */
	BSBEHAVIORGRAPHEXTRADATA,
	/**
	 *
	 */
	NICONTROLLERSEQUENCE,
	/**
	 *
	 */
	BSINVMARKER,
	/**
	 *
	 */
	NITRISHAPE,
	/**
	 *
	 */
	NITRISHAPEDATA,
	/**
	 *
	 */
	NISKININSTANCE,
	/**
	 *
	 */
	NISKINDATA,
	/**
	 *
	 */
	NISKINPARTITION,
	/**
	 *
	 */
	BSLIGHTINGSHADERPROPERTY,
	/**
	 *
	 */
	BSEFFECTSHADERPROPERTY,
	/**
	 *
	 */
	BSSHADERTEXTURESET,
	/**
	 *
	 */
	NIALPHAPROPERTY,
	/**
	 *
	 */
	UNKNOWN;

	static NodeType SPvalueOf(String in) {
	    try {
		return valueOf(in.toUpperCase());
	    } catch (IllegalArgumentException e) {
		return UNKNOWN;
	    }
	}
    }

    /**
     *
     * @return List of the node types in the nif file, in order.
     */
    public ArrayList<NodeType> getNodeTypes() {
	ArrayList<NodeType> out = new ArrayList<>(nodes.size());
	for (int i = 0; i < nodes.size(); i++) {
	    out.add(nodes.get(i).type);
	}
	return out;
    }

    /**
     *
     * @param i
     * @return The ith node in the NIF object.
     */
    public Node getNode(int i) {
	return new Node(nodes.get(i));
    }

    /**
     *
     * @param i
     * @return The title of the ith node in the NIF object.
     */
    public String getNodeTitle(int i) {
	return nodes.get(i).title;
    }

    /**
     *
     * @param type Type to retrieve.
     * @return Map of all the Node objects matching the given type, with their
     * node index as keys.
     */
    public Map<Integer, Node> getNodes(NodeType type) {
	Map<Integer, Node> out = new TreeMap<>();
	String name = "";
	for (int i = 0; i < nodes.size(); i++) {
	    Node n = getNode(i);
	    if (n.type == NodeType.NITRISHAPE) {

		name = n.title;
	    }
	    if (n.type == type) {
		if (n.title == null) {
		    n.title = name;
		}
		out.put(n.number, n);
	    }
	}
	return out;
    }

    /**
     * A special function that returns sets of nodes each relating to a
     * NiTriShape package. This return a list of lists containing a NiTriShape
     * and all the nodes following up until another NiTriShape is encountered,
     * which will start another list.
     *
     * @return List of NiTriShape node sets.
     */
    public ArrayList<ArrayList<Node>> getNiTriShapePackages() {
	ArrayList<ArrayList<Node>> out = new ArrayList<>();
	ArrayList<Node> NiTriShapePackage = new ArrayList<>();
	boolean on = false;
	String title = "";
	for (Node n : nodes) {
	    if (n.type == NodeType.NITRISHAPE) {
		NiTriShapePackage = new ArrayList<>();
		out.add(NiTriShapePackage);
		title = n.title;
		NiTriShapePackage.add(n);
		on = true;
	    } else if (on) {
		n.title = title;
		NiTriShapePackage.add(n);
	    }
	}
	return out;
    }

    /**
     *
     * @return A map with node index numbers as key, and Pairs of Node Name +
     * List of textures as value.
     */
    public Map<Integer, LPair<String, ArrayList<String>>> extractTextures() {
	Map<Integer, LPair<String, ArrayList<String>>> out = new HashMap<>();
	Map<Integer, NIF.Node> BiLightingShaderProperties = getNodes(NIF.NodeType.BSLIGHTINGSHADERPROPERTY);
	Map<Integer, NIF.Node> BiShaderTextureNodes = getNodes(NIF.NodeType.BSSHADERTEXTURESET);
	Map<Integer, ArrayList<String>> BiShaderTextureSets = new HashMap<>();


	for (Integer i : BiShaderTextureNodes.keySet()) {
	    BiShaderTextureSets.put(i, NIF.extractBSTextures(BiShaderTextureNodes.get(i)));
	}

	Map<Integer, NIF.Node> together = new TreeMap<>();
	together.putAll(BiLightingShaderProperties);
	together.putAll(getNodes(NIF.NodeType.BSEFFECTSHADERPROPERTY));

	int i = 0;
	for (Integer key : together.keySet()) {
	    // Only care if it's a texture set, but must still increment index if it's effects
	    if (BiLightingShaderProperties.containsKey(key)) {
		String name = BiLightingShaderProperties.get(key).title;
		int textureLink = BiLightingShaderProperties.get(key).data.extractInt(40, 4);
		ArrayList<String> textures = BiShaderTextureSets.get(textureLink);
		if (textures != null) {
		    LPair pair = new LPair(name, new ArrayList<>(textures));
		    out.put(i, pair);
		}
	    }
	    i++;
	}

	return out;
    }

    /**
     * 
     * @return
     */
    public ArrayList<TextureSet> extractTextureSets() {
	Map<Integer, LPair<String, ArrayList<String>>> data = extractTextures();
	ArrayList<TextureSet> out = new ArrayList<>(data.size());
	for (Integer i : data.keySet()) {
	    out.add(new TextureSet(i, data.get(i)));
	}
	return out;
    }

    /**
     *
     * @param n Node to extract texture names from. Must be a valid
     * BSShaderTextureSet node or the function will fail.
     * @return List of the textures in the node.
     */
    public static ArrayList<String> extractBSTextures(Node n) {
	int numTextures = n.data.extractInt(4);
	ArrayList<String> maps = new ArrayList<>(numTextures);
	for (int i = 0; i < numTextures; i++) {
	    maps.add(n.data.extractString(n.data.extractInt(4)));
	}
	return maps;
    }

    /**
     * Represents a set of textures on a node of a nif file.
     */
    public static class TextureSet {

	int index;
	String name;
	ArrayList<String> textures;

	/**
	 * 
	 * @param i
	 * @param data
	 */
	public TextureSet(Integer i, LPair<String, ArrayList<String>> data) {
	    this(i, data.a, data.b);
	}

	/**
	 * 
	 * @param i
	 * @param name
	 * @param tex
	 */
	public TextureSet(Integer i, String name, ArrayList<String> tex) {
	    index = i;
	    this.name = name;
	    textures = tex;
	}

	/**
	 * 
	 * @param rhs
	 */
	public TextureSet(TextureSet rhs) {
	    index = rhs.index;
	    name = rhs.name;
	    textures = new ArrayList<>(rhs.textures);
	}

	/**
	 * 
	 * @return
	 */
	public int getIndex() {
	    return index;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
	    return name;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getTextures() {
	    return textures;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String toString() {
	    return name;
	}
    }
}
