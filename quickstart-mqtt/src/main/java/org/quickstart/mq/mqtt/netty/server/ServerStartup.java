/**
 * 项目名称：quickstart-mqtt 
 * 文件名：ServerStarter.java
 * 版本信息：
 * 日期：2017年10月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.mqtt.netty.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.quickstart.mq.mqtt.netty.server.config.NettyServerConfig;
import org.quickstart.mq.mqtt.netty.server.config.RemotingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

/**
 * ServerStarter
 * 
 * @author：youngzil@163.com
 * @2017年10月25日 下午5:13:31
 * @since 1.0
 */
public class ServerStartup {

    private static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);

    private final NettyServerConfig nettyServerConfig;

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupBoss;
    private final EventLoopGroup eventLoopGroupWork;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    private boolean isSsl = false;
    private static SslContext sslCtx;

    private static final AtomicBoolean started = new AtomicBoolean(false);

    public ServerStartup() {

        this.nettyServerConfig = new NettyServerConfig();

        this.serverBootstrap = new ServerBootstrap();

        this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("NettyBoss_%d", this.threadIndex.incrementAndGet()));
            }
        });

        if (useEpoll()) {
            this.eventLoopGroupWork = new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = nettyServerConfig.getServerSelectorThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerEPOLLSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        } else {
            this.eventLoopGroupWork = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(), new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = nettyServerConfig.getServerSelectorThreads();

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("NettyServerNIOSelector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        }

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerWorkerThreads(), new ThreadFactory() {

            private AtomicInteger threadIndex = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "NettyServerCodecThread_" + this.threadIndex.incrementAndGet());
            }
        });

        if (this.isSsl) {
            try {
                SelfSignedCertificate bootstrap = new SelfSignedCertificate();
                this.sslCtx = SslContextBuilder.forServer(bootstrap.certificate(), bootstrap.privateKey()).build();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

    }

    public static void main(String[] args) throws Exception {

        if (started.compareAndSet(false, true)) {
            new ServerStartup().start();
        }
    }

    public void start() throws Exception {

        ServerBootstrap childHandler = this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWork)//
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)//
                .option(ChannelOption.SO_BACKLOG, 1024)//
                .option(ChannelOption.SO_REUSEADDR, true)//
                .option(ChannelOption.SO_KEEPALIVE, false)//
                .childOption(ChannelOption.TCP_NODELAY, true)//
                .childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSndBufSize())//
                .childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketRcvBufSize())//
                .localAddress(new InetSocketAddress(this.nettyServerConfig.getListenPort()))//
                .childHandler(new ChannelInitializer<SocketChannel>() {//
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {

                        ChannelPipeline pipeline = ch.pipeline();

                        // if(this.sslCtx != null) {
                        // pipeline.addLast(new ChannelHandler[]{this.sslCtx.newHandler(ch.alloc())});
                        // }

                        pipeline.addLast(defaultEventExecutorGroup, //
                                new MqttDecoder(), //
                                MqttEncoder.INSTANCE, //
                                new IdleStateHandler(0, 1000, nettyServerConfig.getServerChannelMaxIdleTimeSeconds()), //
                                new LoggingHandler(LogLevel.INFO), //
                                new NettyMqttServerHandler());//
                    }
                });

        if (nettyServerConfig.isServerPooledByteBufAllocatorEnable()) {
            childHandler.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        try {
            ChannelFuture sync = this.serverBootstrap.bind().sync();
            InetSocketAddress addr = (InetSocketAddress) sync.channel().localAddress();

            // ChannelFuture future = this.serverBootstrap.bind(this.nettyServerConfig.getListenPort());
            // future.channel().closeFuture().sync();

            logger.info("监听端口" + addr.getPort() + "启动成功");
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        }

        // Runtime.getRuntime().addShutdownHook();

    }

    private boolean useEpoll() {
        return RemotingUtil.isLinuxPlatform() && nettyServerConfig.isUseEpollNativeSelector() && Epoll.isAvailable();
    }

    public void shutdown() {
        if (this.eventLoopGroupBoss != null) {
            this.eventLoopGroupBoss.shutdownGracefully();
        }

        if (this.eventLoopGroupWork != null) {
            this.eventLoopGroupWork.shutdownGracefully();
        }

    }

    protected void destory() {
        try {
            this.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
