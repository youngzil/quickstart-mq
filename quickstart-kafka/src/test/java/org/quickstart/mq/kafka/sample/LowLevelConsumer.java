package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.Properties;

public class LowLevelConsumer {

    public static void main(String args[]) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.29.100:9092");
        props.put("group.id", "group2");
        props.put("client.id", "consumer2");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializier", StringSerializer.class.getName());
        props.put("value.deserializier", StringSerializer.class.getName());
        props.put("auto.AotoCommitDemo.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "test";
        consumer.assign(Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1)));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            records.forEach(record -> System.out
                .printf("client: %s,topic: %s,partition: %d,AotoCommitDemo: %d,key: %s", record.partition(),
                    record.offset(), record.key(), record.value()));
        }

    }

}
