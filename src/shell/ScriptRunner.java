package shell;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public final class ScriptRunner {
    private static final String COMMENT_PREFIX = "#";
    private static final int FIRST_LINE = 1;

    private ScriptRunner() {
    }

    public static boolean run(Path script, Shell shell,
                              Consumer<String> out) {
        List<String> lines;
        try {
            lines = Files.readAllLines(script, StandardCharsets.UTF_8);
        } catch (IOException error) {
            out.accept("script error: cannot read script: " + script);
            return false;
        }
        return runLines(lines, shell, out);
    }

    private static boolean runLines(List<String> lines, Shell shell,
                                    Consumer<String> out) {
        for (int number = FIRST_LINE; number <= lines.size(); number++) {
            String line = lines.get(number - FIRST_LINE);
            if (!runLine(line, number, shell, out)) {
                return false;
            }
            if (shell.isExitRequested()) {
                return true;
            }
        }
        return true;
    }

    private static boolean runLine(String line, int number, Shell shell,
                                   Consumer<String> out) {
        if (line.isBlank()) {
            return true;
        }
        out.accept(Shell.PROMPT + line);
        if (line.trim().startsWith(COMMENT_PREFIX)) {
            return true;
        }
        try {
            String result = shell.execute(line);
            if (!result.isEmpty()) {
                out.accept(result);
            }
            return true;
        } catch (CommandException error) {
            out.accept(error.getMessage());
            out.accept("script error: stopped at line " + number);
            return false;
        }
    }
}