package com.andyadc.kratos.context.definition;

import java.io.Serializable;
import java.util.Objects;

public class ServiceInstance implements Serializable {

    private static final long serialVersionUID = -2831827682845856138L;

    /**
     * 服务实例ID: ip:port
     */
    protected String instanceId;

    /**
     * 服务id，一般由serviceId:version组成
     */
    protected String uniqueId;

    /**
     * 服务实例地址： ip:port
     */
    protected String address;

    /**
     * 标签信息
     */
    protected String tags;

    /**
     * 权重信息
     */
    protected Integer weight;

    /**
     * 服务注册的时间戳，主要用于注册
     */
    protected long registerTime;

    /**
     * 服务实例启用禁用
     */
    protected boolean enable = true;

    /**
     * 服务实例对应的版本号
     */
    protected String version;

    /**
     * 是否是灰度发布
     */
    private boolean gray;

    public ServiceInstance() {
    }

    public ServiceInstance(String instanceId, String uniqueId, String address, String tags, Integer weight,
                           long registerTime, boolean enable, String version, boolean gray) {
        this.instanceId = instanceId;
        this.uniqueId = uniqueId;
        this.address = address;
        this.tags = tags;
        this.weight = weight;
        this.registerTime = registerTime;
        this.enable = enable;
        this.version = version;
        this.gray = gray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (getClass() != o.getClass()) return false;
        ServiceInstance serviceInstance = (ServiceInstance) o;
        return Objects.equals(instanceId, serviceInstance.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }
}
