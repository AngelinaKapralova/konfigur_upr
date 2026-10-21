package shell;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class VfsNode {
    private final String name;
    private final boolean directory;
    private final byte[] content;
    private final Map<String, VfsNode> children = new TreeMap<>();

    private VfsNode(String name, boolean directory, byte[] content) {
        this.name = name;
        this.directory = directory;
        this.content = content;
    }

    public static VfsNode directory(String name) {
        return new VfsNode(name, true, new byte[0]);
    }

    public static VfsNode file(String name, byte[] content) {
        return new VfsNode(name, false, content);
    }

    public String getName() {
        return name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public byte[] getContent() {
        return content;
    }

    public Map<String, VfsNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public void addChild(VfsNode child) {
        if (!directory) {
            throw new IllegalStateException("not a directory: " + name);
        }
        children.put(child.getName(), child);
    }
}