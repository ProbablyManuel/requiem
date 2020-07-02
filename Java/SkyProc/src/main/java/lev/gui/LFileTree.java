/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import lev.Ln;

/**
 * A structure to store and print file / directory contents.
 * @author Justin Swanson
 */
public class LFileTree {

    /**
     *
     */
    protected DefaultTreeModel tree;
    /**
     *
     */
    protected DefaultMutableTreeNode root;

    /**
     *
     */
    public LFileTree() {
	root = new DefaultMutableTreeNode("");
	tree = new DefaultTreeModel(root);
    }

    /**
     *
     * @param f
     */
    public void addFile(File f) {
	addFile(f.getPath());
    }

    /**
     *
     * @param path
     */
    public void addFile(String path) {
	ArrayList<String> list = new ArrayList<>();
	path = Ln.standardizeFilePath(path);
	list.addAll(Arrays.asList(path.split("/")));
	addFile(root, list);
    }

    void addFile(DefaultMutableTreeNode node, ArrayList<String> path) {
	if (path.isEmpty()) {
	    return;
	}
	String target = path.get(0);
	path.remove(0);
	for (Enumeration e = node.children(); e.hasMoreElements();) {
	    DefaultMutableTreeNode nodeC = (DefaultMutableTreeNode) e.nextElement();
	    if (nodeC.toString().equalsIgnoreCase(target)) {
		addFile(nodeC, path);
		return;
	    }
	}

	//Doesn't exist
	DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(target);
	node.add(newNode);
	addFile(newNode, path);
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getList() {
	ArrayList<String> out = new ArrayList<>();
	for (Enumeration e = root.depthFirstEnumeration(); e.hasMoreElements();) {
	    DefaultMutableTreeNode nodeC = (DefaultMutableTreeNode) e.nextElement();
	    out.add(nodeC.toString());
	}
	return out;
    }

    /**
     *
     * @param fluff
     * @return
     */
    public String print(String fluff) {
	String out = "";
	for (Enumeration e = root.children(); e.hasMoreElements();) {
	    DefaultMutableTreeNode nodeC = (DefaultMutableTreeNode) e.nextElement();
	    out += print(nodeC, 0, fluff);
	}
	return out;
    }

    String print(DefaultMutableTreeNode node, int depth, String fluff) {
	String out = "";
	for (int i = 0 ; i < depth ; i++) {
	    out += fluff;
	}
	out += node.toString();
	if (!node.isLeaf()) {
	    out += "/";
	}
	out += "\n";
	depth++;
	for (Enumeration e = node.children(); e.hasMoreElements();) {
	    DefaultMutableTreeNode nodeC = (DefaultMutableTreeNode) e.nextElement();
	    out += print(nodeC, depth, fluff);
	}
	return out;
    }
}
