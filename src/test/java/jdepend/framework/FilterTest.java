package jdepend.framework;

import java.io.File;
import java.util.List;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class FilterTest extends JDependTestCase {

    public FilterTest(final String name) {
        super(name);
    }

    protected void setUp() {
        super.setUp();
        System.setProperty("user.home", getTestDataDir());
    }

    public void testDefault() {
        final PackageFilter filter = new PackageFilter();
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    public void testFile() {
        final String filterFile = getTestDataDir() + "jdepend.properties";

        final PackageFilter filter = new PackageFilter(new File(filterFile));
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    public void testCollection() {
        final var filters = List.of(
                "java.*",
                "javax.*",
                "sun.*",
                "com.sun.*",
                "com.xyz.tests.*"
        );

        final PackageFilter filter = new PackageFilter(filters);
        assertEquals(5, filter.getFilters().size());
        assertFiltersExist(filter);
    }

    public void testCollectionSubset() {
        final var filters = List.of("com.xyz");
        final PackageFilter filter = new PackageFilter(filters);
        assertEquals(1, filter.getFilters().size());
    }

    private void assertFiltersExist(final PackageFilter filter) {
        assertFalse(filter.accept("java.lang"));
        assertFalse(filter.accept("javax.ejb"));
        assertTrue(filter.accept("com.xyz.tests"));
        assertFalse(filter.accept("com.xyz.tests.a"));
        assertTrue(filter.accept("com.xyz.ejb"));
    }
}
