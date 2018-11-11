/**
 * 项目名称：quickstart-kafka 
 * 文件名：SimpleKafkaProducer.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.example2;

import java.util.Properties;

import org.apache.log4j.Logger;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * SimpleKafkaProducer
 * 
 * @author：youngzil@163.com
 * @2017年9月25日 下午10:17:09
 * @since 1.0
 */
public class SimpleKafkaProducer {
    private static final Logger logger = Logger.getLogger(SimpleKafkaProducer.class);

    /**
     * 
     */
    private void execMsgSend() {
        Properties props = new Properties();
        props.put("metadata.broker.list", "192.168.137.117:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "0");

        ProducerConfig config = new ProducerConfig(props);

        logger.info("set config info(" + config + ") ok.");

        Producer<String, String> procuder = new Producer<>(config);

        String topic = "mytopic";
        for (int i = 1; i <= 10; i++) {
            String value = "value_" + i;
            KeyedMessage<String, String> msg = new KeyedMessage<String, String>(topic, value);
            procuder.send(msg);
        }
        logger.info("send message over.");

        procuder.close();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SimpleKafkaProducer simpleProducer = new SimpleKafkaProducer();
        simpleProducer.execMsgSend();
    }

}
