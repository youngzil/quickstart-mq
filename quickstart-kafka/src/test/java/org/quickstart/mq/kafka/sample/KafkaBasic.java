package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class KafkaBasic {

    public static Producer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        //配置partitionner选择策略，可选配置
        //        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "cn.ljh.kafka.kafka_helloworld.SimplePartitioner");

        //        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "org.quickstart.mq.kafka.v2.sample.ProducerInterceptor");

        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
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
            ProducerRecord<String, String> data = new ProducerRecord<String, String>("topic03", ip, msg);
            producer.send(data, (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                }
                if (null != metadata) {
                    //输出成功发送消息的元信息
                    System.out.println(
                        "The offset of the record we just sent is:partition= " + metadata.partition() + ", offset="
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

        long events = 10;
        Random rnd = new Random();
        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            ProducerRecord<String, String> data = new ProducerRecord<>("topic03", ip, msg);
            RecordMetadata record = producer.send(data).get();
            System.out.println("Producer msg: partition=" + record.partition() + ", offset=" + record.offset());
            TimeUnit.SECONDS.sleep(5);
        }

        producer.close();

    }

    @Test
    public void consumer() {

        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList("topic03"));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    // 处理消息的逻辑
                    System.out
                        .printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                }
            }
        } catch (WakeupException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }

    }

    @Test
    public void topic() throws ExecutionException, InterruptedException {

        Properties props = new Properties();
        //配置kafka的服务连接
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        //KafkaUtils.getTopicNames(zkAddress)

        //创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        //创建topic
        adminClient.createTopics(Arrays.asList(new NewTopic("topic01", 2, (short)1),//(名称，分区数，副本因子)
            new NewTopic("topic02", 2, (short)1), new NewTopic("topic03", 2, (short)1)));

        //查看topic列表
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> names = topicsResult.names().get();
        names.stream().forEach(name -> System.out.println(name));

        //查看topic详细信息：主题的属性、主题的partition分区信息
        // name、partitions（partition、leader、replicas、isr）、authorizedOperations、internal
        DescribeTopicsResult topic = adminClient.describeTopics(Arrays.asList("topic01"));
        Map<String, TopicDescription> map = topic.all().get();
        System.out.println("TopicDescription:" + map);

        //删除topic
        adminClient.deleteTopics(Arrays.asList("topic01", "topic02"));

        //查看topic列表
        topicsResult = adminClient.listTopics();
        names = topicsResult.names().get();
        names.stream().forEach(name -> System.out.println(name));

        //        adminClient.listOffsets()
        //        adminClient.listPartitionReassignments()

        //关闭AdminClient
        adminClient.close();

    }

    @Test
    public void producerMonitor() {

        //生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        Properties props = new Properties();
        //配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        //KafkaUtils.getTopicNames(zkAddress)

        //创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

    }

    @Test
    public void consumerMonitor() {

        // 消费者的信息、主题等、消费速率、拉取消息配置策略等
        // 具体某个消费者的操作：暂停消费、开始消费、消费重置、重置到最新、回溯消费
        // 消费的offset信息

        //生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        Properties props = new Properties();
        //配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        //KafkaUtils.getTopicNames(zkAddress)

        //创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        ListConsumerGroupsResult consumerGroupsResult = adminClient.listConsumerGroups();

        // dminClient.listConsumerGroupOffsets();
        //        adminClient.alterConsumerGroupOffsets();
        //        adminClient.deleteConsumerGroupOffsets();

        //        adminClient.listConsumerGroups();
        //        adminClient.deleteConsumerGroups();
        //        adminClient.describeConsumerGroups();

        //        adminClient.removeMembersFromConsumerGroup();

    }

    @Test
    public void message() {

        // 消息的查询等

        //生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        Properties props = new Properties();
        //配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        //KafkaUtils.getTopicNames(zkAddress)

        //创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

    }

    @Test
    public void broker() {

        // broker的信息等

        // 消息的查询等

        //生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        Properties props = new Properties();
        //配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        //KafkaUtils.getTopicNames(zkAddress)

        //创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

        DescribeClusterResult clusterResult = adminClient.describeCluster();

        //        adminClient.describeConfigs()

    }

    @Test
    public void acl() {

        // ACL控制和Token控制等

        Properties props = new Properties();
        //配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        //KafkaUtils.getTopicNames(zkAddress)

        //创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);

    }

}
