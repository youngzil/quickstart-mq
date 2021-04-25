package org.quickstart.mq.kafka.sample.api;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StreamsAPITest {

    // 同一个集群 topicA ---> topicB

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    private static final long POLL_TIMEOUT = 100;

    Properties props = new Properties();

    @Before
    public void setup() {
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }

    @Test
    public void testBasic() {

        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream("TextLinesTopic");

        // Consumed.with(stringSerde, stringSerde);

        KTable<String, Long> wordCounts = textLines//
            // Split each text line, by whitespace, into words.
            .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))//
            // Group the text words as message keys
            .groupBy((key, word) -> word)//
            // Count the occurrences of each word (message key).
            .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));

        // Store the running counts as a changelog stream to the output topic.
        wordCounts.toStream().to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));

       /* KTable wordCounts = textLines
            // Split each text line, by whitespace, into words.
            .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("W+")))

            // Ensure the words are available as record keys for the next aggregate operation.
            .map((key, value) -> new KeyValue<>(value, value))

            // Count the occurrences of each word (record key) and store the results into a table named "Counts".
            .countByKey("Counts");*/

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // builder.stream("my-input-topic").mapValues(value -> value.toString().length() + "").to("my-output-topic");
        // KafkaStreams streams2 = new KafkaStreams(builder.build(), props);
        // streams2.start();

    }

    @Test
    public void testPipe() {

        // bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic streams-plaintext-input
        // bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-pipe-output --from-beginning

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream("streams-plaintext-input").to("streams-pipe-output");

        final Topology topology = builder.build();

        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }

    @Test
    public void testLineSplit() {

        // bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic streams-plaintext-input
        // bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-linesplit-output --from-beginning

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        // builder.addGlobalStore()
        // builder.addStateStore()
        // builder.globalTable()
        // builder.table()
        // builder.stream()

        KStream<String, String> source = builder.stream("streams-plaintext-input");
        source.flatMapValues(value -> Arrays.asList(value.split("\\W+"))).to("streams-linesplit-output");

        final Topology topology = builder.build();

        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }

    @Test
    public void testWordCount() {

        // bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic streams-plaintext-input

        // 这种方式消费不到
        // bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-wordcount-output --from-beginning

        // 必须用这种方式消费
        /*bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
        --topic streams-wordcount-output \
        --from-beginning \
        --formatter kafka.tools.DefaultMessageFormatter \
        --property print.key=true \
        --property print.value=true \
        --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
        --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer*/

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);//这个属性一定要有，否则不展示

        // props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> source = builder.stream("streams-plaintext-input");

       /*
       // JDK7
       KTable<String, Long> counts =
            source.flatMapValues(new ValueMapper<String, Iterable<String>>() {
                @Override
                public Iterable<String> apply(String value) {
                    return Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+"));
                }
            })
                .groupBy(new KeyValueMapper<String, String, String>() {
                    @Override
                    public String apply(String key, String value) {
                        return value;
                    }
                })
                // Materialize the result into a KeyValueStore named "counts-store".
                // The Materialized store is always of type <Bytes, byte[]> as this is the format of the inner most store.
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>> as("counts-store"));*/

        // JDK8
        KTable<String, Long> counts = source.flatMapValues(line -> Arrays.asList(line.toLowerCase(Locale.getDefault()).split("\\W+")))//
            .groupBy((key, value) -> value)//
            // Materialize the result into a KeyValueStore named "counts-store".
            // The Materialized store is always of type <Bytes, byte[]> as this is the format of the inner most store.
            .count(Materialized.as("counts-store"));

        // need to override value serde to Long type
        counts.toStream().to("streams-wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        final Topology topology = builder.build();
        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }

}
