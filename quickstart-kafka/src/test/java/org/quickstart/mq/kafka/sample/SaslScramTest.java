package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.security.scram.internals.ScramMechanism;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SaslScramTest {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    private static final long POLL_TIMEOUT = 100;

    public static Producer<String, String> createProducer(String username, String password) {
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

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, username, password);

        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name());
        props.put(SaslConfigs.SASL_MECHANISM, ScramMechanism.SCRAM_SHA_256.mechanismName());
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);

        // System.setProperty("java.security.auth.login.config",jaasCfg);

        // 配置partitionner选择策略，可选配置
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "cn.ljh.kafka.kafka_helloworld.SimplePartitioner");

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleProducerInterceptor.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    public static Consumer<String, String> createConsumer(String username, String password) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, username, password);

        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name());
        props.put(SaslConfigs.SASL_MECHANISM, ScramMechanism.SCRAM_SHA_256.mechanismName());
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    @Test
    public void asyncProducer() throws InterruptedException {

        // String username = "admin";
        // String password = "admin";
        String username = "writer";
        String password = "writer-pwd";

        Producer<String, String> producer = createProducer(username, password);

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
    public void consumer() {

        // String username = "admin";
        // String password = "admin";

        String username = "reader";
        String password = "reader-pwd";

        Consumer<String, String> consumer = createConsumer(username, password);

        String topic = "topic03";

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));

        // 不断的轮询获取主题中的消息
        try {
            // poll() 获取消息列表，可以传入超时时间
            while (true) {
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

        } catch (WakeupException e) {
            // 不用处理这个异常，它只是用来停止循环的
        } finally {
            consumer.close();
        }
    }

}
