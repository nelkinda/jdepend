package jdepend.textui;

import java.io.*;
import java.util.*;
import java.text.NumberFormat;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;

/**
 * The <code>JDepend</code> class analyzes directories of Java class files,
 * generates metrics for each Java package, and reports the metrics in a textual
 * format.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JDepend {

    private jdepend.framework.JDepend analyzer;

    private PrintWriter writer;

    protected NumberFormat formatter;

    /**
     * Constructs a <code>JDepend</code> instance using standard output.
     */
    public JDepend() {
        this(new PrintWriter(System.out));
    }

    /**
     * Constructs a <code>JDepend</code> instance with the specified writer.
     * 
     * @param writer Writer.
     */
    public JDepend(final PrintWriter writer) {
        analyzer = new jdepend.framework.JDepend();

        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(2);

        setWriter(writer);
    }

    /**
     * Sets the output writer.
     * 
     * @param writer Output writer.
     */
    public void setWriter(final PrintWriter writer) {
        this.writer = writer;
    }

    protected PrintWriter getWriter() {
        return writer;
    }

    /**
     * Sets the package filter.
     * 
     * @param filter Package filter.
     */
    public void setFilter(final PackageFilter filter) {
        analyzer.setFilter(filter);
    }

    /**
     * Sets the comma-separated list of components.
     */
    public void setComponents(final String components) {
        analyzer.setComponents(components);
    }
    
    /**
     * Adds the specified directory name to the collection of directories to be
     * analyzed.
     * 
     * @param name Directory name.
     * @throws IOException If the directory does not exist.
     */
    public void addDirectory(final String name) throws IOException {
        analyzer.addDirectory(name);
    }

    /**
     * Determines whether inner classes are analyzed.
     * 
     * @param b <code>true</code> to analyze inner classes; <code>false</code>
     *            otherwise.
     */
    public void analyzeInnerClasses(final boolean b) {
        analyzer.analyzeInnerClasses(b);
    }

    /**
     * Analyzes the registered directories, generates metrics for each Java
     * package, and reports the metrics.
     */
    public void analyze() {
        printHeader();

        final Collection<JavaPackage> packages = analyzer.analyze();

        final List<JavaPackage> packageList = new ArrayList<>(packages);
        packageList.sort(JavaPackage.byName);

        printPackages(packageList);

        printCycles(packageList);

        printSummary(packageList);

        printFooter();

        getWriter().flush();
    }

    protected void printPackages(final Collection<JavaPackage> packages) {
        printPackagesHeader();
        for (final JavaPackage aPackage : packages) {
            printPackage(aPackage);
        }
        printPackagesFooter();
    }

    protected void printPackage(final JavaPackage javaPackage) {

        printPackageHeader(javaPackage);

        if (javaPackage.getClasses().isEmpty()) {
            printNoStats();
            printPackageFooter(javaPackage);
            return;
        }

        printStatistics(javaPackage);

        printSectionBreak();

        printAbstractClasses(javaPackage);

        printSectionBreak();

        printConcreteClasses(javaPackage);

        printSectionBreak();

        printEfferents(javaPackage);

        printSectionBreak();

        printAfferents(javaPackage);

        printPackageFooter(javaPackage);
    }

    protected void printAbstractClasses(final JavaPackage javaPackage) {
        printAbstractClassesHeader();

        final List<JavaClass> members = new ArrayList<>(javaPackage.getClasses());
        members.sort(JavaClass.byName);
        for (final JavaClass jClass : members) {
            if (jClass.isAbstract()) {
                printClassName(jClass);
            }
        }

        printAbstractClassesFooter();
    }

    protected void printConcreteClasses(final JavaPackage javaPackage) {
        printConcreteClassesHeader();

        final List<JavaClass> members = new ArrayList<>(javaPackage.getClasses());
        members.sort(JavaClass.byName);
        for (final JavaClass concrete : members) {
            if (!concrete.isAbstract()) {
                printClassName(concrete);
            }
        }

        printConcreteClassesFooter();
    }

    protected void printEfferents(final JavaPackage javaPackage) {
        printEfferentsHeader();

        final List<JavaPackage> efferents = new ArrayList<>(javaPackage.getEfferents());
        efferents.sort(JavaPackage.byName);
        for (final JavaPackage efferent : efferents) {
            printPackageName(efferent);
        }
        if (efferents.isEmpty()) {
            printEfferentsError();
        }

        printEfferentsFooter();
    }

    protected void printAfferents(final JavaPackage javaPackage) {
        printAfferentsHeader();

        final List<JavaPackage> afferents = new ArrayList<>(javaPackage.getAfferents());
        afferents.sort(JavaPackage.byName);
        for (final JavaPackage afferent : afferents) {
            printPackageName(afferent);
        }
        if (afferents.isEmpty()) {
            printAfferentsError();
        }

        printAfferentsFooter();
    }

    protected void printCycles(final Collection<JavaPackage> packages) {
        printCyclesHeader();

        for (final JavaPackage aPackage : packages) {
            printCycle(aPackage);
        }

        printCyclesFooter();
    }

    protected void printCycle(final JavaPackage javaPackage) {
        final List<JavaPackage> list = new ArrayList<>();
        javaPackage.collectCycle(list);

        if (!javaPackage.containsCycle()) {
            return;
        }

        final JavaPackage cyclePackage = list.get(list.size() - 1);
        final String cyclePackageName = cyclePackage.getName();

        int i = 0;
        for (final JavaPackage otherPackage : list) {
            i++;

            if (i == 1) {
                printCycleHeader(otherPackage);
            } else {
                if (otherPackage.getName().equals(cyclePackageName)) {
                    printCycleTarget(otherPackage);
                } else {
                    printCycleContributor(otherPackage);
                }
            }
        }

        printCycleFooter();
    }

    protected void printHeader() {
        // do nothing
    }

    protected void printFooter() {
        // do nothing
    }

    protected void printPackagesHeader() {
        // do nothing
    }

    protected void printPackagesFooter() {
        // do nothing
    }

    protected void printNoStats() {
        getWriter().println(
                "No stats available: package referenced, but not analyzed.");
    }

    protected void printPackageHeader(final JavaPackage javaPackage) {
        getWriter().println(
                "\n--------------------------------------------------");
        getWriter().println("- Package: " + javaPackage.getName());
        getWriter().println(
                "--------------------------------------------------");
    }

    protected void printPackageFooter(final JavaPackage javaPackage) {
        // do nothing
    }

    protected void printStatistics(final JavaPackage javaPackage) {
        getWriter().println("\nStats:");
        getWriter().println(indent() + "Total Classes: " + javaPackage.getClassCount());
        getWriter().println(indent() + "Concrete Classes: "
                                + javaPackage.getConcreteClassCount());
        getWriter().println(indent() + "Abstract Classes: "
                                + javaPackage.getAbstractClassCount());
        getWriter().println("");
        getWriter().println(indent() + "Ca: " + javaPackage.afferentCoupling());
        getWriter().println(indent() + "Ce: " + javaPackage.efferentCoupling());
        getWriter().println("");
        getWriter().println(indent() + "A: " + toFormattedString(javaPackage.abstractness()));
        getWriter().println(indent() + "I: " + toFormattedString(javaPackage.instability()));
        getWriter().println(indent() + "D: " + toFormattedString(javaPackage.distance()));
    }

    protected void printClassName(final JavaClass javaClass) {
        getWriter().println(indent() + javaClass.getClassName());
    }

    protected void printPackageName(final JavaPackage javaPackage) {
        getWriter().println(indent() + javaPackage.getName());
    }

    protected void printAbstractClassesHeader() {
        getWriter().println("Abstract Classes:");
    }

    protected void printAbstractClassesFooter() {
        // do nothing
    }

    protected void printConcreteClassesHeader() {
        getWriter().println("Concrete Classes:");
    }

    protected void printConcreteClassesFooter() {
        // do nothing
    }

    protected void printEfferentsHeader() {
        getWriter().println("Depends Upon:");
    }

    protected void printEfferentsFooter() {
        // do nothing
    }

    protected void printEfferentsError() {
        getWriter().println(indent() + "Not dependent on any packages.");
    }

    protected void printAfferentsHeader() {
        getWriter().println("Used By:");
    }

    protected void printAfferentsFooter() {
        // do nothing
    }

    protected void printAfferentsError() {
        getWriter().println(indent() + "Not used by any packages.");
    }

    protected void printCyclesHeader() {
        printSectionBreak();
        getWriter().println(
                "\n--------------------------------------------------");
        getWriter().println("- Package Dependency Cycles:");
        getWriter().println(
                "--------------------------------------------------\n");
    }

    protected void printCyclesFooter() {
        // do nothing
    }

    protected void printCycleHeader(final JavaPackage javaPackage) {
        getWriter().println(javaPackage.getName());
        getWriter().println(indent() + "|");
    }

    protected void printCycleTarget(final JavaPackage javaPackage) {
        getWriter().println(indent() + "|-> " + javaPackage.getName());
    }

    protected void printCycleContributor(final JavaPackage javaPackage) {
        getWriter().println(indent() + "|   " + javaPackage.getName());
    }

    protected void printCycleFooter() {
        printSectionBreak();
    }

    protected void printSummary(final Collection<JavaPackage> packages) {
        getWriter().println(
                "\n--------------------------------------------------");
        getWriter().println("- Summary:");
        getWriter().println(
                "--------------------------------------------------\n");

        getWriter()
                .println(
                        "Name, Class Count, Abstract Class Count, Ca, Ce, A, I, D, V:\n");

        for (final JavaPackage jPackage : packages) {
            getWriter().print(jPackage.getName() + ",");
            getWriter().print(jPackage.getClassCount() + ",");
            getWriter().print(jPackage.getAbstractClassCount() + ",");
            getWriter().print(jPackage.afferentCoupling() + ",");
            getWriter().print(jPackage.efferentCoupling() + ",");
            getWriter().print(toFormattedString(jPackage.abstractness()) + ",");
            getWriter().print(toFormattedString(jPackage.instability()) + ",");
            getWriter().print(toFormattedString(jPackage.distance()) + ",");
            getWriter().println(jPackage.getVolatility());
        }
    }

    protected void printSectionBreak() {
        getWriter().println("");
    }

    protected String toFormattedString(final float number) {
        return formatter.format(number);
    }

    protected String indent() {
        return "    ";
    }

    protected String indent(final int indentationLevel) {
        final StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < indentationLevel; i++) {
            indentation.append(indent());
        }

        return indentation.toString();
    }

    protected void usage(final String message) {
        if (message != null) {
            System.err.println("\n" + message);
        }
        final String baseUsage = "\nJDepend ";

        System.err.println("");
        System.err.println("usage: ");
        System.err.println(baseUsage + "[-components <components>]"
                + " [-file <output file>] <directory> "
                + "[directory2 [directory 3] ...]");
        System.exit(1);
    }

    protected void instanceMain(final String... args) {
        if (args.length < 1) {
            usage("Must specify at least one directory.");
        }

        int directoryCount = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equalsIgnoreCase("-file")) {

                    if (args.length <= i + 1) {
                        usage("Output file name not specified.");
                    }

                    try {
                        setWriter(new PrintWriter(new OutputStreamWriter(
                                new FileOutputStream(args[++i]), "UTF8")));
                    } catch (IOException ioe) {
                        usage(ioe.getMessage());
                    }
                    
                } else if (args[i].equalsIgnoreCase("-components")) {
                    if (args.length <= i + 1) {
                        usage("Components not specified.");
                    }
                    setComponents(args[++i]);
                } else {
                    usage("Invalid argument: " + args[i]);
                }
            } else {
                try {
                    addDirectory(args[i]);
                    directoryCount++;
                } catch (IOException ioe) {
                    usage("Directory does not exist: " + args[i]);
                }
            }
        }

        if (directoryCount == 0) {
            usage("Must specify at least one directory.");
        }

        analyze();
    }

    public static void main(final String... args) {
        new JDepend().instanceMain(args);
    }
}
