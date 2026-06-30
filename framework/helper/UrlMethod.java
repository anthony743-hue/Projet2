package helper;

import java.util.Objects;

public final class UrlMethod {
    private final String url;
    private final HttpMethod method;

    public UrlMethod(String url, HttpMethod method) {
        this.url = Objects.requireNonNull(url, "url cannot be null");
        this.method = Objects.requireNonNull(method, "method cannot be null");
    }

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UrlMethod u) {
            return url.equals(u.url) && method == u.method;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    public String toString() {
        return String.format("%s::%s", url, method);
    }
}