
package com.fxz.fuled.swagger.starter.annotation;

import com.fxz.fuled.swagger.starter.config.RegistrarConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({RegistrarConfig.class})
public @interface EnableSwagger {
}
