package jdepend.framework;

public enum Logger {
    ;

    public static boolean debug;

    static void error(final String message) {
        msg(message);
    }

    static void error(final Throwable t) {
        error(t.getMessage());
    }

    static void warn(final String message) {
        msg(message);
    }

    static void info(final String message) {
        msg(message);
    }

    static void debug(final String message) {
        if (debug) {
            msg(message);
        }
    }

    private static void msg(final String message) {
        System.err.println(message);
    }
}
