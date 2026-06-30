package helper;

import java.util.Objects;

public final class UrlMethod {
    private final String url;
    private final String method;

    public UrlMethod(String url, String method) {
        this.url = Objects.requireNonNull(url, "url cannot be null");
        this.method = Objects.requireNonNull(method, "method cannot be null");
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof UrlMethod u) {
            return url.equals(u.url) && method.equals(u.method);
        }
        return false;
    }

    // 2. INDISPENSABLE pour la HashMap
    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    public String toString() {
        return String.format("%s::%s", url, method);
    }
}