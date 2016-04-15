package com.baidu.unbiz.multiengine.transport.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.bytebuf.DefaultByteCodecFactory;
import com.baidu.unbiz.multiengine.codec.common.MsgHeadCodec;
import com.baidu.unbiz.multiengine.codec.common.ProtostuffCodec;
import com.baidu.unbiz.multiengine.transport.HostConf;
import com.baidu.unbiz.multiengine.transport.SequenceIdGen;

import io.netty.bootstrap.Bootstrap;
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
public class AbstractTaskClient {

    protected static final Log LOG = LogFactory.getLog(AbstractTaskClient.class);
    protected static final int SIZE = Integer.parseInt(System.getProperty("size", "1024"));

    protected HostConf hostConf;
    protected String sessionKey;
    protected SequenceIdGen idGen;

    protected void doStart() throws Exception {


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
        codecFactory.setHeadCodec(new MsgHeadCodec());

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

            this.callbackPostInit();
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public void callbackPostInit() {
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
