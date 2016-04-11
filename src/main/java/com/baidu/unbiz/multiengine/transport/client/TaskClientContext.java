package com.baidu.unbiz.multiengine.transport.client;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class TaskClientContext {

    static ConcurrentHashMap<String, Channel> sessionChannelMap = new ConcurrentHashMap<String, Channel>();
    static ConcurrentHashMap<String, SendFutrue> sessionResultMap = new ConcurrentHashMap<String, SendFutrue>();

}
