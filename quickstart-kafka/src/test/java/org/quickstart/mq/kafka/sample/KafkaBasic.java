package org.quickstart.mq.kafka.sample;

import com.google.common.collect.Lists;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.consumer.internals.Fetcher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class KafkaBasic {

    // private static final String brokerList = "localhost:9092";
    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    private static final long POLL_TIMEOUT = 100;

    public static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        // 配置partitionner选择策略，可选配置
        //        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
        // "cn.ljh.kafka.kafka_helloworld.SimplePartitioner");

        //        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
        // "org.quickstart.mq.kafka.v2.sample.ProducerInterceptor");

        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    public static Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    public static KafkaAdminClient createAdminClient() {

        Properties props = new Properties();
        // 配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // KafkaUtils.getTopicNames(zkAddress)

        // 创建KafkaAdminClient
        // Admin adminClient2 = Admin.create(props);
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);
        return adminClient;
    }

    @Test
    public void asyncProducer() throws InterruptedException {

        Producer<String, String> producer = createProducer();

        String topic = "topic03";
        long events = 100;
        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>(topic, ip, msg);
            producer.send(data, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
                if (null != metadata) {
                    // 输出成功发送消息的元信息
                    System.out
                        .println("The offset of the record we just sent is:partition= " + metadata.partition() + ", offset=" + metadata.offset());
                }
            });

            TimeUnit.MILLISECONDS.sleep(10);
        }

        producer.close();
    }

    @Test
    public void syncProducer() throws InterruptedException, ExecutionException {

        Producer<String, String> producer = createProducer();

        String topic = "topic03";
        long events = 10000;
        Random rnd = new Random();

        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>(topic, ip, msg);
            RecordMetadata record = producer.send(data).get();
            System.out.println("Producer msg: partition=" + record.partition() + ", offset=" + record.offset());
            TimeUnit.MICROSECONDS.sleep(1);
        }

        producer.close();
    }

    @Test
    public void consumer() {

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic03";

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));

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
    public void consumerPattern() {

        Consumer<String, String> consumer = createConsumer();

        // 使用消费者对象订阅这些主题
        String topic = "org.*.datatype";
        Pattern pattern = Pattern.compile(topic);
        consumer.subscribe(pattern);
        // consumer.subscribe(Pattern.compile("topic-.*"));

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
    public void consumerMethod() {

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic03";

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));

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

                // [在consumer.poll之后assignment()返回为空的问题](https://www.cnblogs.com/huxi2b/p/10773559.html)
                consumer.poll(0);//不能使用consumer.poll(Duration.ofMillis(0));，否则可能assignment()返回为空

                consumer.assignment().forEach(topicPartition -> {
                    System.out.println(topicPartition);

                    OffsetAndMetadata offsetAndMetadata = consumer.committed(topicPartition);
                    System.out.printf("原始的offset= %s%n", offsetAndMetadata);

                    Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(Collections.singleton(topicPartition));
                    Map<TopicPartition, Long> endOffsets = consumer.endOffsets(Collections.singleton(topicPartition));
                    System.out.println("beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);

                    // consumer.seek(topicPartition, 60);

                    // consumer.seekToBeginning(Collections.singleton(topicPartition));
                    // offsetAndMetadata = consumer.committed(topicPartition);
                    // System.out.printf("seekToBeginning后的offset= %s%n", offsetAndMetadata);
                    //
                    // beginningOffsets = consumer.beginningOffsets(Collections.singleton(topicPartition));
                    // endOffsets = consumer.endOffsets(Collections.singleton(topicPartition));
                    // System.out.println("beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);
                    //
                    // consumer.seekToEnd(Collections.singleton(topicPartition));
                    // offsetAndMetadata = consumer.committed(topicPartition);
                    // System.out.printf("seekToEnd后的offset= %s%n", offsetAndMetadata);

                });
                // seek不是自己的TopicPartition会报错,seek后下次poll不一定生效（有时间延迟），没有commit，通过committed获取的数据就不会变
                //consumer.seek(new TopicPartition(topic, 0), 60);
                // consumer.seek(new TopicPartition(topic, 1), 60);

                TimeUnit.SECONDS.sleep(3);
            }
        } catch (WakeupException | InterruptedException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }
    }

    @Test
    public void consumerOtherOperation() {

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic));

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

        //        consumer.seek(new TopicPartition(topic, 1), 20);

        // records = consumer.poll(100);

        // 按照TopicPartition维度归类消息
        if (!records.isEmpty()) {
            Set<TopicPartition> partitions = records.partitions();
            for (TopicPartition topicPartition : partitions) {
                List<ConsumerRecord<String, String>> recordsInSameTP = records.records(topicPartition);
            }
        }

        // 一定要先poll一下，否则是取不到下面的数据的

        // 查看当前消费者订阅的TopicPartition
        consumer.assignment().forEach(topicPartition -> {
            System.out.println(topicPartition);

            OffsetAndMetadata offsetAndMetadata = consumer.committed(topicPartition);
            System.out.printf("原始的offset= %s%n", offsetAndMetadata);

            // seek不是自己的TopicPartition会报错,seek后下次poll不一定生效（有时间延迟），没有commit，通过committed获取的数据就不会变

            // consumer.seekToBeginning(Collections.singleton(topicPartition));
            // offsetAndMetadata = consumer.committed(topicPartition);
            // System.out.printf("seekToBeginning后的offset= %s%n", offsetAndMetadata);
            //
            // consumer.seekToEnd(Collections.singleton(topicPartition));
            // offsetAndMetadata = consumer.committed(topicPartition);
            // System.out.printf("seekToEnd后的offset= %s%n", offsetAndMetadata);
            //
            // consumer.seek(topicPartition, 17);
            // offsetAndMetadata = consumer.committed(topicPartition);
            // System.out.printf("seek offset 后的offset= %s%n", offsetAndMetadata);

        });

        consumer.close();
    }

    // 持续不断的消费数据
    public static void run() throws InterruptedException {
        final Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";

        consumer.subscribe(Collections.singletonList(topic));
        final int giveUp = 100;
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) {
                    break;
                } else {
                    continue;
                }
            }

            // int i = 0;
            consumerRecords.forEach(record -> {
                // i = i + 1;
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(), record.partition(), record.offset());
            });

            // System.out.println("Consumer Records " + i);
            consumer.commitAsync();
        }

        consumer.close();
        System.out.println("Kafka Consumer Exited");
    }

    @Test
    public void producerMonitor() {

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();
    }

    @Test
    public void message() throws NoSuchFieldException, IllegalAccessException {

        // 消息的查询等

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();

        // adminClient.deleteRecords();

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";
        long timestamp = System.currentTimeMillis();
        int batchSize = 10;

        TopicPartition partition = new TopicPartition(topic, 0);

        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        timestampsToSearch.put(partition, timestamp);
        Map<TopicPartition, OffsetAndTimestamp> otMap = consumer.offsetsForTimes(timestampsToSearch);
        long seekOffset = otMap.get(partition).offset();

        // 设置每次拉取的消息数量
        Fetcher<byte[], byte[]> innerFetcher = (Fetcher<byte[], byte[]>)getFieldValueByFieldName(consumer, "fetcher");
        Field maxPollRecordsField = getFieldByFieldName(innerFetcher, "maxPollRecords");
        maxPollRecordsField.setAccessible(true);
        maxPollRecordsField.set(innerFetcher, batchSize);

        //指定该consumer对应的消费分区
        consumer.assign(Lists.newArrayList(partition));
        //consumer的offset处理
        consumer.seek(partition, seekOffset);

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT));

        if (records.isEmpty()) {
            System.out.println("no message");
        } else {
            records.forEach(record -> {
                // 处理消息的逻辑
                System.out
                    .printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(), record.offset(),
                        record.key(), record.value());
            });

            consumer.commitSync();
        }

    }

    @Test
    public void broker() throws ExecutionException, InterruptedException {

        // broker的信息等

        // 消息的查询等

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();

        DescribeClusterResult clusterResult = adminClient.describeCluster();

        ConfigResource resource = new ConfigResource(Type.BROKER, "0");
        Map<ConfigResource, Config> configMap = adminClient.describeConfigs(Collections.singleton(resource)).all().get();
        System.out.println(configMap);

        Map<ConfigResource, Config> topicConfigMap =
            adminClient.describeConfigs(Collections.singleton(new ConfigResource(Type.TOPIC, "lengfeng.test3.test"))).all().get();
        System.out.println(topicConfigMap);

        Map<ConfigResource, Config> brokerLoggerConfigMap =
            adminClient.describeConfigs(Collections.singleton(new ConfigResource(Type.BROKER_LOGGER, "0"))).all().get();
        System.out.println(brokerLoggerConfigMap);

        System.out.println("sss");

        // adminClient.describeFeatures();
        // adminClient.describeLogDirs()
        // adminClient.describeReplicaLogDirs()

    }

    @Test
    public void acl() {

        // ACL控制和Token控制等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();
    }

    public static Field getFieldByFieldName(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        //设置对象的访问权限，保证对private的属性的访问
        return field;
    }

    public static Object getFieldValueByFieldName(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        //设置对象的访问权限，保证对private的属性的访问
        return field.get(object);
    }

}
