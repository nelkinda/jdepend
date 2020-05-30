package jdepend.framework;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class CollectAllCyclesTest extends JDependTestCase {

    public CollectAllCyclesTest(final String name) {
        super(name);
    }

    public void testNoCycles() {
        final JavaPackage a = new JavaPackage("A");
        final JavaPackage b = new JavaPackage("B");

        a.dependsUpon(b);

        final List<JavaPackage> aCycles = new ArrayList<>();
        assertFalse(a.containsCycle());
        assertFalse(a.collectAllCycles(aCycles));
        assertListEquals(aCycles, new String[]{});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertFalse(b.containsCycle());
        assertFalse(b.collectAllCycles(bCycles));
        assertListEquals(bCycles, new String[]{});
    }

    public void test2Node1BranchCycle() {
        final JavaPackage a = new JavaPackage("A");
        final JavaPackage b = new JavaPackage("B");

        a.dependsUpon(b);
        b.dependsUpon(a);

        final List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectAllCycles(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "A"});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectAllCycles(bCycles));
        assertListEquals(bCycles, new String[]{"B", "A", "B"});
    }

    public void test3Node1BranchCycle() {
        final JavaPackage a = new JavaPackage("A");
        final JavaPackage b = new JavaPackage("B");
        final JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(a);


        final List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectAllCycles(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "C", "A"});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectAllCycles(bCycles));
        assertListEquals(bCycles, new String[]{"B", "C", "A", "B"});

        final List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectAllCycles(cCycles));
        assertListEquals(cCycles, new String[]{"C", "A", "B", "C"});
    }

    public void test3Node1BranchSubCycle() {
        final JavaPackage a = new JavaPackage("A");
        final JavaPackage b = new JavaPackage("B");
        final JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(b);

        final List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectAllCycles(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "C", "B"});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectAllCycles(bCycles));
        assertListEquals(bCycles, new String[]{"B", "C", "B"});

        final List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectAllCycles(cCycles));
        assertListEquals(cCycles, new String[]{"C", "B", "C"});
    }

    public void test3Node2BranchCycle() {
        final JavaPackage a = new JavaPackage("A");
        final JavaPackage b = new JavaPackage("B");
        final JavaPackage c = new JavaPackage("C");

        a.dependsUpon(b);
        b.dependsUpon(a);

        a.dependsUpon(c);
        c.dependsUpon(a);

        final List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectAllCycles(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "A", "C", "A"});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectAllCycles(bCycles));
        assertListEquals(bCycles, new String[]{"B", "A", "B", "C", "A"});

        final List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectAllCycles(cCycles));
        assertListEquals(cCycles, new String[]{"C", "A", "B", "A", "C"});
    }

    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void test5Node2BranchCycle() {
        final JavaPackage a = new JavaPackage("A");
        final JavaPackage b = new JavaPackage("B");
        final JavaPackage c = new JavaPackage("C");
        final JavaPackage d = new JavaPackage("D");
        final JavaPackage e = new JavaPackage("E");

        a.dependsUpon(b);
        b.dependsUpon(c);
        c.dependsUpon(a);

        a.dependsUpon(d);
        d.dependsUpon(e);
        e.dependsUpon(a);

        final List<JavaPackage> aCycles = new ArrayList<>();
        assertTrue(a.containsCycle());
        assertTrue(a.collectAllCycles(aCycles));
        assertListEquals(aCycles, new String[]{"A", "B", "C", "A", "D", "E", "A"});

        final List<JavaPackage> bCycles = new ArrayList<>();
        assertTrue(b.containsCycle());
        assertTrue(b.collectAllCycles(bCycles));
        assertListEquals(bCycles, new String[]{"B", "C", "A", "B", "D", "E", "A"});

        final List<JavaPackage> cCycles = new ArrayList<>();
        assertTrue(c.containsCycle());
        assertTrue(c.collectAllCycles(cCycles));
        assertListEquals(cCycles, new String[]{"C", "A", "B", "C", "D", "E", "A"});

        final List<JavaPackage> dCycles = new ArrayList<>();
        assertTrue(d.containsCycle());
        assertTrue(d.collectAllCycles(dCycles));
        assertListEquals(dCycles, new String[]{"D", "E", "A", "B", "C", "A", "D"});

        final List<JavaPackage> eCycles = new ArrayList<>();
        assertTrue(e.containsCycle());
        assertTrue(e.collectAllCycles(eCycles));
        assertListEquals(eCycles, new String[]{"E", "A", "B", "C", "A", "D", "E"});
    }

    protected void assertListEquals(final List<JavaPackage> list, final String[] names) {
        assertEquals(names.length, list.size());
        for (int i = 0; i < names.length; i++) {
            assertEquals(names[i], list.get(i).getName());
        }
    }
}
