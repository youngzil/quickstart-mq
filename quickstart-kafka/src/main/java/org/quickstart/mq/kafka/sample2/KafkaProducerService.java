/**
 * 项目名称：quickstart-kafka 
 * 文件名：KafkaProducerService.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.sample2;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KafkaProducerService
 * 
 * @author：youngzil@163.com
 * @2017年9月25日 下午10:14:31
 * @since 1.0
 */
public class KafkaProducerService {
    private static Logger LOG = LoggerFactory.getLogger(KafkaProducerService.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.1:9092,192.168.1.2:9092,192.168.1.3:9092");
        props.put("retries", 3);
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        for (int i = 0; i < 1; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("tests", "11", "今天天气不错哟yoyo=======>" + i);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception e) {
                    // TODO Auto-generated method stub
                    if (e != null)
                        System.out.println("the producer has a error:" + e.getMessage());
                    else {
                        System.out.println("The offset of the record we just sent is: " + metadata.offset());
                        System.out.println("The partition of the record we just sent is: " + metadata.partition());
                    }
                }
            });
            try {
                Thread.sleep(1000);
                // producer.close();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
}
