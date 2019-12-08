package org.quickstart.mq.activemq.topic;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQTopic;

public class Receiver {

  public static void main(String[] args) {
    // ConnectionFactory ：连接工厂，JMS用它创建连接
    ActiveMQConnectionFactory connectionFactory;
    // Connection ：JMS客户端到JMS Provider的连接
    Connection connection = null;
    // Session：一个发送或接收消息的线程
    Session session;
    // 消费者，消息接收者
    MessageConsumer consumer;

    connectionFactory = new ActiveMQConnectionFactory(
        "", "", "tcp://localhost:61616");
    try {
      // 构造从工厂得到连接对象
      connection = connectionFactory.createConnection();

      connection.setClientID("clientID001");
      // 启动
      connection.start();
      // 获取操作连接
      session = connection.createSession(false,
          Session.AUTO_ACKNOWLEDGE);
      // 获取session
      Topic topic = session.createTopic("MQ_test");
      // 得到消息生成者【发送者】
      consumer = session.createDurableSubscriber(topic, "MQ_sub");

      while (true) {
        //设置接收者接收消息的时间，为了便于测试，这里谁定为100s
        Message message = (Message) consumer.receive(100000);
        AdvisorySupport.getMasterBrokerAdvisoryTopic();
        System.out.println(
            AdvisorySupport.getProducerAdvisoryTopic(new ActiveMQTopic("MQ_test")).getOptions());
        if (null != message) {
          System.out.println("收到消息" + message.getJMSMessageID());
        } else {
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (null != connection) {
          connection.close();
        }
      } catch (Throwable ignore) {
      }
    }
  }

}
