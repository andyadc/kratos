package com.andyadc.kratos.context.rule;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 网关规则
 */
public class Rule implements Comparable<Rule>, Serializable {

    private static final long serialVersionUID = 4337815413943061045L;

    /**
     * 规则ID，全局唯一
     */
    private String id;
    /**
     * 规则名称
     */
    private String name;
    /**
     * 协议，例如http/rpc/websocket等
     */
    private String protocol;
    /**
     * 后端服务ID
     */
    private String serviceId;
    /**
     * 请求前缀
     */
    private String prefix;
    /**
     * 路径集合
     */
    private List<String> paths;
    /**
     * 规则排序，对应场景：一个路径对应多条规则，然后只执行一条规则的情况
     */
    private Integer order;
    /**
     * 过滤器规则定义集合
     */
    private Set<FilterRule> filterRules;

    public Rule(String id, String name, String protocol, String serviceId, String prefix, List<String> paths, Integer order, Set<FilterRule> filterRules) {
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.serviceId = serviceId;
        this.prefix = prefix;
        this.paths = paths;
        this.order = order;
        this.filterRules = filterRules;
    }

    /**
     * 添加规则
     */
    public boolean addFilterRule(FilterRule filterRule) {
        return this.filterRules.add(filterRule);
    }

    /**
     * 获取匹配到的规则
     */
    public FilterRule getFilterRule(String id) {
        return StringUtils.isEmpty(id) ? null : filterRules.stream().filter(filterRule -> id.equalsIgnoreCase(filterRule.getId())).findFirst().orElse(null);
    }

    /**
     * 是否包含指定的规则
     */
    public boolean containsFilterRule(String id) {
        return !StringUtils.isEmpty(id) && filterRules.stream().anyMatch(filterRule -> id.equalsIgnoreCase(filterRule.getId()));
    }

    @Override
    public int compareTo(Rule o) {
        int orderCompare = Integer.compare(getOrder(), o.getOrder());
        if (orderCompare == 0) {
            return getId().compareTo(o.getId());
        }
        return orderCompare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return id.equals(rule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Set<FilterRule> getFilterRules() {
        return filterRules;
    }

    public void setFilterRules(Set<FilterRule> filterRules) {
        this.filterRules = filterRules;
    }

}
