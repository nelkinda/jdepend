package jdepend.swingui;

import java.util.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 * The <code>DependTreeModel</code> class defines the data model being
 * observed by a <code>DependTree</code> instance.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class DependTreeModel implements TreeModel {

    private final List<TreeModelListener> listeners = new ArrayList<>();
    private final PackageNode root;

    /**
     * Constructs a <code>DependTreeModel</code> with the specified root
     * package node.
     * 
     * @param root Root package node.
     */
    public DependTreeModel(final PackageNode root) {
        this.root = root;
    }

    /**
     * Returns the root of the tree.
     * 
     * @return The root of the tree, or <code>null</code> if the tree has no
     *         nodes.
     */
    public Object getRoot() {
        return root;
    }

    /**
     * Returns the child of the specified parent at the specified index in the
     * parent's child collection.
     * <p>
     * The specified parent must be a node previously obtained from this data
     * source.
     * 
     * @param parent A node in the tree, obtained from this data source.
     * @param index Index of child in the parent's child collection.
     * @return Child.
     */
    public Object getChild(final Object parent, final int index) {
        if (parent instanceof PackageNode) {
            final List<PackageNode> children = ((PackageNode) parent).getChildren();
            if (index < children.size()) {
                return children.get(index);
            }
        }
        return null;
    }

    /**
     * Returns the number of children for the specified parent.
     * <p>
     * The specified parent must be a node previously obtained from this data
     * source.
     * 
     * @param parent A node in the tree, obtained from this data source.
     * @return The number of children of the specified parent, or 0 if the
     *         parent is a leaf node or if it has no children.
     */
    public int getChildCount(final Object parent) {
        return parent instanceof PackageNode ? ((PackageNode) parent).getChildren().size() : 0;
    }

    /**
     * Determines whether the specified tree node is a leaf node.
     * 
     * @param o A node in the tree, obtained from this data source.
     * @return <code>true</code> if the node is a leaf; <code>false</code>
     *         otherwise.
     */
    public boolean isLeaf(final Object o) {
        return !(o instanceof PackageNode) || ((PackageNode) o).isLeaf();
    }

    /**
     * Callback method triggered when the value for the item specified by
     * <i>path </i> has changed to <i>newValue </i>.
     * 
     * @param path Path to the node that has changed.
     * @param newValue The new value of the node.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // do nothing
    }

    /**
     * Returns the index of the specified child within the specified parent.
     * 
     * @param parent Parent node.
     * @param child Child node.
     * @return Index of child within parent.
     */
    public int getIndexOfChild(Object parent, Object child) {
        if (parent instanceof PackageNode) {
            final List<PackageNode> children = ((PackageNode) parent).getChildren();
            return children.indexOf(child);
        }
        return -1;
    }

    /**
     * Adds a listener for the <code>TreeModelEvent</code> posted after the
     * tree changes.
     * 
     * @param l The listener to add.
     */
    public void addTreeModelListener(final TreeModelListener l) {
        listeners.add(l);
    }

    /**
     * Removes a listener for <code>TreeModelEvent</code>s.
     * 
     * @param l The listener to remove.
     */
    public void removeTreeModelListener(final TreeModelListener l) {
        listeners.remove(l);
    }
}

