package shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class VfsLoader {
    private static final String ROOT_NAME = "/";

    private VfsLoader() {
    }

    public static Vfs load(String pathText) throws VfsException {
        Path dir = toPath(pathText);
        if (!Files.exists(dir)) {
            throw new VfsException("path not found: " + pathText);
        }
        if (!Files.isDirectory(dir)) {
            throw new VfsException(
                    "invalid format, not a directory: " + pathText);
        }
        VfsNode root = VfsNode.directory("");
        readChildren(dir, root);
        return new Vfs(vfsName(dir), root);
    }

    private static Path toPath(String pathText) throws VfsException {
        try {
            return Path.of(pathText);
        } catch (InvalidPathException error) {
            throw new VfsException("invalid path: " + pathText);
        }
    }

    private static String vfsName(Path dir) {
        Path fileName = dir.toAbsolutePath().normalize().getFileName();
        return fileName == null ? ROOT_NAME : fileName.toString();
    }

    private static void readChildren(Path dir, VfsNode parent)
            throws VfsException {
        for (Path entry : listEntries(dir)) {
            String name = entry.getFileName().toString();
            if (Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS)) {
                VfsNode child = VfsNode.directory(name);
                readChildren(entry, child);
                parent.addChild(child);
            } else {
                parent.addChild(VfsNode.file(name, readBytes(entry)));
            }
        }
    }

    private static List<Path> listEntries(Path dir) throws VfsException {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream.collect(java.util.stream.Collectors.toList());
        } catch (IOException error) {
            throw new VfsException("cannot read directory: " + dir);
        }
    }

    private static byte[] readBytes(Path file) throws VfsException {
        try {
            return Files.readAllBytes(file);
        } catch (IOException error) {
            throw new VfsException("cannot read file: " + file);
        }
    }
}