package shell;

public final class Vfs {
    private final String name;
    private final VfsNode root;

    public Vfs(String name, VfsNode root) {
        this.name = name;
        this.root = root;
    }

    public String getName() {
        return name;
    }

    public VfsNode getRoot() {
        return root;
    }

    public String getHash() {
        return VfsHasher.hash(root);
    }
}