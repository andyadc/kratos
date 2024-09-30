package com.andyadc.kratos.context.context;

import com.andyadc.kratos.context.definition.ServiceDefinition;
import com.andyadc.kratos.context.definition.ServiceInstance;
import com.andyadc.kratos.context.rule.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 上下文配置缓存实现类
 */
public class ContextConfigCache implements ConfigCache {

    /**
     * 缓存服务定义
     * key: uniqueId
     * value: ServiceDefinition对象
     */
    private final ConcurrentMap<String, ServiceDefinition> serviceDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 缓存服务实例
     * key: uniqueId
     * value: ServiceInstance列表
     */
    private final ConcurrentMap<String, Set<ServiceInstance>> serviceInstanceMap = new ConcurrentHashMap<>();

    /**
     * 缓存服务规则
     * key:规则id
     * value: Rule对象
     */
    private ConcurrentMap<String, Rule> ruleMap = new ConcurrentHashMap<>();

    /**
     * 缓存路径与规则
     * key:请求的路径
     * value:具体规则
     */
    private ConcurrentMap<String, Rule> pathRuleMap = new ConcurrentHashMap<>();

    /**
     * 缓存服务与规则列表
     * key: 服务
     * value: 规则列表
     */
    private ConcurrentMap<String, List<Rule>> serviceRuleMap = new ConcurrentHashMap<>();

    ContextConfigCache() {
    }

    @Override
    public void putServiceDefinition(String uniqueId, ServiceDefinition serviceDefinition) {
        this.serviceDefinitionMap.put(uniqueId, serviceDefinition);
    }

    @Override
    public ServiceDefinition getServiceDefinition(String uniqueId) {
        return this.serviceDefinitionMap.get(uniqueId);
    }

    @Override
    public void removeServiceDefinition(String uniqueId) {
        this.serviceDefinitionMap.remove(uniqueId);
    }

    @Override
    public ConcurrentMap<String, ServiceDefinition> getServiceDefinitionMap() {
        return this.serviceDefinitionMap;
    }

    @Override
    public Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId) {
        return this.serviceInstanceMap.get(uniqueId);
    }

    @Override
    public Set<ServiceInstance> getServiceInstanceByUniqueId(String uniqueId, boolean gray) {
        Set<ServiceInstance> serviceInstanceSet = this.serviceInstanceMap.get(uniqueId);
        if (serviceInstanceSet == null || serviceInstanceSet.isEmpty()) {
            return Collections.emptySet();
        }
        if (gray) {
            serviceInstanceSet = serviceInstanceSet.stream().filter(ServiceInstance::isGray).collect(Collectors.toSet());
        }
        return serviceInstanceSet;
    }

    @Override
    public void addServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        set.add(serviceInstance);
        serviceInstanceMap.put(uniqueId, set);
    }

    @Override
    public void addServiceInstances(String uniqueId, Set<ServiceInstance> serviceInstanceSet) {
        serviceInstanceMap.put(uniqueId, serviceInstanceSet);
    }

    @Override
    public void updateServiceInstance(String uniqueId, ServiceInstance serviceInstance) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        Iterator<ServiceInstance> iterator = set.iterator();
        while (iterator.hasNext()) {
            ServiceInstance instance = iterator.next();
            if (instance.getInstanceId().equals(serviceInstance.getInstanceId())) {
                set.remove(instance);
            }
        }
        set.add(serviceInstance);
    }

    @Override
    public void removeServiceInstance(String uniqueId, String serviceInstanceId) {
        Set<ServiceInstance> set = serviceInstanceMap.get(uniqueId);
        Iterator<ServiceInstance> iterator = set.iterator();
        while (iterator.hasNext()) {
            ServiceInstance instance = iterator.next();
            if (instance.getInstanceId().equals(serviceInstanceId)) {
                set.remove(instance);
            }
        }
    }

    @Override
    public void removeServiceInstancesByUniqueId(String uniqueId) {
        this.serviceInstanceMap.remove(uniqueId);
    }

    @Override
    public void putRule(String ruleId, Rule rule) {
        this.ruleMap.put(ruleId, rule);
    }

    @Override
    public void putAllRule(List<Rule> ruleList) {
        ConcurrentMap<String, Rule> newRuleMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, Rule> newPathMap = new ConcurrentHashMap<>();
        ConcurrentMap<String, List<Rule>> newServiceMap = new ConcurrentHashMap<>();
        for (Rule rule : ruleList) {
            newRuleMap.put(rule.getId(), rule);
            List<Rule> rules = newServiceMap.get(rule.getServiceId());
            if (rules == null) {
                rules = new ArrayList<>();
            }
            rules.add(rule);
            newServiceMap.put(rule.getServiceId(), rules);

            List<String> paths = rule.getPaths();
            for (String path : paths) {
                String key = rule.getServiceId() + "." + path;
                newPathMap.put(key, rule);
            }
        }
        this.ruleMap = newRuleMap;
        this.pathRuleMap = newPathMap;
        this.serviceRuleMap = newServiceMap;
    }

    @Override
    public Rule getRule(String ruleId) {
        return this.ruleMap.get(ruleId);
    }

    @Override
    public void removeRule(String ruleId) {
        this.ruleMap.remove(ruleId);
    }

    @Override
    public ConcurrentMap<String, Rule> getRuleMap() {
        return this.ruleMap;
    }

    @Override
    public Rule getRuleByPath(String path) {
        return this.pathRuleMap.get(path);
    }

    @Override
    public List<Rule> getRuleByServiceId(String serviceId) {
        return this.serviceRuleMap.get(serviceId);
    }
}
