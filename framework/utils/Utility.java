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

import helper.Mapping;

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

    public static List<Class<?>> getAnnotedClasses(Class<? extends Annotation> annotation, List<Class<?>> listClass) throws Exception {
        List<Class<?>> keep = new ArrayList<>();

        for (Class<?> cl : listClass) {
            if (cl.isAnnotationPresent(annotation)) {
                keep.add(cl);
            }
        }
        return keep;
    }

    public static List<Method> getAnnotedMethod(Class<?> cl, Class<? extends Annotation> annotation) throws Exception {
        List<Method> keep = new ArrayList<>();
        Method[] methods = cl.getDeclaredMethods();

        for (Method m : methods) {
            m.setAccessible(true);

            if (m.isAnnotationPresent(annotation)) {
                keep.add(m);        
            }
        }

        return keep;
    }

    public static List<Field> getAnnotedFields(Class<?> cl, Class<? extends Annotation> annotation) {
        List<Field> keep = new ArrayList<>();
        Field[] fields = cl.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(annotation)) {
                keep.add(field);
            }
        }

        return keep;
    }
}
