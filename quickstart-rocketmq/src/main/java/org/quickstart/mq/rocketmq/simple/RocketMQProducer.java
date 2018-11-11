/**
 * 项目名称：msgframe-console 
 * 文件名：RocketMQProducer.java
 * 版本信息：
 * 日期：2016年12月6日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.rocketmq.simple;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * RocketMQProducer
 * 
 * @author：youngzil@163.com
 * @2016年12月6日 下午3:26:02
 * @version 1.0
 */
public class RocketMQProducer {

    public static void main(String[] args) throws MQClientException, InterruptedException {

        /**
         * 一个应用创建一个Producer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ProducerGroupName需要由应用来保证唯一<br>
         * ProducerGroup这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键， 因为服务器会回查这个Group下的任意一个Producer
         */
        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");

        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        // producer.setNamesrvAddr("10.1.243.19:9876;10.1.243.20:9876");
        producer.setNamesrvAddr("20.26.39.58:9876");
        producer.setMaxMessageSize(6 * 1024);
        try {
            producer.start();
        } catch (MQClientException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(//
                20, //
                40, //
                1000 * 60, //
                TimeUnit.MILLISECONDS, //
                queue, //
                new ThreadFactory() {

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "producerSend");
                    }

                });

        for (int i = 0; i < 1; i++) {
            executor.submit(new ProducerSend(producer));
        }

        /**
         * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
         */
        // producer.shutdown();

    }

}


class ProducerSend implements Runnable {

    DefaultMQProducer producer;

    ProducerSend(DefaultMQProducer producer) {
        this.producer = producer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        /**
         * 下面这段代码表明一个Producer对象可以发送多个topic，多个tag的消息。 注意：send方法是同步调用，只要不抛异常就标识成功。但是发送成功也可会有多种状态，<br>
         * 例如消息写入Master成功，但是Slave不成功，这种情况消息属于成功，但是对于个别应用如果对消息可靠性要求极高，<br>
         * 需要对这种情况做处理。另外，消息可能会存在发送失败的情况，失败重试由应用来处理。
         */
        for (int i = 0; i < 10; i++) {
            try {

                // {
                // Message msg = new Message("TopicTestA",// topic
                // "TagA",// tag
                // "OrderID001",// key
                // ("TopicTestA" + i).getBytes());// body
                // SendResult sendResult = producer.send(msg);
                // System.out.println(sendResult);
                // }

                {

                    String context = "12322";
                    while (context.getBytes().length < (2 * 1024)) {
                        context += context;
                    }

                    System.out.println(context.getBytes().length);

                    Message msg = new Message("topicTest", // topic
                            "a", // tag
                            "eee", // key
                            context.getBytes());// body
//                    ("ee--" + i).getBytes());// body

                    SendResult sendResult = producer.send(msg);
                    System.out.println(sendResult);
                    // System.out.println(System.currentTimeMillis());

                }

                // {
                // Message msg = new Message("TopicTestC",// topic
                // "TagC",// tag
                // "OrderID061",// key
                // ("TopicTestC" + i).getBytes());// body
                // SendResult sendResult = producer.send(msg);
                // System.out.println(sendResult);
                // }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Thread.sleep(3000);
        }

    }
}
