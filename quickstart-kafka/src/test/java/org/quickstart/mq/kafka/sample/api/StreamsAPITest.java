package org.quickstart.mq.kafka.sample.api;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

public class StreamsAPITest {

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

}
