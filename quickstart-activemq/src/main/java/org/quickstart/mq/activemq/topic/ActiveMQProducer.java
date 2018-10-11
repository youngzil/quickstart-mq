/**
 * 项目名称：msgframe-console 
 * 文件名：ActiveMQConsumer.java
 * 版本信息：
 * 日期：2016年12月23日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.topic;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;

/**
 * ActiveMQConsumer
 * 
 * @author：yangzl@asiainfo.com
 * @2016年12月23日 下午3:21:06
 * @version 1.0
 */
public class ActiveMQProducer {

    public static void main(String[] args) throws JMSException {

        // String url = "failover:(tcp://10.21.20.154:20001,tcp://10.20.16.209:20001,tcp://10.20.16.211:20001)";
        // String url = "failover:(tcp://localhost:61616)";
        // String url = "failover:(tcp://10.11.20.101:61616)";
        // String url = "failover:(tcp://10.11.20.103:61616,tcp://10.21.20.154:61616)";
        String url = "failover:(tcp://20.26.39.56:61616)";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        // connection.start();//不需要启动

        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);// 创建时候自动启动

        Destination topic = session.createTopic("topicTest");
        MessageProducer producer = (MessageProducer) session.createProducer(topic);

        // 设置不持久化，可以更改
        // productor.setDeliveryMode(DeliveryMode.PERSISTENT);

        for (int i = 0; i < 1; i++) {
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText("this is a message vvvvvv---" + i);
            // txtMessage.setJMSExpiration(1);
            // txtMessage.setJMSDeliveryMode(2);
            // 通过消息生产者发出消息
            // txtMessage.setStringProperty("test", "hahaha");
            // txtMessage.setJMSMessageID("ID:dddd");

            producer.send(txtMessage);

            // session.commit();
            System.out.println("发送消息" + i + txtMessage.getText());
        }

        // producer.close();
        // session.close();
        // connection.close();

    }

}
