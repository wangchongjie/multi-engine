package com.baidu.unbiz.multiengine.transport;

/**
 * Created by wangchongjie on 16/3/31.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
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

        //        TaskCommand command = new TaskCommand();
        //        command.setTaskBean("deviceStatFetcher");
        //        command.setParams(null);
        //
        //        ByteBuf buf = Unpooled.buffer(TaskClient.SIZE);
        //
        //        Codec codec = new ProtostuffCodec();
        //        buf.writeBytes(codec.encode(TaskCommand.class, command));
        //        ctx.writeAndFlush(buf);
        //
        //        LOG.info("Send command:" + command);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof ByteBuf) {

            TaskClient.setResult(msg);

            //            Codec codec = new ProtostuffCodec();
            //
            //            ByteBuf buf = (ByteBuf) msg;
            //            byte[] bytes = new byte[buf.readableBytes()];
            //            buf.readBytes(bytes);
            //            RpcResult result = codec.decode(RpcResult.class, bytes);
            //            LOG.info("client channel read task:" + result.getResult());
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