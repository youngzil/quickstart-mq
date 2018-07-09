package org.quickstart.msgframe.v1.producer;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.activemq.transport.RequestTimedOutIOException;

import com.ai.aif.msgframe.ClientFactory;
import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class ProducerTest {

    private final static ExecutorService pool = Executors.newFixedThreadPool(20);

    @org.junit.Test
    public void test() {

        try {
            for (int i = 0; i < 1; i++) {
                Thread.sleep(100);
                pool.submit(new ProducerThreadTest(i));
                while (true) {

                }
            }

        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                MsgFTextMessage message_order = null;
                for (int i = 0; i < 10; i++) {

                    MfProducerTxClient client1 = ClientFactory.createTxClient();
                    // MfProducerTxClient client2 = ClientFactory.createTxClient();
                    // Thread.currentThread().sleep(6000);
                    message_order = new MsgFTextMessage();
                    message_order.setText(
                            "sdasdasdsadsadsasdfdsafasdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdasdasdasdsadsadsasdfdsafasdadsfadsfadsfgwertjtykyulktykjkhgjdadsfadsfadsfgwertjtykyulktykjkhgj");
                    // message_order.getHeaderMap().put("region", "BUSI_LOG_573");
                    client1.send("AmsPaperLessPrint", message_order);
                    // client2.send("Test_R", message_order);
                    // client.sendOrderMsg("abc_1", message_order,i+"");
                    System.out.println("发送成功");
                    // ClientFactory.getCurrenTxClient().commit();
                    for (MfProducerTxClient client : ClientFactory.getCurrenTxClient()) {
                        client.commit();
                        // ClientFactory.getCurrenTxClient().remove(client);
                    }
                    // for(Iterator it = ClientFactory.getCurrenTxClient().iterator(); it.hasNext();){
                    // MfProducerTxClient client = (MfProducerTxClient) it.next();
                    // client.commit();
                    // it.remove();
                    // }
                    System.out.println("提交成功");
                }
            } catch (Exception e) {

                if (e.getCause() != null && e.getCause() instanceof RequestTimedOutIOException) {
                    System.out.println("超时异常");
                } else {
                    System.out.println("不是超时异常");
                }
                e.printStackTrace();
            }

        }
    }

}
