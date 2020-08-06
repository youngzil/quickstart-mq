package org.quickstart.mq.kafka.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class HighLevelConsumer {
    private ConsumerConnector consumer;// 消费者

    private String topic;// topic名称

    // private ConfigProperties properties;

    private int consumerBatch;// 每次消费数量(存入HBase的数量)

    public HighLevelConsumer(String topic, String groupId, int consumerBatch) {
        consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(groupId));
        this.topic = topic;
        this.consumerBatch = consumerBatch;
    }

    // 获取消费者相关的参数配置
    private static ConsumerConfig createConsumerConfig(String groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.88.101:2181");
        // props.put("group.id", "test-consumer-group");
        // props.put("zookeeper.session.timeout.ms", "400");
        // props.put("zookeeper.sync.time.ms", "200");
        props.put("group.id", groupId);
        // props.put("num.consumer.fetchers", 10);// The number fetcher threads
        // used to fetch data.
        // props.put("auto.commit.interval.ms", "5000");//The frequency in ms
        // that the consumer offsets are committed to zookeeper.
        // props.put("queued.max.message.chunks", "10");// Max number of message
        // chunks buffered for
        // consumption. Each
        // chunk can be up to
        // fetch.message.max.bytes.

        // long fetchLength = 1024*1024*1024;
        // props.put("fetch.message.max.bytes", String.valueOf(fetchLength));
        return new ConsumerConfig(props);
    }

    private void consumerMsg() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> metadata = it.next();
            String msg = String.valueOf(metadata.message());
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        String topic = "Kafka-Test-201702091542";
        String groupId = "testGroup";
        int consumerBatch = 1;
        HighLevelConsumer comsumer = new HighLevelConsumer(topic, groupId, consumerBatch);
        comsumer.consumerMsg();
    }
}
