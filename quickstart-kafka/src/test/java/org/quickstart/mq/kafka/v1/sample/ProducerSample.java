package org.quickstart.mq.kafka.v1.sample;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerSample {

    public static void main(String[] args) {
        // long events = Long.parseLong(args[0]);
        long events = 5;
        String topic = "Kafka-Test-201702091542";// Storm-Test-201702081520
        Random rnd = new Random();

        Properties props = new Properties();
        props.put("zookeeper.connect", "192.168.88.101:2181,192.168.88.102:2181,192.168.88.103:2181");
        props.put("metadata.broker.list", "192.168.88.101:6667,192.168.88.102:6667,192.168.88.105:6667");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        // props.put("partitioner.class", "example.producer.SimplePartitioner");
        props.put("request.required.acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        ProducerConfig config = new ProducerConfig(props);

        Producer<String, String> producer = new Producer<String, String>(config);

        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, ip, msg);
            producer.send(data);
            System.out.println(topic + "," + ip + "," + msg);
        }
        producer.close();
    }
}
