package com.baidu.unbiz.multiengine.transport.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.transport.HostConf;

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
    private static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private HostConf hostConf;
    private String sessionKey;

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
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), hostConf.getHost(), hostConf.getPort()));
                            }
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new TaskClientHandler(sessionKey));
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(hostConf.getHost(), hostConf.getPort()).sync();

            TaskClientContext.sessionChannelMap.put(sessionKey, f.channel());

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public <T> T makeCall(TaskCommand command) {
        ByteBuf buf = Unpooled.buffer(TaskClient.SIZE);

        Codec codec = new ProtostuffCodec();
        buf.writeBytes(codec.encode(TaskCommand.class, command));

        Channel channel = TaskClientContext.sessionChannelMap.get(sessionKey);
        channel.writeAndFlush(buf);
        SendFutrue sendFutrue = new SendFutrueImpl();
        TaskClientContext.sessionResultMap.put(sessionKey, sendFutrue);

        return handleResult(sessionKey);
    }

    private static <T> T handleResult(String sessionKey) {
        SendFutrue sendFutrue = TaskClientContext.sessionResultMap.get(sessionKey);

        ByteBuf buf = sendFutrue.get();
        Codec codec = new ProtostuffCodec();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        RpcResult result = codec.decode(RpcResult.class, bytes);
        LOG.info("client channel read task:" + result.getResult());

        return (T) result;
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
}