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

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * RocketMQConsumer
 * 
 * @author：yangzl@asiainfo.com
 * @2016年12月6日 下午3:25:42
 * @version 1.0
 */
public class RocketMQConsumer2 {

    /**
     * 当前例子是PushConsumer用法，使用方式给用户感觉是消息从RocketMQ服务器推到了应用客户端。<br>
     * 但是实际PushConsumer内部是使用长轮询Pull方式从Broker拉消息，然后再回调用户Listener方法<br>
     */
    public static void main(String[] args) throws InterruptedException, MQClientException {
        /**
         * 一个应用创建一个Consumer，由应用来维护此对象，可以设置为全局对象或者单例<br>
         * 注意：ConsumerGroupName需要由应用来保证唯一
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroupNaeeemewww");

        // consumer.setNamesrvAddr("10.1.243.19:9876;10.1.243.20:9876");
        // consumer.setNamesrvAddr("10.21.38.150:9876");
        // consumer.setNamesrvAddr("10.1.235.106:9876");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.setConsumeThreadMin(1);
        consumer.setPullBatchSize(1);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET_AND_FROM_MIN_WHEN_BOOT_FIRST);

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
        consumer.subscribe("nihao", "*");

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
                System.out.println(Thread.currentThread().getName() + " Receive New Messages: " + msgs + ",size=" + msgs.size());

                MessageExt msg = msgs.get(0);
                for (MessageExt msg2 : msgs) {
                    System.out.println("===" + msg2);
                }

                String ss = new String(msg.getBody());
                System.out.println(ss);

                // if(ss.indexOf("1") != -1){
                // return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                // }else if(ss.indexOf("2") != -1){
                // return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                // }

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
