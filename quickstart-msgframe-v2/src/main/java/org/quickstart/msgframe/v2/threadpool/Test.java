/**
 * 项目名称：quickstart-msgframe 
 * 文件名：Test.java
 * 版本信息：
 * 日期：2017年2月19日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * Test
 * 
 * @author：youngzil@163.com
 * @2017年2月19日 下午9:33:53
 * @version 1.0
 */
public class Test {

    public static void main(String[] args) {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

        ThreadPoolExecutor consumeExecutor = new ThreadPoolExecutor(//
                5, //
                10, //
                1000 * 60, //
                TimeUnit.MILLISECONDS, //
                queue, //
                new ThreadFactoryImpl("ConsumeMessageThread_"));

        for (int i = 0; i < 20; i++) {
            consumeExecutor.submit(new ConsumerThread());
        }

    }

}


class ConsumerThread implements Runnable {

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("Test_PerfTest1");
        consumer.setInstanceName(System.currentTimeMillis() + "");

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.setNamesrvAddr("localhost:9876");
        consumer.setConsumeThreadMin(30);// 第一次测试注释掉，处理线程是默认的20个，第二次取消注释，只有一个线程,范围[1, 1000]
        consumer.setConsumeThreadMax(1000);
        consumer.setPullBatchSize(1024);// 范围[1, 1024]
        try {
            consumer.subscribe("TopicTestC", "TagC");
        } catch (MQClientException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            /**
            
             */
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
                // return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.printf("Consumer Started...");
    }
}


class ThreadFactoryImpl implements ThreadFactory {
    private final AtomicLong threadIndex = new AtomicLong(0);
    private final String threadNamePrefix;

    public ThreadFactoryImpl(final String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadNamePrefix + this.threadIndex.incrementAndGet());

    }
}
