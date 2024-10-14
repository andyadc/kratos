package com.andyadc.kratos.context.cache;

/**
 * 配置缓存工厂类
 */
public class ConfigCacheFactory {

    public static ConfigCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ConfigCache INSTANCE = new ContextConfigCache();
    }
}
