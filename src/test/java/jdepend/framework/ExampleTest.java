package jdepend.framework;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * The <code>ExampleTest</code> is an example <code>TestCase</code> 
 * that demonstrates tests for measuring the distance from the 
 * main sequence (D), package dependency constraints, and the 
 * existence of cyclic package dependencies.
 * <p>
 * This test analyzes the JDepend class files.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ExampleTest extends TestCase {

    private JDepend jdepend;

    public String jdependHomeDirectory;

    public ExampleTest(String name) {
        super(name);
    }

    protected void setUp() throws IOException {

        jdependHomeDirectory = "./";

        PackageFilter filter = new PackageFilter();
        filter.addPackage("java.*");
        filter.addPackage("javax.*");
        jdepend = new JDepend(filter);

        String classesDir = jdependHomeDirectory + File.separator + "build";

        jdepend.addDirectory(classesDir);
    }

    /**
     * Tests the conformance of a single package to a distance 
     * from the main sequence (D) within a tolerance.
     */
    public void testOnePackageDistance() {

        double ideal = 0.0;
        double tolerance = 0.8;

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("jdepend.framework");

        assertEquals("Distance exceeded: " + p.getName(), 
                     ideal, p.distance(), tolerance);
    }

    /**
     * Tests that a single package does not contain any 
     * package dependency cycles.
     */
    public void testOnePackageHasNoCycles() {

        jdepend.analyze();

        JavaPackage p = jdepend.getPackage("jdepend.framework");

        assertFalse("Cycles exist: " + p.getName(), p.containsCycle());
    }

    /**
     * Tests the conformance of all analyzed packages to a 
     * distance from the main sequence (D) within a tolerance.
     */
    public void testAllPackagesDistance() {

        double ideal = 0.0;
        double tolerance = 1.0;

        Collection<JavaPackage> packages = jdepend.analyze();

        for (final JavaPackage p : packages) {
            assertEquals("Distance exceeded: " + p.getName(), ideal, p.distance(), tolerance);
        }
    }

    /**
     * Tests that a package dependency cycle does not exist 
     * for any of the analyzed packages.
     */
    public void ignoredTestAllPackagesHaveNoCycles() {

        Collection packages = jdepend.analyze();

        assertFalse("Cycles exist", jdepend.containsCycles());
    }

    /**
     * Tests that a package dependency constraint is matched 
     * for the analyzed packages.
     * <p>
     * Fails if any package dependency other than those declared 
     * in the dependency constraints are detected.
     */
    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void ignoredTestDependencyConstraint() {
        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage junitframework = constraint.addPackage("junit.framework");
        JavaPackage junitui = constraint.addPackage("junit.textui");
        JavaPackage framework = constraint.addPackage("jdepend.framework");
        JavaPackage text = constraint.addPackage("jdepend.textui");
        JavaPackage xml = constraint.addPackage("jdepend.xmlui");
        JavaPackage swing = constraint.addPackage("jdepend.swingui");
        JavaPackage orgjunitrunners = constraint.addPackage("orgjunitrunners");
        JavaPackage jdependframeworkp2 = constraint.addPackage("jdependframeworkp2");
        JavaPackage jdependframeworkp3 = constraint.addPackage("jdependframeworkp3");
        JavaPackage jdependframeworkp1 = constraint.addPackage("jdependframeworkp1");
        JavaPackage orgjunit = constraint.addPackage("orgjunit");

        framework.dependsUpon(junitframework);
        framework.dependsUpon(junitui);
        text.dependsUpon(framework);
        xml.dependsUpon(framework);
        xml.dependsUpon(text);
        swing.dependsUpon(framework);
        framework.dependsUpon(jdependframeworkp2);
        framework.dependsUpon(jdependframeworkp3);
        framework.dependsUpon(jdependframeworkp1);
        framework.dependsUpon(orgjunitrunners);
        framework.dependsUpon(orgjunit);

        jdepend.analyze();

        assertTrue("Constraint mismatch", jdepend.dependencyMatch(constraint));
    }
}
