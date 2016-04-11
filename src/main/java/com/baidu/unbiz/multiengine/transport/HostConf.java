package com.baidu.unbiz.multiengine.transport;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class HostConf {

    private String host = System.getProperty("host", "127.0.0.1");
    private int port = Integer.parseInt(System.getProperty("port", "8007"));
    private boolean ssl = System.getProperty("ssl") != null;

    public HostConf(){
    }

    public HostConf(String host, int port){
        this.host = host;
        this.port = port;
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
}
