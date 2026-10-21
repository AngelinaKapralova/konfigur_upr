package shell;

import java.util.List;


public final class ShellTest {
    private static final int EXIT_FAILURE = 1;
    private static boolean failed;

    private ShellTest() {
    }

    public static void main(String[] args) throws CommandException {
        testParser();
        testStubs();
        testErrors();
        testExit();
        if (failed) {
            System.exit(EXIT_FAILURE);
        }
        System.out.println("All tests passed");
    }

    private static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            System.out.println("OK   " + name);
        } else {
            failed = true;
            System.out.println("FAIL " + name + ": expected <" + expected
                    + ">, got <" + actual + ">");
        }
    }

    private static String run(Shell shell, String line) {
        try {
            return shell.execute(line);
        } catch (CommandException error) {
            return "ERROR: " + error.getMessage();
        }
    }

    private static void testParser() throws CommandException {
        check("plain", List.of("ls", "a", "b"),
                CommandParser.parse("ls a b"));
        check("quotes", List.of("cd", "My Docs"),
                CommandParser.parse("cd \"My Docs\""));
        check("extra spaces", List.of("ls", "a"),
                CommandParser.parse("   ls    a  "));
        check("empty arg", List.of("echo", ""),
                CommandParser.parse("echo \"\""));
        check("empty line", List.of(), CommandParser.parse("   "));
    }

    private static void testStubs() {
        Shell shell = new Shell();
        check("ls no args", "ls", run(shell, "ls").trim());
        check("ls args", "ls -l /home", run(shell, "ls -l /home"));
        check("cd quoted", "cd My Docs", run(shell, "cd \"My Docs\""));
        check("empty line", "", run(shell, ""));
    }

    private static void testErrors() {
        Shell shell = new Shell();
        check("unknown", "ERROR: foo: command not found",
                run(shell, "foo"));
        check("cd extra", "ERROR: cd: too many arguments",
                run(shell, "cd a b"));
        check("exit extra", "ERROR: exit: too many arguments",
                run(shell, "exit now"));
        check("quote", "ERROR: parse error: unclosed quote",
                run(shell, "ls \"abc"));
    }

    private static void testExit() {
        Shell shell = new Shell();
        check("not yet", false, shell.isExitRequested());
        run(shell, "exit");
        check("exit flag", true, shell.isExitRequested());
    }
}