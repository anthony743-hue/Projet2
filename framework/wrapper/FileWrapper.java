package wrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class FileWrapper {

    private final String name;
    private final String extension;
    private final byte[] content;

    private FileWrapper(String name, String extension, byte[] content) {
        this.name = name;
        this.extension = extension;
        this.content = content;
    }

    public static FileWrapper from(InputStream in, String fileName) throws IOException {
        Objects.requireNonNull(in,"in ne peut pas etre null");
        Objects.requireNonNull(fileName, "Le fileName ne peut pas etre null");
        byte[] content = in.readAllBytes();
        String name = Path.of(fileName).getFileName().toString();
        String extension = extractExtension(name);

        return new FileWrapper(name, extension, content);
    }

    public String name() {
        return name;
    }

    public String extension() {
        return extension;
    }

    public byte[] readBytes() {
        return content.clone();
    }

    public String readString() {
        return readString(StandardCharsets.UTF_8);
    }

    public String readString(Charset charset) {
        return new String(content, charset);
    }

    public List<String> readLines() {
        return readLines(StandardCharsets.UTF_8);
    }

    public List<String> readLines(Charset charset) {
        return readString(charset).lines().toList();
    }

    public InputStream openStream() {
        return new ByteArrayInputStream(content);
    }

    private static String extractExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FileWrapper other))
            return false;
        return name.equals(other.name)
                && extension.equals(other.extension)
                && Arrays.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + extension.hashCode();
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}