package com.fxz.fuled.config.starter.nacos;


import com.fxz.fuled.config.starter.Config;
import com.fxz.fuled.config.starter.model.ConfigChange;
import com.fxz.fuled.config.starter.model.ConfigChangeEvent;
import com.fxz.fuled.config.starter.model.InterestedConfigChangeEvent;
import com.fxz.fuled.config.starter.spring.ConfigChangeListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fuled
 */
public class DefaultConfig implements Config {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConfig.class);
    private final List<ConfigChangeListener> m_listeners = Lists.newCopyOnWriteArrayList();
    private final Map<ConfigChangeListener, Set<String>> m_interestedKeys = Maps.newConcurrentMap();
    private final Map<ConfigChangeListener, Set<String>> m_interestedKeyPrefixes = Maps.newConcurrentMap();
    private static final ExecutorService m_executorService;
    private String nameSpace;

    public DefaultConfig(String namespace) {
        this.nameSpace = namespace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    static {
        m_executorService = Executors.newCachedThreadPool();
    }
    @Override
    public void fireConfigChange(String namespace, Map<String, ConfigChange> changes) {
        final Set<String> changedKeys = changes.keySet();
        final List<ConfigChangeListener> listeners = this.findMatchedConfigChangeListeners(changedKeys);
        // notify those listeners
        for (ConfigChangeListener listener : listeners) {
            Set<String> interestedChangedKeys = resolveInterestedChangedKeys(listener, changedKeys);
            InterestedConfigChangeEvent interestedConfigChangeEvent = new InterestedConfigChangeEvent(
                    namespace, changes, interestedChangedKeys);
            this.notifyAsync(listener, interestedConfigChangeEvent);
        }
    }

    private void notifyAsync(final ConfigChangeListener listener, final ConfigChangeEvent changeEvent) {
        m_executorService.submit(new Runnable() {
            @Override
            public void run() {
                String listenerName = listener.getClass().getName();
                try {
                    listener.onChange(changeEvent);
                } catch (Throwable ex) {
                    logger.error("Failed to invoke config change listener {}", listenerName, ex);
                } finally {
                }
            }
        });
    }

    private List<ConfigChangeListener> findMatchedConfigChangeListeners(Set<String> changedKeys) {
        final List<ConfigChangeListener> configChangeListeners = new ArrayList<>();
        for (ConfigChangeListener configChangeListener : this.m_listeners) {
            // check whether the listener is interested in this change event
            if (this.isConfigChangeListenerInterested(configChangeListener, changedKeys)) {
                configChangeListeners.add(configChangeListener);
            }
        }
        return configChangeListeners;
    }

    private boolean isConfigChangeListenerInterested(ConfigChangeListener configChangeListener, Set<String> changedKeys) {
        Set<String> interestedKeys = m_interestedKeys.get(configChangeListener);
        Set<String> interestedKeyPrefixes = m_interestedKeyPrefixes.get(configChangeListener);

        if ((interestedKeys == null || interestedKeys.isEmpty())
                && (interestedKeyPrefixes == null || interestedKeyPrefixes.isEmpty())) {
            return true; // no interested keys means interested in all keys
        }
        if (interestedKeys != null) {
            for (String interestedKey : interestedKeys) {
                if (changedKeys.contains(interestedKey)) {
                    return true;
                }
            }
        }
        if (interestedKeyPrefixes != null) {
            for (String prefix : interestedKeyPrefixes) {
                for (final String changedKey : changedKeys) {
                    if (changedKey.startsWith(prefix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Set<String> resolveInterestedChangedKeys(ConfigChangeListener configChangeListener, Set<String> changedKeys) {
        Set<String> interestedChangedKeys = new HashSet<>();
        if (this.m_interestedKeys.containsKey(configChangeListener)) {
            Set<String> interestedKeys = this.m_interestedKeys.get(configChangeListener);
            for (String interestedKey : interestedKeys) {
                if (changedKeys.contains(interestedKey)) {
                    interestedChangedKeys.add(interestedKey);
                }
            }
        }
        if (this.m_interestedKeyPrefixes.containsKey(configChangeListener)) {
            Set<String> interestedKeyPrefixes = this.m_interestedKeyPrefixes.get(configChangeListener);
            for (String interestedKeyPrefix : interestedKeyPrefixes) {
                for (String changedKey : changedKeys) {
                    if (changedKey.startsWith(interestedKeyPrefix)) {
                        interestedChangedKeys.add(changedKey);
                    }
                }
            }
        }
        return Collections.unmodifiableSet(interestedChangedKeys);
    }

    @Override
    public void addChangeListener(ConfigChangeListener listener) {
        addChangeListener(listener, null);
    }

    @Override
    public void addChangeListener(ConfigChangeListener listener, Set<String> interestedKeys) {
        addChangeListener(listener, interestedKeys, null);
    }

    @Override
    public void addChangeListener(ConfigChangeListener
                                          listener, Set<String> interestedKeys, Set<String> interestedKeyPrefixes) {
        if (!m_listeners.contains(listener)) {
            m_listeners.add(listener);
            if (interestedKeys != null && !interestedKeys.isEmpty()) {
                m_interestedKeys.put(listener, Sets.newHashSet(interestedKeys));
            }
            if (interestedKeyPrefixes != null && !interestedKeyPrefixes.isEmpty()) {
                m_interestedKeyPrefixes.put(listener, Sets.newHashSet(interestedKeyPrefixes));
            }
        }
    }

    @Override
    public boolean removeChangeListener(ConfigChangeListener listener) {
        m_interestedKeys.remove(listener);
        m_interestedKeyPrefixes.remove(listener);
        return m_listeners.remove(listener);
    }
}
