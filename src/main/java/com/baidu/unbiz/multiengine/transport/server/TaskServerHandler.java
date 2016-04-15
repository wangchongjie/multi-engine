package com.baidu.unbiz.multiengine.transport.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.transport.dto.Signal;
import com.baidu.unbiz.multiengine.task.TaskCommand;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class TaskServerHandler extends ContextAwareInboundHandler {

    private static final Log LOG = LogFactory.getLog(TaskServerHandler.class);

    public TaskServerHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Signal) {
            handleSignal(ctx, (Signal) msg);
        }
    }

    private void handleSignal(ChannelHandlerContext ctx, Signal signal) {

        TaskCommand command = (TaskCommand) signal.getMessage();

        // execute command
        ParallelExePool parallelExePool = bean("simpleParallelExePool");
        MultiResult results = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));

        Object response = results.getResult(command.getTaskBean());
        Signal<Object> resultSignal = new Signal<Object>(response);
        resultSignal.setSeqId(signal.getSeqId());

        ctx.writeAndFlush(resultSignal);
    }

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