package shell;

import java.awt.BorderLayout;
import java.awt.Font;
import java.nio.file.Path;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ShellWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "VFS";
    private static final String WELCOME =
            "VFS. Commands: ls, cd, conf-dump, vfs-info, exit";
    private static final String DEBUG = "[debug] ";
    private static final int WIDTH = 700;
    private static final int HEIGHT = 450;
    private static final int FONT_SIZE = 14;

    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final transient Config config;
    private final transient Shell shell;

    public ShellWindow(Config config) {
        super(TITLE);
        this.config = config;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE);
        output.setFont(font);
        output.setEditable(false);
        input.setFont(font);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);
        input.addActionListener(event -> handleInput());
        print(WELCOME);
        printDebug();
        this.shell = new Shell(config, loadVfs());
        setLocationRelativeTo(null);
    }

    public void open() {
        setVisible(true);
        input.requestFocusInWindow();
        runStartupScript();
    }

    private void printDebug() {
        print(DEBUG + "startup parameters:");
        for (String line : config.dump().split("\n")) {
            print(DEBUG + "  " + line);
            System.out.println(DEBUG + line);
        }
    }

    private void runStartupScript() {
        if (!config.hasScript()) {
            return;
        }
        Path script = Path.of(config.getScriptPath());
        ScriptRunner.run(script, shell, this::print);
        if (shell.isExitRequested()) {
            dispose();
        }
    }

    private void handleInput() {
        String line = input.getText();
        input.setText("");
        print(Shell.PROMPT + line);
        try {
            String result = shell.execute(line);
            if (!result.isEmpty()) {
                print(result);
            }
        } catch (CommandException error) {
            print(error.getMessage());
        }
        if (shell.isExitRequested()) {
            dispose();
        }
    }

    private Vfs loadVfs() {
        if (config.getVfsPath() == null) {
            return null;
        }
        try {
            Vfs vfs = VfsLoader.load(config.getVfsPath());
            print(DEBUG + "VFS loaded: " + vfs.getName());
            return vfs;
        } catch (VfsException error) {
            print(error.getMessage());
            System.err.println(error.getMessage());
            return null;
        }
    }

    private void print(String text) {
        output.append(text + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }
}