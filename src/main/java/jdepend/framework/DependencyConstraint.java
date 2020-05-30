package jdepend.framework;

import java.util.*;

/**
 * The <code>DependencyConstraint</code> class is a constraint that tests
 * whether two package-dependency graphs are equivalent.
 * <p>
 * This class is useful for writing package dependency assertions (e.g. JUnit).
 * For example, the following JUnit test will ensure that the 'ejb' and 'web'
 * packages only depend upon the 'util' package, and no others:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * public void testDependencyConstraint() {
 * 
 *     JDepend jdepend = new JDepend();
 *     jdepend.addDirectory(&quot;/path/to/classes&quot;);
 *     Collection analyzedPackages = jdepend.analyze();
 * 
 *     DependencyConstraint constraint = new DependencyConstraint();
 * 
 *     JavaPackage ejb = constraint.addPackage(&quot;com.xyz.ejb&quot;);
 *     JavaPackage web = constraint.addPackage(&quot;com.xyz.web&quot;);
 *     JavaPackage util = constraint.addPackage(&quot;com.xyz.util&quot;);
 * 
 *     ejb.dependsUpon(util);
 *     web.dependsUpon(util);
 * 
 *     assertEquals(&quot;Dependency mismatch&quot;, true, constraint
 *             .match(analyzedPackages));
 * }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author <b>Mike Clark</b> 
 * @author Clarkware Consulting, Inc.
 */
public class DependencyConstraint {

    private final Map<String, JavaPackage> packages;

    public DependencyConstraint() {
        packages = new HashMap<>();
    }

    public JavaPackage addPackage(final String packageName) {
        JavaPackage javaPackage = packages.get(packageName);
        if (javaPackage == null) {
            javaPackage = new JavaPackage(packageName);
            addPackage(javaPackage);
        }
        return javaPackage;
    }

    public void addPackage(final JavaPackage javaPackage) {
        if (!packages.containsValue(javaPackage)) {
            packages.put(javaPackage.getName(), javaPackage);
        }
    }

    public Collection<JavaPackage> getPackages() {
        return packages.values();
    }

    /**
     * Indicates whether the specified packages match the 
     * packages in this constraint.
     * 
     * @return <code>true</code> if the packages match this constraint
     */
    public boolean match(final Collection<JavaPackage> expectedPackages) {
        if (packages.size() == expectedPackages.size()) {
            for (final JavaPackage next : expectedPackages) {
                if (!matchPackage(next)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean matchPackage(final JavaPackage expectedPackage) {
        final JavaPackage actualPackage = packages.get(expectedPackage.getName());
        return actualPackage != null && equalsDependencies(actualPackage, expectedPackage);
    }

    private boolean equalsDependencies(final JavaPackage a, final JavaPackage b) {
        return equalsAfferents(a, b) && equalsEfferents(a, b);
    }

    private boolean equalsAfferents(final JavaPackage a, final JavaPackage b) {
        if (a.equals(b)) {
            final Collection<JavaPackage> otherAfferents = b.getAfferents();
            if (a.getAfferents().size() == otherAfferents.size()) {
                for (final JavaPackage afferent : a.getAfferents()) {
                    if (!otherAfferents.contains(afferent)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean equalsEfferents(final JavaPackage a, final JavaPackage b) {
        if (a.equals(b)) {
            final Collection<JavaPackage> otherEfferents = b.getEfferents();
            if (a.getEfferents().size() == otherEfferents.size()) {
                for (final JavaPackage efferent : a.getEfferents()) {
                    if (!otherEfferents.contains(efferent)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
