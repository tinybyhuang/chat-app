package com.chat.netty;

import com.chat.user.CurrentUser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 咨询消息接收
 *
 * @author markhuang
 * @since 2019/1/22 17:58
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final Logger websocketLogger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    private final Logger heartbeatLogger = LoggerFactory.getLogger(TextWebSocketFrameHandler.class);

    /**
     * 保存客户端连接
     */
    private final ChannelGroup group;


    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //websocket握手成功事件(成功建立连接)
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {

            ctx.pipeline().remove(HttpRequestHandler.class);
            //医生端小程序已连接
            doctorOnline(ctx);

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 接收消息
     *
     * @param ctx ctx
     * @param msg 消息内容
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        received(ctx, msg.text());
    }

    private void received(ChannelHandlerContext ctx, String text) {

        if (StringUtils.isBlank(text)) {
            return;
        }
        String channelId = ctx.channel().id().asLongText();
        //心跳不处理
        if (text.startsWith(SessionUtils.HEARTBEAT)) {
            heartbeatLogger.info("接收客户端心跳, HB: {}, ChannelId: {}", text, channelId);
            return;
        }
        websocketLogger.info("接收到医生回复消息，message: {}, ChannelId: {}", text, channelId);
        //接收消息
        receive(text);
    }

    private void receive(String text) {

        System.out.println(text);
        //TODO receive message
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        doctorOffline(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    }

    private void doctorOnline(ChannelHandlerContext ctx) {

        Channel channel = ctx.channel();
        ChannelId channelId = channel.id();
        String token = channel.attr(SessionUtils.CHANNEL_TOKEN_KEY).get();
        CurrentUser user = new CurrentUser();
        //记录当前用户已连接websocket
        SessionUtils.userOnline(token, channelId, user);
        group.add(channel);

        websocketLogger.info("医生端小程序已连接， CurrentUser: {}, ChannelId: {}", user, channelId.asLongText());
    }

    private void doctorOffline(ChannelHandlerContext ctx) {

        Channel channel = ctx.channel();
        String channelId = ctx.channel().id().asLongText();
        String token = channel.attr(SessionUtils.CHANNEL_TOKEN_KEY).get();
        CurrentUser user = new CurrentUser();
        SessionUtils.userOffline(token);
        group.remove(channel);
        ctx.close();

        websocketLogger.warn("医生端小程序已断开， CurrentUser: {}, ChannelId: {}", user, channelId);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        websocketLogger.error("TextWebSocketFrameHandler exceptionCaught: {}", cause.getMessage());
    }

}
