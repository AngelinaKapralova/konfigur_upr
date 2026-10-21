package shell;

public final class Config {
    private static final String NOT_SET = "<not set>";

    private final String vfsPath;
    private final String scriptPath;

    public Config(String vfsPath, String scriptPath) {
        this.vfsPath = vfsPath;
        this.scriptPath = scriptPath;
    }

    public static Config empty() {
        return new Config(null, null);
    }

    public String getVfsPath() {
        return vfsPath;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public boolean hasScript() {
        return scriptPath != null;
    }

    public String dump() {
        return "vfs-path=" + valueOrDefault(vfsPath) + "\n"
                + "script-path=" + valueOrDefault(scriptPath);
    }

    private static String valueOrDefault(String value) {
        return value == null ? NOT_SET : value;
    }
}