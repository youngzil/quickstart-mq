package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.security.scram.internals.ScramMechanism;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaAdminClientManager {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    private static final String username = "admin";
    private static final String password = "admin";

    private volatile static Map<String/*brokerList*/, KafkaAdminClient> KAFKA_ADMIN_CLIENT_CONTAINER = new ConcurrentHashMap<>();

    public static KafkaAdminClient getKafkaAdminClient() {
        return getKafkaAdminClient(brokerList);
    }

    public static KafkaAdminClient getKafkaAdminClient(String brokerList) {
        KafkaAdminClient kafkaAdminClient = KAFKA_ADMIN_CLIENT_CONTAINER.computeIfAbsent(brokerList, key -> createAdminClient(key));
        return kafkaAdminClient;
    }

    public static KafkaAdminClient createAdminClient(String brokerList) {

        Properties props = new Properties();
        // 配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);

        // 创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);
        return adminClient;
    }

    public static KafkaAdminClient createAdminClientWithScram() {
        return createAdminClientWithScram(brokerList, username, password);
    }

    public static KafkaAdminClient createAdminClientWithScram(String brokerList, String username, String password) {

        Properties props = new Properties();
        // 配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);

        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, username, password);

        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.name());
        props.put(SaslConfigs.SASL_MECHANISM, ScramMechanism.SCRAM_SHA_256.mechanismName());
        props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasCfg);

        // 创建KafkaAdminClient
        KafkaAdminClient adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);
        return adminClient;
    }

    public static Consumer createConsumer() {
        return createConsumer(brokerList);
    }

    public static Consumer createConsumer(String brokerList) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        Consumer consumer = new KafkaConsumer<>(props);
        return consumer;
    }

}
