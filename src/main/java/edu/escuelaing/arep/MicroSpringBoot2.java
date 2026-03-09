package edu.escuelaing.arep;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MicroSpringBoot2{
    static Map<String,Method> controllerMethod = new HashMap<>();

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        System.out.println("Loading controller classes...");
        Class<?> c = Class.forName(args[0]);

        if(c.isAnnotationPresent(RestController.class)){
            for(Method m : c.getDeclaredMethods()){
                if(m.isAnnotationPresent(GetMapping.class)){
                    GetMapping a = m.getAnnotation(GetMapping.class);
                    controllerMethod.put(a.value(),m);
                }
            }
        }

        String path = args[1];

        System.out.println("Executing web method for path: "+path);

        Method m = controllerMethod.get(path);

        System.out.println(m.invoke(null));

    }

}