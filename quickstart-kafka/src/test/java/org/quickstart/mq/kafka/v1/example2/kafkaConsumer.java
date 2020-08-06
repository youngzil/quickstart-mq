/**
 * 项目名称：quickstart-kafka 
 * 文件名：kafkaConsumer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.v1.example2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * kafkaConsumer
 * 
 * @author：yangzl
 * @2017年9月25日 下午9:42:23
 * @since 1.0
 */
public class kafkaConsumer extends Thread {

    private String topic;

    public kafkaConsumer(String topic) {
        super();
        this.topic = topic;
    }

    @Override
    public void run() {
        ConsumerConnector consumer = createConsumer();
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1); // 一次从主题中获取一个数据
        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = messageStreams.get(topic).get(0);// 获取每次接收到的这个数据
        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
        while (iterator.hasNext()) {
            String message = new String(iterator.next().message());
            System.out.println("接收到: " + message);
        }
    }

    private ConsumerConnector createConsumer() {
        Properties properties = new Properties();
        properties.put("zookeeper.connect", "10.1.130.138:21821,10.1.130.138:21822,10.1.130.138:21823");// 声明zk
        properties.put("group.id", "group1");// 必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    public static void main(String[] args) {
        new kafkaConsumer("test").start();// 使用kafka集群中创建好的主题 test

    }

}
