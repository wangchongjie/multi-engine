package com.baidu.unbiz.multiengine.tmp;


import io.netty.channel.Channel;

/**
 * 终端
 */
public interface Endpoint {

    String getMsg();

    String setMsg(String msg);


    /**
     * 设置通道 @see Channel
     * 
     * @param channel Netty的通道
     */
    void setChannel(Channel channel);

    /**
     * 终端类型
     * 
     * @return 终端类型
     */
    Type type();

    /**
     * 终端类型
     * 
     * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
     * @version create on 2015-7-4 下午10:31:59
     */
    enum Type {
        /**
         * 代表客户端
         */
        CLIENT,
        /**
         * 代表服务端
         */
        SERVER;
    }
}
