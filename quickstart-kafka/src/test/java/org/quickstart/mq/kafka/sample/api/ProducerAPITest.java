package org.quickstart.mq.kafka.sample.api;

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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerAPITest {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    Properties props = new Properties();

    @Before
    public void setup() {
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // 配置partitionner选择策略，可选配置
        // props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, SimplePartitioner.class.getName());

        // 2 构建滤器链
        List<String> interceptors = new ArrayList<>();
        // interceptors.add(SimpleProducerInterceptor.class.getName());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

    }

    @Test
    public void testBasic() throws ExecutionException, InterruptedException {

        // InterruptException - 如果线程在阻塞中断。
        // SerializationException - 如果key或value不是给定有效配置的serializers。
        // TimeoutException - 如果获取元数据或消息分配内存话费的时间超过max.block.ms。
        // KafkaException - Kafka有关的错误（不属于公共API的异常）。

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> data = new ProducerRecord<>("topic01", Integer.toString(i), Integer.toString(i));
            RecordMetadata metadata = producer.send(data).get();
            System.out.println("metadata=" + metadata);

            // 由于send调用是异步的，它将为分配消息的此消息的RecordMetadata返回一个Future。
            // 如果future调用get()，则将阻塞，直到相关请求完成并返回该消息的metadata，或抛出发送异常。

        }

        producer.close();

    }

    @Test
    public void testIdempotence() {

        // 要启用幂等（idempotence），必须将enable.idempotence配置设置为true。 如果设置，则retries（重试）配置将默认为Integer.MAX_VALUE，acks配置将默认为all。
        // 生产者只能保证单个会话内发送的消息的幂等性。

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> data = new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i));
            producer.send(data);
        }

        producer.close();

    }

    @Test
    public void testTransactional() {

        // 要使用事务生产者和attendant API，必须设置transactional.id。

        // 如果设置了transactional.id，幂等性会和幂等所依赖的生产者配置一起自动启用。
        // 此外，应该对包含在事务中的topic进行耐久性配置。特别是，replication.factor应该至少是3，而且这些topic的min.insync.replicas应该设置为2。
        // 最后，为了实现从端到端的事务性保证，消费者也必须配置为只读取已提交的消息。

        // 所有新的事务性API都是阻塞的，并且会在失败时抛出异常。

        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my-transactional-id");
        Producer<String, String> producer = new KafkaProducer<>(props);
        producer.initTransactions();

        try {
            producer.beginTransaction();

            // 所有100条消息都是一个事务的一部分
            for (int i = 0; i < 100; i++) {
                ProducerRecord<String, String> data = new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i));
                producer.send(data);
            }

            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
        } catch (KafkaException e) {
            // 如果任何producer.send()或事务性调用在事务过程中遇到不可恢复的错误，就会抛出KafkaException。
            // For all other exceptions, just abort the transaction and try again.
            producer.abortTransaction();
        }
        producer.close();

    }

    @Test
    public void testSync() throws ExecutionException, InterruptedException {
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> data = new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i));
            producer.send(data).get(); //将阻塞，直到相关请求完成并返回该消息的metadata，或抛出发送异常。

            // 由于send调用是异步的，它将为分配消息的此消息的RecordMetadata返回一个Future。
        }

        producer.close();

    }

    @Test
    public void testAsync() {
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> data = new ProducerRecord<>("my-topic", Integer.toString(i), Integer.toString(i));

            // 完全无阻塞的话,可以利用回调参数提供的请求完成时将调用的回调通知。
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

        }

        // 发送到同一个分区的消息回调保证按一定的顺序执行，也就是说，在下面的例子中 callback1 保证执行 callback2 之前：
        // producer.send(new ProducerRecord<byte[],byte[]>(topic, partition, key1, value1), callback1);
        // producer.send(new ProducerRecord<byte[],byte[]>(topic, partition, key2, value2), callback2);

        // 注意：callback一般在生产者的I/O线程中执行，所以是相当的快的，否则将延迟其他的线程的消息发送。
        // 如果你需要执行阻塞或计算昂贵（消耗）的回调，建议在callback主体中使用自己的Executor来并行处理。

        producer.close();

    }

}
