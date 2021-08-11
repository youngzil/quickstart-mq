package org.quickstart.mq.kafka.sample;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
public class LowLevelConsumer {

    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    private static final long POLL_TIMEOUT = 100;

    @Test
    public void testAssign() {

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic03";
        consumer.assign(Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1)));

        // consumer从指定的offset处理,其实是重置这个TopicPartition的offset
        //consumer.seek(partition, seekOffset);

        int count = 0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            // records.forEach(record -> System.out
            //     .printf("topic: %s,partition: %f,offset: %f,key: %s,value: %s", record.topic(), record.partition(), record.offset(), record.key(),
            //         record.value()));

            if (records.isEmpty()) {
                System.out.println("ddddd，count=" + count++);
                if (count > 100 && count < 399) {
                    System.out.println("assign23 ");
                    consumer.assign(Arrays.asList(new TopicPartition(topic, 2), new TopicPartition(topic, 3)));
                } else if (count > 400) {
                    System.out.println("assign01");
                    consumer.assign(Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1)));
                }
            }

            records.forEach(System.out::println);
        }

    }

    @Test
    public void testConsumer() throws IOException {

        //声明kafka分区数相等的消费线程数，一个分区对应一个消费线程
        int consumeThreadNum = 1;
        //特殊指定每个分区开始消费的offset
        // List<Long> partitionOffsets = Lists.newArrayList(1111L, 1112L, 1113L, 1114L, 1115L, 1116L, 1117L, 1118L, 1119L);

        List<Long> partitionOffsets = null;

        ExecutorService executorService = Executors.newFixedThreadPool(consumeThreadNum);

        //循环遍历创建消费线程
        IntStream.range(0, consumeThreadNum)
            .forEach(partitionIndex -> executorService.submit(() -> startConsume(partitionIndex, partitionOffsets, consumeThreadNum)));

        System.in.read();
    }

    private void startConsume(int partitionIndex, List<Long> partitionOffsets, int consumeThreadNum) {
        //创建kafka consumer
        Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";

        try {
            //指定该consumer对应的消费分区
            TopicPartition partition = new TopicPartition(topic, partitionIndex);
            consumer.assign(Lists.newArrayList(partition));

            //consumer的offset处理
            if (CollectionUtils.isNotEmpty(partitionOffsets) && partitionOffsets.size() == consumeThreadNum) {
                Long seekOffset = partitionOffsets.get(partitionIndex);
                log.info("partition:{} , offset seek from {}", partition, seekOffset);
                consumer.seek(partition, seekOffset);
            }

            //开始消费数据任务
            kafkaRecordConsume(consumer, partition);
        } catch (Exception e) {
            log.error("kafka consume error:{}", e);
        } finally {
            try {
                //consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private void kafkaRecordConsume(Consumer<String, String> consumer, TopicPartition partition) {
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(POLL_TIMEOUT);

                //🌿很重要：日志记录当前consumer的offset，partition相关信息(之后如需重新指定offset消费就从这里的日志中获取offset，partition信息)
                if (records.count() > 0) {
                    //具体的处理流程
                    records.forEach((k) -> handleKafkaInput(k));

                    String currentOffset = String.valueOf(consumer.position(partition));
                    log.info("current records size is：{}, partition is: {}, offset is:{}", records.count(), consumer.assignment(), currentOffset);
                }

                //offset提交
                consumer.commitAsync();
            } catch (Exception e) {
                log.error("handlerKafkaInput error{}", e);
            }
        }
    }

    private void handleKafkaInput(ConsumerRecord<String, String> record) {
        System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
    }

    public static Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer3");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase(Locale.ROOT));

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

}
