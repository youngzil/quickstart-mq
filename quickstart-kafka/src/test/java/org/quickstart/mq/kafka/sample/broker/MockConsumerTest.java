package org.quickstart.mq.kafka.sample.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

public class MockConsumerTest {

    MockConsumer consumer;
    List updates;

    CountryPopulationConsumer countryPopulationConsumer;
    Throwable pollException;

    String TOPIC = "topic";
    int PARTITION = 0;

    // Creating a MockConsumer Instance
    @BeforeEach
    void setUp() {
        consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        updates = new ArrayList<>();

        countryPopulationConsumer = new CountryPopulationConsumer(consumer, ex -> this.pollException = ex, updates::add);
    }

    // Assigning Topic Partitions
    @Test
    void whenStartingByAssigningTopicPartition_thenExpectUpdatesAreConsumedCorrectly() {
        // GIVEN
        consumer.schedulePollTask(() -> consumer.addRecord(record(TOPIC, PARTITION, "Romania", 19_410_000)));
        consumer.schedulePollTask(() -> countryPopulationConsumer.stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);

        // WHEN
        countryPopulationConsumer.startByAssigning(TOPIC, PARTITION);

        // THEN
        // assertThat(updates).hasSize(1);
        // assertThat(consumer.closed()).isTrue();
    }

    // Subscribing to Topics
    @Test
    void whenStartingBySubscribingToTopic_thenExpectUpdatesAreConsumedCorrectly() {
        // GIVEN
        consumer.schedulePollTask(() -> {
            consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, 0)));
            consumer.addRecord(record("Romania", 1000, TOPIC, 0));
        });
        consumer.schedulePollTask(() -> countryPopulationConsumer.stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, 0);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);

        // WHEN
        countryPopulationConsumer.startBySubscribing(TOPIC);

        // THEN
        // assertThat(updates).hasSize(1);
        // assertThat(consumer.closed()).isTrue();
    }


    // Controlling the Polling Loop
    @Test
    void whenStartingBySubscribingToTopicAndExceptionOccurs_thenExpectExceptionIsHandledCorrectly() {
        // GIVEN
        consumer.schedulePollTask(() -> consumer.setPollException(new KafkaException("poll exception")));
        consumer.schedulePollTask(() -> countryPopulationConsumer.stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, 0);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);

        // WHEN
        countryPopulationConsumer.startBySubscribing(TOPIC);

        // THEN
        // assertThat(pollException).isInstanceOf(KafkaException.class).hasMessage("poll exception");
        // assertThat(consumer.closed()).isTrue();
    }

    private ConsumerRecord record(String romania, int i, String topic, int i1) {
        return new ConsumerRecord(topic, i1, i, "key", romania);
    }

    @AllArgsConstructor
    public static class CountryPopulationConsumer {

        private Consumer<String, Integer> consumer;
        private java.util.function.Consumer<Throwable> exceptionConsumer;
        private java.util.function.Consumer<CountryPopulation> countryPopulationConsumer;

        // standard constructor

        void startBySubscribing(String topic) {
            consume(() -> consumer.subscribe(Collections.singleton(topic)));
        }

        void startByAssigning(String topic, int partition) {
            consume(() -> consumer.assign(Collections.singleton(new TopicPartition(topic, partition))));
        }

        private void consume(Runnable beforePollingTask) {
            try {
                beforePollingTask.run();
                while (true) {
                    ConsumerRecords<String, Integer> records = consumer.poll(Duration.ofMillis(1000));
                    StreamSupport.stream(records.spliterator(), false).map(record -> new CountryPopulation(record.key(), record.value()))
                        .forEach(countryPopulationConsumer);
                    consumer.commitSync();
                }
            } catch (WakeupException e) {
                System.out.println("Shutting down...");
            } catch (RuntimeException ex) {
                exceptionConsumer.accept(ex);
            } finally {
                consumer.close();
            }
        }

        public void stop() {
            consumer.wakeup();
        }
    }

    @Data
    @AllArgsConstructor
    public static class CountryPopulation {

        private String country;
        private Integer population;

        // standard constructor, getters and setters
    }
}
