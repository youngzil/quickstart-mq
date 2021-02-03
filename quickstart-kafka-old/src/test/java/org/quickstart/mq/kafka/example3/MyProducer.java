/**
 * 项目名称：quickstart-kafka 
 * 文件名：MyProducer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example3;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * MyProducer
 * 
 * @author：yangzl
 * @2017年9月25日 下午10:22:17
 * @since 1.0
 */
public class MyProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("metadata.broker.list", "localhost:9092");
        props.setProperty("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        // 创建生产这对象
        Producer<String, String> producer = new Producer<String, String>(config);
        // 生成消息
        KeyedMessage<String, String> data = new KeyedMessage<String, String>("mykafka", "test-kafka");
        try {
            int i = 1;
            while (i < 100) {
                // 发送消息
                producer.send(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.close();
    }
}
