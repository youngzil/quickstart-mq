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
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

/**
 * ThreadPoolSendTest
 * 
 * @author：youngzil@163.com
 * @2018年7月3日 下午2:13:00
 * @since 1.0
 */
public class SendTagTest {

    private static final Logger logger = LoggerFactory.getLogger(SendTagTest.class);

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

            for (int i = 0; i < 10000; i++) {
                try {
                    MsgFTextMessage message = new MsgFTextMessage();
                    message.setText("message tag test" + i);
                    message.setFilterTag("tag1");// 设置消息tag
                    client.send("TagTest", message);

                    logger.info("发送成功，" + message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
