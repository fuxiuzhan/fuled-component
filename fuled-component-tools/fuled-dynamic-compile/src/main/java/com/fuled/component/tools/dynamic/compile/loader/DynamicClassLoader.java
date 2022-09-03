package com.fuled.component.tools.dynamic.compile.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class DynamicClassLoader extends URLClassLoader {
    public DynamicClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class<?> findClassByClassName(String className) throws ClassNotFoundException {
        return this.findClass(className);
    }

    public Class<?> loadClassByBytes(byte[] classData) {
        return this.defineClass(null, classData, 0, classData.length);
    }
}