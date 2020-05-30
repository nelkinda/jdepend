package jdepend.framework;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.util.Comparator.comparing;

/**
 * The <code>JavaClass</code> class represents a Java
 * class or interface.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class JavaClass {
    public static final Comparator<JavaClass> byName = comparing(JavaClass::getClassName);

    private final Map<String, JavaPackage> importedPackages = new HashMap<>();
    private String className;
    private String packageName;
    private boolean isAbstract;
    private String sourceFile;

    public JavaClass(final String className) {
        this.className = className;
        packageName = "default";
        isAbstract = false;
        sourceFile = "Unknown";
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(final String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Collection<JavaPackage> getImportedPackages() {
        return importedPackages.values();
    }

    public void addImportedPackage(final JavaPackage importedPackage) {
        if (!importedPackage.getName().equals(getPackageName())) {
            importedPackages.put(importedPackage.getName(), importedPackage);
        }
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void isAbstract(final boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof JavaClass) {
            final JavaClass otherClass = (JavaClass) other;
            return otherClass.getClassName().equals(getClassName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClassName().hashCode();
    }
}
