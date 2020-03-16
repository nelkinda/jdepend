package jdepend.swingui;

import java.util.*;

import jdepend.framework.*;

/**
 * The <code>EfferentNode</code> class is a <code>PackageNode</code> for an
 * efferent Java package and its efferent packages.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class EfferentNode extends PackageNode {

    /**
     * Constructs an <code>EfferentNode</code> with the specified parent node
     * and efferent Java package.
     * 
     * @param parent Parent package node.
     * @param javaPackage Efferent Java package.
     */
    public EfferentNode(final PackageNode parent, final JavaPackage javaPackage) {
        super(parent, javaPackage);
    }

    /**
     * Creates and returns a <code>PackageNode</code> with the specified
     * parent node and Java package.
     * 
     * @param parent Parent package node.
     * @param javaPackage Java package.
     * @return A non-null <code>PackageNode</code>.
     */
    @Override
    protected PackageNode makeNode(final PackageNode parent, final JavaPackage javaPackage) {
        return new EfferentNode(parent, javaPackage);
    }

    /**
     * Returns the collection of Java packages coupled to the package
     * represented in this node.
     * 
     * @return Collection of coupled packages.
     */
    @Override
    protected Collection<JavaPackage> getCoupledPackages() {
        return getPackage().getEfferents();
    }

    /**
     * Indicates whether the specified package should be displayed as a child of
     * this node.
     *
     * <p>* Efferent packages without classes are never shown at the root level to
     * exclude non-analyzed packages.
     * 
     * @param javaPackage Package to test.
     * @return <code>true</code> to display the package; <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean isChild(final JavaPackage javaPackage) {
        return getParent() != null || javaPackage.getClassCount() > 0;
    }

    /**
     * Returns the string representation of this node in it's current tree
     * context.
     * 
     * @return Node label.
     */
    @Override
    public String toString() {
        if (getParent() == null) {
            return "Depends Upon - Efferent Dependencies" + " (" + getChildren().size() + " Packages)";
        }
        return super.toString();
    }
}

