package utils;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.*;
import wrapper.FileWrapper;

public class Utility {

    public static String getUrlFromRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int slashIndex = uri.indexOf('/', 1);

        return (slashIndex == -1) ? "/" : uri.substring(slashIndex);
    }

    public static Object resolveParameter(Parameter parameter, HttpServletRequest request) throws Exception {
        Class<?> type = parameter.getType();
        String name = parameter.getName();

        if (Converter.isSimpleType(type)) {
            String rawField = request.getParameter(name);
            if (rawField == null) {
                throw new Exception(Messages.get("error.param.missing", request.getLocale(), name));
            }
            return Converter.convert(rawField, type);
        }

        Object instance = type.getConstructor().newInstance();
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            String rawField = request.getParameter(field.getName());
            if (rawField == null)
                continue;

            Object value = Converter.convert(rawField, field.getType());
            field.set(instance, value);
        }
        return instance;
    }

    public static void getClassInPackage(String packageName, List<Class<?>> classes) throws Exception {
        String packagePath = packageName.replace('.', '/');
        File dir = new File(Thread.currentThread().getContextClassLoader()
                .getResource(packagePath).toURI());
        String fileName = null;
        String className = null;
        for (File f : dir.listFiles()) {
            fileName = f.getName();
            if (fileName.endsWith(".class")) {
                className = fileName.substring(0, fileName.length() - 6);
                classes.add(Class.forName(packageName + "." + className));
            } else if (f.isDirectory()) {
                getClassInPackage(packageName + "." + fileName, classes);
            }
        }
    }

    public static String toJson(Object obj) throws IllegalArgumentException, IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        toJson(obj, sb);
        return sb.toString();
    }

    public static Object getFieldValue(Object obj, String fieldName) throws IllegalAccessException {
        if (obj == null)
            throw new IllegalArgumentException("L'objet ne peut pas être null");
        if (fieldName == null)
            throw new IllegalArgumentException("Le nom du champ ne peut pas être null");

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new IllegalArgumentException(
                "Champ '" + fieldName + "' introuvable dans " + obj.getClass().getName());
    }

    private static void toJson(Object obj, StringBuilder sb) throws IllegalArgumentException, IllegalAccessException {
        if (obj == null) {
            sb.append("null");
            return;
        }

        if (obj instanceof String s) {
            sb.append("\"").append(s.replace("\"", "\\\"")).append("\"");
            return;
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            sb.append(obj);
            return;
        }

        Field[] champs = obj.getClass().getDeclaredFields();
        sb.append("{");
        boolean first = true;

        for (Field champ : champs) {
            champ.setAccessible(true);
            Object value = champ.get(obj);

            if (value == null)
                continue;

            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("\"").append(champ.getName()).append("\":");

            toJson(value, sb);
        }
        sb.append("}");
    }
}