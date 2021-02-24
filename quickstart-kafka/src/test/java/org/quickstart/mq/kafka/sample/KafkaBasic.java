package org.quickstart.mq.kafka.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import kafka.admin.TopicCommand;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

public class KafkaBasic {

    // private static final String brokerList = "localhost:9092";
    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");
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
        KafkaAdminClient adminClient = (KafkaAdminClient) KafkaAdminClient.create(props);
        return adminClient;
    }

    @Test
    public void asyncProducer() throws InterruptedException {

        Producer<String, String> producer = createProducer();

        long events = 10;
        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>("topic03", ip, msg);
            producer.send(data, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
                if (null != metadata) {
                    // 输出成功发送消息的元信息
                    System.out.println(
                        "The offset of the record we just sent is:partition= " + metadata
                            .partition() + ", offset="
                            + metadata.offset());
                }
            });

            TimeUnit.SECONDS.sleep(5);
        }

        producer.close();
    }

    @Test
    public void syncProducer() throws InterruptedException, ExecutionException {

        Producer<String, String> producer = createProducer();

        String topic = "topic01";
        long events = 10;
        Random rnd = new Random();

        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>(topic, ip, msg);
            RecordMetadata record = producer.send(data).get();
            System.out.println(
                "Producer msg: partition=" + record.partition() + ", offset=" + record.offset());
            TimeUnit.MICROSECONDS.sleep(1);
        }

        producer.close();
    }

    @Test
    public void consumer() {

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                if (records.isEmpty()) {
                    System.out.println("no message");
                } else {
                    records.forEach(record -> {
                        // 处理消息的逻辑
                        System.out
                            .printf(
                                "topic = %s,partition = %d, offset = %d, key = %s, value = %s%n",
                                record.topic(),
                                record.partition(), record.offset(), record.key(), record.value());
                    });

                    consumer.commitSync();
                }

                consumer.assignment().forEach(topicPartition -> {
                    System.out.println(topicPartition);

                    OffsetAndMetadata offsetAndMetadata = consumer.committed(topicPartition);
                    System.out.printf("原始的offset= %s%n", offsetAndMetadata);

                    // consumer.seek(topicPartition, 60);

                    /*consumer.seekToBeginning(Collections.singleton(topicPartition));
                    offsetAndMetadata = consumer.committed(topicPartition);
                    System.out.printf("seekToBeginning后的offset= %s%n", offsetAndMetadata);*/

                  /*consumer.seekToEnd(Collections.singleton(topicPartition));
                  offsetAndMetadata = consumer.committed(topicPartition);
                  System.out.printf("seekToEnd后的offset= %s%n", offsetAndMetadata);*/

                });
                // seek不是自己的TopicPartition会报错,seek后下次poll不一定生效（有时间延迟），没有commit，通过committed获取的数据就不会变
                // consumer.seek(new TopicPartition(topic, 0), 60);
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
    public void consumer2() {

        Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);

                if (records.isEmpty()) {
                    System.out.println("no message");
                } else {
                    records.forEach(record -> {
                        // 处理消息的逻辑
                        System.out
                            .printf(
                                "topic = %s,partition = %d, offset = %d, key = %s, value = %s%n",
                                record.topic(),
                                record.partition(), record.offset(), record.key(), record.value());
                    });

                    consumer.commitSync();
                }

                consumer.assignment().forEach(topicPartition -> {
                    System.out.println(topicPartition);

                    OffsetAndMetadata offsetAndMetadata = consumer.committed(topicPartition);
                    System.out.printf("原始的offset= %s%n", offsetAndMetadata);

                    // consumer.seek(topicPartition, 60);

                    consumer.seekToBeginning(Collections.singleton(topicPartition));
                    offsetAndMetadata = consumer.committed(topicPartition);
                    System.out.printf("seekToBeginning后的offset= %s%n", offsetAndMetadata);

                  /*consumer.seekToEnd(Collections.singleton(topicPartition));
                  offsetAndMetadata = consumer.committed(topicPartition);
                  System.out.printf("seekToEnd后的offset= %s%n", offsetAndMetadata);*/

                });
                // seek不是自己的TopicPartition会报错,seek后下次poll不一定生效（有时间延迟），没有commit，通过committed获取的数据就不会变
                //consumer.seek(new TopicPartition(topic, 0), 60);
                //                consumer.seek(new TopicPartition(topic, 1), 60);

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
        ConsumerRecords<String, String> records = consumer.poll(100);

        //        consumer.seek(new TopicPartition(topic, 1), 20);

        // records = consumer.poll(100);

        // 按照TopicPartition维度归类消息
        if (!records.isEmpty()) {
            Set<TopicPartition> partitions = records.partitions();
            for (TopicPartition topicPartition : partitions) {
                List<ConsumerRecord<String, String>> recordsInSameTP = records
                    .records(topicPartition);
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
            final ConsumerRecords<String, String> consumerRecords = consumer.poll(1000);

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
                System.out
                    .printf("Consumer Record:(%d, %s, %d, %d)\n", record.key(), record.value(),
                        record.partition(),
                        record.offset());
            });

            // System.out.println("Consumer Records " + i);
            consumer.commitAsync();
        }

        consumer.close();
        System.out.println("Kafka Consumer Exited");
    }

    @Test
    public void topic() throws ExecutionException, InterruptedException {

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();

        // 创建topic
        adminClient
            .createTopics(Arrays.asList(new NewTopic("topic01", 2, (short) 1), // (名称，分区数，副本因子)
                new NewTopic("topic02", 2, (short) 2), new NewTopic("topic03", 6, (short) 3)));

        TopicCommand.TopicCommandOptions topicCommandOptions = new TopicCommand.TopicCommandOptions(
            new String[]{"--alter", "--topic", "topic01", "--partitions", "" + 6});

        TopicCommand.AdminClientTopicService adminClientTopicService =
            new TopicCommand.AdminClientTopicService(adminClient);

        // adminClientTopicService.alterTopic(topicCommandOptions);

        // 查看topic列表
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> names = topicsResult.names().get();
        names.stream().forEach(name -> System.out.println(name));

        // 查看topic详细信息：主题的属性、主题的partition分区信息
        // name、partitions（partition、leader、replicas、isr）、authorizedOperations、internal
        DescribeTopicsResult topic = adminClient
            .describeTopics(Arrays.asList("topic01", "topic02", "topic03"));
        Map<String, TopicDescription> map = topic.all().get();
        System.out.println("TopicDescription:" + map);

        // 删除topic
        // adminClient.deleteTopics(Arrays.asList("topic01", "topic02", "topic03"));

        // 查看topic列表
        topicsResult = adminClient.listTopics();
        names = topicsResult.names().get();
        names.stream().forEach(System.out::println);

        // adminClient.listOffsets()
        // adminClient.listPartitionReassignments()

        // 关闭AdminClient
        adminClient.close();
    }

    // 获取某个Topic的所有分区以及分区最新的Offset
    @Test
    public void getPartitionsForTopic() {

        // 脚本方式获取
        // ./bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic test

        final Consumer<String, String> consumer = createConsumer();

        String topic = "topic01";
        Collection<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        System.out.println("Get the partition info as below:");
        List<TopicPartition> tp = new ArrayList<TopicPartition>();
        partitionInfos.forEach(str -> {
            System.out.println("Partition Info:");
            System.out.println(str);

            tp.add(new TopicPartition(topic, str.partition()));
            consumer.assign(tp);
            consumer.seekToEnd(tp);

            System.out.println("Partition " + str.partition() + " 's latest offset is '" + consumer
                .position(new TopicPartition(topic, str.partition())));
        });
    }

    @Test
    public void producerMonitor() {

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();
    }

    @Test
    public void consumerMonitor() throws IOException {

        // 消费者的信息、主题等、消费速率、拉取消息配置策略等
        // 具体某个消费者的操作：暂停消费、开始消费、消费重置、重置到最新、回溯消费
        // 消费的offset信息

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();

        ListConsumerGroupsResult consumerGroupsResult = adminClient.listConsumerGroups();

        consumerGroupsResult.all().thenApply(consumerGroupList -> {
            consumerGroupList.stream().forEach(group -> {
                System.out.println(group.groupId());

                ListConsumerGroupOffsetsResult consumerGroupOffsetsResult =
                    adminClient.listConsumerGroupOffsets(group.groupId());

                consumerGroupOffsetsResult.partitionsToOffsetAndMetadata()
                    .whenComplete((topicPartitionOffsetMap, throwable) -> {
                        if (null != throwable) {
                            System.out.println("Exception" + throwable.getMessage());
                            return;
                        }

                        topicPartitionOffsetMap.forEach((topicPartition, offsetMetadata) -> {
                            System.out.printf("group=%s, topic = %s, partition = %d, offset = %d%n",
                                group.groupId(),
                                topicPartition.topic(), topicPartition.partition(),
                                offsetMetadata.offset());
                        });
                    });
            });
            return null;
        });

        //        adminClient.alterConsumerGroupOffsets();
        //        adminClient.deleteConsumerGroupOffsets();

        //        adminClient.listConsumerGroups();
        //        adminClient.deleteConsumerGroups();
        //        adminClient.describeConsumerGroups();

        //        adminClient.removeMembersFromConsumerGroup();

        System.in.read();
    }

    @Test
    public void message() {

        // 消息的查询等

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();
    }

    @Test
    public void broker() {

        // broker的信息等

        // 消息的查询等

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();

        DescribeClusterResult clusterResult = adminClient.describeCluster();

        //        adminClient.describeConfigs()

    }

    @Test
    public void acl() {

        // ACL控制和Token控制等

        // 获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient();
    }
}
