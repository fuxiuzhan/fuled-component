package com.fxz.fuled.logger.starter.annotation;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author xiuzhan.fu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Monitor {
    String value() default "";

    /**
     * 是否打印参数表
     *
     * @return
     */
    boolean printParams() default true;

    /**
     * 是否打印返回值JSON
     *
     * @return
     */
    boolean printResult() default true;

    /**
     * tags
     *
     * @return
     */
    String[] tags() default {};
}
