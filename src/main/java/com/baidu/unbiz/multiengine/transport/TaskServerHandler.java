package com.baidu.unbiz.multiengine.transport;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multitask.common.TaskPair;
import com.baidu.unbiz.multitask.task.ParallelExePool;
import com.baidu.unbiz.multitask.task.thread.MultiResult;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 23   * Handler implementation for the echo server.
 * 24
 */
@Sharable
@Component
public class TaskServerHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(TaskServerHandler.class);

    // Spring应用上下文环境
    private static ApplicationContext applicationContext;

    public TaskServerHandler() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        TaskServerHandler.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @return Object
     *
     * @throws BeansException
     */
    public static ParallelExePool getParallelExePool() throws BeansException {
        ParallelExePool parallelExePool = (ParallelExePool) applicationContext.getBean("simpleParallelExePool");
        return parallelExePool;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {

            Codec codec = new ProtostuffCodec();

            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            TaskCommand command = codec.decode(TaskCommand.class, bytes);
            LOG.info("channel read task:" + command);

            ParallelExePool parallelExePool = getParallelExePool();
            MultiResult result = parallelExePool.submit(new TaskPair(command.getTaskBean(), command.getParams()));
            List<DeviceViewItem> response = result.getResult(command.getTaskBean());

            buf.clear();
            buf.writeBytes(codec.encode(List.class, response));
            ctx.writeAndFlush(buf);
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
        //        String msgStr = "hello netty! im server.";
        //        ByteBuf firstMessage = Unpooled.buffer(TaskClient.SIZE);
        //        for(char c : msgStr.toCharArray()) {
        //            firstMessage.writeChar(c);
        //        }
        //        ctx.writeAndFlush(firstMessage);
        //        LOG.debug("channelRead");
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