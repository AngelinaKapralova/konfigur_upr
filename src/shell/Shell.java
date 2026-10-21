package shell;

import java.util.List;

public class Shell {
    private static final int COMMAND_INDEX = 0;
    private static final int ARGS_START = 1;
    private static final int CD_MAX_ARGS = 1;
    private static final int EXIT_MAX_ARGS = 0;

    private boolean exitRequested;
    public String execute(String line) throws CommandException {
        List<String> tokens = CommandParser.parse(line);
        if (tokens.isEmpty()) {
            return "";
        }
        String name = tokens.get(COMMAND_INDEX);
        List<String> args = tokens.subList(ARGS_START, tokens.size());
        switch (name) {
            case "ls":
                return describe(name, args);
            case "cd":
                requireMaxArgs(name, args, CD_MAX_ARGS);
                return describe(name, args);
            case "exit":
                requireMaxArgs(name, args, EXIT_MAX_ARGS);
                exitRequested = true;
                return "";
            default:
                throw new CommandException(name + ": command not found");
        }
    }

    public boolean isExitRequested() {
        return exitRequested;
    }
    private String describe(String name, List<String> args) {
        return name + " " + String.join(" ", args);
    }
    private void requireMaxArgs(String name, List<String> args, int max)
            throws CommandException {
        if (args.size() > max) {
            throw new CommandException(name + ": too many arguments");
        }
    }
}