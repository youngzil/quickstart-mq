package org.quickstart.mq.kafka.sample;

import com.codahale.metrics.Meter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.consumer.internals.Fetcher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Utils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
public class KafkaBasic {

    // private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.49.125:9092,172.16.49.131:9092,172.16.49.133:9092";
    // private static final String brokerList = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    // private static final String brokerList = "172.16.112.232:9095,172.16.112.232:9093,172.16.112.232:9094";
    // private static final String brokerList = "172.16.49.125:9092,172.16.49.131:9092,172.16.49.133:9092";
    private static final String brokerList = "172.30.130.82:9092";
    // private static final String brokerList = "172.16.49.125:9092,172.16.49.131:9092,172.16.49.133:9092";
    // private static final String brokerList = "localhost:9092,localhost:9093,localhost:9094";
    // private static final String brokerList = "kafka1:9092,kafka2:9093,kafka3:9094";
    // private static final String brokerList = "172.16.49.66:9092,172.16.49.68:9092,172.16.49.72:9092";


    private static final long POLL_TIMEOUT = 100;

    public static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);// Kafka服务端的主机名和端口号
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);// key序列化
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);// value序列化
        props.put(ProducerConfig.ACKS_CONFIG, "all");// 等待所有副本节点的应答
        props.put(ProducerConfig.RETRIES_CONFIG, 3);// 消息发送最大尝试次数
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 一批消息处理大小
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);// 生产者在发送批次之前等待更多消息加入批次的时间。
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 发送缓存区内存大小

        // 配置partition选择策略，可选配置
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, SimplePartitioner.class.getName());

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleProducerInterceptor.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    public static Consumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);// 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");// 制定consumer group
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); // 自动确认offset的时间间隔
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "300");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);// key的序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // value的序列化类
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase(Locale.ROOT));
        // props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT));

        // 配置partition分配策略，可选配置
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, Collections.singletonList(SimplePartitionAssignor.class));

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    public static Admin createAdminClient() {

        Properties props = new Properties();
        // 配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

        // KafkaUtils.getTopicNames(zkAddress)

        // 创建KafkaAdminClient
        // Admin adminClient2 = Admin.create(props);
        Admin adminClient = Admin.create(props);
        return adminClient;
    }

    @Test
    public void asyncProducer() throws InterruptedException {

        int i = Utils.toPositive(Utils.murmur2("yhy_wac_loan_goblin".getBytes(StandardCharsets.UTF_8))) % 128;
        System.out.println(i);

        Producer<String, String> producer = createProducer();

        // String topic = "lengfeng.direct.test";
        String topic = "lengfeng.stable.test";
        // long events = Long.MAX_VALUE;
        long events = 100000;
        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            // ProducerRecord<String, String> data = new ProducerRecord<>(topic, ip, msg);


            long fff = System.currentTimeMillis();
            String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fff);
            System.out.println(fff+","+result);

            ProducerRecord<String, String> data = new ProducerRecord<>( topic,  null, fff, ip, msg, null);
            producer.send(data, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
                if (null != metadata) {

                    String result2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(metadata.timestamp());
                    System.out.println(metadata.timestamp()+","+result2);
                    // 输出成功发送消息的元信息
                    System.out.println(
                        "The offset of the record we just sent is:partition= " + metadata.partition() + ", offset=" + metadata.offset());
                }
            });

            System.out.println("dff");
            // TimeUnit.MILLISECONDS.sleep(100);
        }

        producer.close();
    }



    @Test
    public void syncProducer() throws InterruptedException, ExecutionException {


        Admin  admin = createAdminClient();
        admin.deleteTopics(Arrays.asList("topic01"));
        //创建topic
        CreateTopicsResult topicsResult = admin.createTopics(Arrays.asList(new NewTopic("topic01", 2, (short)1)));//(名称，分区数，副本因子)
        topicsResult.all().get();

        Producer<String, String> producer = createProducer();

        String topic = "topic01";
        long events = 150;
        Random rnd = new Random();

        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>(topic, ip, msg);
            RecordMetadata record = producer.send(data).get();
            System.out.println("Producer msg: partition=" + record.partition() + ", offset=" + record.offset());
            TimeUnit.MICROSECONDS.sleep(100);
        }

        producer.close();
    }

    @Test
    public void consumer() {

        MetricsReporter metricsReporter = new MetricsReporter();
        metricsReporter.init();
        Meter mqMsgMeter = metricsReporter.getRegistry().meter("consumer_count");

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss:SSS");

        Consumer<String, String> consumer = createConsumer();

        // String topic = "topic03";
        String topic = "lengfeng.order.test";
        // String topic = "bkk.item.tradetgt.count";
        // String topic = "test.topic.7";
        // String topic2 = "test";
        String topic2 = "lengfeng.order.test";
        String topic3 = "lengfeng.order.test";

        // 使用消费者对象订阅这些主题
        // consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));
        consumer.subscribe(Arrays.asList(topic, topic2, topic3), new SaveOffsetOnRebalance(consumer));

        long startTime = System.currentTimeMillis();
        int count = 0;
        int allcount = 0;
        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));

                if (records.isEmpty()) {
                    // System.out.println(dateTimeFormatter.format(LocalDateTime.now()));
                    // System.out.println("no message");
                } else {

                    // mqMsgMeter.mark(records.count());
                    //
                    // count += records.count();
                    // allcount += records.count();

                    System.out.println("num=" + records.count());
                    records.forEach(record -> {
                        // 处理消息的逻辑
                        System.out.printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
                            record.offset(), record.key(), record.value());

                        String result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.timestamp());
                        System.out.println(result);

                    });

                    /*if (allcount < 300) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }*/

                    consumer.commitSync();
                }

               /* if (System.currentTimeMillis() - startTime > 30 * 1000) {
                    startTime = System.currentTimeMillis();
                    Map<MetricName, ? extends Metric> metrics = consumer.metrics();
                    metrics.values().stream()//
                        // .filter(metric -> "records-consumed-rate".equals(metric.metricName().name()) || "records-per-request-avg".equals(metric.metricName().name()))//
                        .forEach(metric -> {
                            log.error("name={},value={}", metric.metricName(), metric.metricValue());
                        });

                    log.error("count=" + count);
                    count = 0;
                    log.error("--------------------------------------------------------------------------------------");
                }*/

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
        Admin adminClient = createAdminClient();
    }

    @Test
    public void message() throws NoSuchFieldException, IllegalAccessException {

        // 消息的查询等

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        Admin adminClient = createAdminClient();

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
                System.out.printf("topic = %s,partition = %d, offset = %d, key = %s, value = %s%n", record.topic(), record.partition(),
                    record.offset(), record.key(), record.value());
            });

            consumer.commitSync();
        }

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
