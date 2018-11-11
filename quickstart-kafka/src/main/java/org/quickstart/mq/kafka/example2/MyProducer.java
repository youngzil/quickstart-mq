/**
 * 项目名称：quickstart-kafka 
 * 文件名：MyProducer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example2;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * MyProducer
 * 
 * @author：youngzil@163.com
 * @2017年9月25日 下午10:01:18
 * @since 1.0
 */
public class MyProducer {
    private static final String TOPIC = "my-replicated-topic5"; // kafka创建的topic
    private static final String CONTENT = "This is a single message"; // 要发送的内容
    private static final String BROKER_LIST = "10.101.214.71:9092,10.101.214.73:9092,10.101.214.74:9092"; // broker的地址和端口
    private static final String SERIALIZER_CLASS = "kafka.serializer.StringEncoder"; // 序列化类

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("serializer.class", SERIALIZER_CLASS);
        props.put("metadata.broker.list", BROKER_LIST);

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        // Send one message.
        KeyedMessage<String, String> message = new KeyedMessage<String, String>(TOPIC, CONTENT);
        producer.send(message);

        // Send multiple messages.
        List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>();
        for (int i = 0; i < 5; i++) {
            messages.add(new KeyedMessage<String, String>(TOPIC, "Multiple message at a time. " + i));
        }
        producer.send(messages);

    }
}
