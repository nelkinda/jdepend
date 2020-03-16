package jdepend.swingui;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import jdepend.framework.ParserListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static java.lang.Math.max;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.UIManager.getCrossPlatformLookAndFeelClassName;
import static javax.swing.UIManager.setLookAndFeel;

/**
 * The <code>JDepend</code> class analyzes directories of Java class files,
 * generates metrics for each Java package, and reports the metrics in a Swing
 * tree.
 *
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class JDepend implements ParserListener {
    private static final Font BOLD_FONT = new Font("dialog", Font.BOLD, 12);
    private static final Map<String, String> resourceStrings = Map.of(
            "menubar", "File",
            "File", "About Exit"
    );
    private final jdepend.framework.JDepend analyzer;
    private final Map<String, Action> actions = Map.of(
            "About", new AboutAction(),
            "Exit", new ExitAction()
    );

    private final JTextField statusField = createStatusField();
    private final StatusPanel statusPanel = createStatusPanel(statusField);
    private final JProgressBar progressBar = createProgressBar();
    private final DependTree afferentTree = createAfferentTree();
    private final DependTree efferentTree = createEfferentTree();
    private final JFrame frame = createUI(actions, efferentTree, afferentTree, statusPanel);

    public JDepend() {
        setCrossPlatformLookAndFeel();
        analyzer = new jdepend.framework.JDepend();
        analyzer.addParseListener(this);
        frame.setVisible(true);
    }

    private static void setCrossPlatformLookAndFeel() {
        try {
            setLookAndFeel(getCrossPlatformLookAndFeelClassName());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static JFrame createUI(
            final Map<String, Action> actions,
            final DependTree efferentTree,
            final DependTree afferentTree,
            final StatusPanel statusPanel
    ) {
        final JFrame frame = createFrame(actions);
        final JMenuBar menuBar = createMenubar(actions);
        frame.setJMenuBar(menuBar);

        final JPanel treePanel = createTreePanel(efferentTree, afferentTree);

        frame.getContentPane().add("Center", treePanel);
        frame.getContentPane().add("South", statusPanel);
        frame.pack();

        center(frame);

        return frame;
    }

    private static void center(final JFrame frame) {
        final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        size.width = max(frame.getWidth(), size.width / 2);
        size.height = max(frame.getHeight(), size.height / 2);
        frame.setSize(size);
        frame.setLocationRelativeTo(null);
    }

    private static JFrame createFrame(final Map<String, Action> actions) {
        final JFrame frame = new JFrame("JDepend");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.setBackground(SystemColor.control);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                actions.get("Exit").actionPerformed(null);
            }
        });

        return frame;
    }

    private static JPanel createTreePanel(final DependTree efferentTree, final DependTree afferentTree) {
        final JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(2, 1));
        panel.add(efferentTree);
        panel.add(afferentTree);

        /*
         * panel.setLayout(new GridLayout(1,1)); JSplitPane splitPane = new
         * JSplitPane(JSplitPane.VERTICAL_SPLIT);
         * splitPane.setOneTouchExpandable(true);
         * splitPane.setTopComponent(efferentTree);
         * splitPane.setBottomComponent(afferentTree);
         * panel.add(splitPane);
         */

        return panel;
    }

    private static StatusPanel createStatusPanel(final JComponent statusField) {
        final StatusPanel panel = new StatusPanel();
        panel.setStatusComponent(statusField);
        return panel;
    }

    private static JProgressBar createProgressBar() {
        final JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        return bar;
    }

    private static JTextField createStatusField() {
        final JTextField statusField = new JTextField();
        statusField.setFont(BOLD_FONT);
        statusField.setEditable(false);
        statusField.setForeground(Color.black);
        statusField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        final Insets insets = new Insets(5, 5, 5, 5);
        statusField.setMargin(insets);
        return statusField;
    }

    private static JMenuBar createMenubar(final Map<String, Action> actions) {
        final JMenuBar menuBar = new JMenuBar();
        for (final String menuKey : tokenize(resourceStrings.get("menubar"))) {
            menuBar.add(createMenu(actions, menuKey));
        }
        return menuBar;
    }

    private static JMenu createMenu(final Map<String, Action> actions, final String key) {
        final String[] itemKeys = tokenize(resourceStrings.get(key));
        final JMenu menu = new JMenu(key);
        for (final String itemKey : itemKeys) {
            if ("-".equals(itemKey)) {
                menu.addSeparator();
            } else {
                menu.add(createMenuItem(actions, itemKey));
            }
        }

        final char mnemonic = key.charAt(0);
        menu.setMnemonic(mnemonic);

        return menu;
    }

    private static JMenuItem createMenuItem(final Map<String, Action> actions, final String key) {
        final JMenuItem mi = new JMenuItem(key);

        final char mnemonic = key.charAt(0);
        mi.setMnemonic(mnemonic);

        final char accelerator = key.charAt(0);
        mi.setAccelerator(KeyStroke.getKeyStroke(accelerator, java.awt.Event.CTRL_MASK));

        mi.setActionCommand(key);

        final Action a = actions.get(key);
        if (a != null) {
            mi.addActionListener(a);
            mi.setEnabled(a.isEnabled());
        } else {
            mi.setEnabled(false);
        }
        return mi;
    }

    /*
     * Parses the specified string into an array of strings on whitespace
     * boundaries. @param input String to tokenize. @return Strings.
     */
    private static String[] tokenize(final String input) {
        return Collections
                .list(new StringTokenizer(input))
                .stream()
                .map(token -> (String) token)
                .toArray(String[]::new);
    }

    public static void main(final String... args) {
        new JDepend().instanceMain(args);
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
     * Analyzes the registered directories, generates metrics for each Java
     * package, and reports the metrics in a graphical format.
     */
    public void analyze() {
        startProgressMonitor(analyzer.countClasses());
        final List<JavaPackage> packages = new ArrayList<>(analyzer.analyze());
        packages.sort(JavaPackage.byName);
        stopProgressMonitor();
        updateTree(packages);
    }

    /**
     * Called whenever a Java source file is parsed into the specified
     * <code>JavaClass</code> instance.
     *
     * @param javaClass Parsed Java class.
     */
    @Override
    public void onParsedJavaClass(final JavaClass javaClass) {
        invokeLater(() -> progressBar.setValue(progressBar.getValue() + 1));
    }

    private void updateTree(final List<JavaPackage> packages) {
        final JavaPackage jPackage = new JavaPackage("root");
        jPackage.setAfferents(packages);
        jPackage.setEfferents(packages);

        final AfferentNode ah = new AfferentNode(null, jPackage);
        afferentTree.setModel(new DependTreeModel(ah));

        final EfferentNode eh = new EfferentNode(null, jPackage);
        efferentTree.setModel(new DependTreeModel(eh));
    }

    private void startProgressMonitor(final int maxValue) {
        invokeLater(() -> {
            progressBar.setMinimum(0);
            progressBar.setMaximum(maxValue);
            statusPanel.setStatusComponent(progressBar);
        });
    }

    private void stopProgressMonitor() {
        invokeLater(() -> {
            statusPanel.setStatusComponent(statusField);
            final int classCount = analyzer.countClasses();
            final int packageCount = analyzer.countPackages();
            showStatusMessage("Analyzed " + packageCount + " packages (" + classCount + " classes).");
        });
    }

    private void showStatusMessage(final String message) {
        statusField.setFont(BOLD_FONT);
        statusField.setForeground(Color.black);
        statusField.setText(" " + message);
    }

    private void showStatusError(final String message) {
        statusField.setFont(BOLD_FONT);
        statusField.setForeground(Color.red);
        statusField.setText(" " + message);
    }

    private DependTree createAfferentTree() {
        final DependTree afferentTree = new DependTree();
        afferentTree.addTreeSelectionListener(new TreeListener());
        return afferentTree;
    }

    private DependTree createEfferentTree() {
        final DependTree efferentTree = new DependTree();
        efferentTree.addTreeSelectionListener(new TreeListener());
        return efferentTree;
    }

    private void postStatusMessage(final String message) {
        invokeLater(() -> showStatusMessage(message));
    }

    private void postStatusError(final String message) {
        invokeLater(() -> showStatusError(message));
    }

    private void usage(final String message) {
        if (message != null) {
            System.err.println("\n" + message);
        }

        final String baseUsage = "\nJDepend ";

        System.err.println();
        System.err.println("usage: ");
        System.err.println(baseUsage + "-components <components> " + "<directory> [directory2 [directory 3] ...]");
        System.exit(1);
    }

    private void instanceMain(final String... args) {
        if (args.length < 1) {
            usage("Must specify at least one directory.");
        }

        int directoryCount = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (args[i].equalsIgnoreCase("-components")) {
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

    //
    // Tree selection handler.
    //
    private class TreeListener implements TreeSelectionListener {

        /**
         * Constructs a <code>TreeListener</code> instance.
         */
        TreeListener() {
        }

        /**
         * Callback method triggered whenever the value of the tree selection
         * changes.
         *
         * @param te Event that characterizes the change.
         */
        @Override
        public void valueChanged(final TreeSelectionEvent te) {
            final TreePath path = te.getNewLeadSelectionPath();

            if (path != null) {
                final PackageNode node = (PackageNode) path.getLastPathComponent();
                showStatusMessage(node.toMetricsString());
            }
        }
    }

    private class AboutAction extends AbstractAction {
        AboutAction() {
            super("About");
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            showMessageDialog(
                    frame,
                    "<html><h1>JDepend</h1>Mike Clark<br>Clarkware Consulting<br><a href=\"http://www.clarkware.com/\">www.clarkware.com</a>",
                    "About",
                    PLAIN_MESSAGE
            );
        }
    }

    //
    // Exit action handler.
    //
    private class ExitAction extends AbstractAction {

        /**
         * Constructs an <code>ExitAction</code> instance.
         */
        ExitAction() {
            super("Exit");
        }

        /**
         * Handles the action.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            frame.dispose();
            System.exit(0);
        }
    }
}
