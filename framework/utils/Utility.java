package utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
// import java.lang.annotation.Annotation;
// import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
// import java.lang.reflect.Method;
// import java.nio.file.DirectoryStream;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.util.ArrayList;
// import java.util.Arrays;
import java.util.List;
// import java.util.Map;

// import annotation.UrlMapping;
// // import jakarta.servlet.http.*;
// import helper.HttpMethod;
// import helper.Mapping;
// import helper.UrlMethod;

public class Utility {

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
        Field[] champs = obj.getClass().getDeclaredFields();
        StringBuffer sb = new StringBuffer("{");
        String format = "%s : %s";
        for (int i = 0; i < champs.length; i++) {
            champs[i].setAccessible(true);
            Object value = champs[i].get(obj);
            String name = champs[i].getName();
            Class<?> type = champs[i].getType();
            if (value == null)
                continue;
            if (type.isPrimitive() || type == String.class) {
                sb.append(String.format(format, name, String.valueOf(value)));
            } else {
                sb.append(String.format(format, name, toJson(value)));
            }
            if (i < champs.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    // public static String getUrlFromRequest(HttpServletRequest request) {
    // String URI = request.getRequestURI().substring(1);
    // String[] parts = URI.split("/");
    // String url = "/" + String.join("/", Arrays.copyOfRange(parts, 1,
    // parts.length));
    // return url;
    // }
}
