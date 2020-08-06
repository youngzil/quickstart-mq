/**
 * 项目名称：quickstart-kafka 
 * 文件名：KafkaConsumerExample.java
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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * KafkaConsumerExample
 * 
 * @author：yangzl
 * @2017年9月25日 下午9:46:28
 * @since 1.0
 */
public class KafkaConsumerExample {
    public static void main(String[] args) {
        /*Properties props = new Properties();
        props.put("bootstrap.servers", "10.11.20.103:9093,10.11.20.103:9094");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");*/

        Properties props = new Properties();
        // props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.11.20.103:9093,10.11.20.103:9094");// 该地址是集群的子集，用来探测集群。
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.1.243.23:59092,10.1.243.23:59093,10.1.243.23:59094");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer_1");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");// cousumer的分组id
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");// 自动提交offsets
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");// 每隔1s，自动提交offsets
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");// Consumer向集群发送自己的心跳，超时则认为Consumer已经死了，kafka会把它的分区分配给其他进程
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");// 反序列化器

        // 构建拦截链
        List<String> interceptors = new ArrayList<>();
        interceptors.add("org.quickstart.kafka.sample2.interceptor.consumer.TimeStampPrependerInterceptor"); // interceptor 1
        interceptors.add("org.quickstart.kafka.sample2.interceptor.consumer.CounterInterceptor"); // interceptor 2
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);// 拦截器

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "testTopic";
        consumer.subscribe(Arrays.asList(topic));// 订阅的topic,可以多个
        long currentTime = System.currentTimeMillis();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(10000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("partition = %d , offset = %d, key = %s, value = %s\n", record.partition(), record.offset(), record.key(), record.value());
                currentTime = System.currentTimeMillis();
            }

            // if ((System.currentTimeMillis() - currentTime) > 60_000) {
            // break;
            // }
        }

        // 一定要关闭producer，这样才会调用interceptor的close方法
        // consumer.close();

    }
}
