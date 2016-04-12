package com.baidu.unbiz.multiengine.dto;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by wangchongjie on 16/4/7.
 */
public class RpcParam {

    private Object params;

    public static RpcParam newInstance() {
        return new RpcParam();
    }

    public Object getParams() {
        return params;
    }

    public RpcParam setParams(Object params) {
        this.params = params;
        return this;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
