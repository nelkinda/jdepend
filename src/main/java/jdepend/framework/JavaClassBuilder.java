package jdepend.framework;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

/**
 * The <code>JavaClassBuilder</code> builds <code>JavaClass</code> 
 * instances from .class, .jar, .war, or .zip files.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class JavaClassBuilder {
    private final AbstractParser parser;
    private final FileManager fileManager;

    public JavaClassBuilder() {
        this(new ClassFileParser(), new FileManager());
    }

    public JavaClassBuilder(final FileManager fm) {
        this(new ClassFileParser(), fm);
    }

    public JavaClassBuilder(final AbstractParser parser, final FileManager fm) {
        this.parser = parser;
        this.fileManager = fm;
    }

    public int countClasses() {
        final AbstractParser counter = new AbstractParser() {
            @Override
            public JavaClass parse(final InputStream inputStream) {
                return new JavaClass("");
            }
        };
        final JavaClassBuilder builder = new JavaClassBuilder(counter, fileManager);
        final Collection<JavaClass> classes = builder.build();
        return classes.size();
    }

    /**
     * Builds the <code>JavaClass</code> instances.
     * 
     * @return Collection of <code>JavaClass</code> instances.
     */
    public Collection<JavaClass> build() {
        final Collection<JavaClass> classes = new ArrayList<>();
        for (final File nextFile : fileManager.extractFiles()) {
            try {
                classes.addAll(buildClasses(nextFile));
            } catch (final IOException ioe) {
                System.err.println("\n" + ioe.getMessage());
            }
        }
        return classes;
    }

    /**
     * Builds the <code>JavaClass</code> instances from the 
     * specified file.
     * 
     * @param file Class or Jar file.
     * @return Collection of <code>JavaClass</code> instances.
     * @throws IOException in case of I/O problems.
     */
    public Collection<JavaClass> buildClasses(final File file) throws IOException {
        if (fileManager.acceptClassFile(file)) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                final JavaClass parsedClass = parser.parse(is);
                return Collections.singleton(parsedClass);
            }
        } else if (fileManager.acceptJarFile(file)) {
            try (JarFile jarFile = new JarFile(file)) {
                return buildClasses(jarFile);
            }
        } else {
            throw new IOException("File is not a valid " + ".class, .jar, .war, or .zip file: " + file.getPath());
        }
    }

    /**
     * Builds the <code>JavaClass</code> instances from the specified 
     * jar, war, or zip file.
     * 
     * @param file Jar, war, or zip file.
     * @return Collection of <code>JavaClass</code> instances.
     * @throws IOException in case of I/O problems.
     */
    public Collection<JavaClass> buildClasses(final JarFile file) throws IOException {
        final Collection<JavaClass> javaClasses = new ArrayList<>();

        final Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry e = entries.nextElement();
            if (fileManager.acceptClassFileName(e.getName())) {
                try (InputStream is = new BufferedInputStream(file.getInputStream(e))) {
                    javaClasses.add(parser.parse(is));
                }
            }
        }

        return javaClasses;
    }
}
