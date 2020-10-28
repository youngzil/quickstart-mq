package org.quickstart.msgframe.v2.consumer;

import org.junit.Test;

import com.ai.aif.msgframe.consumer.MfConsumerClient;
import com.ai.aif.msgframe.consumer.MfServiceStartup;

public class ServiceStartupTest {

    public static void main(String[] args) {
        MfConsumerClient.main(null);
//        MfServiceStartup.main();
    }

    @Test
    public void testMainConsume() {
        MfServiceStartup.main();
        while (true) {

        }
    }

    @Test
    public void testMfConsumerClientMain() {
        MfConsumerClient.main();
        while (true) {

        }
    }

}
