package io.github.kuromeforever.thefooldarkdoppelgangermorph;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class ContractTestSupport {
    private ContractTestSupport() {
    }

    static Path projectRoot() {
        return Path.of(requireProperty("thefool.darkdoppelganger.project.root"));
    }

    static Path sourceJar() {
        return Path.of(requireProperty("thefool.darkdoppelganger.contract.jar"));
    }

    static Path adapterJar() {
        return Path.of(requireProperty("thefool.darkdoppelganger.adapter.jar"));
    }

    static String source(String relativePath) throws IOException {
        return Files.readString(projectRoot().resolve(relativePath), StandardCharsets.UTF_8);
    }

    static String zipText(ZipFile zip, String path) throws IOException {
        ZipEntry entry = zip.getEntry(path);
        if (entry == null) {
            throw new IOException("Missing zip entry " + path);
        }
        try (var input = zip.getInputStream(entry)) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static ClassNode classNode(ZipFile zip, String internalName) throws IOException {
        ZipEntry entry = zip.getEntry(internalName + ".class");
        if (entry == null) {
            throw new IOException("Missing class " + internalName);
        }
        ClassNode node = new ClassNode();
        try (var input = zip.getInputStream(entry)) {
            new ClassReader(input).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        }
        return node;
    }

    static String sha256(Path path) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (var input = Files.newInputStream(path)) {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                digest.update(buffer, 0, read);
            }
        }
        return HexFormat.of().withUpperCase().formatHex(digest.digest());
    }

    private static String requireProperty(String key) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing test system property " + key);
        }
        return value;
    }
}
