/**
 * 项目名称：quickstart-activemq 
 * 文件名：CompositeDestinationsTest.java
 * 版本信息：
 * 日期：2017年9月20日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.composite.destination;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

/**
 * CompositeDestinationsTest
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月20日 下午7:26:39
 * @since 1.0
 */
public class CompositeDestinationTest {

    /*
     * 组合队列Composite Destinations ： 允许用一个虚拟的destination代表多个destinations,这样就可以通过composite destinations在一个操作中同时向多个queue/topic发送消息。
    有两种实现方式：
    第一种:在客户端编码实现
    第二种：在activemq.xml配置文件中实现
    第一种：在客户端编码实现 在composite destinations中，多个destination之间采用","分隔。如下：这里有2个destination "my-queue1"和"my-queue2"
     */
    /*
     * 组合队列（Composite Destinations）
    当你想把同一个消息一次发送到多个消息队列，那么可以在客户端使用组合队列。
    // send to 3 queues as one logical operation
    Queue queue = new ActiveMQQueue("FOO.A,FOO.B,FOO.C");
     producer.send(queue, someMessage);
    或
    Destination destination = session.createQueue("my-queue,my-queue2");
    producer.send(destination, someMessage);
    当然，也可以混合使用队列和主题，只需要使用前缀：queue:// 或 topic://
    // send to queues and topic one logical operation
    Queue queue = new ActiveMQQueue("FOO.A,topic://NOTIFY.FOO.A");
    producer.send(queue, someMessage);
    还有一种更透明的方式是在broker端使用，需要配合虚拟队列。
     */

    public static void main(String[] args) throws JMSException {

        // String url = "failover:(tcp://10.11.20.101:61616,tcp://10.11.20.102:61616,tcp://10.11.20.103:61616)";
        // String url = "failover:(tcp://10.21.20.154:61616)";

        String url = "failover:(tcp://localhost:61616)";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);

        // 想把同一个消息一次发送到多个消息队列，那么可以在客户端使用组合队列。
        Queue queue = new ActiveMQQueue("FOO.A,FOO.B,FOO.C");
        // 或
        Destination destination = session.createQueue("my-queue,my-queue2");

        // Queue multiQueue = new ActiveMQQueue("FOO.A,topic://NOTIFY.FOO.A");
        Queue multiQueue = new ActiveMQQueue("TEST.A,topic://NOTIFY.TEST.A");

        // Destination queue = session.createQueue("queueTest");
        // Destination topic = session.createTopic("topicTest");

        // MessageProducer producer = (MessageProducer) session.createProducer(queue);
        MessageProducer producer = (MessageProducer) session.createProducer(multiQueue);

        // 设置不持久化，可以更改
        // productor.setDeliveryMode(DeliveryMode.PERSISTENT);

        TextMessage txtMessage = session.createTextMessage();

        for (int i = 0; i < 10; i++) {
            txtMessage.setText("this is a message vvvvvv---" + i);
            // txtMessage.setJMSExpiration(1);
            // txtMessage.setJMSDeliveryMode(2);

            // 通过消息生产者发出消息
            producer.send(txtMessage);

            // producer.send(queue, txtMessage);
            // producer.send(multiQueue, txtMessage);

            /*producer.send(txtMessage, new CompletionListener(){
            
            @Override
            public void onCompletion(Message message) {
                // TODO Auto-generated method stub
                
                System.out.println(message);
                System.out.println(message);
                
            }
            
            @Override
            public void onException(Message message, Exception exception) {
                // TODO Auto-generated method stub
                
            }
            
            });*/

            session.commit();
            System.out.println("发送消息" + i + txtMessage.getText());
        }

        session.close();
        connection.close();

    }

}
