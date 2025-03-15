package com.fxz.fuled.dynamic.datasource.starter.ponitcut;

import com.fxz.fuled.dynamic.datasource.starter.utils.ProxyUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DynamicMethodMatcher;
import org.springframework.aop.support.RootClassFilter;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * 包名前缀pointcut
 */

public class PackagePrefixPointcut implements Pointcut {

    private Class baseClass;

    private PackagePrefixClassFilter packagePrefixClassFilter;
    private PackagePrefixMethodMatcher packagePrefixMethodMatcher;

    public PackagePrefixPointcut(Set<String> packages, Class baseClass) {
        this.baseClass = baseClass;
        packagePrefixClassFilter = new PackagePrefixClassFilter(packages);
        packagePrefixMethodMatcher = new PackagePrefixMethodMatcher(packages);

    }

    @Override
    public ClassFilter getClassFilter() {
        return Objects.equals(Object.class, baseClass) ? packagePrefixClassFilter : new RootClassFilter(baseClass);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return packagePrefixMethodMatcher;
    }


    public static class PackagePrefixClassFilter implements ClassFilter {

        private Set<String> packages;

        public PackagePrefixClassFilter(Set<String> packages) {
            this.packages = packages;
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return ProxyUtils.isMatch(ProxyUtils.findAllSuperClass(clazz), packages);
        }
    }

    public static class PackagePrefixMethodMatcher extends DynamicMethodMatcher {

        private Set<String> packages;

        public PackagePrefixMethodMatcher(Set<String> packages) {
            this.packages = packages;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return ProxyUtils.isMatch(ProxyUtils.findAllSuperClass(targetClass), packages);
        }
    }
}
