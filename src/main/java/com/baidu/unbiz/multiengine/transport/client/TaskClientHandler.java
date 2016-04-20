package com.baidu.unbiz.multiengine.transport.client;

/**
 * Created by wangchongjie on 16/3/31.
 */

import java.util.List;

import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.endpoint.EndpointSupervisor;
import com.baidu.unbiz.multiengine.endpoint.HostConf;
import com.baidu.unbiz.multiengine.endpoint.gossip.GossipInfo;
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
        if (SignalType.GOSSIC_ACK.equals(signal.getType())) {
            handleGossipAck(ctx, signal);
            return;
        }
        if (SignalType.SERVER_STOP.equals(signal.getType())) {
            handleServerStop(ctx, signal);
            return;
        }
        handleDefault(ctx, signal);
    }

    private void handleDefault(ChannelHandlerContext ctx, Signal<GossipInfo> signal) {
        TaskClientContext.fillSessionResult(sessionKey, signal);
    }

    private void handleGossipAck(ChannelHandlerContext ctx, Signal<GossipInfo> signal) {
        GossipInfo diffInfo = new GossipInfo();
        List<HostConf> locals = EndpointSupervisor.getTaskHostConf();
        GossipInfo remoteInfo = signal.getMessage();

        locals.removeAll(remoteInfo.getHostConfs());
        diffInfo.setHostConfs(locals);

        Signal<GossipInfo> reack = new Signal(diffInfo);
        reack.setType(SignalType.GOSSIP_REACK);
        ctx.writeAndFlush(reack);

        EndpointSupervisor.mergeTaskServer(remoteInfo.getHostConfs());
        LOG.debug("gossip re-ack：" + signal);
    }

    private void handleServerStop(ChannelHandlerContext ctx, Signal signal) {
        String sessionKey = ctx.channel().attr(TaskClientContext.SESSION_ATTRIBUTE).get();
        TaskClient taskClient = TaskClientContext.sessionClientMap.get(sessionKey);
        if (taskClient != null) {
            taskClient.setInvalid(true);
            LOG.info("server close：" + signal);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().attr(TaskClientContext.SESSION_ATTRIBUTE).setIfAbsent(sessionKey);
        super.channelRegistered(ctx);
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