/**
 * 项目名称：quickstart-activemq 
 * 文件名：VirtualTopicTest.java
 * 版本信息：
 * 日期：2017年9月20日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.virtual.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * VirtualTopicTest
 * 
 * @author：youngzil@163.com
 * @2017年9月20日 下午7:50:05
 * @since 1.0
 */
public class VirtualTopicProducer {

    public static void main(String[] args) throws JMSException {

        // String url = "failover:(tcp://10.11.20.101:61616,tcp://10.11.20.102:61616,tcp://10.11.20.103:61616)";
        // String url = "failover:(tcp://10.21.20.154:61616)";

        String url = "failover:(tcp://localhost:61616)";

        // 连接到ActiveMQ服务器
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

        // 创建主题Topic
        Topic topic = session.createTopic("VirtualTopic.TEST");
        MessageProducer producer = session.createProducer(topic);
        // NON_PERSISTENT 非持久化 PERSISTENT 持久化,发送消息时用使用持久模式
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        // 循环发送消息
        for (int i = 0; i < 10; i++) {
            TextMessage message = session.createTextMessage();
            message.setText("topic 消息" + i);
            message.setStringProperty("property", "消息Property-" + i);
            // 发布主题消息
            producer.send(message);
            System.out.println("Sent message: " + message.getText());
        }

        session.close();
        connection.close();
    }

}
