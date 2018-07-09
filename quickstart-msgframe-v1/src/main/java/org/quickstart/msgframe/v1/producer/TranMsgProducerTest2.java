package org.quickstart.msgframe.v1.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class TranMsgProducerTest2 {

    private final static ExecutorService pool = Executors.newFixedThreadPool(10);

    @org.junit.Test
    public void test() {

        try {
            for (int i = 0; i < 1; i++) {
                pool.submit(new ProducerThreadTest(i));
            }

            while (true) {

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
            // TODO Auto-generated method stub
            MsgFTextMessage message_order = new MsgFTextMessage();
            message_order.setText("订单模块发送内容ffff痴痴缠缠f：_" + index);
            MsgFTextMessage message_order1 = new MsgFTextMessage();
            message_order1.setText("订单模块发送内容对对对fff1：_" + index);

            List<MsgFMessage> list = new ArrayList<MsgFMessage>();
            list.add(message_order);
            list.add(message_order1);

            try {
                MfProducerTxClient client = new MfProducerTxClient();
                // 两个消息是同一个事务的话，relatedId要相同，csf通过relatedId查找事务
                message_order.getHeaderMap().put("relatedId", "order001");
                client.send("aaaa", message_order);
                message_order1.getHeaderMap().put("relatedId", "order001");
                client.send("aaaa", message_order1);
                System.out.println("开始提交事务！");
                Thread.sleep(10000);
                client.commit();
                System.out.println("事务提交完成！");

            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}
