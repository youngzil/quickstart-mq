package org.quickstart.mq.kafka.sample.broker;

import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;
import static net.mguenther.kafka.junit.ObserveKeyValues.on;
import static net.mguenther.kafka.junit.SendValues.to;

public class MguentherKafkaJunit5 {

    private EmbeddedKafkaCluster kafka;

    @BeforeEach
    void setupKafka() {
        kafka = provisionWith(defaultClusterConfig());
        kafka.start();
    }

    @AfterEach
    void tearDownKafka() {
        kafka.stop();
    }

    @Test
    void shouldWaitForRecordsToBePublished() throws Exception {
        kafka.send(to("test-topic", "a", "b", "c"));
        kafka.observe(on("test-topic", 3));
    }

}
