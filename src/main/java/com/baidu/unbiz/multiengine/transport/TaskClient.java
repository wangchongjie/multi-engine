package com.baidu.unbiz.multiengine.transport;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.unbiz.multiengine.codec.Codec;
import com.baidu.unbiz.multiengine.codec.impl.ProtostuffCodec;
import com.baidu.unbiz.multiengine.dto.RpcResult;
import com.baidu.unbiz.multiengine.dto.TaskCommand;
import com.baidu.unbiz.multiengine.tmp.Endpoint;
import com.baidu.unbiz.multiengine.tmp.EndpointUtil;

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
 * 32   * Sends one message when a connection is open and echoes back any received
 * 33   * data to the server.  Simply put, the echo client initiates the ping-pong
 * 34   * traffic between the echo client and server by sending the first message to
 * 35   * the server.
 * 36
 */
public final class TaskClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    private static ConcurrentHashMap<String, Channel> sessionChannelMap = new ConcurrentHashMap<String, Channel>();
    private static ConcurrentHashMap<String, Object> sessionResultMap = new ConcurrentHashMap<String, Object>();

    private static final Log LOG = LogFactory.getLog(TaskClient.class);


    public static void main(String[] args) throws Exception {
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
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
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new TaskClientHandler());
                        }
                    });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            sessionChannelMap.put("test", f.channel());
            mockCall();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    public static void mockCall() {
        TaskCommand command = new TaskCommand();
        command.setTaskBean("deviceStatFetcher");
        command.setParams(null);

        ByteBuf buf = Unpooled.buffer(TaskClient.SIZE);

        Codec codec = new ProtostuffCodec();
        buf.writeBytes(codec.encode(TaskCommand.class, command));

        Channel channel = sessionChannelMap.get("test");
        channel.writeAndFlush(buf);
    }

    public static void setResult(Object result) {
        sessionResultMap.put("test", result);
    }

    public static Object getResult() {

        ByteBuf buf = (ByteBuf) sessionResultMap.get("test");
        Codec codec = new ProtostuffCodec();
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        RpcResult result = codec.decode(RpcResult.class, bytes);
        LOG.info("client channel read task:" + result.getResult());

        return result;
        //        Channel channel = sessionChannelMap.get("test");
        //        channel.read();
        //        if (msg instanceof ByteBuf) {
        //            Codec codec = new ProtostuffCodec();
        //
        //            ByteBuf buf = (ByteBuf) msg;
        //            byte[] bytes = new byte[buf.readableBytes()];
        //            buf.readBytes(bytes);
        //            RpcResult result = codec.decode(RpcResult.class, bytes);
        //            LOG.info("client channel read task:" + result.getResult());
        //        }
    }

}
