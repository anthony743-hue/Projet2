package utils;

import constants.HttpCode;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public final class Responder {

    private Responder() {}

    public static void json(HttpServletResponse response, HttpCode code, String body) throws IOException {
        send(response, code, body, "application/json");
    }

    public static void text(HttpServletResponse response, HttpCode code, String body) throws IOException {
        send(response, code, body, "text/plain");
    }

    public static void html(HttpServletResponse response, HttpCode code, String body) throws IOException {
        send(response, code, body, "text/html");
    }

    public static void error(HttpServletResponse response, HttpCode code, String message) throws IOException {
        String body = String.format("{\"error\": \"%s\", \"status\": %d}", escape(message), code.getCode());
        json(response, code, body);
    }

    public static void send(HttpServletResponse response, HttpCode code, String body, String contentType) throws IOException {
        response.setStatus(code.getCode());
        response.setContentType(contentType + ";charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.write(body);
            out.flush();
        }
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}