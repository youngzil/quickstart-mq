/**
 * 项目名称：quickstart-mq-msgframe-v2 
 * 文件名：ThreadPoolSendTest.java
 * 版本信息：
 * 日期：2018年7月3日
 * Copyright youngzil Corporation 2018
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.producer.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

/**
 * ThreadPoolSendTest
 * 
 * @author：yangzl
 * @2018年7月3日 下午2:13:00
 * @since 1.0
 */
public class SendOrderTest {

    private static final Logger logger = LoggerFactory.getLogger(SendOrderTest.class);

    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(//
            5, //
            10, //
            1000 * 60, //
            TimeUnit.MILLISECONDS, //
            queue, //
            new ThreadFactoryImpl("Thread_"));

    @org.junit.Test
    public void test() {

        MfProducerClient client = new MfProducerClient();
        for (int i = 0; i < 1; i++) {
            pool.submit(new ProducerThreadTest(client, i));
        }

        while (true) {
        }

    }

    static class ProducerThreadTest implements Runnable {
        private int index;
        private MfProducerClient client;

        public ProducerThreadTest(MfProducerClient client, int index) {
            super();
            this.index = index;
            this.client = client;
        }

        @Override
        public void run() {

            String orderId = "2013";// orderId对于同一组顺序消息必须一致
            for (int i = 0; i < 3; i++) {
                try {
                    MsgFTextMessage orderMessage = new MsgFTextMessage();
                    orderMessage.setText("order message " + i);
                    // message_order.setHeaderAttribute(MsgFMessage.MQ_SCHEDULED_DELAY, "10");
                    // message_order.setHeaderAttribute(MsgFMessage.MQ_PRIORITY, "7");
                    orderMessage.setHeaderAttribute(MsgFMessage.MESSAGE_KEY, "iskeyTest");
                    client.sendOrderMsg("OrderTest", orderMessage, orderId);
                    logger.info("发送成功，" + orderMessage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
