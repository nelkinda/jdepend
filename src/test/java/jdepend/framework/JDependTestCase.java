package jdepend.framework;

import junit.framework.*;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public abstract class JDependTestCase extends TestCase {

    private String homeDir;
    private String testDir;
    private String testDataDir;
    private String javaMainDir;
    private String javaTestDir;
    private String packageSubDir;
    private String originalUserHome;

    
    public JDependTestCase(String name) {
        super(name);
    }

    protected void setUp() {
        homeDir = "./";
        testDir = "./src/test/";
        testDataDir = "src/test/resources/";
        javaTestDir = "build/classes/java/test/";
        javaMainDir = "build/classes/java/main/";
        packageSubDir = "jdepend/framework/";
        originalUserHome = System.getProperty("user.home");
    }

    protected void tearDown() {
        System.setProperty("user.home", originalUserHome);
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getTestDataDir() {
        return testDataDir;
    }
    
    public String getTestDir() {
        return testDir;
    }

    public String getJavaTestDir() {
        return javaTestDir;
    }

    public String getJavaMainDir() {
        return javaMainDir;
    }
    
    public String getPackageSubDir() {
        return packageSubDir;
    }
}
