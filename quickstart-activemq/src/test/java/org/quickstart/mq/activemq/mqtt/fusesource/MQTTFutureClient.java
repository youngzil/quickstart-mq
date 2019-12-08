/**
 * 项目名称：quickstart-activemq 
 * 文件名：MQTTFutureClient.java
 * 版本信息：
 * 日期：2016年12月22日
 * Copyright youngzil Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.mqtt.fusesource;

/**
 * MQTTFutureClient 
 *  
 * @author：youngzil@163.com
 * @2016年12月22日 下午4:17:12 
 * @version 1.0
 */
import java.net.URISyntaxException;

import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * MQTT moquette 的Client 段用于订阅主题，并接收主题信息
 * 
 * 采用Future 式 订阅主题
 * 
 * @author longgangbai
 */
public class MQTTFutureClient {
    // private static final Logger LOG = LoggerFactory.getLogger(MQTTFutureClient.class);
    private final static String CONNECTION_STRING = "tcp://10.20.16.209:21881";
    private final static boolean CLEAN_START = true;
    private final static short KEEP_ALIVE = 30;// 低耗网络，但是又需要及时获取数据，心跳30s
    private final static String CLIENT_ID = "publishService";
    public static Topic[] topics = {new Topic("china/aaa", QoS.EXACTLY_ONCE), new Topic("china/bbb", QoS.AT_LEAST_ONCE), new Topic("china/ccc", QoS.AT_MOST_ONCE)};
    public final static long RECONNECTION_ATTEMPT_MAX = 6;
    public final static long RECONNECTION_DELAY = 2000;

    public final static int SEND_BUFFER_SIZE = 2 * 1024 * 1024;// 发送最大缓冲为2M

    public static void main(String[] args) {
        // 创建MQTT对象
        MQTT mqtt = new MQTT();
        try {
            // 设置mqtt broker的ip和端口
            mqtt.setHost(CONNECTION_STRING);
            // 连接前清空会话信息
            mqtt.setCleanSession(CLEAN_START);
            // 设置重新连接的次数
            mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);
            // 设置重连的间隔时间
            mqtt.setReconnectDelay(RECONNECTION_DELAY);
            // 设置心跳时间
            mqtt.setKeepAlive(KEEP_ALIVE);
            // 设置缓冲的大小
            mqtt.setSendBufferSize(SEND_BUFFER_SIZE);

            // 获取mqtt的连接对象BlockingConnection
            final FutureConnection connection = mqtt.futureConnection();
            connection.connect();
            connection.subscribe(topics);
            while (true) {
                Future<Message> futrueMessage = connection.receive();
                Message message = futrueMessage.await();

                System.out.println("MQTTFutureClient.Receive Message " + "Topic Title :" + message.getTopic() + " context :" + String.valueOf(message.getPayloadBuffer()));
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
    }
}
