package org.quickstart.mq.kafka.sample;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;

public class KafkaConsumerTest {

    private static Properties kafkaProps = new Properties();

    static {
        kafkaProps.put("bootstrap.servers", "localhost:9092");
        kafkaProps.put("group.id", "test");
        kafkaProps
            .put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps
            .put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    private static Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private static KafkaConsumer<String, String> consumer;

    /**
     * 指定偏移量提交
     */
    public static void commitSelfAppointWithRebalanceListener() {
        int count = 0;

        //关闭自动提交偏移量，改用手动提交，与下方consumer.commitSync();一起使用
        kafkaProps.put("enable.auto.commit", false);
        consumer = new KafkaConsumer<String, String>(kafkaProps);
        //订阅主题,可传入一个主题列表，也可以是正则表达式，如果有人创建了与正则表达式匹配的新主题，会立即触发一次再均衡，消费者就可以读取新添加的主题。
        //如：test.*，订阅test相关的所有主题
        consumer.subscribe(Collections.singleton("test_partition"), new HandleRebalance());
        System.out.println("==== subscribe success ====");
        try {
            while (true) {
                //消费者持续对kafka进行轮训，否则会被认为已经死亡，它的分区会被移交给群组里的其他消费者。
                //传给poll方法的是一个超时时间，用于控制poll()方法的阻塞时间（在消费者的缓冲区里没有可用数据时会发生阻塞）
                //如果该参数被设为0，poll会立即返回，否则它会在指定的毫秒数内一直等待broker返回数据
                //poll方法返回一个记录列表。每条记录包含了记录所属主题的信息、记录所在分区的信息、记录在分区里的偏移量，以及记录的键值对。
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                System.out.println("==== data get ====");
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(
                        String.format("topic=%s, partition=%s, offset=%d, key=%s, value=%s",
                            record.topic(), record.partition(), record.offset(), record.key(),
                            record.value()));
                    currentOffsets.put(new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset() + 1, "no metadata"));
                    if (count % 2 == 0) {
                        //每2次提交一次，还可以根据时间间隔来提交
                        consumer.commitAsync(currentOffsets, new OffsetCommitCallback() {
                            @Override
                            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets,
                                Exception exception) {
                                if (null != exception) {
                                    System.out.println(String
                                        .format("==== Commit failed for offsets %s, error:%s ====",
                                            offsets, ExceptionUtils.getStackTrace(exception)));
                                }
                            }
                        });
                    }
                    count++;
                }
                //异步提交（结合下方同步提交）
                consumer.commitAsync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //退出应用前使用close方法关闭消费者。
            //网络连接和socket也会随之关闭，并立即触发一次再均衡，而不是等待群组协调器发现它不在发送心跳并认定它已死亡，因为那样需要更长的时间，导致政哥群组在一段时间内无法读取消息。
            consumer.close();
        }
    }


    /**
     * 再均衡监听器
     */
    private static class HandleRebalance implements ConsumerRebalanceListener {

        /**
         * 方法会在再均衡开始之前和消费者停止读取消息之后被调用。 如果在这里提交偏移量，下一个接管分区的消费者就知道该从哪里读取了。
         *
         * @param partitions
         */
        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            System.out.println(
                "Lost partitions in rebalance.Committing current offsets:" + currentOffsets);
            consumer.commitSync(currentOffsets);
        }

        /**
         * 方法会在重新分配分区之后和消费者开始读取消息之前被调用。
         *
         * @param partitions
         */
        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

        }
    }

}
