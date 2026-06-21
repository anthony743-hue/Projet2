package helper;

import java.util.regex.Pattern;

public class UrlValidator {

    private static final Pattern URL_PATTERN = Pattern.compile("^/[a-zA-Z0-9\\-_./]*$");

    public static boolean isValid(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
}
