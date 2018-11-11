/**
 * 项目名称：quickstart-mqtt 
 * 文件名：ServerMQTT.java
 * 版本信息：
 * 日期：2017年9月22日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.mqtt.mosquitto;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * ServerMQTT 服务器向多个客户端推送主题，即不同客户端可向服务器订阅相同主题
 * 
 * @author：youngzil@163.com
 * @2017年9月22日 下午11:35:56
 * @since 1.0
 */
public class MQTTPublisher {

    // tcp://MQTT安装的服务器地址:MQTT定义的端口号
    // public static final String HOST = "tcp://10.21.20.154:1883";
    public static final String HOST = "tcp://127.0.0.1:1883";
    // public static final String HOST = "tcp://10.1.31.40:1883";
    // 定义一个主题
    // public static final String TOPIC = "root/topic/123";
    public static final String TOPIC = "sensor";
    // 定义MQTT的ID，可以在MQTT服务配置中指定
    private static final String clientid = "server11";

    private MqttClient client;
    private MqttTopic topic11;
    private String userName = "mosquitto";
    private String passWord = "mosquitto";

    private MqttMessage message;

    /**
     * 构造函数
     * 
     * @throws MqttException
     */
    public MQTTPublisher() throws MqttException {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        connect();
    }

    /**
     * 用来连接服务器
     */
    private void connect() {

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback());
            client.connect(options);

            topic11 = client.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! " + token.isComplete());
    }

    /**
     * 启动入口
     * 
     * @param args
     * @throws MqttException
     */
    public static void main(String[] args) throws MqttException {
        MQTTPublisher server = new MQTTPublisher();

        server.message = new MqttMessage();
        server.message.setQos(0);
        server.message.setRetained(true);
        server.message.setPayload("hello,topic2".getBytes());

        server.publish(server.topic11, server.message);
        System.out.println(server.message.isRetained() + "------ratained状态");
    }
}
