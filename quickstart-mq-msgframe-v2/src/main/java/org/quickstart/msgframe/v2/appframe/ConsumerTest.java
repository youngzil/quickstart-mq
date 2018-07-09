package org.quickstart.msgframe.v2.appframe;

import com.ai.aif.msgframe.consumer.MfConsumerClient;

public class ConsumerTest {
    public static void main(String[] args) {
        test(args);
    }

    public static void test(String[] args) {
        MfConsumerClient.subscribe("request_577", "open_quene_request_577_2", "com.ai.aif.msgframe.SubscribeImpl1");

    }

}
