package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SaslOauthbearerTest {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

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

        // oauth2认证

        // OAuth Settings
        //	- sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");

        //	- security.protocol=SASL_PLAINTEXT
        props.put("security.protocol", "SASL_PLAINTEXT");

        //	- sasl.mechanism=OAUTHBEARER
        props.put("sasl.mechanism", "OAUTHBEARER");

        //	- sasl.login.callback.handler.class=com.bfm.kafka.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
        props.put("sasl.login.callback.handler.class", "com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler");

        System.setProperty("OAUTH_LOGIN_SERVER", "dev-276677.okta.com");
        System.setProperty("OAUTH_LOGIN_ENDPOINT", "/oauth2/default/v1/token");
        System.setProperty("OAUTH_LOGIN_GRANT_TYPE", "client_credentials");
        System.setProperty("OAUTH_LOGIN_SCOPE", "kafka");
        System.setProperty("OAUTH_AUTHORIZATION", "Basic MG9hOGtoYnI0eHIyVUJ2U1IzNTc6VWk1aUo0TTFMcjR1cmdELXFmTzRxdHlnMDF0REFhaWlqSUpMZS1Wbg==");
        System.setProperty("OAUTH_INTROSPECT_SERVER", "dev-276677.okta.com");
        System.setProperty("OAUTH_INTROSPECT_ENDPOINT", "/oauth2/default/v1/introspect");
        System.setProperty("OAUTH_INTROSPECT_AUTHORIZATION",
            "Basic MG9hOGtoYnI0eHIyVUJ2U1IzNTc6VWk1aUo0TTFMcjR1cmdELXFmTzRxdHlnMDF0REFhaWlqSUpMZS1Wbg==");

        // 配置partitionner选择策略，可选配置
        //        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,
        // "cn.ljh.kafka.kafka_helloworld.SimplePartitioner");

        //        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
        // "org.quickstart.mq.kafka.v2.sample.ProducerInterceptor");

        Producer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
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

}
