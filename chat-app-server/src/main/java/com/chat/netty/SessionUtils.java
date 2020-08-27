package com.chat.netty;

import com.chat.user.CurrentUser;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 医生咨询会话
 *
 * @author markhuang
 * @since 2019-2-14 14:21
 */
public class SessionUtils {

    public static final String AUTH_TOKEN = "authToken";

    public static final String HEARTBEAT = "HB";

    /**
     * 鉴权token
     */
    public static final AttributeKey<String> CHANNEL_TOKEN_KEY = AttributeKey.valueOf("netty.channel.token");
    /**
     * 用来保存当前在线医生
     */
    public static final ConcurrentHashMap<String, ChannelId> ONLINE_USERS = new ConcurrentHashMap<>(1000);
    /**
     * 当前用户信息
     */
    public static final ConcurrentHashMap<String, CurrentUser> CURRENT_USERS = new ConcurrentHashMap<>(1000);

    public static void userOnline(String sessionId, ChannelId channelId, CurrentUser currentUser) {
        ONLINE_USERS.putIfAbsent(sessionId, channelId);
        CURRENT_USERS.putIfAbsent(sessionId, currentUser);
    }

    public static void userOffline(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            return;
        }
        ONLINE_USERS.remove(sessionId);
        CURRENT_USERS.remove(sessionId);
    }

    public static CurrentUser getCurrentUser(String sessionId) {
        return CURRENT_USERS.get(sessionId);
    }

}
