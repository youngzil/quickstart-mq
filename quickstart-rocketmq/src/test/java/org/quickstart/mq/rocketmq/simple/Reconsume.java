/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quickstart.mq.rocketmq.simple;

import java.util.List;
import java.util.Set;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.store.LocalFileOffsetStore;
import org.apache.rocketmq.client.consumer.store.ReadOffsetType;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

public class Reconsume {

    public static void main(String[] args) throws InterruptedException, MQClientException {
        
        // String subString = "nihao||hahaha";
        String subString = "TagA || TagC || TagD";
        String[] tags = subString.split("\\|\\|");
        System.out.println(tags);

        final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("Test_PerfTest1");
        consumer.setInstanceName(System.currentTimeMillis() + "");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        // consumer.setNamesrvAddr("10.1.243.19:9876;10.1.243.20:9876");
        consumer.setNamesrvAddr("10.1.235.106:9876");
        consumer.setConsumeThreadMin(1);// 第一次测试注释掉，处理线程是默认的20个，第二次取消注释，只有一个线程
        consumer.setConsumeThreadMax(50);
        consumer.setPullBatchSize(1);
        consumer.setMaxReconsumeTimes(3);
        consumer.subscribe("TopicTestA", "TagA");

        /**
         * 1：必须set监听，不能regist监听； 2：必须start后才能修改； 3：必须用consumer的Impl设置OffsetStore,不能用consumer直接设置
         */
        consumer.setMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");

                try {
                    Set<MessageQueue> set = consumer.fetchSubscribeMessageQueues("TopicTestA");
                    if (null != set) {
                        for (MessageQueue mq : set) {
                            long offset = consumer.getDefaultMQPushConsumerImpl().getOffsetStore().readOffset(mq, ReadOffsetType.MEMORY_FIRST_THEN_STORE);
                            System.out.println("mq=" + mq + ",queueId=" + mq.getQueueId() + ",offset=" + offset);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.printf("Consumer Started.%n");

        MQClientInstance clientInstance = MQClientManager.getInstance().getOrCreateMQClientInstance(consumer);
        //MQClientInstance clientInstance = MQClientManager.getInstance().getAndCreateMQClientInstance(consumer, null);
        LocalFileOffsetStore offsetStore = new LocalFileOffsetStore(clientInstance, "");
        Set<MessageQueue> set = consumer.fetchSubscribeMessageQueues("TopicTestA");
        if (null != set) {
            for (MessageQueue mq : set) {
                offsetStore.updateOffset(mq, 0, true);
            }
            consumer.getDefaultMQPushConsumerImpl().setOffsetStore(offsetStore);
            consumer.setOffsetStore(offsetStore);// 错误，拿不到
        }

        System.out.println("ConsumeFromWhere=" + consumer.getConsumeFromWhere());

    }
}
