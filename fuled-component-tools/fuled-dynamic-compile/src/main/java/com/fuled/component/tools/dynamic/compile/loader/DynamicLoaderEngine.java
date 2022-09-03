package com.fuled.component.tools.dynamic.compile.loader;

import com.fuled.component.tools.dynamic.compile.compiler.ClassCompiler;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * 动态加载引擎，负责动态编译、加载类
 *
 * @author xbc
 * @date 2019年1月9日
 */
public class DynamicLoaderEngine {

    public static byte[] compile(String code) {
        return compile(code, new PrintWriter(new ByteArrayOutputStream()), Arrays.asList());
    }

    public static byte[] compile(String javaCode, PrintWriter out, List<String> options) {
        ClassCompiler classCompiler = new ClassCompiler();
        byte[] classBytes = classCompiler.compile(javaCode, out, options);
        if (null == classBytes) {
            return null;
        }
        return classBytes;
    }

    public static Class<?> loadClass(DynamicClassLoader classLoader, byte[] classBytes, PrintWriter out) {
        Class<?> dynamicClass = classLoader.loadClassByBytes(classBytes);
        if (null == dynamicClass) {
            out.println("Failed to load class.");
            return null;
        }
        return dynamicClass;
    }

    /**
     * 加载类
     * 加载失败，则返回null, 同时out中包含错误信息
     *
     * @param classLoader 类加载器
     * @param javaCode    源码
     * @param out         错误信息输出
     * @param options     编译过程中的参数
     * @return
     */
    public static Class<?> loadClass(DynamicClassLoader classLoader, String javaCode, PrintWriter out, List<String> options) {
        byte[] classBytes = compile(javaCode, out, options);
        if (null == classBytes) {
            return null;
        }
        return loadClass(classLoader, classBytes, out);
    }

    public static Class<?> loadClass(String code) {
        DynamicClassLoader dynamicClassLoader = new DynamicClassLoader((URLClassLoader) Thread.currentThread().getContextClassLoader());
        return loadClass(dynamicClassLoader, code, new PrintWriter(new ByteArrayOutputStream()), Arrays.asList());
    }
}