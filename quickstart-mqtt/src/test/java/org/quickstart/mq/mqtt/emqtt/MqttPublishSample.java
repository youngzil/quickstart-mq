/**
 * 项目名称：quickstart-mqtt 
 * 文件名：MqttPublishSample.java
 * 版本信息：
 * 日期：2017年9月22日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.mqtt.emqtt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * MqttPublishSample
 * 
 * @author：yangzl
 * @2017年9月22日 下午11:46:16
 * @since 1.0
 */
public class MqttPublishSample {

    public static void main(String[] args) throws KeyManagementException, CertificateException, FileNotFoundException, IOException, KeyStoreException {

        String topic = "sensor";
        String content = "Message from MqttPublishSample";
        int qos = 2;
        // String broker = "ssl://10.21.20.154:1883";
        // String broker = "tcp://10.21.20.154:1883";
        String broker = "tcp://localhost:1883";

        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);

            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

}
