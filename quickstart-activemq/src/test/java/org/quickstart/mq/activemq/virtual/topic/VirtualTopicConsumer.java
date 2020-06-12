/**
 * 项目名称：quickstart-activemq 
 * 文件名：VirtualTopicConsumer.java
 * 版本信息：
 * 日期：2017年9月20日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.virtual.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * VirtualTopicConsumer
 * 
 * @author：yangzl
 * @2017年9月20日 下午7:50:42
 * @since 1.0
 */
public class VirtualTopicConsumer {

    public static void main(String[] args) throws JMSException, InterruptedException {

        // String url = "failover:(tcp://10.11.20.101:61616,tcp://10.11.20.102:61616,tcp://10.11.20.103:61616)";
        // String url = "failover:(tcp://10.21.20.154:61616)";

        String url = "failover:(tcp://localhost:61616)";

        // 连接到ActiveMQ服务器
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

        // 创建主题Queue
        // 同一个虚拟消费组是集群消费模式，不同的虚拟消费组是广播消费
        Queue queueA = session.createQueue("Consumer.A.VirtualTopic.TEST");
        Queue queueB = session.createQueue("Consumer.B.VirtualTopic.TEST");

        // 消费者A组创建订阅
        MessageConsumer consumerA1 = session.createConsumer(queueA);
        consumerA1.setMessageListener(new MessageListener() {
            // 订阅接收方法
            @Override
            public void onMessage(Message message) {
                TextMessage tm = (TextMessage) message;
                try {
                    System.out.println("Received message A1: " + tm.getText() + ":" + tm.getStringProperty("property"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        MessageConsumer consumerA2 = session.createConsumer(queueA);
        consumerA2.setMessageListener(new MessageListener() {
            // 订阅接收方法
            @Override
            public void onMessage(Message message) {
                TextMessage tm = (TextMessage) message;
                try {
                    System.out.println("Received message A2: " + tm.getText() + ":" + tm.getStringProperty("property"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        // 消费者B组创建订阅
        MessageConsumer consumerB1 = session.createConsumer(queueB);
        consumerB1.setMessageListener(new MessageListener() {
            // 订阅接收方法
            @Override
            public void onMessage(Message message) {
                TextMessage tm = (TextMessage) message;
                try {
                    System.out.println("Received message B1: " + tm.getText() + ":" + tm.getStringProperty("property"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        MessageConsumer consumerB2 = session.createConsumer(queueB);
        consumerB2.setMessageListener(new MessageListener() {
            // 订阅接收方法
            @Override
            public void onMessage(Message message) {
                TextMessage tm = (TextMessage) message;
                try {
                    System.out.println("Received message B2: " + tm.getText() + ":" + tm.getStringProperty("property"));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });

        // while(true){
        //
        // }

        // session.close();
        // connection.close();
    }

}
