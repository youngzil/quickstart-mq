/**
 * 项目名称：quickstart-kafka 
 * 文件名：kafkaProducer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example2;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

/**
 * kafkaProducer
 * 
 * @author：youngzil@163.com
 * @2017年9月25日 下午9:39:22
 * @since 1.0
 */
public class kafkaProducer extends Thread {

    private String topic;

    public kafkaProducer(String topic) {
        super();
        this.topic = topic;
    }

    @Override
    public void run() {
        Producer producer = createProducer();
        int i = 0;
        while (true) {
            producer.send(new KeyedMessage<Integer, String>(topic, "message: " + i++));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Producer createProducer() {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", "10.1.130.138:21821,10.1.130.138:21822,10.1.130.138:21823");// 声明zk
        properties.put("serializer.class", StringEncoder.class.getName());
        // properties.put("metadata.broker.list", "192.168.1.110:9092,192.168.1.111:9093,192.168.1.112:9094");// 声明kafka broker
        properties.put("metadata.broker.list", "10.21.20.154:9092");// 声明kafka broker
        return new Producer<Integer, String>(new ProducerConfig(properties));
    }

    public static void main(String[] args) {
        new kafkaProducer("test").start();// 使用kafka集群中创建好的主题 test

    }

}
