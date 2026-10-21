package shell;

import javax.swing.SwingUtilities;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShellWindow().open());
    }
}