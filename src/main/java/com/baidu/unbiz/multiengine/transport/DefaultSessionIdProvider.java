package com.baidu.unbiz.multiengine.transport;

import java.util.UUID;

/**
 * 缺省实现，使用uuid作为sessionId
 * 
 * @author wagnchongjie
 * 
 */
public class DefaultSessionIdProvider implements SessionIdProvider, Cloneable {
    private String prefix;
    private volatile String currentSessionId;

    /**
     * 无参数构造方法
     */
    public DefaultSessionIdProvider() {
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 构造方法
     * 
     * @param prefix sessionid前缀
     */
    public DefaultSessionIdProvider(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getSessionId(boolean refresh) {
        if (refresh) {
            currentSessionId = genSessionId();
        }
        return currentSessionId;
    }

    /**
     * 生成uuid session
     * 
     * @return session
     */
    private String genSessionId() {
        if (prefix == null) {
            return UUID.randomUUID().toString();
        } else {
            return prefix + "_" + UUID.randomUUID().toString();
        }
    }

}
