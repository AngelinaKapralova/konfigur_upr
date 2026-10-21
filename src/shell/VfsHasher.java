package shell;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class VfsHasher {
    private static final String ALGORITHM = "SHA-256";

    private VfsHasher() {
    }

    public static String hash(VfsNode root) {
        MessageDigest digest = newDigest();
        update(digest, "", root);

        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException(ALGORITHM + " unavailable", error);
        }
    }

    private static void update(MessageDigest digest, String path,
                               VfsNode node) {
        if (node.isDirectory()) {
            digest.update(bytes("D:" + path + "\0"));
            for (VfsNode child : node.getChildren().values()) {
                update(digest, path + "/" + child.getName(), child);
            }
        } else {
            byte[] content = node.getContent();
            digest.update(bytes("F:" + path + ":" + content.length + "\0"));
            digest.update(content);
        }
    }

    private static byte[] bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }
}