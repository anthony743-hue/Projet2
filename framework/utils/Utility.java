package utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static List<Class<?>> getClassInPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();

        String packagePath = packageName.replace('.', '/');

        // Récupérer le dossier du package via le classloader
        var classLoader = Thread.currentThread().getContextClassLoader();
        var resource = classLoader.getResource(packagePath);
        if (resource == null) {
            throw new Exception("On ne peut pas lire le fichier");
        }
        Path dossier = Path.of(resource.toURI());

        if (!Files.isDirectory(dossier)) {
            throw new Exception("Ce n'est pas un dossier");
        }

        String fileName = null, simpleClassName = null, fullClassName = null;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dossier)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry) && entry.getFileName().toString().endsWith(".class")) {
                    fileName = entry.getFileName().toString();
                    simpleClassName = fileName.substring(0, fileName.lastIndexOf('.'));
                    fullClassName = packageName + "." + simpleClassName;

                    Class<?> clazz = Class.forName(fullClassName);
                    classes.add(clazz);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception(e);
        } catch (Exception e) {
            throw new Exception(e);
        }

        return classes;
    }

    public static List<String> getAnnoted(String packageName,
            Class<? extends Annotation> annotation,
            ElementType type) throws Exception {
        List<Class<?>> ls = getClassInPackage(packageName);
        List<String> keep = new ArrayList<>();

        if (type == ElementType.TYPE) {
            for (Class<?> cl : ls) {
                if (cl.isAnnotationPresent(annotation)) {
                    keep.add(cl.getPackageName() + "." + cl.getSimpleName());
                }
            }
        } else if (type == ElementType.METHOD) {
            for (Class<?> cl : ls) {
                keep.addAll(getAnnotedMethod(cl, annotation));
            }
        } else if (type == ElementType.FIELD) {
            for (Class<?> cl : ls) {
                keep.addAll(getAnnotedFields(cl, annotation));
            }
        }
        return keep;
    }

    private static List<String> getAnnotedMethod(Class<?> cl, Class<? extends Annotation> annotation) {
        List<String> keep = new ArrayList<>();
        Method[] methods = cl.getDeclaredMethods();
        String nom = null;
        for (Method m : methods) {
            m.setAccessible(true);

            if (m.isAnnotationPresent(annotation)) {
                nom = m.getName();
                keep.add(cl.getName() + "." + nom);
            }
        }

        return keep;
    }

    private static List<String> getAnnotedFields(Class<?> cl, Class<? extends Annotation> annotation) {
        List<String> keep = new ArrayList<>();
        Field[] fields = cl.getDeclaredFields();
        String nom = null;
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(annotation)) {
                nom = field.getName();
                keep.add(cl.getName() + "." + nom);
            }
        }

        return keep;
    }
}
