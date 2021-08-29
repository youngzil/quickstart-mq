package org.quickstart.mq.pulsar;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class PulsarProducer {

    private PulsarClient client;

    @Before
    public void setup() throws PulsarClientException {
        client = PulsarClient.builder()//
            .serviceUrl("pulsar://localhost:6650")//
            .build();
    }

    @Test
    public void testSync() throws PulsarClientException, InterruptedException {

        Producer<byte[]> producer = client.newProducer()//
            .topic("my-topic")//
            .create();

        int i = 0;
        // while (true) {
        i++;
        // 然后你就可以发送消息到指定的broker 和topic上：
        MessageId messageId = producer.send((" My message " + i).getBytes()); // 同步发送
        System.out.println("messageId=" + messageId);
        TimeUnit.SECONDS.sleep(2);
        // }

        // 配置消息同步发送
        producer.newMessage()//
            .key("my-message-key")//
            .value("my-async-message".getBytes())//
            .property("my-key", "my-value")//
            .property("my-other-key", "my-other-value")//
            .send();

        // 同步关闭
        producer.close();
        client.close();
    }

    @Test
    public void testAsync() throws PulsarClientException {

        Producer<byte[]> producer = client.newProducer()//
            .topic("my-topic")//
            .create();

        // 异步发送
        producer.sendAsync("my-async-message".getBytes())//
            .thenAccept(msgId -> {
                System.out.println("Message with ID " + msgId + " successfully sent");
            });

        // 异步关闭
        producer.closeAsync()//
            .thenRun(() -> System.out.println("Producer closed"))//
            .exceptionally((ex) -> {
                System.err.println("Failed to close producer: " + ex);
                return null;
            });

    }

    @Test
    public void testStringProducer() throws PulsarClientException {

        Producer<String> stringProducer = client.newProducer(Schema.STRING)//
            .topic("my-topic")//
            .create();
        stringProducer.send("My message");

        stringProducer.closeAsync()//
            .thenRun(() -> System.out.println("Producer closed"))//
            .exceptionally((ex) -> {
                System.err.println("Failed to close producer: " + ex);
                return null;
            });
    }

    @Test
    public void testBatch() throws PulsarClientException {
        Producer<byte[]> producer = client.newProducer()//
            .topic("my-topic")//
            .batchingMaxPublishDelay(10, TimeUnit.MILLISECONDS)//
            .sendTimeout(10, TimeUnit.SECONDS)//
            .blockIfQueueFull(true)//
            .create();

        producer.send("My message".getBytes(StandardCharsets.UTF_8));

    }
}
