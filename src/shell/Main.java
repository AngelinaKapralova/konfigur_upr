package shell;

import javax.swing.SwingUtilities;


public final class Main {
    private static final int EXIT_BAD_ARGS = 2;
    private static final String USAGE =
            "usage: java -cp out shell.Main [--vfs <path>] [--script <path>]";

    private Main() {
    }

    public static void main(String[] args) {
        Config config = readConfig(args);
        if (config == null) {
            System.exit(EXIT_BAD_ARGS);
        }
        SwingUtilities.invokeLater(() -> new ShellWindow(config).open());
    }

    private static Config readConfig(String[] args) {
        try {
            return ArgsParser.parse(args);
        } catch (CommandException error) {
            System.err.println("error: " + error.getMessage());
            System.err.println(USAGE);
            return null;
        }
    }
}