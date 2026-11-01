package constants;

public enum HttpCode {

    // 2xx - Succès
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),

    // 3xx - Redirection
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    NOT_MODIFIED(304, "Not Modified"),

    // 4xx - Erreur client
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    // 5xx - Erreur serveur
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable");

    private final int code;
    private final String reason;

    HttpCode(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode()   { return code; }
    public String getReason() { return reason; }

    // --- Helpers de catégorie ---

    public boolean isSuccess()     { return code >= 200 && code < 300; }
    public boolean isRedirect()    { return code >= 300 && code < 400; }
    public boolean isClientError() { return code >= 400 && code < 500; }
    public boolean isServerError() { return code >= 500; }
    public boolean isError()       { return code >= 400; }

    // --- Récupérer une enum à partir de son code numérique ---

    public static HttpCode fromCode(int code) {
        for (HttpCode hc : values()) {
            if (hc.code == code) return hc;
        }
        throw new IllegalArgumentException("Code HTTP inconnu : " + code);
    }
}