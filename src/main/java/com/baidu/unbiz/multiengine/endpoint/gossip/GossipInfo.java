package com.baidu.unbiz.multiengine.endpoint.gossip;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.baidu.unbiz.multiengine.endpoint.HostConf;

/**
 * Created by wangchongjie on 16/4/19.
 */
public class GossipInfo {

    private long version;
    private List<HostConf> hostConfs;

    public List<HostConf> getHostConfs() {
        return hostConfs;
    }

    public void setHostConfs(List<HostConf> hostConfs) {
        this.hostConfs = hostConfs;
    }

    public void GossipInfo() {
        version = System.currentTimeMillis();
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
