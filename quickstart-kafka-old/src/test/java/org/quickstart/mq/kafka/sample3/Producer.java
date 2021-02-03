/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quickstart.mq.kafka.sample3;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Producer {
    private final KafkaProducer<String, String> producer;
    private final String topic;

    public Producer(String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.17.11.120:9092,172.17.11.117:9092,172.17.11.118:9092");
        props.put("client.id", "DemoProducer");
        props.put("batch.size", 16384);// 16M
        props.put("linger.ms", 1000);
        props.put("buffer.memory", 33554432);// 32M
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // props.put("partitioner.class", "com.horizon.kafka.v010.SimplePartitioner");

        producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    public void producerMsg() throws InterruptedException {
        Random rnd = new Random();
        int events = 10;
        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            int num = rnd.nextInt(255);
            String ip = "192.168.2." + num;
            int partition = 1;
            if (num % 2 == 0) {
                partition = 2;
            }
            String msg = runtime + ",www.example.com," + ip;
            try {
                producer.send(new ProducerRecord<>(topic, partition, ip, msg));
                System.out.println("Sent message: (" + ip + ", " + msg + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Thread.sleep(10000);
    }

    public static void main(String[] args) throws InterruptedException {
        Producer producer = new Producer(Constants.TOPIC);
        producer.producerMsg();
    }
}
