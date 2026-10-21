package shell;

import java.util.List;

public class Shell {
    public static final String PROMPT = "vfs$ ";

    private static final int COMMAND_INDEX = 0;
    private static final int ARGS_START = 1;
    private static final int CD_MAX_ARGS = 1;
    private static final int NO_ARGS = 0;

    private final Config config;
    private boolean exitRequested;

    public Shell() {
        this(Config.empty());
    }

    public Shell(Config config) {
        this.config = config;
    }

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
            case "conf-dump":
                requireMaxArgs(name, args, NO_ARGS);
                return config.dump();
            case "exit":
                requireMaxArgs(name, args, NO_ARGS);
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
        if (args.isEmpty()) {
            return name;
        }
        return name + " " + String.join(" ", args);
    }

    private void requireMaxArgs(String name, List<String> args, int max)
            throws CommandException {
        if (args.size() > max) {
            throw new CommandException(name + ": too many arguments");
        }
    }
}