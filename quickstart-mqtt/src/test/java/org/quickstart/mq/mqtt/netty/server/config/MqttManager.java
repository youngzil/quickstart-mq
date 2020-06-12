/**
 * 项目名称：quickstart-mqtt 
 * 文件名：MqttManager.java
 * 版本信息：
 * 日期：2017年10月27日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.mqtt.netty.server.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.netty.channel.Channel;
import io.netty.util.internal.ConcurrentSet;

/**
 * MqttManager
 * 
 * @author：yangzl
 * @2017年10月27日 下午6:58:24
 * @since 1.0
 */
public class MqttManager {

    private static final ConcurrentMap<String/*topic*/, ConcurrentSet<Channel>> topicTable = new ConcurrentHashMap<>();

    public static void registerTopic(String topic, Channel channel) {

        if (channel != null && topic != null) {
            ConcurrentSet<Channel> channels = topicTable.get(topic);

            if (null == channels || channels.isEmpty()) {
                channels = new ConcurrentSet<>();
                topicTable.putIfAbsent(topic, channels);
            }

            channels.add(channel);
        }
    }

    public static void unregisterTopic(String topic, Channel channel) {

        if (channel != null && topic != null) {
            ConcurrentSet<Channel> channels = topicTable.get(topic);

            if (null != channels && channels.isEmpty()) {
                channels.remove(channel);

                if (channels.isEmpty()) {
                    topicTable.remove(topic);
                }
            }

        }
    }

    public static ConcurrentMap<String, ConcurrentSet<Channel>> getTopictable() {
        return topicTable;
    }

}
