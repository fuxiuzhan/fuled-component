package com.fxz.fuled.base.arch.env.config;

import com.fxz.fuled.base.arch.env.locator.BaseEnvSourceLocator;
import org.springframework.context.annotation.Import;

/**
 *
 */
@Import(BaseEnvSourceLocator.class)
public class BaseEnvAutoConfig {
}
