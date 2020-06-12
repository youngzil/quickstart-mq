/**
 * 项目名称：msgframe-console 
 * 文件名：ActiveMQConsumer.java
 * 版本信息：
 * 日期：2016年12月23日
 * Copyright youngzil Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.delay;

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
 * @author：yangzl
 * @2016年12月23日 下午3:21:06
 * @version 1.0
 */
public class ActiveMQProducer {

    public static void main(String[] args) throws JMSException {

        // 第一步需要修改activemq.xml配置文件，开启延时发送
        // <broker xmlns="http://activemq.apache.org/schema/core" ... schedulerSupport="true" >
        // </broker>

        // String url = "failover:(tcp://10.21.20.154:20001,tcp://10.20.16.209:20001,tcp://10.20.16.211:20001)";
        // String url = "failover:(tcp://localhost:61616)";
        // String url = "failover:(tcp://10.11.20.101:61616)";
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
        // productor.setDeliveryMode(DeliveryMode.PERSISTENT);

        for (int i = 0; i < 1; i++) {
            TextMessage txtMessage = session.createTextMessage();
            txtMessage.setText("this is a message vvvvvv---" + i);
            // txtMessage.setJMSExpiration(1);
            // txtMessage.setJMSDeliveryMode(2);
            // 通过消息生产者发出消息
            // txtMessage.setStringProperty("test", "hahaha");

            // 延迟30秒，投递10次，间隔10秒:
            long delay = 30 * 1000;
            long period = 10 * 1000;
            int repeat = 9;
            txtMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);// 设置延迟时间
            // message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, period);//设置重复投递间隔（非必要，根据实际情况）
            // message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, repeat);//重复投递次数（非必要，根据实际情况）

            // linux中corntab中的表达式
            // 使用 CRON 表达式的例子：
            // message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "0 * * * *");

            // CRON表达式的优先级高于另外三个参数，如果在设置了CRON的同时，也有repeat和period参数，则会在每次CRON执行的时候，重复投递repeat次，每次间隔为period。
            // 就是说设置是叠加的效果。例如每小时都会发生消息被投递10次，延迟1秒开始，每次间隔1秒:
            // message.setStringProperty(ScheduledMessage.AMQ_SCHEDULED_CRON, "0 * * * *");
            // message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000);
            // message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_PERIOD, 1000);
            // message.setIntProperty(ScheduledMessage.AMQ_SCHEDULED_REPEAT, 9);

            producer.send(txtMessage);
            System.out.println("发送消息" + i + txtMessage.getText());
        }

        // producer.close();
        // session.close();
        // connection.close();

    }

}
