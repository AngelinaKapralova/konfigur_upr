package shell;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConfigTest {
    private static final int EXIT_FAILURE = 1;
    private static boolean failed;
    private static final String DUMP =
            "vfs-path=<not set>\nscript-path=<not set>";
    private ConfigTest() {
    }

    public static void main(String[] args) throws Exception {
        testArgs();
        testArgErrors();
        testConfDump();
        testScriptOk();
        testScriptError();
        testScriptMissing();
        if (failed) {
            System.exit(EXIT_FAILURE);
        }
        System.out.println("All config tests passed");
    }

    private static void check(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) {
            System.out.println("OK   " + name);
        } else {
            failed = true;
            System.out.println("FAIL " + name + ": expected <" + expected
                    + ">, got <" + actual + ">");
        }
    }

    private static String parseError(String... args) {
        try {
            ArgsParser.parse(args);
            return "no error";
        } catch (CommandException error) {
            return error.getMessage();
        }
    }

    private static String run(Shell shell, String line) {
        try {
            return shell.execute(line);
        } catch (CommandException error) {
            return "ERROR: " + error.getMessage();
        }
    }

    private static boolean runScript(List<String> lines, List<String> out)
            throws IOException {
        Path file = Files.createTempFile("script", ".txt");
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
            return ScriptRunner.run(file, new Shell(), out::add);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static void testArgs() throws CommandException {
        Config none = ArgsParser.parse(new String[0]);
        check("no args vfs", null, none.getVfsPath());
        check("no args script", false, none.hasScript());
        Config both = ArgsParser.parse(
                new String[] {"--vfs", "dir", "--script", "s.txt"});
        check("vfs path", "dir", both.getVfsPath());
        check("script path", "s.txt", both.getScriptPath());
    }

    private static void testArgErrors() {
        check("unknown option", "unknown option: --foo",
                parseError("--foo"));
        check("missing value", "option --vfs requires a value",
                parseError("--vfs"));
        check("missing script value", "option --script requires a value",
                parseError("--vfs", "d", "--script"));
    }

    private static void testConfDump() {
        Shell shell = new Shell(new Config("vfsdir", null));
        check("conf-dump", "vfs-path=vfsdir\nscript-path=<not set>",
                run(shell, "conf-dump"));
        check("conf-dump args", "ERROR: conf-dump: too many arguments",
                run(shell, "conf-dump x"));
    }

    private static void testScriptOk() throws IOException {
        List<String> out = new ArrayList<>();
        boolean ok = runScript(
                List.of("conf-dump", "# note", "", "conf-dump"), out);
        check("script ok result", true, ok);
        check("script ok output", List.of("vfs$ conf-dump", DUMP,
                "vfs$ # note", "vfs$ conf-dump", DUMP), out);
    }

    private static void testScriptError() throws IOException {
        List<String> out = new ArrayList<>();
        boolean ok = runScript(List.of("conf-dump", "foo", "conf-dump"), out);
        check("script error result", false, ok);
        check("script error output", List.of("vfs$ conf-dump", DUMP,
                "vfs$ foo", "foo: command not found",
                "script error: stopped at line 2"), out);
    }

    private static void testScriptMissing() {
        List<String> out = new ArrayList<>();
        boolean ok = ScriptRunner.run(Path.of("no-such-script.txt"),
                new Shell(), out::add);
        check("missing result", false, ok);
        check("missing message", true,
                out.get(0).startsWith("script error: cannot read script"));
    }
}