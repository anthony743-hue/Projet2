package utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Converter {

    private static final Set<Class<?>> SIMPLE_TYPES = Set.of(
        String.class, Integer.class, int.class, Long.class, long.class,
        Double.class, double.class, Boolean.class, boolean.class,
        BigDecimal.class, LocalDate.class, LocalDateTime.class
    );

    public static boolean isSimpleType(Class<?> type) {
        return SIMPLE_TYPES.contains(type);
    }

    public static Object convert(String rawValue, Class<?> type) {
        if (type == null) throw new IllegalArgumentException("type must not be null");

        // 1. Gestion des valeurs nulles (valeurs par défaut pour les primitifs)
        if (rawValue == null) {
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == double.class) return 0.0d;
            if (type == boolean.class) return false;
            return null;
        }

        // 2. Parsing direct (les primitifs et leurs wrappers renvoient le même objet grâce à l'autoboxing)
        if (type == String.class) return rawValue;
        if (type == Integer.class || type == int.class) return Integer.valueOf(rawValue);
        if (type == Long.class || type == long.class) return Long.valueOf(rawValue);
        if (type == Double.class || type == double.class) return Double.valueOf(rawValue);
        if (type == Boolean.class || type == boolean.class) return Boolean.valueOf(rawValue);
        if (type == BigDecimal.class) return new BigDecimal(rawValue);
        if (type == LocalDate.class) return LocalDate.parse(rawValue, DateTimeFormatter.ISO_LOCAL_DATE);
        if (type == LocalDateTime.class) return LocalDateTime.parse(rawValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        throw new IllegalArgumentException("Unsupported parameter type: " + type.getName());
    }
}