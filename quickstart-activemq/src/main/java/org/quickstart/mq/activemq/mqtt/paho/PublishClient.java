/**
 * 项目名称：quickstart-activemq 
 * 文件名：Server.java
 * 版本信息：
 * 日期：2016年12月22日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.mqtt.paho;

/**
 * Server 
 *  
 * @author：yangzl@asiainfo.com
 * @2016年12月22日 下午5:27:38 
 * @version 1.0
 */
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PublishClient {

    public static final String HOST = "tcp://10.20.16.209:21881";

    public static final String TOPIC = "tokudu/yzq124";
    private static final String clientid = "server";

    private MqttClient client;
    private MqttTopic topic;
    private String userName = "admin";
    private String passWord = "admin";

    private MqttMessage message;

    public PublishClient() throws MqttException {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        connect();
    }

    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback());
            client.connect(options);
            topic = client.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(MqttMessage message) throws MqttPersistenceException, MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println(token.isComplete() + "========");
    }

    public static void main(String[] args) throws MqttException {
        PublishClient server = new PublishClient();
        server.message = new MqttMessage();
        server.message.setQos(1);
        server.message.setRetained(true);
        server.message.setPayload("eeeeeaaaaaawwwwww---".getBytes());
        server.publish(server.message);
        System.out.println(server.message.isRetained() + "------ratained状态");
    }

}
