package com.chat.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author markhuang
 * @since 2019/1/22 18:02
 */
public class NettyServerInitializer extends ChannelInitializer<Channel> {

    private final ChannelGroup group;

    public NettyServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) {

        ChannelPipeline pipeline = ch.pipeline();
        //处理日志
        pipeline.addLast(new LoggingHandler(LogLevel.INFO));

        //处理心跳
        pipeline.addLast(new IdleStateHandler(0, 0, 1800, TimeUnit.SECONDS));
        pipeline.addLast(new ChatHeartbeatHandler());

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));

        //websocket建立连接时的拦截器
        pipeline.addLast(new HttpRequestHandler("/chat-app-server/ws"));

        //仅作为websocket服务器使用
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat-app-server/ws"));

        //消息接收处理
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}
