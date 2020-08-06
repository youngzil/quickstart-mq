/**
 * 项目名称：quickstart-kafka 
 * 文件名：ManualOffsetConsumer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.v1.sample2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
* 自己控制偏移量提交
* 很多时候，我们是希望在获得消息并经过一些逻辑处理后，才认为该消息已被消费，这可以通过自己控制偏移量提交来实现。
*/
public class ManualOffsetConsumer {
    private static Logger LOG = LoggerFactory.getLogger(ManualOffsetConsumer.class);

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Properties props = new Properties();
        // 设置brokerServer(kafka)ip地址
        props.put("bootstrap.servers", "192.168.1.1:9092,192.168.1.2:9092,192.168.1.3:9092");
        // 设置consumer group name
        props.put("group.id", "mygroup11");
        props.put("enable.auto.commit", "false");
        // 设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        // 如果采用latest，消费者只能得道其启动后，生产者生产的消息
        props.put("auto.offset.reset", "earliest");
        // 设置心跳时间
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("test"));
        final int minBatchSize = 5; // 批量提交数量
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("consumer message values is " + record.value() + " and the offset is " + record.offset());
                buffer.add(record);
            }
            if (buffer.size() >= minBatchSize) {
                System.out.println("now commit offset" + buffer.size());
                consumer.commitSync();
                buffer.clear();
            }
        }
    }
}
