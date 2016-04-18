package com.baidu.unbiz.multiengine.transport.client;

/**
 * Created by wangchongjie on 16/3/31.
 */

import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.transport.dto.Signal;
import com.baidu.unbiz.multiengine.transport.dto.SignalType;
import com.baidu.unbiz.multiengine.transport.server.TaskServerHandler;
import com.baidu.unbiz.multitask.log.AopLogFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class TaskClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = AopLogFactory.getLogger(TaskServerHandler.class);

    private String sessionKey;

    /**
     * Creates a client-side handler.
     */
    public TaskClientHandler() {
    }

    public TaskClientHandler(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Signal) {
            this.handleSignal(ctx, (Signal) msg);
        }
    }

    private void handleSignal(ChannelHandlerContext ctx, Signal signal) {
        TaskClientContext.fillSessionResult(sessionKey, signal);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOG.debug("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        ctx.close();
        LOG.error("exceptionCaught", cause);
    }

}