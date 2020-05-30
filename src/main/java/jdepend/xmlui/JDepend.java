package jdepend.xmlui;

import java.io.*;
import java.util.*;

import java.text.NumberFormat;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;

/**
 * The <code>JDepend</code> class analyzes directories of Java class files,
 * generates metrics for each Java package, and reports the metrics in an XML
 * format.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */
public class JDepend extends jdepend.textui.JDepend {

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
        super(writer);

        formatter = NumberFormat.getInstance(Locale.ENGLISH);
        formatter.setMaximumFractionDigits(2);
    }

    @Override
    protected void printHeader() {
        getWriter().println("<?xml version=\"1.0\"?>");
        getWriter().println("<JDepend>");
    }

    @Override
    protected void printFooter() {
        getWriter().println("</JDepend>");
    }

    @Override
    protected void printPackagesHeader() {
        getWriter().println(indent() + "<Packages>");
    }

    @Override
    protected void printPackagesFooter() {
        getWriter().println(indent() + "</Packages>");
    }

    @Override
    protected void printPackageHeader(final JavaPackage javaPackage) {
        printSectionBreak();
        getWriter().println(
                indent(2) + "<Package name=\"" + javaPackage.getName() + "\">");
    }

    @Override
    protected void printPackageFooter(final JavaPackage javaPackage) {
        getWriter().println(indent(2) + "</Package>");
    }

    @Override
    protected void printNoStats() {
        getWriter().println(
                indent(3) + "<error>No stats available: "
                        + "package referenced, but not analyzed.</error>");
    }

    @Override
    protected void printStatistics(final JavaPackage javaPackage) {
        getWriter().println(indent(3) + "<Stats>");
        getWriter().println(
                indent(4) + "<TotalClasses>" + javaPackage.getClassCount()
                        + "</TotalClasses>");
        getWriter().println(
                indent(4) + "<ConcreteClasses>" + javaPackage.getConcreteClassCount()
                        + "</ConcreteClasses>");
        getWriter().println(
                indent(4) + "<AbstractClasses>" + javaPackage.getAbstractClassCount()
                        + "</AbstractClasses>");
        getWriter().println(
                indent(4) + "<Ca>" + javaPackage.afferentCoupling() + "</Ca>");
        getWriter().println(
                indent(4) + "<Ce>" + javaPackage.efferentCoupling() + "</Ce>");
        getWriter().println(
                indent(4) + "<A>" + toFormattedString(javaPackage.abstractness())
                        + "</A>");
        getWriter().println(
                indent(4) + "<I>" + toFormattedString(javaPackage.instability())
                        + "</I>");
        getWriter().println(
                indent(4) + "<D>" + toFormattedString(javaPackage.distance())
                        + "</D>");
        getWriter().println(indent(4) + "<V>" + javaPackage.getVolatility() + "</V>");
        getWriter().println(indent(3) + "</Stats>");
    }

    @Override
    protected void printClassName(final JavaClass javaClass) {
        getWriter().println(
                indent(4) + "<Class sourceFile=\"" + javaClass.getSourceFile()
                        + "\">");
        getWriter().println(indent(5) + javaClass.getClassName());
        getWriter().println(indent(4) + "</Class>");
    }

    @Override
    protected void printPackageName(final JavaPackage javaPackage) {
        getWriter().println(
                indent(4) + "<Package>" + javaPackage.getName() + "</Package>");
    }

    @Override
    protected void printAbstractClassesHeader() {
        getWriter().println(indent(3) + "<AbstractClasses>");
    }

    @Override
    protected void printAbstractClassesFooter() {
        getWriter().println(indent(3) + "</AbstractClasses>");
    }

    @Override
    protected void printConcreteClassesHeader() {
        getWriter().println(indent(3) + "<ConcreteClasses>");
    }

    @Override
    protected void printConcreteClassesFooter() {
        getWriter().println(indent(3) + "</ConcreteClasses>");
    }

    @Override
    protected void printEfferentsHeader() {
        getWriter().println(indent(3) + "<DependsUpon>");
    }

    @Override
    protected void printEfferentsFooter() {
        getWriter().println(indent(3) + "</DependsUpon>");
    }

    @Override
    protected void printEfferentsError() {
        // do nothing
    }

    @Override
    protected void printAfferentsHeader() {
        getWriter().println(indent(3) + "<UsedBy>");
    }

    @Override
    protected void printAfferentsFooter() {
        getWriter().println(indent(3) + "</UsedBy>");
    }

    @Override
    protected void printAfferentsError() {
        // do nothing
    }

    @Override
    protected void printCyclesHeader() {
        printSectionBreak();
        getWriter().println(indent() + "<Cycles>");
    }

    @Override
    protected void printCyclesFooter() {
        getWriter().println(indent() + "</Cycles>");
    }

    @Override
    protected void printCycleHeader(final JavaPackage javaPackage) {
        getWriter().println(
                indent(2) + "<Package Name=\"" + javaPackage.getName() + "\">");
    }

    @Override
    protected void printCycleFooter() {
        getWriter().println(indent(2) + "</Package>");
        printSectionBreak();
    }

    @Override
    protected void printCycleTarget(final JavaPackage javaPackage) {
        printCycleContributor(javaPackage);
    }

    @Override
    protected void printCycleContributor(final JavaPackage javaPackage) {
        getWriter().println(
                indent(3) + "<Package>" + javaPackage.getName() + "</Package>");
    }

    @Override
    protected void printSummary(final Collection<JavaPackage> packages) {
        // do nothing
    }

    /**
     * Main.
     */
    public static void main(final String... args) {
        new JDepend().instanceMain(args);
    }
}
