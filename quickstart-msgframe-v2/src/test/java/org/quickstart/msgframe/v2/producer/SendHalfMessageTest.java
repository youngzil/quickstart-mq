package org.quickstart.msgframe.v2.producer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ai.aif.msgframe.MfHalfmessageClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class SendHalfMessageTest {

    private final static ExecutorService pool = Executors.newFixedThreadPool(5);

    @org.junit.Test
    public void test() {

        try {
            for (int i = 0; i < 1; i++) {

                pool.submit(new ProducerThreadTest(i));

            }

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        while (true) {

        }

    }

    static class ProducerThreadTest extends Thread implements Runnable {
        private int index;

        public ProducerThreadTest(int index) {
            super();
            this.index = index;
        }

        @Override
        public void run() {

            try {
                MsgFTextMessage crm_product = new MsgFTextMessage();
                crm_product.setText("产品发送内容yyyyyyyyyy：_" + 1);
                MfHalfmessageClient halfClient = new MfHalfmessageClient();
                halfClient.send("PerfTest", crm_product);
                halfClient.send("PerfTest", crm_product);
                Thread.currentThread().sleep(10000);
                halfClient.commit();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }

}
