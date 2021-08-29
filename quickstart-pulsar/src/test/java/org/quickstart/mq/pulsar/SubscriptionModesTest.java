package org.quickstart.mq.pulsar;

import org.apache.pulsar.client.api.BatcherBuilder;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class SubscriptionModesTest {

    private PulsarClient client;

    @Before
    public void setup() throws PulsarClientException {
        client = PulsarClient.builder()//
            .serviceUrl("pulsar://localhost:6650")//
            .build();
    }

    @Test
    public void testProducerMessage() throws PulsarClientException, InterruptedException {

        // 如果在生产者端启用批处理，则默认情况下将具有不同键的消息添加到批处理中。代理会将批处理分派给消费者，因此默认批处理机制可能会破坏 Key_Shared 订阅保证的消息分发语义。生产者需要使用KeyBasedBatcher.
        Producer<String> producer2 = client.newProducer(Schema.STRING)//
            .topic("my-topic")//
            .batcherBuilder(BatcherBuilder.KEY_BASED)//
            .create();

        // 或者生产者可以禁用批处理。
        Producer<String> producer = client.newProducer(Schema.STRING)//
            .topic("my-topic")//
            .enableBatching(false)//
            .create();

        // 禁用批量发送主要是为了测试Key_Shared消费模式

        int i = 0;
        while (true) {
            i++;
            // 3 messages with "key-1", 3 messages with "key-2", 2 messages with "key-3" and 2 messages with "key-4"
            producer.newMessage().key("key-1" + i).value("message-1-1" + i).send();
            producer.newMessage().key("key-1" + i).value("message-1-2" + i).send();
            producer.newMessage().key("key-1" + i).value("message-1-3" + i).send();
            producer.newMessage().key("key-2" + i).value("message-2-1" + i).send();
            producer.newMessage().key("key-2" + i).value("message-2-2" + i).send();
            producer.newMessage().key("key-2" + i).value("message-2-3" + i).send();
            producer.newMessage().key("key-3" + i).value("message-3-1" + i).send();
            producer.newMessage().key("key-3" + i).value("message-3-2" + i).send();
            producer.newMessage().key("key-4" + i).value("message-4-1" + i).send();
            producer.newMessage().key("key-4" + i).value("message-4-2" + i).send();

            TimeUnit.SECONDS.sleep(3);
        }
        
    }

    @Test
    public void testExclusive1() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscriptionType(SubscriptionType.Exclusive)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testExclusive1 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    // 第二次启动直接报错，启动不起来
    //org.apache.pulsar.client.api.PulsarClientException$ConsumerBusyException: Exclusive consumer is already connected
    @Test
    public void testExclusive2() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscriptionType(SubscriptionType.Exclusive)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testExclusive2 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Test
    public void testFailover1() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscriptionType(SubscriptionType.Failover)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testFailover1 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Test
    public void testFailover2() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription")//
            .subscriptionType(SubscriptionType.Failover)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testFailover2 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Test
    public void testShared1() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription-share")//
            .subscriptionType(SubscriptionType.Shared)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testShared1 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Test
    public void testShared2() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription-share")//
            .subscriptionType(SubscriptionType.Shared)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testShared2 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Test
    public void testKeyShared1() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription-keyshare")//
            .subscriptionType(SubscriptionType.Key_Shared)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testKeyShared1 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

    @Test
    public void testKeyShared2() throws PulsarClientException {
        Consumer consumer = client.newConsumer()//
            .topic("my-topic")//
            .subscriptionName("my-subscription-keyshare")//
            .subscriptionType(SubscriptionType.Key_Shared)//
            .subscribe();

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("testKeyShared2 Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
        }
    }

}
