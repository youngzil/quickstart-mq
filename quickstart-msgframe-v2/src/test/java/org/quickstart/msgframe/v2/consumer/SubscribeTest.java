package org.quickstart.msgframe.v2.consumer;

import org.junit.Test;

import com.ai.aif.msgframe.consumer.MfConsumerClient;

public class SubscribeTest {

    @Test
    public void testClass() {
        ConsumerProcessorImpl consumerProcessor = new ConsumerProcessorImpl();
        // 订阅全部消息
        MfConsumerClient.subscribe("AmsPaperLessPrint", "*", consumerProcessor);

    }

    @Test
    public void testSubscribe() {
        try {

            // MfConsumerClient.subscribe("topicTest", "*", "com.ai.aif.msgframe.SubscribeImpl1");
            // MfConsumerClient mc = new MfConsumerClient();
            // MfConsumerClient.subscribe("topicTest", "*", "com.ai.aif.msgframe.SubscribeImpl1");
            // MfConsumerClient.subscribe("R_OSS", "471", "com.ai.aif.msgframe.SubscribeImpl1");

            MfConsumerClient mc = new MfConsumerClient();
            // mc.subscribe("yang", "471", "org.quickstart.msgframe.v2.consumer.ConsumerProcessorImpl");
            // mc.subscribe("yang", "473||477", "org.quickstart.msgframe.v2.consumer.ConsumerProcessorImpl");
            mc.subscribe("topicTest", "*", "org.quickstart.msgframe.v2.consumer.ConsumerProcessorImpl");
            // mc.subscribe("ESb_INVOKE_TEST", "*", "org.quickstart.msgframe.v2.consumer.ConsumerProcessorImpl");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("sss");
        while (true) {

        }
    }
}
