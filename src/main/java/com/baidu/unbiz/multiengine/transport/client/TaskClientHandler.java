package com.baidu.unbiz.multiengine.transport.client;

/**
 * Created by wangchongjie on 16/3/31.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.transport.server.TaskServerHandler;

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
        if (msg instanceof Signal) {
            Signal signal = (Signal) msg;
            RpcResult result = (RpcResult) signal.getMessage();
            TaskClientContext.fillSessionResult(sessionKey, signal.getSeqId(), result);
        }

//        if (msg instanceof ByteBuf) {
//
//            ByteBuf buf = (ByteBuf) msg;
//            byte[] headBytes = new byte[MsgHead.SIZE];
//            buf.readBytes(headBytes);
//            MsgHead packHead = MsgHead.fromBytes(headBytes);
//
//            byte[] bodyBytes = new byte[packHead.getBodyLen()];
//            buf.readBytes(bodyBytes);
//
//            this.fillResult(packHead, bodyBytes);
//        }
    }

//    private void fillResult(final MsgHead head, Object result) {
//        boolean finished = (head.getRemainLen() == 0);
//
//        SendFuture.AppendHandler handler = new SendFuture.AppendHandler() {
//            @Override
//            public void append(Object data, Object tail) {
//                byte[] dataByte = (byte[]) data;
//                byte[] tailByte = (byte[]) tail;
//
//                Assert.isTrue(tailByte.length == head.getBodyLen());
//                int index = dataByte.length - head.getRemainLen() - tailByte.length;
//System.out.println(head.getSeqId() + "index:" + index + "|" +dataByte.length + "|"+head.getRemainLen() +"|"+tailByte
//        .length);
//                System.arraycopy(tail, 0, data, index, tailByte.length);
//            }
//
//            @Override
//            public Object init() {
//                return new byte[head.getSumLen()];
//            }
//        };
//
//        TaskClientContext.appendSessionResult(sessionKey, head.getSeqId(), result, handler, finished);
//    }

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