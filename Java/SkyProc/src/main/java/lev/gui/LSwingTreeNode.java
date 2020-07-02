/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Justin Swanson
 */
public class LSwingTreeNode extends DefaultMutableTreeNode {

    /**
     *
     * @param node
     * @return True if children contains a node equal to the parameter.
     */
    public boolean contains(LSwingTreeNode node) {
	return (get(node) != null);
    }

    /**
     * Recursively searches the node's children for one equal to the input and
     * returns it.
     *
     * @param node
     * @return
     */
    public LSwingTreeNode get(LSwingTreeNode node) {
	if (this.children != null) {
	    for (Object rhs : this.children) {
		if (node.equals(rhs)) {
		    return (LSwingTreeNode) rhs;
		}
	    }
	}
	return null;
    }

    /**
     *
     * @return
     */
    public ArrayList<LSwingTreeNode> getAllObjects() {
	return getAllObjects(false);
    }

    /**
     *
     * @param recursive
     * @return
     */
    public ArrayList<LSwingTreeNode> getAllObjects(boolean recursive) {
	ArrayList<LSwingTreeNode> out = new ArrayList<>();
	if (children != null) {
	    for (Object o : children) {
		LSwingTreeNode n = (LSwingTreeNode) o;
		out.add(n);
		if (recursive) {
		    out.addAll(n.getAllObjects(recursive));
		}
	    }
	}
	return out;
    }

    /**
     *
     * @param depth
     */
    public void print(int depth) {
	for (Object o : getAllObjects()) {
	    if (o instanceof LSwingTreeNode) {
		LSwingTreeNode n = (LSwingTreeNode) o;
		n.print(depth + 1);
	    }
	}
    }

    /**
     * Adds the node to the children of this current node.  If the parameter node
     * exists already, it merges their children.
     * @param node Node to merge into the current node
     */
    public void mergeIn(LSwingTreeNode node) {
	LSwingTreeNode existing = get(node);
	if (existing != null) {
	    for (Object o : node.getAllObjects()) {
		if (o instanceof LSwingTreeNode) {
		    existing.mergeIn((LSwingTreeNode) o);
		}
	    }
	} else {
	    this.add(node);
	}
    }
}
