package com.baidu.unbiz.multiengine.transport.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

import io.netty.buffer.ByteBuf;
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
        if (msg instanceof ByteBuf) {

            // decode command
            Codec codec = new ProtostuffCodec();
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            TaskCommand command = codec.decode(TaskCommand.class, bytes);
            LOG.info("channel read task:" + command);

            // execute command
            ParallelExePool parallelExePool = bean("simpleParallelExePool");
            MultiResult results = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));

            Object response = results.getResult(command.getTaskBean());
            RpcResult result = RpcResult.newInstance().setResult(response);

            // send result to client
            buf.clear();
            buf.writeBytes(codec.encode(RpcResult.class, result));
            ctx.writeAndFlush(buf);
            // ReferenceCountUtil.release(buf);
        }
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