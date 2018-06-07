package test.server;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tianhong on 2018/5/11.
 */
public class ChannelManager {
    public static final Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();
    public static final long HEART_BEAT_TIMEOUT = 3;
}
