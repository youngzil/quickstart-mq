package org.quickstart.mq.pulsar;

import org.apache.pulsar.client.api.BatchReceivePolicy;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.Messages;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.RegexSubscriptionMode;
import org.apache.pulsar.client.api.SubscriptionType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PulsarConsumer {

    private PulsarClient client;

    @Before
    public void setup() throws PulsarClientException {
        client = PulsarClient.builder()//
            .serviceUrl("pulsar://localhost:6650")//
            .build();
    }

    @Test
    public void testSync() throws PulsarClientException {

        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscribe();

        // 配置消费者
        Consumer consumer2 = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .ackTimeout(10, TimeUnit.SECONDS)//
            .subscriptionType(SubscriptionType.Exclusive)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }

    }

    @Test
    public void testNonBlocking() throws PulsarClientException {

        MessageListener myMessageListener = (consumer, msg) -> {
            try {
                System.out.println("Message received: " + new String(msg.getData()));
                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        };

        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .messageListener(myMessageListener)//
            .subscribe();

        System.out.println("consumer=" + consumer);

    }

    @Test
    public void testAsync() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscribe();
        CompletableFuture<Message> asyncMessage = consumer.receiveAsync();//

        asyncMessage.thenApply(msg -> {
            System.out.println("Message received: " + new String(msg.getData()));
            return msg;
        });
    }

    @Test
    public void testBatch() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscribe();

        // 如果满足以下任一条件，则批量接收操作完成：足够数量的消息、消息字节数、等待超时。
        Consumer consumer2 = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .batchReceivePolicy(BatchReceivePolicy.builder()//
                .maxNumMessages(100)//
                .maxNumBytes(1024 * 1024)//
                .timeout(200, TimeUnit.MILLISECONDS)//
                .build())//
            .subscribe();

        Messages messages = consumer.batchReceive();
        for (Object message : messages) {
            // do something
        }
        consumer.acknowledge(messages);

    }

    @Test
    public void testPattern() throws PulsarClientException {
        ConsumerBuilder consumerBuilder = client.newConsumer()//
            .subscriptionName("my-subscription");

        // Subscribe to all topics in a namespace
        Pattern allTopicsInNamespace = Pattern.compile("public/default/.*");
        Consumer allTopicsConsumer = consumerBuilder//
            .topicsPattern(allTopicsInNamespace)//
            .subscribe();

        // Subscribe to a subsets of topics in a namespace, based on regex
        Pattern someTopicsInNamespace = Pattern.compile("public/default/foo.*");
        Consumer someTopicsConsumer = consumerBuilder//
            .topicsPattern(someTopicsInNamespace)//
            .subscribe();

        Pattern pattern = Pattern.compile("public/default/.*");
        client.newConsumer()//
            .subscriptionName("my-sub")//
            .topicsPattern(pattern)//
            .subscriptionTopicsMode(RegexSubscriptionMode.AllTopics)//
            .subscribe();

        // 具体的多个topic
        List<String> topics = Arrays.asList(//
            "topic-1",//
            "topic-2",//
            "topic-3"//
        );

        Consumer multiTopicConsumer = consumerBuilder//
            .topics(topics)//
            .subscribe();

        // Alternatively:
        Consumer multiTopicConsumer2 = consumerBuilder//
            .topic(//
                "topic-1",//
                "topic-2",//
                "topic-3"//
            )//
            .subscribe();

        Pattern allTopicsInNamespace2 = Pattern.compile("persistent://public/default.*");
        consumerBuilder//
            .topics(topics)//
            .subscribeAsync()//
            .thenAccept(this::receiveMessageFromConsumer);

    }

    private void receiveMessageFromConsumer(Object consumer) {
        ((Consumer)consumer).receiveAsync().thenAccept(message -> {
            // Do something with the received message
            receiveMessageFromConsumer(consumer);
        });
    }

}
