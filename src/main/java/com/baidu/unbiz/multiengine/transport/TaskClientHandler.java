package com.baidu.unbiz.multiengine.transport;

/**
 * Created by baidu on 16/3/31.
 */

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtobufCodec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multitask.task.Params;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 24   * Handler implementation for the echo client.  It initiates the ping-pong
 * 25   * traffic between the echo client and server by sending the first message to
 * 26   * the server.
 * 27
 */
public class TaskClientHandler extends ChannelInboundHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(TaskServerHandler.class);

    /**
     * Creates a client-side handler.
     */
    public TaskClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);

        ByteBuf buf = Unpooled.buffer(TaskClient.SIZE);

        Codec codec = new ProtostuffCodec();
        buf.writeBytes(codec.encode(TaskCommand.class, command));
        ctx.writeAndFlush(buf);

        LOG.info("Send command:" + command);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof ByteBuf) {
            Codec codec = new ProtostuffCodec();

            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            RpcResult result = codec.decode(RpcResult.class, bytes);
            LOG.info("client channel read task:" + result.getResult());
        }

//        ByteBuf in = (ByteBuf) msg;
//        try {
//            while (in.isReadable()) { // (1)
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
//        } finally {
//            ReferenceCountUtil.release(msg); // (2)
//        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
        System.out.println("exceptionCaught");
    }

}