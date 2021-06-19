package org.quickstart.mq.kafka.exporter;

import io.prometheus.client.Gauge;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

class KafkaExporter {
    private final Gauge gaugeOffsetLag;
    private final Gauge gaugeCurrentOffset;

    private AdminClient adminClient;
    private final KafkaConsumer<String, String> consumer;
    private final Pattern groupBlacklistPattern;

    private final String kafkaHostname;
    private final int kafkaPort;

    public KafkaExporter(String kafkaHostname, int kafkaPort, String groupBlacklistRegexp) {
        this.kafkaHostname = kafkaHostname;
        this.kafkaPort = kafkaPort;
        this.adminClient = createAdminClient(kafkaHostname, kafkaPort);
        this.consumer = createNewConsumer(kafkaHostname, kafkaPort);
        this.groupBlacklistPattern = Pattern.compile(groupBlacklistRegexp);
        this.gaugeOffsetLag = Gauge.build().name("kafka_broker_consumer_group_offset_lag").help("Offset lag of a topic/partition")
            .labelNames("group_id", "partition", "topic").register();

        this.gaugeCurrentOffset =
            Gauge.build().name("kafka_broker_consumer_group_current_offset").help("Current consumed offset of a topic/partition")
                .labelNames("group_id", "partition", "topic").register();
    }

    private AdminClient createAdminClient(String kafkaHostname, int kafkaPort) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHostname + ":" + kafkaPort);

        return AdminClient.create(props);
    }

    synchronized void updateMetrics() {

        try {

            List<String> groups = adminClient.listConsumerGroups().all().get().stream()//
                .map(ConsumerGroupListing::groupId)//
                .filter(g -> !groupBlacklistPattern.matcher(g).matches())//
                .collect(toList());

            groups.forEach(groupId -> {
                try {
                    adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get()//
                        .forEach((topicPartition, offsetAndMetadata) -> {
                            Long currentOffset = offsetAndMetadata.offset();
                            Long lag = getLogEndOffset(topicPartition) - currentOffset;
                            String partition = topicPartition.partition() + "";
                            String topic = topicPartition.topic();

                            gaugeOffsetLag.labels(groupId, partition, topic).set(lag);
                            gaugeCurrentOffset.labels(groupId, partition, topic).set(currentOffset);
                        });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            this.adminClient = createAdminClient(this.kafkaHostname, this.kafkaPort);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long getLogEndOffset(TopicPartition topicPartition) {
        consumer.assign(Arrays.asList(topicPartition));
        consumer.seekToEnd(Arrays.asList(topicPartition));
        return consumer.position(topicPartition);
    }

    private KafkaConsumer<String, String> createNewConsumer(String kafkaHost, int kafkaPort) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost + ":" + kafkaPort);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        return new KafkaConsumer<>(properties);
    }

}
