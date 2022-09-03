package com.fuled.component.tools.dynamic.compile.compiler;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * 存放java源码的JavaFileObject
 *
 *
 * @author xbc
 * @date 2019年1月9日 
 *
 */
public class SourceJavaFileObject extends SimpleJavaFileObject {
    private String content;

    /** 
     * 调用父类构造器,并设置content 
     * @param className 
     * @param content 
     */
    public SourceJavaFileObject(String className, String content) { super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
    }

    /**
     * 实现getCharContent,使得JavaCompiler可以从content获取java源码
     */
    @Override
    public String getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }
}
