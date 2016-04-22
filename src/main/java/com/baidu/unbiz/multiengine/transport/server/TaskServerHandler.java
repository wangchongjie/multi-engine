package com.baidu.unbiz.multiengine.transport.server;

import org.slf4j.Logger;

import com.baidu.unbiz.multiengine.endpoint.supervisor.DefaultEndpointSupervisor;
import com.baidu.unbiz.multiengine.endpoint.gossip.GossipInfo;
import com.baidu.unbiz.multiengine.task.TaskCommand;
import com.baidu.unbiz.multiengine.transport.dto.Signal;
import com.baidu.unbiz.multiengine.transport.dto.SignalType;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.log.AopLogFactory;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class TaskServerHandler extends ContextAwareInboundHandler {

    private static final Logger LOG = AopLogFactory.getLogger(TaskServerHandler.class);

    public TaskServerHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Signal) {
            handleSignal(ctx, (Signal) msg);
        }
    }

    private void handleSignal(ChannelHandlerContext ctx, Signal signal) {
        if (SignalType.HEART_BEAT.equals(signal.getType())) {
            handleHeartbeat(ctx, signal);
            return;
        }
        if (SignalType.TASK_COMMOND.equals(signal.getType())) {
            handleTaskCommand(ctx, signal);
            return;
        }
        if (SignalType.GOSSIP_SYNC.equals(signal.getType())) {
            handleGossipSync(ctx, signal);
            return;
        }
        if (SignalType.GOSSIP_REACK.equals(signal.getType())) {
            handleGossipReack(ctx, signal);
            return;
        }
    }

    private void handleTaskCommand(ChannelHandlerContext ctx, Signal signal) {
        TaskCommand command = (TaskCommand) signal.getMessage();

        // execute command
        ParallelExePool parallelExePool = bean("simpleParallelExePool");
        MultiResult results = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));

        Object response = results.getResult(command.getTaskBean());
        Signal<Object> resultSignal = new Signal<Object>(response);
        resultSignal.setSeqId(signal.getSeqId());

        ctx.writeAndFlush(resultSignal);
    }

    private void handleHeartbeat(ChannelHandlerContext ctx, Signal signal) {
        // echo heat beat
        ctx.writeAndFlush(signal);
        LOG.debug("heart beat echo：" + signal);
    }

    private void handleGossipSync(ChannelHandlerContext ctx, Signal signal) {
        GossipInfo info = new GossipInfo();
        info.setHostConfs(DefaultEndpointSupervisor.getTaskHostConf());
        Signal<GossipInfo> ack = new Signal(info);
        ack.setType(SignalType.GOSSIC_ACK);
        ctx.writeAndFlush(ack);
        LOG.debug("gossip ack：" + signal);
    }

    private void handleGossipReack(ChannelHandlerContext ctx, Signal<GossipInfo> signal) {
        GossipInfo remoteInfo = signal.getMessage();
        DefaultEndpointSupervisor.mergeTaskServer(remoteInfo.getHostConfs());
        LOG.debug("gossip re-ack：" + signal);


    }

//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx)  throws Exception {
//        Signal<HeartbeatInfo> signal = new Signal<HeartbeatInfo>();
//        signal.setType(SignalType.SERVER_STOP);
//        ctx.writeAndFlush(signal);
//        super.channelUnregistered(ctx);
//    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOG.debug("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("exceptionCaught", cause);
        // Close the connection when an exception is raised.
        ctx.close();
    }

}