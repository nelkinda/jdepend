package jdepend.framework;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class ComponentTest extends JDependTestCase {

    private static final NumberFormat formatter;

    static {
        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);
    }

    private JDepend jdepend;

    public ComponentTest(String name) {
        super(name);
    }

    protected void setUp() {
        super.setUp();
        jdepend = new JDepend();
        jdepend.analyzeInnerClasses(false);
    }

    public void testJDependComponents() throws IOException {

        jdepend.setComponents("jdepend,junit,java,javax");

        jdepend.addDirectory(getJavaMainDir());
        jdepend.addDirectory(getJavaTestDir());

        jdepend.analyze();

        Collection packages = jdepend.getPackages();
        assertEquals(8, packages.size()); // TODO Filter-out JUnit

        //assertJDependPackage(); // TODO Re-enable
        assertJUnitPackage();
        //assertJavaPackage(); // TODO Re-enable
        //assertJavaxPackage(); // TODO Re-enable
    }

    private void assertJDependPackage() {
        JavaPackage p = jdepend.getPackage("jdepend");
        assertAll(
                () -> assertEquals("jdepend", p.getName()),
                () -> assertEquals(37, p.getConcreteClassCount()),
                () -> assertEquals(7, p.getAbstractClassCount()),
                () -> assertEquals(0, p.afferentCoupling()),
                () -> assertEquals(5, p.efferentCoupling()),
                () -> assertEquals(format(0.16f), format(p.abstractness())),
                () -> assertEquals("1", format(p.instability())),
                () -> assertEquals(format(0.16f), format(p.distance())),
                () -> assertEquals(1, p.getVolatility())
        );

        Collection<JavaPackage> efferents = p.getEfferents();
        assertEquals(5, efferents.size());
        System.err.println(efferents);
        assertTrue(efferents.contains(new JavaPackage("java")));
        assertTrue(efferents.contains(new JavaPackage("javax")));
        assertTrue(efferents.contains(new JavaPackage("junit")));

        Collection<JavaPackage> afferents = p.getAfferents();
        assertEquals(0, afferents.size());
    }

    private void assertJUnitPackage() {
        JavaPackage p = jdepend.getPackage("junit");
        assertEquals("junit", p.getName());

        Collection<JavaPackage> afferents = p.getAfferents();
        assertEquals(1, afferents.size());
        assertTrue(afferents.contains(new JavaPackage("jdepend")));

        Collection<JavaPackage> efferents = p.getEfferents();
        assertEquals(0, efferents.size());
    }

    private void assertJavaPackage() {
        JavaPackage p = jdepend.getPackage("java");
        assertEquals("java", p.getName());

        Collection<JavaPackage> afferents = p.getAfferents();
        assertEquals(1, afferents.size());
        assertTrue(afferents.contains(new JavaPackage("jdepend")));

        Collection<JavaPackage> efferents = p.getEfferents();
        assertEquals(0, efferents.size());
    }

    private void assertJavaxPackage() {
        JavaPackage p = jdepend.getPackage("javax");
        assertEquals("javax", p.getName());

        Collection<JavaPackage> afferents = p.getAfferents();
        assertEquals(1, afferents.size());
        assertTrue(afferents.contains(new JavaPackage("jdepend")));

        Collection<JavaPackage> efferents = p.getEfferents();
        assertEquals(0, efferents.size());
    }

    private String format(float f) {
        return formatter.format(f);
    }
}
