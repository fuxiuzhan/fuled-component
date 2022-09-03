package com.fuled.component.tools.dynamic.compile;

import com.fuled.component.tools.dynamic.compile.loader.DynamicLoaderEngine;

import java.lang.reflect.Method;

public class AppMain {
    public void main(String[] args) {
        String code = "package com.fuled.component.tools.dynamic.compile;\n" +
                "\n" +
                "public class TestClass {\n" +
                "\n" +
                "    public String sayHi(String name) {\n" +
                "        return \"hi \" + name;\n" +
                "    }\n" +
                "}\n";

        try {
            //字节码可以存储起来，可以在集群环境使用，使用时直接加载
            byte[] compile = DynamicLoaderEngine.compile(code);
            Class<?> aClass = DynamicLoaderEngine.loadClass(code);
            Object o = aClass.newInstance();
            Method sayHi = o.getClass().getDeclaredMethod("sayHi", new Class[]{String.class});
            Object tester = sayHi.invoke(o, "tester");
            System.out.printf("res->" + tester);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.printf("11");
    }
}
