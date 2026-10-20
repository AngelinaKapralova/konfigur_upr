package shell;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ShellWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String TITLE = "VFS";
    private static final String PROMPT = "vfs$ ";
    private static final String WELCOME =
            "VFS. Commands: ls, cd, exit";
    private static final int WIDTH = 700;
    private static final int HEIGHT = 450;
    private static final int FONT_SIZE = 14;

    private final JTextArea output = new JTextArea();
    private final JTextField input = new JTextField();
    private final Shell shell = new Shell();

    public ShellWindow() {
        super(TITLE);
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
        setLocationRelativeTo(null);
    }

    public void open() {
        setVisible(true);
        input.requestFocusInWindow();
    }

    private void handleInput() {
        String line = input.getText();
        input.setText("");
        print(PROMPT + line);
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

    private void print(String text) {
        output.append(text + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }
}