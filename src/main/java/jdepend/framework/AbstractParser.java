package jdepend.framework;

import java.io.*;
import java.util.*;

/**
 * The <code>AbstractParser</code> class is the base class 
 * for classes capable of parsing files to create a 
 * <code>JavaClass</code> instance.
 * 
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public abstract class AbstractParser {

    private final List<ParserListener> parserListeners = new ArrayList<>();
    private PackageFilter filter;

    public AbstractParser() {
        this(new PackageFilter());
    }

    public AbstractParser(final PackageFilter filter) {
        setFilter(filter);
    }

    public void addParseListener(final ParserListener listener) {
        parserListeners.add(listener);
    }

    /**
     * Registered parser listeners are informed that the resulting
     * <code>JavaClass</code> was parsed.
     */
    public abstract JavaClass parse(final InputStream is) throws IOException;

    /**
     * Informs registered parser listeners that the specified
     * <code>JavaClass</code> was parsed.
     * 
     * @param jClass Parsed Java class.
     */
    protected void onParsedJavaClass(final JavaClass jClass) {
        for (final ParserListener parserListener : parserListeners) {
            parserListener.onParsedJavaClass(jClass);
        }
    }

    protected PackageFilter getFilter() {
        if (filter == null) {
            setFilter(new PackageFilter());
        }
        return filter;
    }

    protected void setFilter(final PackageFilter filter) {
        this.filter = filter;
    }
}