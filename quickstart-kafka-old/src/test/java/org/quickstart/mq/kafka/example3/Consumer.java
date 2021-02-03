/**
 * 项目名称：quickstart-kafka 
 * 文件名：Consumer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example3;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

/**
 * Consumer 消费者的线程执行器实现
 * 
 * @author：yangzl
 * @2017年9月25日 下午10:23:30
 * @since 1.0
 */
public class Consumer implements Runnable {

    private KafkaStream stream;
    private int threadNumber;

    public Consumer(KafkaStream a_stream, int a_threadNumber) {
        threadNumber = a_threadNumber;
        stream = a_stream;
    }

    public void run() {
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext())
            System.out.println("Thread " + threadNumber + ": " + new String(it.next().message()));
        System.out.println("Shutting down Thread: " + threadNumber);
    }
}
