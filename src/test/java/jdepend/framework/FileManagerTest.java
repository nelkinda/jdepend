package jdepend.framework;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class FileManagerTest extends JDependTestCase {

    private FileManager fileManager;
    
    public FileManagerTest(String name) {
        super(name);
    }

    protected void setUp() {
        super.setUp();
        fileManager = new FileManager();
        fileManager.acceptInnerClasses(false);
    }

    public void testEmptyFileManager() {
        assertEquals(0, fileManager.extractFiles().size());
    }

    public void testBuildDirectory() throws IOException {
        fileManager.addDirectory(getJavaTestDir());
        fileManager.addDirectory(getJavaMainDir());
        assertEquals(45, fileManager.extractFiles().size());
    }

    public void testNonExistentDirectory() {
        assertThrows(
                IOException.class,
                () -> fileManager.addDirectory(getJavaTestDir() + "junk"),
                "Non-existent directory: Should raise IOException"
        );
    }

    public void testInvalidDirectory() {
        final String file = getTestDir() + getPackageSubDir() + "ExampleTest.java";
        assertThrows(
                IOException.class,
                () -> fileManager.addDirectory(file),
                "Invalid directory: Should raise IOException"
        );
    }

    public void testClassFile() {
        final File f = new File(getJavaMainDir() + getPackageSubDir() + "JDepend.class");
        assertTrue(new FileManager().acceptClassFile(f));
    }

    public void testNonExistentClassFile() {
        final File f = new File(getJavaMainDir() + "JDepend.class");
        assertFalse(new FileManager().acceptClassFile(f));
    }

    public void testInvalidClassFile() {
        final File f = new File(getHomeDir() + "build.gradle");
        assertFalse(new FileManager().acceptClassFile(f));
    }

    public void testJar() throws IOException {
        testZip(".jar");
    }

    public void testWar() throws IOException {
        testZip(".war");
    }

    public void testZip() throws IOException {
        testZip(".zip");
    }

    private void testZip(final String suffix) throws IOException {
        final File f = File.createTempFile("bogus", suffix, new File(getTestDataDir()));
        f.deleteOnExit();
        fileManager.addDirectory(f.getPath());
    }
}
