package com.fxz.component.fuled.cat.starter.configuration;

import com.fxz.component.fuled.cat.starter.mark.Mark;
import com.fxz.fuled.common.version.ComponentVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fxz
 */
@Configuration
@ConditionalOnBean(Mark.MarkClass.class)
public class ComponentVersionConfiguration {
    @Bean("catComponentVersion")
    public ComponentVersion configVersion() {
        return new ComponentVersion("fuled-cat-component.version", "1.0.0.waterdrop", "fuled-cat-component");
    }
}
