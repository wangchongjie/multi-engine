package com.baidu.unbiz.multiengine.transport.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.bytebuf.DefaultByteCodecFactory;
import com.baidu.unbiz.multiengine.codec.common.MsgHeadCodec;
import com.baidu.unbiz.multiengine.codec.common.ProtostuffCodec;
import com.baidu.unbiz.multiengine.endpoint.HostConf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Echoes back any received data from a client.
 */
public class AbstractTaskServer {

    private static final Log LOG = LogFactory.getLog(AbstractTaskServer.class);

    private HostConf hostConf;

    protected Channel channel;

    protected void doStart() throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (hostConf.isSsl()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        final DefaultByteCodecFactory codecFactory = new DefaultByteCodecFactory();
        codecFactory.setMsgCodec(new ProtostuffCodec());
        codecFactory.setHeadCodec(new MsgHeadCodec());

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslCtx != null) {
                                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            pipeline.addLast("decoder", codecFactory.getDecoder());
                            pipeline.addLast("encoder", codecFactory.getEncoder());
                            // pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            pipeline.addLast(new TaskServerHandler());
                        }
                    });

            // Start the server.
            ChannelFuture f = b.bind(hostConf.getPort()).sync();
            this.channel = f.channel();

            this.callbackPostInit();
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            callbackOnException(e);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void callbackOnException(Exception e) {
    }

    public void callbackPostInit() {
    }

    public HostConf getHostConf() {
        return hostConf;
    }

    public void setHostConf(HostConf hostConf) {
        this.hostConf = hostConf;
    }

}
