package jdepend.swingui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import jdepend.framework.ParserListener;

import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.invokeLater;

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

    private jdepend.framework.JDepend analyzer;

    private JFrame frame;

    private StatusPanel statusPanel;

    private JTextField statusField;

    private JProgressBar progressBar;

    private DependTree afferentTree;

    private DependTree efferentTree;

    private Hashtable resourceStrings;

    private Hashtable actions;


    /**
     * Constructs a <code>JDepend</code> instance.
     */
    public JDepend() {
        analyzer = new jdepend.framework.JDepend();

        analyzer.addParseListener(this);

        //
        // Force the cross platform L&F.
        //
        try {
            UIManager.setLookAndFeel(UIManager
                    .getCrossPlatformLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        // Install the resource string table.
        //
        resourceStrings = new Hashtable();
        resourceStrings.put("menubar", "File");
        resourceStrings.put("File", "About Exit");

        //
        // Install the action table.
        //
        actions = new Hashtable();
        actions.put("About", new AboutAction());
        actions.put("Exit", new ExitAction());
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
        display();
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
     * @param jClass Parsed Java class.
     */
    public void onParsedJavaClass(final JavaClass jClass) {
        invokeLater(() -> getProgressBar().setValue(getProgressBar().getValue() + 1));
    }

    private void display() {
        frame = createUI();
        frame.setVisible(true);
    }

    private void updateTree(final List<JavaPackage> packages) {
        final JavaPackage jPackage = new JavaPackage("root");
        jPackage.setAfferents(packages);
        jPackage.setEfferents(packages);

        final AfferentNode ah = new AfferentNode(null, jPackage);
        getAfferentTree().setModel(new DependTreeModel(ah));

        final EfferentNode eh = new EfferentNode(null, jPackage);
        getEfferentTree().setModel(new DependTreeModel(eh));
    }

    private void startProgressMonitor(final int maxValue) {
        invokeLater(() -> {
            getProgressBar().setMinimum(0);
            getProgressBar().setMaximum(maxValue);
            getStatusPanel().setStatusComponent(getProgressBar());
        });
    }

    private void stopProgressMonitor() {
        invokeLater(() -> {
            getStatusPanel().setStatusComponent(getStatusField());
            int classCount = analyzer.countClasses();
            int packageCount = analyzer.countPackages();
            showStatusMessage("Analyzed " + packageCount + " packages (" + classCount + " classes).");
        });
    }

    private JFrame createUI() {
        final JFrame frame = createFrame("JDepend");
        final JMenuBar menuBar = createMenubar();
        frame.setJMenuBar(menuBar);

        final JPanel treePanel = createTreePanel();
        StatusPanel statusPanel = getStatusPanel();

        frame.getContentPane().add("Center", treePanel);
        frame.getContentPane().add("South", statusPanel);
        frame.pack();

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 700;
        final int height = 500;
        final int x = (screenSize.width - width) / 2;
        final int y = (screenSize.height - height) / 2;
        frame.setBounds(x, y, width, height);
        frame.setSize(width, height);

        return frame;
    }

    private JFrame createFrame(final String title) {
        final JFrame frame = new JFrame(title);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.setBackground(SystemColor.control);

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                new ExitAction().actionPerformed(null);
            }
        });

        return frame;
    }

    private JPanel createTreePanel() {
        final JPanel panel = new JPanel();

        panel.setLayout(new GridLayout(2, 1));
        panel.add(getEfferentTree());
        panel.add(getAfferentTree());

        /*
         * panel.setLayout(new GridLayout(1,1)); JSplitPane splitPane = new
         * JSplitPane(JSplitPane.VERTICAL_SPLIT);
         * splitPane.setOneTouchExpandable(true);
         * splitPane.setTopComponent(getEfferentTree());
         * splitPane.setBottomComponent(getAfferentTree());
         * panel.add(splitPane);
         */

        return panel;
    }

    private StatusPanel createStatusPanel() {
        final StatusPanel panel = new StatusPanel();
        panel.setStatusComponent(getStatusField());
        return panel;
    }

    private JProgressBar createProgressBar() {
        final JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        return bar;
    }

    private JTextField createStatusField() {
        final JTextField statusField = new JTextField();
        statusField.setFont(BOLD_FONT);
        statusField.setEditable(false);
        statusField.setForeground(Color.black);
        statusField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        final Insets insets = new Insets(5, 5, 5, 5);
        statusField.setMargin(insets);
        return statusField;
    }

    private JMenuBar createMenubar() {
        final JMenuBar menuBar = new JMenuBar();

        final String[] menuKeys = tokenize((String) resourceStrings.get("menubar"));
        for (final String menuKey : menuKeys) {
            final JMenu m = createMenu(menuKey);
            if (m != null) {
                menuBar.add(m);
            }
        }
        return menuBar;
    }

    private JMenu createMenu(final String key) {
        final String[] itemKeys = tokenize((String) resourceStrings.get(key));
        final JMenu menu = new JMenu(key);
        for (final String itemKey : itemKeys) {
            if (itemKey.equals("-")) {
                menu.addSeparator();
            } else {
                final JMenuItem mi = createMenuItem(itemKey);
                menu.add(mi);
            }
        }

        char mnemonic = key.charAt(0);
        menu.setMnemonic(mnemonic);

        return menu;
    }

    private JMenuItem createMenuItem(final String key) {
        final JMenuItem mi = new JMenuItem(key);

        final char mnemonic = key.charAt(0);
        mi.setMnemonic(mnemonic);

        final char accelerator = key.charAt(0);
        mi.setAccelerator(KeyStroke.getKeyStroke(accelerator, java.awt.Event.CTRL_MASK));

        final String actionString = key;
        mi.setActionCommand(actionString);

        final Action a = getActionForCommand(actionString);
        if (a != null) {
            mi.addActionListener(a);
            mi.setEnabled(a.isEnabled());
        } else {
            mi.setEnabled(false);
        }

        return mi;
    }

    private void showStatusMessage(final String message) {
        getStatusField().setFont(BOLD_FONT);
        getStatusField().setForeground(Color.black);
        getStatusField().setText(" " + message);
    }

    private void showStatusError(final String message) {
        getStatusField().setFont(BOLD_FONT);
        getStatusField().setForeground(Color.red);
        getStatusField().setText(" " + message);
    }

    private DependTree getAfferentTree() {
        if (afferentTree == null) {
            afferentTree = new DependTree();
            afferentTree.addTreeSelectionListener(new TreeListener());
        }

        return afferentTree;
    }

    private DependTree getEfferentTree() {
        if (efferentTree == null) {
            efferentTree = new DependTree();
            efferentTree.addTreeSelectionListener(new TreeListener());
        }

        return efferentTree;
    }

    private StatusPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = createStatusPanel();
        }
        return statusPanel;
    }

    private JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = createProgressBar();
        }

        return progressBar;
    }

    private JTextField getStatusField() {
        if (statusField == null) {
            statusField = createStatusField();
        }
        return statusField;
    }

    private Action getActionForCommand(final String command) {
        return (Action) actions.get(command);
    }

    /*
     * Parses the specified string into an array of strings on whitespace
     * boundaries. @param input String to tokenize. @return Strings.
     */
    private String[] tokenize(final String input) {

        final List<String> v = new ArrayList<>();
        StringTokenizer t = new StringTokenizer(input);

        while (t.hasMoreTokens()) {
            v.add(t.nextToken());
        }

        final String[] cmd = new String[v.size()];
        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = v.get(i);
        }

        return cmd;
    }

    private void postStatusMessage(final String message) {
        invokeLater(() -> showStatusMessage(message));
    }

    private void postStatusError(final String message) {
        invokeLater(() -> showStatusError(message));
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
        public void valueChanged(TreeSelectionEvent te) {
            final TreePath path = te.getNewLeadSelectionPath();

            if (path != null) {
                PackageNode node = (PackageNode) path.getLastPathComponent();
                showStatusMessage(node.toMetricsString());
            }
        }
    }

    private class AboutAction extends AbstractAction {
        AboutAction() {
            super("About");
        }
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
        public void actionPerformed(final ActionEvent e) {
            frame.dispose();
            System.exit(0);
        }
    }

    private void usage(String message) {
        if (message != null) {
            System.err.println("\n" + message);
        }

        final String baseUsage = "\nJDepend ";

        System.err.println("");
        System.err.println("usage: ");
        System.err.println(baseUsage + "-components <components> " +
            "<directory> [directory2 [directory 3] ...]");
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

    public static void main(final String... args) {
        System.err.println(String.join(", ", args));
        new JDepend().instanceMain(args);
    }
}

