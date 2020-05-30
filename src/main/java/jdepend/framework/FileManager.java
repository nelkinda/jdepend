package jdepend.framework;

import java.io.*;
import java.util.*;

/**
 * The <code>FileManager</code> class is responsible for extracting 
 * Java class files (<code>.class</code> files) from a collection of 
 * registered directories.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class FileManager {
    private final List<File> directories;
    private boolean acceptInnerClasses;


    public FileManager() {
        directories = new ArrayList<>();
        acceptInnerClasses = true;
    }

    /**
     * Determines whether inner classes should be collected.
     * 
     * @param b <code>true</code> to collect inner classes; 
     *          <code>false</code> otherwise.
     */
    public void acceptInnerClasses(final boolean b) {
        acceptInnerClasses = b;
    }

    public void addDirectory(final String name) throws IOException {
        final File directory = new File(name);
        if (directory.isDirectory() || acceptJarFile(directory)) {
            directories.add(directory);
        } else {
            throw new IOException("Invalid directory or JAR file: " + name);
        }
    }

    public boolean acceptFile(final File file) {
        return acceptClassFile(file) || acceptJarFile(file);
    }

    public boolean acceptClassFile(final File file) {
        if (!file.isFile()) {
            return false;
        }
        return acceptClassFileName(file.getName());
    }

    public boolean acceptClassFileName(final String name) {
        return (acceptInnerClasses || name.toLowerCase().indexOf('$') <= 0) && name.toLowerCase().endsWith(".class");
    }

    public boolean acceptJarFile(final File file) {
        return isEar(file) || isJar(file) || isZip(file) || isWar(file);
    }

    public Collection<File> extractFiles() {
        final Collection<File> files = new TreeSet<>();
        for (final File directory : directories) {
            collectFiles(directory, files);
        }
        return files;
    }

    private void collectFiles(final File directory, final Collection<File> files) {
        if (directory.isFile()) {
            addFile(directory, files);
        } else if (directory.isDirectory()) {
            final String[] directoryFiles = directory.list();
            assert directoryFiles != null;
            for (final String directoryFile: directoryFiles) {
                final File file = new File(directory, directoryFile);
                if (acceptFile(file)) {
                    addFile(file, files);
                } else if (file.isDirectory()) {
                    collectFiles(file, files);
                }
            }
        }
    }

    private void addFile(final File f, final Collection<File> files) {
        if (!files.contains(f)) {
            files.add(f);
        }
    }

    private boolean isEar(final File file) {
        return existsWithExtension(file, ".ear");
    }

    private boolean isWar(final File file) {
        return existsWithExtension(file, ".war");
    }

    private boolean isZip(final File file) {
        return existsWithExtension(file, ".zip");
    }
 
    private boolean isJar(final File file) {
        return existsWithExtension(file, ".jar");
    }

    private boolean existsWithExtension(final File file, final String extension) {
        return file.isFile() && file.getName().toLowerCase().endsWith(extension);
    }
}
