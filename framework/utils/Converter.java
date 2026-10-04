package utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converter {

    public static Object convert(String rawValue, Class<?> targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException("targetType must not be null");
        }

        if (rawValue == null) {
            return defaultValue(targetType);
        }

        return parseValue(rawValue, targetType);
    }

    private static Object defaultValue(Class<?> targetType) {
        if (targetType == int.class)
            return 0;
        if (targetType == long.class)
            return 0L;
        if (targetType == double.class)
            return 0.0d;
        if (targetType == boolean.class)
            return false;

        // Pour tous les objets : null
        return null;
    }

    private static Object parseValue(String rawValue, Class<?> targetType) {
        Class<?> type = toWrapper(targetType);

        if (type == String.class)
            return rawValue;
        if (type == Integer.class)
            return Integer.valueOf(rawValue);
        if (type == Long.class)
            return Long.valueOf(rawValue);
        if (type == Double.class)
            return Double.valueOf(rawValue);
        if (type == Boolean.class)
            return Boolean.valueOf(rawValue);
        if (type == BigDecimal.class)
            return new BigDecimal(rawValue);
        if (type == LocalDate.class)
            return LocalDate.parse(rawValue, DateTimeFormatter.ISO_LOCAL_DATE);
        if (type == LocalDateTime.class)
            return LocalDateTime.parse(rawValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        throw new IllegalArgumentException("Unsupported parameter type: " + targetType.getName());
    }

    public static boolean isSimpleType(Class<?> type) {
        return type == String.class
                || type == Integer.class || type == int.class
                || type == Long.class || type == long.class
                || type == Double.class || type == double.class
                || type == Boolean.class || type == boolean.class
                || type == BigDecimal.class
                || type == LocalDate.class
                || type == LocalDateTime.class;
    }

    private static Class<?> toWrapper(Class<?> targetType) {
        if (targetType == int.class)
            return Integer.class;
        if (targetType == long.class)
            return Long.class;
        if (targetType == double.class)
            return Double.class;
        if (targetType == boolean.class)
            return Boolean.class;
        return targetType;
    }
}