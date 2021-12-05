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
package com.fxz.fuled.config.starter;


import com.fxz.fuled.config.starter.model.ConfigChange;
import com.fxz.fuled.config.starter.spring.ConfigChangeListener;

import java.util.Map;
import java.util.Set;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface Config {

   void fireConfigChange(String namespace, Map<String, ConfigChange> changes);
  /**
   * Add change listener to this config instance, will be notified when any key is changed in this namespace.
   *
   * @param listener the config change listener
   */
  void addChangeListener(ConfigChangeListener listener);

  /**
   * Add change listener to this config instance, will only be notified when any of the interested keys is changed in this namespace.
   *
   * @param listener the config change listener
   * @param interestedKeys the keys interested by the listener
   *
   * @since 1.0.0
   */
  void addChangeListener(ConfigChangeListener listener, Set<String> interestedKeys);

  /**
   * Add change listener to this config instance, will only be notified when any of the interested keys is changed in this namespace.
   *
   * @param listener the config change listener
   * @param interestedKeys the keys that the listener is interested in
   * @param interestedKeyPrefixes the key prefixes that the listener is interested in,
   *                              e.g. "spring." means that {@code listener} is interested in keys that starts with "spring.", such as "spring.banner", "spring.jpa", etc.
   *
   * @since 1.3.0
   */
  void addChangeListener(ConfigChangeListener listener, Set<String> interestedKeys,
                         Set<String> interestedKeyPrefixes);

  /**
   * Remove the change listener
   *
   * @param listener the specific config change listener to remove
   * @return true if the specific config change listener is found and removed
   *
   * @since 1.1.0
   */
  boolean removeChangeListener(ConfigChangeListener listener);
}
