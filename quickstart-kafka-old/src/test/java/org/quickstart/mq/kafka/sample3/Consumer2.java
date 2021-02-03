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

import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Consumer2 extends Thread {
    private final KafkaConsumer<Integer, String> consumer;
    private final String topic;

    public Consumer2(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.11.120:9092,172.17.11.117:9092,172.17.11.118:9092");
        props.put("zookeeper.connect", "172.17.11.120:2181,172.17.11.117:2181,172.17.11.118:2181");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, Constants.GROUP);
        // props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");// latest,earliest
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
        this.topic = topic;
    }

    /*
     * public void consumerMsg(){ try {
     * consumer.subscribe(Collections.singletonList(this.topic)); while(true){
     * ConsumerRecords<Integer, String> records1 = consumer.poll(1000); for
     * (ConsumerRecord<Integer, String> record : records1) {
     * System.out.println(this.toString()+"Received message: (" + record.key() +
     * ", " + record.value() + ") at partition "+record.partition()+" offset " +
     * record.offset()); } }
     * 
     * } catch (Exception e) { e.printStackTrace(); } }
     */
    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            consumer.subscribe(Collections.singletonList(this.topic));
            while (true) {
                ConsumerRecords<Integer, String> records = consumer.poll(1000);
                for (ConsumerRecord<Integer, String> record : records) {
                    // 通过打印当前对象地址，来确定两个分区是否被两个不同的消费者消费
                    System.out.println(this.toString() + "Received message: (" + record.key() + ", " + record.value() + ") at partition " + record.partition() + " offset " + record.offset());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Consumer2 Consumer1 = new Consumer2(Constants.TOPIC);
        Consumer2 Consumer2 = new Consumer2(Constants.TOPIC);
        Consumer1.start();
        Consumer2.start();
        // Consumer1.consumerMsg();
        // Consumer2.consumerMsg();

    }
}
