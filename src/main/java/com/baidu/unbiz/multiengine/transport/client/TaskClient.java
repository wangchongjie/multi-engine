package com.baidu.unbiz.multiengine.transport.client;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.baidu.unbiz.multiengine.codec.DefaultByteCodecFactory;
import com.baidu.unbiz.multiengine.codec.MsgCodec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.Signal;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.transport.HostConf;
import com.baidu.unbiz.multiengine.transport.SequenceIdGen;
import com.baidu.unbiz.multiengine.transport.protocol.PackUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class TaskClient {

    private static final Log LOG = LogFactory.getLog(TaskClient.class);
    private static final int SIZE = Integer.parseInt(System.getProperty("size", "1024"));

    private HostConf hostConf;
    private String sessionKey;
    private SequenceIdGen idGen;

    public void start() {
        final TaskClient client = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    client.doStart();
                } catch (Exception e) {
                    LOG.error("client run fail:", e);
                }
            }
        }.start();
    }

    private void doStart() throws Exception {
        // Configure SSL.git
        final SslContext sslCtx;
        if (hostConf.isSsl()) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        final DefaultByteCodecFactory codecFactory = new DefaultByteCodecFactory();
        codecFactory.setMsgCodec(new ProtostuffCodec());

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslCtx != null) {
                                pipeline.addLast(sslCtx.newHandler(ch.alloc(), hostConf.getHost(), hostConf.getPort()));
                            }
                            // pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast("decoder", codecFactory.getDecoder());
                            pipeline.addLast("encoder", codecFactory.getEncoder());
                            pipeline.addLast(new TaskClientHandler(sessionKey));
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(hostConf.getHost(), hostConf.getPort()).sync();
            // keep channel in context
            TaskClientContext.sessionChannelMap.put(sessionKey, f.channel());

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public <T> T makeCall(TaskCommand command) {
//        MsgCodec codec = new ProtostuffCodec();
//        byte[] bytes = codec.encode(command);
//
        long seqId = idGen.genId();
//        List<byte[]> packDatas = PackUtils.buildPackData(seqId, bytes, TaskClient.SIZE);
//
//        Assert.isTrue(packDatas.size() == 1);
//        byte[] commandByte = packDatas.get(0);
//        ByteBuf buf = Unpooled.buffer(commandByte.length);
//        buf.writeBytes(commandByte);

        Signal<TaskCommand> signal = new Signal<TaskCommand>(command);
        signal.setSeqId(seqId);


        Channel channel = TaskClientContext.sessionChannelMap.get(sessionKey);
        channel.writeAndFlush(signal);

        SendFuture sendFutrue = new SendFutrueImpl();
        TaskClientContext.placeSessionResult(sessionKey, seqId, sendFutrue);
        return handleResult(sessionKey, seqId);
    }

    private static <T> T handleResult(String sessionKey, long seqId) {
        SendFuture sendFutrue = TaskClientContext.getSessionResult(sessionKey, seqId);
//        LOG.debug("client channel read result:" + result.getResult());
        return sendFutrue.get();
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public HostConf getHostConf() {
        return hostConf;
    }

    public void setHostConf(HostConf hostConf) {
        this.hostConf = hostConf;
    }

    public SequenceIdGen getIdGen() {
        return idGen;
    }

    public void setIdGen(SequenceIdGen idGen) {
        this.idGen = idGen;
    }
}
