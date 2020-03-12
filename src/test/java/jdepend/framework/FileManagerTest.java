package jdepend.framework;

import java.io.File;
import java.io.IOException;

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

    protected void tearDown() {
        super.tearDown();
    }

    public void testEmptyFileManager() {
        assertEquals(0, fileManager.extractFiles().size());
    }

    public void testBuildDirectory() throws IOException {
        fileManager.addDirectory(getJavaTestDir());
        fileManager.addDirectory(getJavaMainDir());
        assertEquals(44, fileManager.extractFiles().size());
    }

    public void testNonExistentDirectory() {

        try {
            
            fileManager.addDirectory(getJavaTestDir() + "junk");
            fail("Non-existent directory: Should raise IOException");
        
        } catch (IOException expected) {
            assertTrue(true);
        }
    }

    public void testInvalidDirectory() {

        String file = getTestDir() + getPackageSubDir() + "ExampleTest.java";
        
        try {
            
            fileManager.addDirectory(file);
            fail("Invalid directory: Should raise IOException");
            
        } catch (IOException expected) {
            assertTrue(true);
        }
    }

    public void testClassFile() throws IOException {

        File f = new File(getJavaMainDir() + getPackageSubDir() + "JDepend.class");

        assertTrue(new FileManager().acceptClassFile(f));
    }

    public void testNonExistentClassFile() {
        File f = new File(getJavaMainDir() + "JDepend.class");
        assertFalse(new FileManager().acceptClassFile(f));
    }

    public void testInvalidClassFile() {
        File f = new File(getHomeDir() + "build.gradle");
        assertFalse(new FileManager().acceptClassFile(f));
    }

    public void testJar() throws IOException {
        File f = File.createTempFile("bogus", ".jar", 
            new File(getTestDataDir()));
        fileManager.addDirectory(f.getPath());
        f.deleteOnExit();
    }

    public void testZip() throws IOException {
        File f = File.createTempFile("bogus", ".zip", 
            new File(getTestDataDir()));
        fileManager.addDirectory(f.getPath());
        f.deleteOnExit();
    }

    public void testWar() throws IOException {
        File f = File.createTempFile("bogus", ".war", 
            new File(getTestDataDir()));
        fileManager.addDirectory(f.getPath());
        f.deleteOnExit();
    }
}