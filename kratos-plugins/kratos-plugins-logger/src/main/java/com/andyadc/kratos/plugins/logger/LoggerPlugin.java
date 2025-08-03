package com.andyadc.kratos.plugins.logger;

import com.andyadc.kratos.common.exception.PluginException;
import com.andyadc.kratos.plugins.api.Plugin;
import com.andyadc.kratos.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
public class LoggerPlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(LoggerPlugin.class);

    @Override
    public void init() {
        logger.info("init...");
    }

    @Override
    public void destroy() {
        logger.info("destroy...");
    }

    @Override
    public Plugin get(String pluginName) {
        if (check() && (LoggerPlugin.class.getName()).equalsIgnoreCase(pluginName)) {
            return this;
        }
        logger.warn("No corresponding plugin found => {}", pluginName);
        throw new PluginException("No corresponding plugin found [" + pluginName + "]");
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

}
