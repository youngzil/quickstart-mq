/**
 * 项目名称：quickstart-mq-msgframe-v2 
 * 文件名：ThreadPoolSendTest.java
 * 版本信息：
 * 日期：2018年7月3日
 * Copyright asiainfo Corporation 2018
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

import com.ai.aif.msgframe.ClientFactory;
import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

/**
 * ThreadPoolSendTest
 * 
 * @author：yangzl@asiainfo.com
 * @2018年7月3日 下午2:13:00
 * @since 1.0
 */
public class SendTransactionTest {

    private static final Logger logger = LoggerFactory.getLogger(SendTransactionTest.class);

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

        MfProducerTxClient client = new MfProducerTxClient();
        for (int i = 0; i < 1; i++) {
            pool.submit(new ProducerThreadTest(client, i));
        }

        while (true) {
        }

    }

    @org.junit.Test
    public void tesTxError() throws Exception {
        MfProducerTxClient client = ClientFactory.createTxClient();
        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("hello");
        client.send("open_quene", message);

        MfProducerTxClient client2 = ClientFactory.createTxClient();
        client2.send("AMS_ACCT_BUSI_DEAL", message);

        client.commit();
        client2.commit();
    }

    static class ProducerThreadTest implements Runnable {
        private int index;
        private MfProducerTxClient client;

        public ProducerThreadTest(MfProducerTxClient client, int index) {
            super();
            this.index = index;
            this.client = client;
        }

        @Override
        public void run() {

            for (int i = 0; i < 10000; i++) {
                try {
                    MsgFTextMessage message = new MsgFTextMessage();
                    message.setText("hello" + index + i);

                    this.client.send("topicTest", message);

                    // TimeUnit.SECONDS.sleep(200L);

                    this.client.commit();
                    logger.info("发送成功，" + message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
