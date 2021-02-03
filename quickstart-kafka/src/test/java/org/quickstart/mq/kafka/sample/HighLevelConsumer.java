package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class HighLevelConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.29.100:9092");
        props.put("group.id", "group2");
        props.put("client.id", "consumer2");
        props.put("enable.auto.commit", "true");//
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializier", StringSerializer.class.getName());
        props.put("value.deserializier", StringSerializer.class.getName());
        props.put("max.poll.interval.ms", "300000");
        props.put("max.poll.records", "500");
        props.put("auto.offset.reset", "earliest");

        //将offset存储到Kafka的Topic
        props.put("offset.storage", "kafka");
        //自动存储到对应的介质中
        props.put("dual.commit.enabled", "true");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "test";
        String clientid = "consumer2";
        consumer.subscribe(Arrays.asList(topic), new ConsumerRebalanceListener() {

            @Override
            //parition原先被当前consumer消费，经过rebalance后不再被当前consumer消费了，就会调用
            // 当 consumer 触发 balance操作时，会触发onPartitionsRevoked方法，参数partitions表示那些仅仅需要回收的分区，而不是分配的所有分区。
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                partitions.forEach(topicPartition -> {
                    System.out.printf("revoked partition for client %s : %s-%s %n", clientid, topicPartition.topic(),
                        topicPartition.partition());
                });
            }

            @Override
            //parition原先不被当前consumer消费，经过rebalance后将分配给当前consumer消费的Partition时调用
            // kafka 在获取到分区结果后，会调用onPartitionsAssigned方法，参数partitions表示它所分配的分区结果。
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                collection.forEach(topicPartition -> {
                    System.out.printf("assign partition for client %s : %s-%s %n", clientid, topicPartition.topic(),
                        topicPartition.partition());
                });
            }

            // 当 consumer 调用 close 方法或者 unsubscribe 方法，会调用onPartitionsLost方法，参数partitions表示它不在订阅的分区。
            // 接口有个默认实现，默认调用onPartitionsRevoked(partitions)

        });

        while (true) {
            //从阻塞队列中取消息，最高延迟为100ms
            ConsumerRecords<String, String> records = consumer.poll(100);
            //停止对此Topic的partition0消费，同理可用resume方法让该partition能够被消费
            consumer.pause(Arrays.asList(new TopicPartition(topic, 0)));
            records.forEach(record -> System.out
                .printf("client: %s,topic: %s,partition: %d,AotoCommitDemo: %d,key: %s", record.partition(),
                    record.offset(), record.key(), record.value()));
        }

    }
}
