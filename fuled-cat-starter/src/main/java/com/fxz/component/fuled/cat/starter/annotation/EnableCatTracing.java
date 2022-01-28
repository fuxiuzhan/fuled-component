package com.fxz.component.fuled.cat.starter.annotation;

import com.fxz.component.fuled.cat.starter.mark.Mark;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author fuled
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(Mark.class)
public @interface EnableCatTracing {
}
