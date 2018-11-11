/**
 * 项目名称：msgframe-console 
 * 文件名：ActiveMQConsumer.java
 * 版本信息：
 * 日期：2016年12月23日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.async;

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
 * @author：youngzil@163.com
 * @2016年12月23日 下午3:21:06
 * @version 1.0
 */
public class ActiveMQProducer {

    public static void main(String[] args) throws JMSException {

        // 1.在连接的URI中配置 你可以使用连接的URI支持的参数来配置异步发送的模式，new ActiveMQConnectionFactory("tcp://locahost:61616?jms.useAsyncSend=true");
        // 2.在ConnectionFactory层配置 你可以使用ActiveMQConnectionFactory对象实例， ((ActiveMQConnectionFactory)connectionFactory).setUseAsyncSend(true);
        // 3.在Connection层配置 在Connection层的配置，将覆盖在ConnectionFactory层的配置。 ((ActiveMQConnection)connection).setUseAsyncSend(true);

        // String url = "failover:(tcp://10.21.20.154:20001,tcp://10.20.16.209:20001,tcp://10.20.16.211:20001)";
        // String url = "failover:(tcp://localhost:61616)";
        // String url = "failover:(tcp://10.11.20.101:61616)";
        // String url = "failover:(tcp://10.11.20.103:61616,tcp://10.21.20.154:61616)";
        String url = "failover:(tcp://20.26.39.56:61616)";

        // 生产者发送消息 在默认大多数情况 下，AcitveMQ是以异步模式发送消息。
        // producer默认是异步发送消息。在没有开启事务的情况下，producer发送持久化消息是同步的，调用send会阻塞直到broker把消息保存到磁盘并返回确认。
        // 同步发送：没有开启事务+producer发送持久化消息
        // 所以只要connection配置为异步，就走异步发送

        // 在不考虑事务的情况下：
        // producer发送持久化消息是同步发送，发送是阻塞的，直到收到确认。同步发送肯定是有流量控制的。
        // producer默认是异步发送，异步发送不会等待broker的确认， 所以就需要考虑流量控制了：
        // ActiveMQConnectionFactory.setProducerWindowSize(int producerWindowSize)
        // ProducerWindowSize的含义：producer每发送一个消息，统计一下发送的字节数，当字节数达到ProducerWindowSize值时，需要等待broker的确认，才能继续发送。

        // 连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();// 连接
        // connection.start();//不需要启动
        // 重点：使用事务
        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);// 创建时候自动启动,会话，接收或者发送消息的线程

        Destination queue = session.createQueue("queueTest");
        Destination topic = session.createTopic("topicTest");
        MessageProducer producer = (MessageProducer) session.createProducer(queue);

        // 设置持久化，可以更改
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        for (int i = 0; i < 1; i++) {
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText("this is a message vvvvvv---" + i);
            // txtMessage.setJMSExpiration(1);//它表示为一个长整型值的，以毫秒为单位的
            // txtMessage.setJMSDeliveryMode(2);
            // JMS定义从0级到9级的十级优先级。此外，客户端应优先考虑0-4为正常优先级， 5-9为高优先级。
            // JMS不要求提供者严格实现消息的优先级顺序；但是，它应该尽最大努力优先于正常消息投递加急消息。
            // txtMessage.setJMSPriority(4);
            // 通过消息生产者发出消息
            // txtMessage.setStringProperty("test", "hahaha");
            // txtMessage.setJMSMessageID("ID:dddd");

            producer.send(txtMessage);
            // 重点：需要commit
            session.commit();
            System.out.println("发送消息" + i + txtMessage.getText());
        }

        // producer.close();
        // session.close();
        // connection.close();

    }

}
