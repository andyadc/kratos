package com.andyadc.kratos.filter.api.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存工厂
 */
public class CacheFactory {

    public static final String CONFIG_CACHE_ID = "configCache";
    private final ConcurrentMap<String, Cache<String, ?>> cacheMap = new ConcurrentHashMap<>();

    private CacheFactory() {

    }

    public static CacheFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取RPC缓存
     */
    public static Cache<String, GenericService> getRpcCache() {
        return Caffeine.newBuilder().build();
    }

    /**
     * 构建缓存
     */
    public <T> Cache<String, T> buildCache(String cacheId) {
        Cache<String, T> cache = Caffeine.newBuilder().build();
        cacheMap.put(cacheId, cache);
        return cache;
    }

    /**
     * 移除缓存对象
     */
    public <T> void removeCache(String cacheId, String key) {
        @SuppressWarnings("unchecked")
        Cache<String, T> cache = (Cache<String, T>) cacheMap.get(cacheId);
        if (cache != null) {
            cache.invalidate(key);
        }
    }

    /**
     * 移除cacheId下的所有缓存
     */
    public <T> void removeCache(String cacheId) {
        @SuppressWarnings("unchecked")
        Cache<String, T> cache = (Cache<String, T>) cacheMap.get(cacheId);
        if (cache != null) {
            cache.invalidateAll();
        }
    }

    /**
     * 清空缓存
     */
    public void cleanCache() {
        cacheMap.values().forEach(Cache::invalidateAll);
    }

    /**
     * 单例模式
     */
    private static class SingletonHolder {
        private static final CacheFactory INSTANCE = new CacheFactory();
    }

}
