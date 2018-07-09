package org.quickstart.msgframe.v2.producer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class SendMessageTXTest {

    @org.junit.Test
    public void testSingleDestination() {
        try {
            long start = System.currentTimeMillis();
            MfProducerTxClient client = new MfProducerTxClient();
            for (int i = 0; i < 300; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.send("test1", message);
                client.commit();
            }

            long end = System.currentTimeMillis();
            System.out.println("cost time=" + (end - start));
            System.out.println("发送成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testMultipleDestination() {
        try {
            MfProducerTxClient client = new MfProducerTxClient();
            for (int i = 0; i < 100; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.send("test1", message);
                client.send("test2", message);
            }
            client.commit();
            System.out.println("发送成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final ExecutorService pool = Executors.newFixedThreadPool(5);

    @org.junit.Test
    public void testThreadPool() {

        try {
            for (int i = 0; i < 10; i++) {

                pool.submit(new ProducerThreadTest(i));

            }

            while (true) {

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static class ProducerThreadTest implements Runnable {
        private int index;

        public ProducerThreadTest(int index) {
            super();
            this.index = index;
        }

        @Override
        public void run() {

            try {
                MfProducerTxClient client = new MfProducerTxClient();
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + String.valueOf(index));
                int mod = index % 5;
                System.out.println(mod);
                mod++;
                client.send("test" + mod, message);

                client.commit();
                System.out.println("发送成功");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    @org.junit.Test
    public void testOrderMessage() {
        try {
            MfProducerTxClient client = new MfProducerTxClient();
            for (int i = 0; i < 10; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.sendOrderMsg("test1", message, "123123");
            }
            client.commit();

            for (int i = 0; i < 20; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.sendOrderMsg("test1", message, "123123");
            }
            client.commit();

            for (int i = 0; i < 30; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.sendOrderMsg("test2", message, "123123");
            }
            client.commit();

            for (int i = 0; i < 40; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.sendOrderMsg("test3", message, "123123");
            }
            client.commit();
            System.out.println("发送成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testRollBack() {
        try {
            MfProducerTxClient client = new MfProducerTxClient();
            for (int i = 0; i < 10; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.sendOrderMsg("test1", message, "123123");
            }
            client.rollback();

            for (int i = 0; i < 10; i++) {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("hello" + i);
                client.sendOrderMsg("test2", message, "123123");
            }
            client.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
