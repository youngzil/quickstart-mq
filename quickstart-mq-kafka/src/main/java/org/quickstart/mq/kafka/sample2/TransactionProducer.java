/**
 * 项目名称：quickstart-mq-kafka 
 * 文件名：TransactionProducer.java
 * 版本信息：
 * 日期：2018年6月9日
 * Copyright asiainfo Corporation 2018
 * 版权所有 *
 */
package org.quickstart.mq.kafka.sample2;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * TransactionProducer
 * 
 * 生产端一致性: 开启幂等和事务, 包含重试, 发送确认, 同一个连接的最大未确认请求数.
 * 
 * @author：yangzl@asiainfo.com
 * @2018年6月9日 下午4:26:33
 * @since 1.0
 */
public class TransactionProducer {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.31.186:9092");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transactional-id");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, "3");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");
        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());

        producer.initTransactions();

        try {
            producer.beginTransaction();
            for (int i = 0; i < 5; i++) {
                Future<RecordMetadata> send = producer.send(new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i)));
                System.out.println(send.get().offset());
                TimeUnit.MILLISECONDS.sleep(1000L);
            }
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            producer.abortTransaction();
        }
        producer.close();
    }

}
