package com.baidu.unbiz.multiengine.transport.server;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.transport.protocol.PackHead;
import com.baidu.unbiz.multiengine.transport.protocol.PackUtils;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
            // decode command
            Signal signal = (Signal) msg;
            TaskCommand command = (TaskCommand) signal.getMessage();

            // execute command
            ParallelExePool parallelExePool = bean("simpleParallelExePool");
            MultiResult results = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));

            Object response = results.getResult(command.getTaskBean());
            RpcResult result = RpcResult.newInstance().setResult(response);

            Signal<RpcResult> resultSignal = new Signal<RpcResult>(result);
            resultSignal.setSeqId(signal.getSeqId());

            ctx.writeAndFlush(resultSignal);
        }

        if (msg instanceof ByteBuf) {

//            // decode command
//            ByteBuf buf = (ByteBuf) msg;
//
//            byte[] headBytes = new byte[PackHead.SIZE];
//            buf.readBytes(headBytes);
//            PackHead packHead = PackHead.fromBytes(headBytes);
//
//            System.out.println("sumLen:" + packHead.getSumLen() + "|"+packHead.getSeqId()+"|"+packHead.getRemainLen());
//            Assert.isTrue(packHead.getSumLen() == packHead.getBodyLen());
//
//            byte[] bodyBytes = new byte[packHead.getBodyLen()];
//            buf.readBytes(bodyBytes);
//
//            MsgCodec codec = new ProtostuffCodec();
//            TaskCommand command = codec.decode(TaskCommand.class, bodyBytes);
//            LOG.debug("channel read task:" + command);
//
//            // execute command
//            ParallelExePool parallelExePool = bean("simpleParallelExePool");
//            MultiResult results = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));
//
//            Object response = results.getResult(command.getTaskBean());
//            RpcResult result = RpcResult.newInstance().setResult(response);
//
//            // send result to client
//            byte[] resBodyBytes = codec.encode(result);
//            List<byte[]> packBytes = PackUtils.buildPackData(packHead.getSeqId(), resBodyBytes, buf.capacity());
//
//
//            for (byte[] packByte : packBytes) {
//                ByteBuf part = Unpooled.buffer(packByte.length);
//                part.writeBytes(packByte);
//                ctx.writeAndFlush(part);
//            }
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