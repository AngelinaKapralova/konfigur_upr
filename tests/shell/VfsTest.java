package shell;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


public final class VfsTest {
    private static final int EXIT_FAILURE = 1;
    private static final int HASH_LENGTH = 64;
    private static final String EMPTY_DIR_HASH =
            "ea8a9e7f130ea1445018d6a78feead90c95c95cf47f867b73d96faf3f19f9905";
    private static boolean failed;

    private VfsTest() {
    }

    public static void main(String[] args) throws Exception {
        testLoad();
        testHash();
        testEmptyHash();
        testLoadErrors();
        testVfsInfo();
        if (failed) {
            System.exit(EXIT_FAILURE);
        }
        System.out.println("All vfs tests passed");
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

    private static String loadError(String path) {
        try {
            VfsLoader.load(path);
            return "no error";
        } catch (VfsException error) {
            return error.getMessage();
        }
    }

    private static Path createTree() throws IOException {
        Path root = Files.createTempDirectory("vfs-test");
        Files.writeString(root.resolve("a.txt"), "hello");
        Path sub = Files.createDirectory(root.resolve("sub"));
        Files.writeString(sub.resolve("b.txt"), "world");
        return root;
    }

    private static void deleteTree(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> path.toFile().delete());
        }
    }

    private static void testLoad() throws Exception {
        Path dir = createTree();
        try {
            Vfs vfs = VfsLoader.load(dir.toString());
            check("vfs name", dir.getFileName().toString(), vfs.getName());
            check("root children", List.of("a.txt", "sub"),
                    List.copyOf(vfs.getRoot().getChildren().keySet()));
            VfsNode sub = vfs.getRoot().getChildren().get("sub");
            VfsNode file = sub.getChildren().get("b.txt");
            check("file content", "world",
                    new String(file.getContent(), StandardCharsets.UTF_8));
        } finally {
            deleteTree(dir);
        }
    }

    private static void testHash() throws Exception {
        Path dir = createTree();
        try {
            String first = VfsLoader.load(dir.toString()).getHash();
            check("hash length", HASH_LENGTH, first.length());
            check("hash stable", first,
                    VfsLoader.load(dir.toString()).getHash());
            Files.writeString(dir.resolve("a.txt"), "changed");
            String second = VfsLoader.load(dir.toString()).getHash();
            check("hash changes", false, first.equals(second));
        } finally {
            deleteTree(dir);
        }
    }

    private static void testEmptyHash() throws Exception {
        Path dir = Files.createTempDirectory("vfs-empty");
        try {
            check("empty hash", EMPTY_DIR_HASH,
                    VfsLoader.load(dir.toString()).getHash());
        } finally {
            deleteTree(dir);
        }
    }

    private static void testLoadErrors() throws IOException {
        check("not found", "vfs error: path not found: no-such-dir",
                loadError("no-such-dir"));
        Path file = Files.createTempFile("vfs-file", ".txt");
        try {
            check("not a directory",
                    "vfs error: invalid format, not a directory: " + file,
                    loadError(file.toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    private static void testVfsInfo() throws Exception {
        Path dir = createTree();
        try {
            Vfs vfs = VfsLoader.load(dir.toString());
            Shell shell = new Shell(Config.empty(), vfs);
            check("vfs-info", "vfs-name=" + vfs.getName() + "\n"
                    + "vfs-sha256=" + vfs.getHash(), run(shell, "vfs-info"));
            check("vfs-info args", "ERROR: vfs-info: too many arguments",
                    run(shell, "vfs-info x"));
        } finally {
            deleteTree(dir);
        }
        check("vfs-info none", "ERROR: vfs-info: no VFS loaded",
                run(new Shell(), "vfs-info"));
    }
}