package com.baidu.unbiz.multiengine.transport.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;

import io.netty.channel.Channel;

/**
 * Created by wangchongjie on 16/4/11.
 */
public class TaskClientContext {

    static ConcurrentHashMap<String, Channel> sessionChannelMap = new ConcurrentHashMap<String, Channel>();
    private static Map<String, ConcurrentHashMap<Long, SendFuture>> sessionResultMap =
            new ConcurrentHashMap<String, ConcurrentHashMap<Long, SendFuture>>();

    public static void placeSessionResult(String sessionKey, Long seqId, SendFuture futrue) {
        ConcurrentHashMap<Long, SendFuture> resultMap = sessionResultMap.get(sessionKey);
        if (resultMap == null) {
            resultMap = new ConcurrentHashMap<Long, SendFuture>();
            sessionResultMap.put(sessionKey, resultMap);
        }
        resultMap.put(seqId, futrue);
    }

    public static void fillSessionResult(String sessionKey, Long seqId, Object result) {
        ConcurrentHashMap<Long, SendFuture> resultMap = sessionResultMap.get(sessionKey);
        Assert.notNull(resultMap);
        SendFuture futrue = resultMap.get(seqId);
        futrue.set(result);
    }

    public static void appendSessionResult(String sessionKey, Long seqId, Object result,
                                           SendFutrueImpl.AppendHandler handler, boolean finished) {
        ConcurrentHashMap<Long, SendFuture> resultMap = sessionResultMap.get(sessionKey);

        SendFuture future = resultMap.get(seqId);
        future.append(result, handler, finished);
    }

    public static SendFuture getSessionResult(String sessionKey, Long seqId) {
        ConcurrentHashMap<Long, SendFuture> resultMap = sessionResultMap.get(sessionKey);
        Assert.notNull(resultMap);
        return resultMap.get(seqId);
    }
}
