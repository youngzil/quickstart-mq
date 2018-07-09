/**
 * 项目名称：quickstart-kafka 
 * 文件名：KafkaProducerExample.java
 * 版本信息：
 * 日期：2017年9月25日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.sample2;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * KafkaProducerExample
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月25日 下午9:45:49
 * @since 1.0
 */
public class KafkaProducerExample {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        /*Properties props = new Properties();
        props.put("bootstrap.servers", "10.11.20.103:9093,10.11.20.103:9094");
        props.put("metadata.broker.list", "10.1.31.40:9092");
        props.put("client.id","Producer_1");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("compression.type", "gzip");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");*/

        Properties props = new Properties();
        // props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.11.20.103:9093,10.11.20.103:9094");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.1.243.23:59092,10.1.243.23:59093,10.1.243.23:59094");
        // props.put(ProducerConfig.METADATA_MAX_AGE_CONFIG"metadata.broker.list", "10.1.31.40:9092");// 该地址是集群的子集，用来探测集群。
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "Producer_1");
        props.put(ProducerConfig.ACKS_CONFIG, "all");// 记录完整提交，最慢的但是最大可能的持久化
        props.put(ProducerConfig.RETRIES_CONFIG, 0);// 请求失败重试的次数
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);// batch的大小
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);// 默认情况即使缓冲区有剩余的空间，也会立即发送请求，设置一段时间用来等待从而将缓冲区填的更多，单位为毫秒，producer发送数据会延迟1ms，可以减少发送到kafka服务器的请求数据
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);// 提供给生产者缓冲内存总量
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");// 序列化的方式，
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 构建拦截链
        List<String> interceptors = new ArrayList<>();
        interceptors.add("org.quickstart.kafka.sample2.interceptor.producer.TimeStampPrependerInterceptor"); // interceptor 1
        interceptors.add("org.quickstart.kafka.sample2.interceptor.producer.CounterInterceptor"); // interceptor 2
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        // 设置属性 自定义分区类
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "org.quickstart.kafka.sample2.partition.MyPartition");

        String topic = "testTopic";
        // String topic = "test-topic";
        Producer<String, String> producer = new KafkaProducer<>(props);

        for (int i = 0; i < 5; i++) {
            // 三个参数分别为topic, key,value，send()是异步的，添加到缓冲区立即返回，更高效。
            Future<RecordMetadata> result = producer.send(new ProducerRecord<>(topic, Integer.toString(i), "message" + i));

            /*在producer发送消息时指定partition，ProducerRecord的构造方法可以有四个参数，分别是topic，int类型的partition值，key，value，我们直接指定传入的第二个参数即可 
            当我们不指定第二个参数，使用三个参数的构造方式时，会根据传入的key自动分区，传入key为空时消息不分区，会传到同一个partition中*/
            // producer.send(new ProducerRecord<>(topic,partition,ip, msg));

            RecordMetadata record = result.get();
            System.out.printf("发送结果：partition = %d , offset = %d, timestamp = %s\n", record.partition(), record.offset(), record.timestamp());
        }

        while (true) {

        }

        // 一定要关闭producer，这样才会调用interceptor的close方法
        // producer.close();

    }

}
