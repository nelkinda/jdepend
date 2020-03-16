package jdepend.swingui;

import jdepend.framework.JavaPackage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The <code>PackageNode</code> class defines the default behavior for tree
 * nodes representing Java packages.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public abstract class PackageNode {
    private static final NumberFormat formatter;

    static {
        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
    }

    private final PackageNode parent;
    private final JavaPackage javaPackage;

    private List<PackageNode> children;

    /**
     * Constructs a <code>PackageNode</code> with the specified package and
     * its collection of dependent packages.
     *
     * @param parent      Parent package node.
     * @param javaPackage Java package.
     */
    public PackageNode(final PackageNode parent, final JavaPackage javaPackage) {
        this.parent = parent;
        this.javaPackage = javaPackage;
        children = null;
    }

    /*
     * Returns the specified number in a displayable format. @param number
     * Number to format. @return Formatted number.
     */
    private static String format(final float f) {
        return formatter.format(f);
    }

    /**
     * Returns the Java package represented in this node.
     *
     * @return Java package.
     */
    public JavaPackage getPackage() {
        return javaPackage;
    }

    /**
     * Returns the parent of this package node.
     *
     * @return Parent package node.
     */
    public PackageNode getParent() {
        return parent;
    }

    /**
     * Indicates whether this node is a leaf node.
     *
     * @return <code>true</code> if this node is a leaf; <code>false</code> otherwise.
     */
    public boolean isLeaf() {
        return getCoupledPackages().isEmpty();
    }

    /**
     * Creates and returns a <code>PackageNode</code> with the specified
     * parent node and Java package.
     *
     * @param parent      Parent package node.
     * @param javaPackage Java package.
     * @return A non-null <code>PackageNode</code>.
     */
    protected abstract PackageNode makeNode(PackageNode parent, JavaPackage javaPackage);

    /**
     * Returns the collection of Java packages coupled to the package
     * represented in this node.
     *
     * @return Collection of coupled packages.
     */
    protected abstract Collection<JavaPackage> getCoupledPackages();

    /**
     * Indicates whether the specified package should be displayed as a child of
     * this node.
     *
     * @param javaPackage Package to test.
     * @return <code>true</code> to display the package; <code>false</code> otherwise.
     */
    public boolean isChild(final JavaPackage javaPackage) {
        return true;
    }

    /**
     * Returns the child package nodes of this node.
     *
     * @return Collection of child package nodes.
     */
    public List<PackageNode> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            final List<JavaPackage> packages = new ArrayList<>(getCoupledPackages());
            packages.sort(JavaPackage.byName);
            for (final JavaPackage jPackage : packages) {
                if (isChild(jPackage)) {
                    final PackageNode childNode = makeNode(this, jPackage);
                    children.add(childNode);
                }
            }
        }

        return children;
    }

    /**
     * Returns the string representation of this node's metrics.
     *
     * @return Metrics string.
     */
    public String toMetricsString() {
        final StringBuilder label = new StringBuilder();
        label.append(getPackage().getName());
        label.append("  (");
        label.append("CC: " + getPackage().getConcreteClassCount() + "  ");
        label.append("AC: " + getPackage().getAbstractClassCount() + "  ");
        label.append("Ca: " + getPackage().afferentCoupling() + "  ");
        label.append("Ce: " + getPackage().efferentCoupling() + "  ");
        label.append("A: " + format(getPackage().abstractness()) + "  ");
        label.append("I: " + format(getPackage().instability()) + "  ");
        label.append("D: " + format(getPackage().distance()) + "  ");
        label.append("V: " + getPackage().getVolatility());
        if (getPackage().containsCycle()) {
            label.append(" Cyclic");
        }

        label.append(")");

        return label.toString();
    }

    /**
     * Returns the string representation of this node in it's current tree
     * context.
     *
     * @return Node label.
     */
    public String toString() {
        if (getParent().getParent() == null) {
            return toMetricsString();
        }
        return getPackage().getName();
    }
}

