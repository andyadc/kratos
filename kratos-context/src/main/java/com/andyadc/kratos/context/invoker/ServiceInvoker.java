package com.andyadc.kratos.context.invoker;

/**
 * 服务调用
 */
public interface ServiceInvoker extends GatewayInvoker {

    /**
     * 获取规则id
     */
    String getRuleId();

    /**
     * 设置规则id
     */
    void setRuleId(String ruleId);
}
