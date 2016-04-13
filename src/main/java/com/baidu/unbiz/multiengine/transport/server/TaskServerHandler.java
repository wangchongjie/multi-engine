package com.baidu.unbiz.multiengine.transport.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.exception.MultiEngineException;
import com.baidu.unbiz.multiengine.transport.protocol.PackHead;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

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

            long seqId = buf.readLong();

            LOG.info("server read bytes:" + buf.readableBytes());

            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            TaskCommand command = codec.decode(TaskCommand.class, bytes);
            LOG.debug("channel read task:" + command);

            // execute command
            ParallelExePool parallelExePool = bean("simpleParallelExePool");
            MultiResult results = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));

            Object response = results.getResult(command.getTaskBean());
            RpcResult result = RpcResult.newInstance().setResult(response);

            // send result to client

            byte[] bodyBytes = codec.encode(RpcResult.class, result);
            List<byte[]> packBytes = buildPackData(seqId, bodyBytes, buf.capacity());


            for (byte[] packByte : packBytes) {
                ByteBuf part = Unpooled.buffer(packByte.length);
                part.writeBytes(packByte);
                ctx.writeAndFlush(part);
            }
//            ctx.flush();
//            ReferenceCountUtil.release(buf);
        }
    }

    private int countRemainSize(int bufLength, int packSize) {
        int remainSize = bufLength - packSize;
        if (remainSize < 0) {
            remainSize = 0;
        }
        return remainSize;
    }

    public List<byte[]> buildPackData(long seqId, byte[] buf, int packSize) {
        if (packSize < PackHead.SIZE) {
            throw new MultiEngineException("packSize is too small");
        }
        List<byte[]> dataList = new ArrayList<byte[]>();

        int remainSize = countRemainSize(buf.length, packSize - PackHead.SIZE);
        int bodyLength = buf.length;
        int index = 0;
        do {
            PackHead head = PackHead.create(seqId);
            head.setBodyLen(bodyLength);
            head.setRemainLen(remainSize);

            int packLength = Math.min(bodyLength - index + PackHead.SIZE, packSize);
            byte[] pack = new byte[packLength];

//            ByteBuffer bbf = ByteBuffer.wrap(pack);
            // bbf.order(ByteOrder.LITTLE_ENDIAN);
//            bbf.position(PackHead.SIZE);

            int dataLen = packLength - PackHead.SIZE;
            System.arraycopy(buf, index, pack, PackHead.SIZE, dataLen);
//            bbf.put(buf);

            System.arraycopy(head.toBytes(), 0, pack, 0, PackHead.SIZE);
//            bbf.flip();

            index += packLength - PackHead.SIZE;
            remainSize = countRemainSize(buf.length - index, packSize - PackHead.SIZE);
            dataList.add(pack);
        } while (index < bodyLength);

        return dataList;
    }

//    public byte[] buildNsHeadCommand(long seqId, byte[] buf) {
//        NSHead head = NSHead.factory("multi-engine");
//        head.setBodyLen(buf.length + LONG_SIZE);
//
//        byte[] pack = new byte[buf.length + NSHead.SIZE + LONG_SIZE];
//
//        ByteBuffer bbf = ByteBuffer.wrap(pack);
////        bbf.order(ByteOrder.LITTLE_ENDIAN);
//        bbf.position(NSHead.SIZE);
//        bbf.putLong(seqId);
//        bbf.put(buf);
//
//        // 需要校验最终数据
//        checksum.update(pack, NSHead.SIZE, buf.length + LONG_SIZE);
//        head.setReserved((int) (checksum.getValue() & 0xffffffff));
//        checksum.reset();
//        System.arraycopy(head.toBytes(), 0, pack, 0, NSHead.SIZE);
//
//        bbf.flip();
//        return pack;
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