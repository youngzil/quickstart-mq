/**
 * 项目名称：quickstart-kafka 
 * 文件名：ConsumerMsgTask.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * ConsumerMsgTask
 * 
 * @author：youngzil@163.com
 * @2017年9月25日 下午9:13:45
 * @since 1.0
 */
public class ConsumerMsgTask implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;

    public ConsumerMsgTask(KafkaStream stream, int threadNumber) {
        m_threadNumber = threadNumber;
        m_stream = stream;
    }

    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext())
            System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
