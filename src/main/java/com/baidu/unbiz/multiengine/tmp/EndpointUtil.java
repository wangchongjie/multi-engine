package com.baidu.unbiz.multiengine.tmp;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Created by baidu on 16/3/31.
 */
public class EndpointUtil {


    /**
     * 终端的Key
     */
    private static final AttributeKey<Endpoint> TRANSPORT_ENDPOINT = AttributeKey.valueOf("TRANSPORT_ENDPOINT");

    /**
     * 通道和终端绑定
     *
     * @param channel 通道 @see Channel
     * @param endpoint 终端 @see Endpoint
     */
    public static void bind(Channel channel, Endpoint endpoint) {
        Attribute<Endpoint> attribute = channel.attr(TRANSPORT_ENDPOINT);

        attribute.setIfAbsent(endpoint);
    }

    /**
     * 获取当前终端 @see Endpoint
     *
     * @param channel 通道 @see Channel
     * @return 当前终端
     */
    public static Endpoint getEndpoint(Channel channel) {
        return channel.attr(TRANSPORT_ENDPOINT).get();
    }
}
