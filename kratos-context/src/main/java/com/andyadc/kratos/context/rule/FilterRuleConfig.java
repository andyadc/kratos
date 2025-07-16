package com.andyadc.kratos.context.rule;

import com.andyadc.kratos.common.constants.Constants;

import java.io.Serial;
import java.io.Serializable;

/**
 * 过滤器配置
 */
public class FilterRuleConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -5898736012072130717L;

    /**
     * 是否开启日志
     */
    private boolean logEnabled;

    /**
     * 负载均衡类型
     */
    private String balanceType = Constants.BALANCE_TYPE_ROUND_RIBBON;

    /**
     * 超时时间
     */
    private Integer timeout = 5000;

    private boolean rollingNumber = true;

    public FilterRuleConfig(boolean logEnabled, String balanceType, Integer timeout, boolean rollingNumber) {
        this.logEnabled = logEnabled;
        this.balanceType = balanceType;
        this.timeout = timeout;
        this.rollingNumber = rollingNumber;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean isRollingNumber() {
        return rollingNumber;
    }

    public void setRollingNumber(boolean rollingNumber) {
        this.rollingNumber = rollingNumber;
    }

}
