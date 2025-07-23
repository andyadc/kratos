package com.andyadc.kratos.register.api.listener;

import com.andyadc.kratos.common.util.JsonUtils;
import com.andyadc.kratos.context.cache.config.ConfigCache;
import com.andyadc.kratos.context.cache.config.ConfigCacheFactory;
import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class RegistryChangeListener implements RegistryListener {

    private static final Logger logger = LoggerFactory.getLogger(RegistryChangeListener.class);

    @Override
    public void onListener(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
        logger.info("刷新服务和服务实例: {}, {}", serviceDefinition.getUniqueId(), JsonUtils.toJSONString(serviceInstanceSet));
        ConfigCache configCache = ConfigCacheFactory.getInstance();
        configCache.addServiceInstances(serviceDefinition.getUniqueId(), serviceInstanceSet);
        configCache.putServiceDefinition(serviceDefinition.getUniqueId(), serviceDefinition);
    }

}
