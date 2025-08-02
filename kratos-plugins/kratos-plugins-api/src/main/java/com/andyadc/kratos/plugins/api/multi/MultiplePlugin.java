package com.andyadc.kratos.plugins.api.multi;

import com.andyadc.kratos.plugins.api.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 实现多个插件
 */
public class MultiplePlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(MultiplePlugin.class);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(128));
    private Map<String, Plugin> pluginMap = new HashMap<>();

    public MultiplePlugin(Map<String, Plugin> pluginMap) {
        this.pluginMap = pluginMap;
    }

    @Override
    public void init() {
        if (initialized.compareAndSet(false, true)) {
            threadPoolExecutor.submit(() -> pluginMap.values().forEach(plugin -> {
                try {
                    plugin.init();
                } catch (Throwable t) {
                    logger.error("Plugin init errror. {}", plugin.getClass().getName(), t);
                    initialized.set(false);
                }
            }));
        }
    }

    @Override
    public void destroy() {
        pluginMap.values().forEach(plugin -> {
            try {
                plugin.destroy();
            } catch (Throwable t) {
                logger.error("Plugin destroy error. {}", plugin.getClass().getName(), t);
            }
        });
        threadPoolExecutor.shutdown();
    }

    @Override
    public Plugin get(String pluginName) {
        return pluginMap.get(pluginName);
    }

}
