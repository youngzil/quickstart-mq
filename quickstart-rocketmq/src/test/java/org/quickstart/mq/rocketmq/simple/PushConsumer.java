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

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

public class PushConsumer {

    public static void main(String[] args) throws InterruptedException, MQClientException {
        for (int i = 0; i < 1; i++) {

            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("Test_PerfTestw124wswd");
            consumer.setInstanceName(System.currentTimeMillis() + "");

            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

            // consumer.setNamesrvAddr("10.1.243.19:9876;10.1.243.20:9876");
            // consumer.setNamesrvAddr("localhost:9876");
            consumer.setNamesrvAddr("10.1.235.106:9876");
            consumer.setConsumeThreadMin(1);// 第一次测试注释掉，处理线程是默认的20个，第二次取消注释，只有一个线程,范围[1, 1000]
            consumer.setConsumeThreadMax(1000);
            consumer.setPullBatchSize(1024);// 范围[1, 1024]
            consumer.setMaxReconsumeTimes(2);
            consumer.subscribe("MyTest", "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {

                /**
                
                 */
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    // System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
                    // return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    for (MessageExt messageExt : msgs) {
                        System.out.println("==" + messageExt.toString());
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
            System.out.printf("Consumer Started.%n");
        }
    }
}
