package org.apache.kafka.clients.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Properties;

public class MyKafkaConsumer2<K, V> {

    public MyKafkaConsumer2(Map<String, Object> configs, Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        System.out.println("MyKafkaConsumer2");
    }

    public MyKafkaConsumer2(Properties properties) {
        System.out.println("MyKafkaConsumer2 nnnnnnn");
    }

}
