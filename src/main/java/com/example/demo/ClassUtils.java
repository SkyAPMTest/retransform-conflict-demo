package com.example.demo;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gongdewei 2020/6/2
 */
public class ClassUtils {

    public static Set<ClassLoader> getAllClassLoaders(Instrumentation inst) {
        Set<ClassLoader> classLoaderSet = new HashSet<ClassLoader>();
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null && !classLoaderSet.contains(classLoader)) {
                classLoaderSet.add(classLoader);
            }
        }
        return classLoaderSet;
    }

}
