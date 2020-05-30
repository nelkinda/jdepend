package jdepend.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class CycleTest extends JDependTestCase {

    public CycleTest(String name) {
        super(name);
    }

    public void testNoCycles() {

        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");

        a.dependsUpon(b);

        List<JavaPackage> aCycles = new ArrayList<>();
        assertFalse(a.containsCycle());
        assertFalse(a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[] {});

        List<JavaPackage> bCycles = new ArrayList<>();
        assertFalse(b.containsCycle());
        assertFalse(b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[] {});
    }

    public void test2Node1BranchCycle() {

        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");

        a.dependsUpon(b);
        b.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[] { "A", "B", "A"});

        List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[] { "B", "A", "B"});
    }

    public void test3Node1BranchCycle() {

        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[] { "A", "B", "C", "A"});

        List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[] { "B", "C", "A", "B"});

        List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[] { "C", "A", "B", "C"});
    }

    public void test3Node1BranchSubCycle() {

        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(b);

        List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[] { "A", "B", "C", "B"});

        List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[] { "B", "C", "B"});

        List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[] { "C", "B", "C"});
    }

    public void test3Node2BranchCycle() {

        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(a);

        a.dependsUpon(c);
        c.dependsUpon(a);

        List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[] { "A", "B", "A"});

        List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[] { "B", "A", "B"});

        List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[] { "C", "A", "B", "A"});
    }

    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void test5Node2BranchCycle() {
        JavaPackage a = new JavaPackage("A");
        JavaPackage b = new JavaPackage("B");
        JavaPackage c = new JavaPackage("C");
        JavaPackage d = new JavaPackage("D");
        JavaPackage e = new JavaPackage("E");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(a);

        a.dependsUpon(d);
        d.dependsUpon(e);
        e.dependsUpon(a);

        final List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectCycle(aCycles));
        assertListEquals(aCycles, new String[] { "A", "B", "C", "A"});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectCycle(bCycles));
        assertListEquals(bCycles, new String[] { "B", "C", "A", "B"});

        final List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectCycle(cCycles));
        assertListEquals(cCycles, new String[] { "C", "A", "B", "C"});

        final List<JavaPackage> dCycles = new ArrayList<>();
        assertTrue(d.containsCycle());
        assertTrue(d.collectCycle(dCycles));
        assertListEquals(dCycles, new String[] { "D", "E", "A", "B", "C", "A"});

        final List<JavaPackage> eCycles = new ArrayList<>();
        assertTrue(e.containsCycle());
        assertTrue(e.collectCycle(eCycles));
        assertListEquals(eCycles, new String[] { "E", "A", "B", "C", "A"});
    }

    protected void assertListEquals(final List<JavaPackage> list, final String[] names) {
        assertEquals(names.length, list.size());
        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], list.get(i).getName());
        }
    }
}
