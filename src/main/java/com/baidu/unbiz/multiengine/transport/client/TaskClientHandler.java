package com.baidu.unbiz.multiengine.transport.client;

/**
 * Created by wangchongjie on 16/3/31.
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.transport.protocol.NSHead;
import com.baidu.unbiz.multiengine.transport.protocol.PackHead;
import com.baidu.unbiz.multiengine.transport.server.TaskServerHandler;

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
        if (msg instanceof ByteBuf) {

            ByteBuf buf = (ByteBuf) msg;
            byte[] headBytes = new byte[PackHead.SIZE];
            buf.readBytes(headBytes);
            PackHead packHead = PackHead.fromBytes(headBytes);

            byte[] bodyBytes = new byte[buf.readableBytes()];
            buf.readBytes(bodyBytes);

            this.fillResult(packHead, bodyBytes);
        }
    }

    private void fillResult(final PackHead head, Object result) {

        boolean finished = head.getRemainLen() == 0;

        SendFuture.AppendHandler handler = new SendFuture.AppendHandler() {
            @Override
            public void append(Object data, Object tail) {
                byte[] dataByte = (byte[]) data;
                byte[] tailByte = (byte[]) tail;
                int index = dataByte.length - head.getRemainLen() - tailByte.length;
                System.arraycopy(tail, 0, data, index, tailByte.length);
            }

            @Override
            public Object init() {
                return new byte[head.getBodyLen()];
            }
        };

        TaskClientContext.appendSessionResult(sessionKey, head.getSeqId(), result, handler, finished);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOG.debug("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        ctx.close();
        LOG.debug("exceptionCaught", cause);
    }

}