package shell;

public final class ArgsParser {
    public static final String VFS_OPTION = "--vfs";
    public static final String SCRIPT_OPTION = "--script";

    private static final int VALUE_OFFSET = 1;
    private static final int STEP = 2;

    private ArgsParser() {
    }

    public static Config parse(String[] args) throws CommandException {
        String vfsPath = null;
        String scriptPath = null;
        int index = 0;
        while (index < args.length) {
            String option = args[index];
            if (!isKnown(option)) {
                throw new CommandException("unknown option: " + option);
            }
            String value = readValue(args, index);
            if (VFS_OPTION.equals(option)) {
                vfsPath = value;
            } else {
                scriptPath = value;
            }
            index += STEP;
        }
        return new Config(vfsPath, scriptPath);
    }

    private static boolean isKnown(String option) {
        return VFS_OPTION.equals(option) || SCRIPT_OPTION.equals(option);
    }

    private static String readValue(String[] args, int index)
            throws CommandException {
        int valueIndex = index + VALUE_OFFSET;
        if (valueIndex >= args.length) {
            throw new CommandException(
                    "option " + args[index] + " requires a value");
        }
        return args[valueIndex];
    }
}