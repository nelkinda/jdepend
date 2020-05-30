package jdepend.framework;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class ClassFileParserTest extends JDependTestCase {

    private ClassFileParser parser;

    public ClassFileParserTest(final String name) {
        super(name);
    }

    private static void assertContains(final Collection<JavaPackage> imports, final String... expectedPackageName) {
        assertAll(
                Stream.of(expectedPackageName)
                        .map(JavaPackage::new)
                        .map(it -> () -> assertTrue(imports.contains(it)))
        );
    }

    protected void setUp() {
        super.setUp();
        final var filter = new PackageFilter(List.of());
        parser = new ClassFileParser(filter);
    }

    public void testInvalidClassFile() {
        final var f = new File(getTestDir() + getPackageSubDir() + "ExampleTest.java");

        assertThrows(
                IOException.class,
                () -> parser.parse(f),
                "Invalid class file: Should raise IOException"
        );
    }

    public void testInterfaceClass() throws IOException {
        final var f = new File(getJavaTestDir() + getPackageSubDir() + "ExampleInterface.class");

        final var clazz = parser.parse(f);

        assertTrue(clazz.isAbstract());

        assertEquals("jdepend.framework.ExampleInterface", clazz.getClassName());

        assertEquals("ExampleInterface.java", clazz.getSourceFile());

        final var imports = clazz.getImportedPackages();
        assertEquals(6, imports.size());

        assertContains(imports, "java.math", "java.text", "java.lang", "java.io", "java.rmi", "java.util");
    }

    public void testAbstractClass() throws IOException {
        final var f = new File(getJavaTestDir() + getPackageSubDir() + "ExampleAbstractClass.class");

        final var clazz = parser.parse(f);

        assertTrue(clazz.isAbstract());

        assertEquals("jdepend.framework.ExampleAbstractClass", clazz.getClassName());

        assertEquals("ExampleAbstractClass.java", clazz.getSourceFile());

        final var imports = clazz.getImportedPackages();
        assertEquals(7, imports.size());

        assertContains(imports,
                "java.math",
                "java.text",
                "java.lang",
                "java.lang.reflect",
                "java.io",
                "java.rmi",
                "java.util"
        );
    }

    public void testConcreteClass() throws IOException {
        final var f = new File(getJavaTestDir() + getPackageSubDir() + "ExampleConcreteClass.class");

        final var clazz = parser.parse(f);

        assertFalse(clazz.isAbstract());

        assertEquals("jdepend.framework.ExampleConcreteClass", clazz.getClassName());

        assertEquals("ExampleConcreteClass.java", clazz.getSourceFile());

        final var imports = clazz.getImportedPackages();
        assertEquals(19, imports.size());

        assertContains(imports,
                "java.net",
                "java.text",
                "java.sql",
                "java.lang",
                "java.io",
                "java.rmi",
                "java.util",
                "java.util.jar",
                "java.math"
        );

        // annotations
        assertContains(imports,
                "org.junit.runners",
                "java.applet",
                "org.junit",
                "javax.crypto",
                "java.awt.geom",
                "java.awt.image.renderable",
                "jdepend.framework.p1",
                "jdepend.framework.p2",
                "java.awt.im",
                "java.awt.dnd"
        );
    }

    public void testInnerClass() throws IOException {
        final var f = new File(getJavaTestDir() + getPackageSubDir() + "ExampleConcreteClass$ExampleInnerClass.class");

        final var clazz = parser.parse(f);

        assertFalse(clazz.isAbstract());

        assertEquals("jdepend.framework.ExampleConcreteClass$ExampleInnerClass",
                clazz.getClassName());

        assertEquals("ExampleConcreteClass.java", clazz.getSourceFile());

        final var imports = clazz.getImportedPackages();
        assertEquals(1, imports.size());

        assertContains(imports, "java.lang");
    }

    public void testPackageClass() throws IOException {
        final var f = new File(getJavaTestDir() + getPackageSubDir() + "ExamplePackageClass.class");

        final var clazz = parser.parse(f);

        assertFalse(clazz.isAbstract());

        assertEquals("jdepend.framework.ExamplePackageClass", clazz.getClassName());

        assertEquals("ExampleConcreteClass.java", clazz.getSourceFile());

        final var imports = clazz.getImportedPackages();
        assertEquals(1, imports.size());

        assertContains(imports, "java.lang");
    }

    public void testExampleClassFileFromTimDrury() throws IOException {
        // see http://github.com/clarkware/jdepend/issues#issue/1
        parser.parse(ClassFileParser.class.getClassLoader().getResourceAsStream("example_class1.bin"));
    }

    public void testExampleClassFile2() throws IOException {
        parser.parse(ClassFileParser.class.getClassLoader().getResourceAsStream("example_class2.bin"));
    }
}
