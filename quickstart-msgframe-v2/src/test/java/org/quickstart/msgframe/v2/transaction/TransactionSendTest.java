/**
 * 项目名称：quickstart-msgframe-v2 
 * 文件名：TransactionSendTest.java
 * 版本信息：
 * 日期：2018年8月22日
 * Copyright youngzil Corporation 2018
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.transaction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ai.aif.msgframe.MfProducerTxClient;

/**
 * TransactionSendTest
 * 
 * @author：yangzl
 * @2018年8月22日 下午2:44:22
 * @since 1.0
 */
public class TransactionSendTest {

    public static void main(String[] args) {

        if (args.length > 0) {
            for (String str : args)
                System.out.println("接收参数" + str);

        }

        // 配置稽核拦截类消息入库，要执行改行代码，否则并发过大会报错
        // CacheFactory._getCacheInstances();

        boolean isSingle = Boolean.parseBoolean(args[0]);
        int sendTimes = Integer.parseInt(args[1]);

        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(//
                sendTimes, //
                Integer.MAX_VALUE, //
                3000, //
                TimeUnit.MILLISECONDS, //
                queue, //
                new ThreadFactoryImpl("transaction_send_test_thread_"));

        MfProducerTxClient client = new MfProducerTxClient();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(sendTimes);

        for (int i = 0; i < sendTimes; i++) {
            if (!isSingle) {
                client = new MfProducerTxClient();
            }
            executor.submit(new ClientExecutorThread(cyclicBarrier, client));
        }

    }

}
