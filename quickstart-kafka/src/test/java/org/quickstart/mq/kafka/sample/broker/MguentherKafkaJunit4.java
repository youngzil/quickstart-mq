package org.quickstart.mq.kafka.sample.broker;

import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import org.junit.Test;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;
import static net.mguenther.kafka.junit.ObserveKeyValues.on;
import static net.mguenther.kafka.junit.SendValues.to;

public class MguentherKafkaJunit4 {

    private EmbeddedKafkaCluster kafka;

    @Test
    public void shouldWaitForRecordsToBePublished() throws Exception {
        kafka = provisionWith(defaultClusterConfig());
        kafka.start();

        kafka.send(to("test-topic", "a", "b", "c"));
        kafka.observe(on("test-topic", 3));

        kafka.stop();
    }

}
