package com.andyadc.kratos.context.invoker;

/**
 * 抽象服务调用类
 */
public abstract class AbstractServiceInvoker implements ServiceInvoker {

    protected String invokerPath;
    protected String ruleId;
    protected int timeout = 5000;

    @Override
    public String getInvokerPath() {
        return this.invokerPath;
    }

    @Override
    public void setInvokerPath(String invokerPath) {
        this.invokerPath = invokerPath;
    }

    @Override
    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getRuleId() {
        return this.ruleId;
    }

    @Override
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
