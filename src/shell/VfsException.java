package shell;

public class VfsException extends CommandException {
    private static final long serialVersionUID = 1L;

    public VfsException(String message) {
        super("vfs error: " + message);
    }
}