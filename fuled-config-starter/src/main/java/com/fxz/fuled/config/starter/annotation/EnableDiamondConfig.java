/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.fxz.fuled.config.starter.annotation;

import com.fxz.fuled.config.starter.spring.util.ConfigConsts;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * Use this annotation to register Apollo property sources when using Java Config.
 *
 * <p>Configuration example with multiple namespaces:</p>
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableApolloConfig({"someNamespace","anotherNamespace"})
 * public class AppConfig {
 *
 * }
 * </pre>
 *
 * <p>Configuration example with placeholder:</p>
 * <pre class="code">
 * // The namespace could also be specified as a placeholder, e.g. ${redis.namespace:xxx},
 * // which will use the value of the key "redis.namespace" or "xxx" if this key is not configured.
 * // Please note that this placeholder could not be configured in Apollo as Apollo is not activated during this phase.
 * &#064;Configuration
 * &#064;EnableApolloConfig({"${redis.namespace:xxx}"})
 * public class AppConfig {
 *
 * }
 * </pre>
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DiamondConfigRegistrar.class)
public @interface EnableDiamondConfig {
  /**
   * Apollo namespaces to inject configuration into Spring Property Sources.
   */
  String[] value() default {ConfigConsts.NAMESPACE_APPLICATION};

  /**
   * The order of the apollo config, default is {@link Ordered#LOWEST_PRECEDENCE}, which is Integer.MAX_VALUE.
   * If there are properties with the same name in different apollo configs, the apollo config with smaller order wins.
   * @return
   */
  int order() default Ordered.LOWEST_PRECEDENCE;
}
