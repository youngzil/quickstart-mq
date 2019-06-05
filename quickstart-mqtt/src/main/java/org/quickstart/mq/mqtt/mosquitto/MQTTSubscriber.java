/**
 * 项目名称：quickstart-mqtt 
 * 文件名：ClientMQTT.java
 * 版本信息：
 * 日期：2017年9月22日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.mqtt.mosquitto;

import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * ClientMQTT
 * 
 * @author：youngzil@163.com
 * @2017年9月22日 下午11:33:21
 * @since 1.0
 */
public class MQTTSubscriber {

    // public static final String HOST = "tcp://10.21.20.154:1883";
    public static final String HOST = "tcp://127.0.0.1:1883";
    // public static final String HOST = "tcp://10.1.31.40:1883";
    // public static final String TOPIC = "root/topic/123";
    public static final String TOPIC = "sensor";
    private static final String clientid = "client11";
    private MqttClient client;
    private MqttConnectOptions options;
    private String userName = "admin";// 做什么用的？为什么可以随便设置，为什么生产和消费可以不一样
    private String passWord = "admin";

    private ScheduledExecutorService scheduler;

    private void start() {
        try {
            // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(HOST, clientid, new MemoryPersistence());
            // MQTT的连接设置
            options = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            // 设置回调
            client.setCallback(new PushCallback());
            MqttTopic topic = client.getTopic(TOPIC);
            // setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            options.setWill(topic, "close".getBytes(), 2, true);

            client.connect(options);
            // 订阅消息
            int[] Qos = {0};
            String[] topic1 = {TOPIC};
            client.subscribe(topic1, Qos);

            System.out.println("subscribe success");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MqttException {
        MQTTSubscriber client = new MQTTSubscriber();
        client.start();
    }
}
