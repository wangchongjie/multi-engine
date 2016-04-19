package com.baidu.unbiz.multiengine.endpoint;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class HostConf {

    private String host = System.getProperty("host", "127.0.0.1");
    private int port = Integer.parseInt(System.getProperty("port", "8007"));
    private boolean ssl = System.getProperty("ssl") != null;

    public HostConf() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            host = addr.getHostAddress();
        } catch (UnknownHostException e) {
            // do nothing
        }
    }

    public HostConf(String host, int port) {
        this();
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HostConf)) {
            return false;
        }
        HostConf other = (HostConf) obj;
        return this.host.equals(other.getHost()) && this.port == other.getPort() && this.ssl == other.isSsl();
    }

    @Override
    public int hashCode() {
        return this.host.hashCode() + this.host.hashCode();
    }

    public static List<HostConf> resolveHost(String hosts) {
        List<HostConf> hostConfs = new ArrayList<HostConf>();
        if (StringUtils.isEmpty(hosts)) {
            return hostConfs;
        }
        String[] hostStrs = hosts.split(";");
        for (String host : hostStrs) {
            String ip = host.replaceAll(":.*", "");
            String port = host.replaceAll(".*:", "");
            hostConfs.add(new HostConf(ip, Integer.parseInt(port)));
        }
        return hostConfs;
    }

    public static List<HostConf> resolvePort(String ports) {

        List<HostConf> hostConfs = new ArrayList<HostConf>();
        if (StringUtils.isEmpty(ports)) {
            return hostConfs;
        }
        String[] ps = ports.split(";");
        for (String port : ps) {
            HostConf hostConf = new HostConf();
            hostConf.setPort(Integer.parseInt(port));
            hostConfs.add(hostConf);
        }
        return hostConfs;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
