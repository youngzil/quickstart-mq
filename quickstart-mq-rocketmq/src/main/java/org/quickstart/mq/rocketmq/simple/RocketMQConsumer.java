/**
 * 项目名称：msgframe-console 
 * 文件名：RocketMQConsumer.java
 * 版本信息：
 * 日期：2016年12月6日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.rocketmq.simple;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;


/**
 * RocketMQConsumer
 * 
 * @author：yangzl@asiainfo.com
 * @2016年12月6日 下午3:25:42
 * @version 1.0
 */
public class RocketMQConsumer {

    /**
     * 当前例子是PushConsumer用法，使用方式给用户感觉是消息从RocketMQ服务器推到了应用客户端。<br>
     * 但是实际PushConsumer内部是使用长轮询Pull方式从Broker拉消息，然后再回调用户Listener方法<br>
     */
    public static void main(String[] args) throws InterruptedException, MQClientException {
        /**
         * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ConsumerGroupName需要由应用来保证唯一
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroupName");

        // consumer.setNamesrvAddr("10.1.243.19:9876;10.1.243.20:9876");
        // consumer.setNamesrvAddr("10.21.38.150:9876");
        // consumer.setNamesrvAddr("10.1.235.102:9876;10.1.235.103:9876");
        consumer.setNamesrvAddr("10.11.20.102:9876");
        consumer.setConsumeThreadMin(1);
        consumer.setPullBatchSize(3);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.setMessageModel(MessageModel.CLUSTERING);

        /**
         * 订阅指定topic下tags分别等于TagA或TagC或TagD
         */
        // consumer.subscribe("TopicTest1", "TagA || TagC || TagD");
        /**
         * 订阅指定topic下所有消息<br>
         * 注意：一个consumer对象可以订阅多个topic
         */
        // consumer.subscribe("TopicTest2", "*");
        // consumer.subscribe("TopicTestA", "TagA");
        // consumer.subscribe("TopicTestwwwwwwwwA", "TagA");
        // consumer.subscribe("yang3", "*");
        consumer.subscribe("topicTest", "*");

        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        // consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            /**
             * * 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
             */
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                // System.out.println(Thread.currentThread().getName()
                // + " Receive New Messages: " + msgs);

                MessageExt msg = msgs.get(0);

                for (MessageExt messageExt : msgs) {

                    System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + messageExt);
                }

                // System.out.println(new String(msg.getBody()));
                if (msg.getTopic().equals("TopicTest1")) {
                    // 执行TopicTest1的消费逻辑
                    if (msg.getTags() != null && msg.getTags().equals("TagA")) {
                        // 执行TagA的消费
                    } else if (msg.getTags() != null && msg.getTags().equals("TagC")) {
                        // 执行TagC的消费
                    } else if (msg.getTags() != null && msg.getTags().equals("TagD")) {
                        // 执行TagD的消费
                    }
                } else if (msg.getTopic().equals("TopicTest2")) {
                    // 执行TopicTest2的消费逻辑
                } else if (msg.getTopic().equals("yang3")) {
                    if (new String(msg.getBody()).indexOf("7") != -1) {
                        System.out.println(new String(msg.getBody()));
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    } else {
                        System.out.println(new String(msg.getBody()));
                    }
                    // try {
                    // Thread.sleep(1000);
                    // } catch (InterruptedException e) {
                    // // TODO Auto-generated catch block
                    // e.printStackTrace();
                    // }
                } else if (msg.getTopic().equals("MyTest")) {
                    System.out.println("3+" + new String(msg.getBody()));
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        /**
         * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         */
        consumer.start();

        System.out.println("Consumer Started.");
    }
}
