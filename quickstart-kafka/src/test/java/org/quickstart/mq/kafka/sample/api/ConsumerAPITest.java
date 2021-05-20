package org.quickstart.mq.kafka.sample.api;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.quickstart.mq.kafka.sample.SaveOffsetOnRebalance;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ConsumerAPITest {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    private static final long POLL_TIMEOUT = 100;

    Properties props = new Properties();

    @Before
    public void setup() {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");

        // 设置`enable.auto.commit`,偏移量由`auto.commit.interval.ms`控制自动提交的频率。
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        // interceptors.add(SimpleConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);
    }

    @Test
    public void testBasic() {

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "topic01";

        // 使用消费者对象订阅这些主题
        // consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));
        // consumer.subscribe(Arrays.asList(topic));
        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT));
                if (records.isEmpty()) {
                    System.out.println("no message");
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }

                records.forEach(record -> {
                    // 处理消息的逻辑
                    System.out
                        .printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(), record.offset(),
                            record.key(), record.value());
                });
                consumer.commitSync();

            }

        } catch (WakeupException | InterruptedException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }

    }

    @Test
    public void testConsumerPattern() {

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        // 使用消费者对象订阅这些主题
        String topic = "org.*.datatype";
        Pattern pattern = Pattern.compile(topic);
        consumer.subscribe(pattern);
        consumer.subscribe(Pattern.compile("topic.+"));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (records.isEmpty()) {
                    System.out.println("no message");
                } else {
                    records.forEach(record -> {
                        // 处理消息的逻辑
                        System.out.printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
                            record.offset(), record.key(), record.value());
                    });

                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }
    }

    @Test
    public void testCommitTopicPartition() {

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "topic03";

        // 使用消费者对象订阅这些主题
        // consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));
        consumer.subscribe(Arrays.asList(topic));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT));

                for (TopicPartition partition : records.partitions()) {
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                    for (ConsumerRecord<String, String> record : partitionRecords) {
                        System.out.println(record.offset() + ": " + record.value());
                    }
                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
                }

            }

        } catch (WakeupException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }

    }

    @Test
    public void testAssign() {

        // 每个消息都有自己的偏移，所以要管理自己的偏移，你只需要做到以下几点：
        // 配置 enable.auto.commit=false
        // 使用提供的每个 ConsumerRecord 来保存你的位置。.
        // 在重启时使用恢复消费者的位置用 seek(TopicPartition, long).

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "topic03";
        consumer.assign(Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1)));

        // consumer从指定的offset处理,其实是重置这个TopicPartition的offset
        // consumer.seek(partition, seekOffset);

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT));
                if (records.isEmpty()) {
                    System.out.println("no message");
                    continue;
                }

                records.forEach(record -> {
                    // 处理消息的逻辑
                    System.out
                        .printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(), record.offset(),
                            record.key(), record.value());
                });
                consumer.commitSync();

            }

        } catch (WakeupException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }

    }

    @Test
    public void testFlowControl() {

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "topic03";

        // 使用消费者对象订阅这些主题
        // consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));
        consumer.subscribe(Arrays.asList(topic));

        // kafka支持动态控制消费流量，分别在future的`
        // poll(long)`调用中执行中使用 `
        // pause(Collection)来暂停消费指定分配的分区
        // resume(Collection)重新开始消费指定暂停的分区。

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT));
                if (records.isEmpty()) {
                    System.out.println("no message");
                    continue;
                }

                consumer.pause(Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1)));
                consumer.resume(Arrays.asList(new TopicPartition(topic, 0), new TopicPartition(topic, 1)));

                records.forEach(record -> {
                    // 处理消息的逻辑
                    System.out
                        .printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(), record.offset(),
                            record.key(), record.value());
                });
                consumer.commitSync();

            }

        } catch (WakeupException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }

    }

}
