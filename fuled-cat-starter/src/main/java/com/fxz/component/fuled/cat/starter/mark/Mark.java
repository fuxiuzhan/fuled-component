package com.fxz.component.fuled.cat.starter.mark;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fuled
 */
@Configuration
public class Mark {

    @Bean
    public MarkClass injectMarkClass() {
        return new MarkClass();
    }

    public class MarkClass {
    }
}
