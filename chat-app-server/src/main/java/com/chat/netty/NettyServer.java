package com.chat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 咨询消息管理器
 *
 * @author markhuang
 * @since 2019-2-14 11:59
 */
@Component
public class NettyServer {
    /**
     * 用于保存客户端连接
     */
    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    private final EventLoopGroup workGroup = new NioEventLoopGroup();

    private Channel channel;

    public ChannelFuture start(InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyServerInitializer(channelGroup))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind(address).syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy() {
        if (channel != null) {
            channel.close();
        }
        channelGroup.close();
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
