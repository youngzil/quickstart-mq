/**
 * 项目名称：console 文件名：ActiveMQConsumer.java 版本信息： 日期：2016年12月23日 Copyright youngzil
 * Corporation 2016 版权所有 *
 */
package org.quickstart.mq.activemq.simple;

import java.util.Enumeration;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * ActiveMQConsumer
 * 
 * @author：yangzl
 * @2016年12月23日 下午3:21:06
 * @version 1.0
 */
public class ActiveMQConsumer {

    public static void main(String[] args) throws JMSException {

        // String url = "failover:(tcp://10.20.16.210:20001,tcp://10.20.16.209:20001,tcp://10.20.16.211:20001)";
        // String url = "failover:(tcp://10.20.16.210:20002,tcp://10.20.16.209:20002,tcp://10.20.16.211:20002)";

        // String url = "failover:(tcp://20.26.39.56:61616)";
//        String url = "failover:(tcp://10.11.20.101:61616,tcp://10.11.20.103:61616,tcp://10.1.226.100:61616)";
         String url = "failover:(tcp://localhost:61616)";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        connection.start();

        Enumeration names = connection.getMetaData().getJMSXPropertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            System.out.println("jmsx name===" + name);
        }

        Session session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
//        Destination queue = session.createQueue("pooledqueueTest");
        Destination queue = session.createQueue("queueTest");
        Destination topic = session.createTopic("topicTest");

        MessageConsumer consumer = session.createConsumer(queue);

        // 第一种情况
        /* while (true) {
            TextMessage message = (TextMessage) consumer.receive();
        
            try {
            //Thread.sleep(24*60*10*1000);
            Thread.sleep(5*1000);
            } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        //            session.commit();
            // TODO something....
        //            System.out.println("收到消息：" + message.getText());
        }*/
        // ----------------第一种情况结束----------------------

        // 第二种方式
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
//                if (message instanceof TextMessage) {
//                    System.out.println("arg0=" + message);
                    /* try {
                    throw new JMSException("sss");
                    session.commit();
                    } catch (JMSException e) {
                    e.printStackTrace();
                    }*/
//                }
            }
        });

        // 第三种情况
        /*while (true) {
        	Message msg = consumer.receive(1000);
        	TextMessage message = (TextMessage) msg;
        	if (null != message) {
        		System.out.println("收到消息:" + message.getText());
        	}
        }*/

        // consumer.close();
        // session.close();
        // conn.close();

    }

}
