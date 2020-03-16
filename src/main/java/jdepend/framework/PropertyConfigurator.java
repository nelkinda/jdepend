package jdepend.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The <code>PropertyConfigurator</code> class contains configuration
 * information contained in the <code>jdepend.properties</code> file, 
 * if such a file exists either in the user's home directory or somewhere 
 * in the classpath.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class PropertyConfigurator {

    private final Properties properties;

    public static final String DEFAULT_PROPERTY_FILE = "jdepend.properties";

    /**
     * Constructs a <code>PropertyConfigurator</code> instance 
     * containing the properties specified in the file
     * <code>jdepend.properties</code>, if it exists.
     */
    public PropertyConfigurator() {
        this(getDefaultPropertyFile());
    }

    /**
     * Constructs a <code>PropertyConfigurator</code> instance 
     * with the specified property set.
     * 
     * @param p Property set.
     */
    public PropertyConfigurator(Properties p) {
        this.properties = p;
    }

    /**
     * Constructs a <code>PropertyConfigurator</code> instance 
     * with the specified property file.
     * 
     * @param f Property file.
     */
    public PropertyConfigurator(File f) {
        this(loadProperties(f));
    }

    public Collection<String> getFilteredPackages() {
        final Collection<String> packages = new ArrayList<>();

        final Enumeration<String> e = (Enumeration<String>) properties.propertyNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            if (key.startsWith("ignore")) {
                final String path = properties.getProperty(key);
                final StringTokenizer st = new StringTokenizer(path, ",");
                while (st.hasMoreTokens()) {
                    final String name = st.nextToken().trim();
                    packages.add(name);
                }
            }
        }

        return packages;
    }

    public Collection<JavaPackage> getConfiguredPackages() {
        final Collection<JavaPackage> packages = new ArrayList<>();

        final Enumeration<String> e = (Enumeration<String>) properties.propertyNames();
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            if (!key.startsWith("ignore")
                    && (!key.equals("analyzeInnerClasses"))) {
                String v = properties.getProperty(key);
                packages.add(new JavaPackage(key, new Integer(v)));
            }
        }

        return packages;
    }

    public boolean getAnalyzeInnerClasses() {
        final String key = "analyzeInnerClasses";
        if (properties.containsKey(key)) {
            final String value = properties.getProperty(key);
            return new Boolean(value);
        }
        return true;
    }

    public static File getDefaultPropertyFile() {
        final String home = System.getProperty("user.home");
        return new File(home, DEFAULT_PROPERTY_FILE);
    }

    @SuppressWarnings("EmptyCatchBlock")
    public static Properties loadProperties(File file) {
        final Properties p = new Properties();
        InputStream is = null;

        try {
            is = new FileInputStream(file);
        } catch (final Exception e) {
            is = PropertyConfigurator.class.getResourceAsStream("/" + DEFAULT_PROPERTY_FILE);
        }

        try {
            if (is != null) {
                p.load(is);
            }
        } catch (final IOException ignore) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
        }

        return p;
    }
}