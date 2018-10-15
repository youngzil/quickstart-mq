/**
 * 项目名称：msgframe-console 
 * 文件名：ActiveMQConsumer.java
 * 版本信息：
 * 日期：2016年12月23日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.expiration;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

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
        // String url = "failover:(tcp://10.11.20.103:61616,tcp://10.21.20.154:61616)";
        String url = "failover:(tcp://20.26.39.56:61616)";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        // connection.start();//不需要启动

        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);// 创建时候自动启动

        Destination queue = session.createQueue("queueTest");
        Destination topic = session.createTopic("topicTest");
        MessageProducer producer = (MessageProducer) session.createProducer(queue);

        // 设置不持久化，可以更改
        // producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        // producer.setTimeToLive(3 * 1000);// 在该生产者发送的所有消息上设置相同的消息到期时间。

        // 1、MessageProducer.setTimeToLive()在该生产者发送的所有消息上设置相同的消息到期时间。
        // 2、另一方面TextMessage.setJMSExpiration()，基于每个消息设置消息到期时间。
        // 每条消息设置的到期时间将覆盖设置的到期时间MessageProducer。
        // 3、JMS还指定了一种在调用MessageProducer.send方法时设置消息到期时间的方法。
        // void send(Message message, int deliveryMode,int priority, long timeToLive)
        // 您可以在MessageProducer或中设置消息到期TextMessage。无需同时设置。

        for (int i = 0; i < 1; i++) {

            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText("this is a message vvvvvv---" + i);
             txtMessage.setJMSExpiration(3 * 1000);// 它表示为一个长整型值的，以毫秒为单位的,0消息永不过期
            // txtMessage.setJMSDeliveryMode(2);
            // JMS定义从0级到9级的十级优先级。此外，客户端应优先考虑0-4为正常优先级， 5-9为高优先级。
            // JMS不要求提供者严格实现消息的优先级顺序；但是，它应该尽最大努力优先于正常消息投递加急消息。
            // txtMessage.setJMSPriority(4);
            // 通过消息生产者发出消息
            // txtMessage.setStringProperty("test", "hahaha");
            // txtMessage.setJMSMessageID("ID:dddd");

            producer.send(txtMessage);
            // 第2个参数：是否持久化；第3个参数：优先级（0~4普通 5~9加急）；第4个参数：消息在ActiveMQ中间件中存放的有效期
            // producer.send(txtMessage, DeliveryMode.PERSISTENT, 4, 3 * 1000);

            // session.commit();
            System.out.println("发送消息" + i + txtMessage.getText());
        }

        // producer.close();
        // session.close();
        // connection.close();

    }

}
