package edu.escuelaing.arep;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ClassPathScanner {

    /**
     * Scans the base package and registers every discovered @RestController.
     */
    public static void scan(String basePackage) throws Exception {
        // "edu.escuelaing.arep" -> "edu/escuelaing/arep"
        String packagePath = basePackage.replace('.', '/');

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.toURI());

            if (directory.exists()) {
                for (String className : findClasses(directory, basePackage)) {
                    tryLoadController(className);
                }
            }
        }
    }

    private static void tryLoadController(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(RestController.class)) {
                System.out.println("  [SCAN] Loading: " + className);
                MicroSpringBoot.loadController(className);
            }
        } catch (Exception e) {
            // Ignore classes that cannot be loaded
        }
    }

    /**
     * Recursively walks the directory and returns class names.
     * directory = /home/.../target/classes/edu/escuelaing/arep
     * packageName = edu.escuelaing.arep
     * Result: ["edu.escuelaing.arep.HelloController", ...]
     */
    private static List<String> findClasses(File directory, String packageName) {
        List<String> classes = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null) return classes;

        for (File file : files) {
            if (file.isDirectory()) {
                // Subdirectory = subpackage: recursion
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                // Remove ".class" to get the simple class name
                classes.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
        return classes;
    }
}
