package com.andyadc.kratos.plugins.api.factory;

import com.andyadc.kratos.common.util.CollectionUtils;
import com.andyadc.kratos.plugins.api.Plugin;
import com.andyadc.kratos.plugins.api.multi.MultiplePlugin;
import com.andyadc.kratos.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件工厂
 */
public class PluginFactory {

    private static final Logger logger = LoggerFactory.getLogger(PluginFactory.class);
    private static final PluginFactory INSTANCE = new PluginFactory();
    private MultiplePlugin multiplePlugin;

    private PluginFactory() {
        List<Plugin> plugins = ExtensionLoader.getExtensionLoader(Plugin.class).getSpiClassInstances();
        if (CollectionUtils.isEmpty(plugins)) {
            return;
        }
        Map<String, Plugin> pluginMap = new HashMap<>();
        for (Plugin plugin : plugins) {
            if (!plugin.check()) {
                continue;
            }
            String pluginName = plugin.getClass().getName();
            pluginMap.put(pluginName, plugin);
            logger.info("load plugin: {}", pluginName);
        }
        this.multiplePlugin = new MultiplePlugin(pluginMap);
    }

    public static Plugin getMultiplePlugin() {
        return INSTANCE.multiplePlugin;
    }

}
