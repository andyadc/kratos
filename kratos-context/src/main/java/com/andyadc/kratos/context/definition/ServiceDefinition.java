package com.andyadc.kratos.context.definition;

import com.andyadc.kratos.context.invoker.ServiceInvoker;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * 服务注册到注册中心的模型类
 */
public class ServiceDefinition implements Serializable {

    @Serial
    private static final long serialVersionUID = -3718652210391955705L;

    /**
     * 服务id，一般由serviceId:version组成
     */
    private String uniqueId;

    /**
     * 服务唯一id
     */
    private String serviceId;

    /**
     * 服务的版本号
     */
    private String version;

    /**
     * 服务的具体协议:例如，http/rpc/websocket
     */
    private String protocol;

    /**
     * 路径匹配规则
     */
    private String patternPath;

    /**
     * 环境名称
     */
    private String envType;

    /**
     * 服务启用禁用
     */
    private boolean enable = true;

    /**
     * 服务列表信息：
     * key是invokerPath，value是ServiceInvoker
     */
    private Map<String, ServiceInvoker> invokerMap;

    public ServiceDefinition() {
    }

    public ServiceDefinition(String uniqueId, String serviceId, String version, String protocol, String patternPath,
                             String envType, boolean enable, Map<String, ServiceInvoker> invokerMap) {
        this.uniqueId = uniqueId;
        this.serviceId = serviceId;
        this.version = version;
        this.protocol = protocol;
        this.patternPath = patternPath;
        this.envType = envType;
        this.enable = enable;
        this.invokerMap = invokerMap;
    }

    @Override
    public boolean equals(Object o) {
        if (getClass() != o.getClass()) return false;
        if (this == o) return true;
        ServiceDefinition serviceDefinition = (ServiceDefinition) o;
        return Objects.equals(uniqueId, serviceDefinition.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getPatternPath() {
        return patternPath;
    }

    public void setPatternPath(String patternPath) {
        this.patternPath = patternPath;
    }

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Map<String, ServiceInvoker> getInvokerMap() {
        return invokerMap;
    }

    public void setInvokerMap(Map<String, ServiceInvoker> invokerMap) {
        this.invokerMap = invokerMap;
    }
}
