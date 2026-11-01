package wrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class FileWrapper {
    private final String name;
    private final String extension;
    private final long size;
    private final String mimeType;
    private final byte[] content;

    private FileWrapper(String name, String extension, long size, String mimeType, byte[] content) {
        this.name = name;
        this.extension = extension;
        this.size = size;
        this.mimeType = mimeType;
        this.content = content;
    }

    public static FileWrapper from(InputStream in, String filename, String mimeType) throws IOException {
        Objects.requireNonNull(in, "in ne peut pas être null");
        Objects.requireNonNull(filename, "filename ne peut pas être null");

        byte[] content = in.readAllBytes();
        String cleanName = Path.of(filename).getFileName().toString();
        String extension = extractExtension(cleanName);
        String mime = (mimeType != null) ? mimeType : "application/octet-stream";

        return new FileWrapper(cleanName, extension, content.length, mime, content);
    }

    // MODIFICATION : Prend une String en paramètre
    public void save(String uploadDir) throws IOException {
        Path directory = Path.of(uploadDir);
        Files.createDirectories(directory); // Crée le dossier s'il n'existe pas
        Path target = directory.resolve(this.name);
        Files.write(target, this.content);
    }

    public String name()      { return name; }
    public String extension() { return extension; }
    public long size()        { return size; }
    public String mimeType()  { return mimeType; }

    public byte[] readBytes() { return content.clone(); }
    public String readString() { return new String(content, StandardCharsets.UTF_8); }
    public List<String> readLines() { return readString().lines().toList(); }
    public InputStream openStream() { return new ByteArrayInputStream(content); }

    private static String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot < 0 || dot == filename.length() - 1) ? "" : filename.substring(dot + 1).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileWrapper other)) return false;
        return name.equals(other.name) && extension.equals(other.extension) && Arrays.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + extension.hashCode();
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}