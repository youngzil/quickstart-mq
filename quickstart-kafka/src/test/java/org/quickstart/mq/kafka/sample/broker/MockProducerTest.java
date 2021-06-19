package org.quickstart.mq.kafka.sample.broker;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.util.Collections.emptySet;
import static org.junit.Assert.assertTrue;

public class MockProducerTest {

    private static String TOPIC_NAME = "topic";

    MockProducer mockProducer;
    KafkaProducer kafkaProducer;

    @Test
    void givenKeyValue_whenSend_thenVerifyHistory() throws ExecutionException, InterruptedException {

        mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());

        kafkaProducer = new KafkaProducer(mockProducer);
        Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send("soccer", "{\"site\" : \"baeldung\"}");

        assertTrue(mockProducer.history().size() == 1);

        ProducerRecord<String, String> producerRecord = (ProducerRecord<String, String>)mockProducer.history().get(0);
        assertTrue(producerRecord.key().equalsIgnoreCase("data"));
        assertTrue(recordMetadataFuture.get().partition() == 0);
    }

    @Test
    void givenKeyValue_whenSendWithPartitioning_thenVerifyPartitionNumber() throws ExecutionException, InterruptedException {
        PartitionInfo partitionInfo0 = new PartitionInfo(TOPIC_NAME, 0, null, null, null);
        PartitionInfo partitionInfo1 = new PartitionInfo(TOPIC_NAME, 1, null, null, null);
        List<PartitionInfo> list = new ArrayList<>();
        list.add(partitionInfo0);
        list.add(partitionInfo1);

        Cluster cluster = new Cluster("kafkab", new ArrayList<Node>(), list, emptySet(), emptySet());
        this.mockProducer = new MockProducer<>(cluster, true, new EvenOddPartitioner(), new StringSerializer(), new StringSerializer());

        kafkaProducer = new KafkaProducer(mockProducer);
        Future<RecordMetadata> recordMetadataFuture = kafkaProducer.send("partition", "{\"site\" : \"baeldung\"}");

        assertTrue(recordMetadataFuture.get().partition() == 1);
    }

    public static class KafkaProducer {

        private final Producer<String, String> producer;

        public KafkaProducer(Producer<String, String> producer) {
            this.producer = producer;
        }

        public Future<RecordMetadata> send(String key, String value) {
            ProducerRecord record = new ProducerRecord("topic_sports_news", key, value);
            return producer.send(record);
        }
    }

    public static class EvenOddPartitioner extends DefaultPartitioner {

        @Override
        public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
            if (((String)key).length() % 2 == 0) {
                return 0;
            }
            return 1;
        }
    }
}
