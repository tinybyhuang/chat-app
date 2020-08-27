package com.chat.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * websocket请求拦截器
 * 用于鉴权，并将authToken保存至当前channel中
 *
 * @author markhuang
 * @since 2019/1/22 17:29
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    private final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final String webUri;

    public HttpRequestHandler(String webUri) {
        this.webUri = webUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {

        String uri = StringUtils.substringBefore(fullHttpRequest.uri(), "?");
        //websocket请求
        if (webUri.equalsIgnoreCase(uri)) {
            QueryStringDecoder query = new QueryStringDecoder(fullHttpRequest.uri());
            //鉴权
            String token = checkAndGetToken(query);
            //保存token到当前的channel中
            channelHandlerContext.channel().attr(SessionUtils.CHANNEL_TOKEN_KEY).getAndSet(token);

            fullHttpRequest.setUri(uri);
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("HttpRequestHandler exceptionCaught: {}", cause.getMessage());
        ctx.close();
    }

    private String checkAndGetToken(QueryStringDecoder query) {
        return "token";
//        Map<String, List<String>> map = query.parameters();
//        List<String> tokens = map.get(SessionUtils.AUTH_TOKEN);
//        if (tokens == null || tokens.isEmpty()) {
//            throw new IllegalArgumentException("401");
//        }
//        String token = tokens.get(0);
//        if (!checkToken(token)) {
//            throw new IllegalArgumentException("401");
//        }
//        return token;
    }

    private boolean checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }
        return true;
    }
}
