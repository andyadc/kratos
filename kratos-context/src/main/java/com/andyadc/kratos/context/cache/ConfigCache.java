package com.andyadc.kratos.context.cache;

import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.context.rule.Rule;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 配置缓存接口
 */
public interface ConfigCache {

    /**
     * 缓存uniqueId与服务定义的关系
     */
    void putServiceDefinition(String uniqueId, ServiceDefinition serviceDefinition);

    /**
     * 根据uniqueId从缓存中获取服务定义信息
     */
    ServiceDefinition getServiceDefinition(String uniqueId);

    /**
     * 从缓存中移除指定uniqueId的服务定义信息
     */
    void removeServiceDefinition(String uniqueId);

    /**
     * 获取服务定义的Map实例
     */
    ConcurrentMap<String, ServiceDefinition> getServiceDefinitionMap();

    /**
     * 根据uniqueId获取服务实例
     */
    Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId);

    /**
     * 获取服务实例列表
     */
    Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId, boolean gray);

    /**
     * 向缓存中添加服务实例信息
     */
    void addServiceInstance(String uniqueId, ServiceInstance serviceInstance);

    /**
     * 向缓存中添加服务实例列表
     */
    void addServiceInstances(String uniqueId, Set<ServiceInstance> serviceInstanceSet);

    /**
     * 更新缓存中指定的服务实例
     */
    void updateServiceInstance(String uniqueId, ServiceInstance serviceInstance);

    /**
     * 移除缓存中指定的服务实例
     */
    void removeServiceInstance(String uniqueId, String serviceInstanceId);

    /**
     * 从缓存中移除指定的服务实例列表
     */
    void removeServiceInstancesByUniqueId(String uniqueId);

    /**
     * 缓存网关规则配置
     */
    void putRule(String ruleId, Rule rule);

    /**
     * 缓存网关配置列表
     */
    void putAllRule(List<Rule> ruleList);

    /**
     * 根据规则id获取网关配置规则
     */
    Rule getRule(String ruleId);

    /**
     * 移除规则
     */
    void removeRule(String ruleId);

    /**
     * 获取规则Map
     */
    ConcurrentMap<String, Rule> getRuleMap();

    /**
     * 根据path获取Rule规则
     */
    Rule getRuleByPath(String path);

    /**
     * 获取某个服务的规则列表
     */
    List<Rule> getRuleByServiceId(String serviceId);
}
