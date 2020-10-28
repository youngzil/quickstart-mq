package org.quickstart.msgframe.v2.producer;

import org.junit.Test;

import com.ai.aif.msgframe.ClientFactory;
import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class TranMsgProducerTest {

    public static void main(String[] args) {
        int index = 0;
        while (true) {
            try {
                index++;

                MsgFTextMessage message_order = new MsgFTextMessage();
                message_order.setText("Transaction message-" + index);

                MfProducerTxClient client = ClientFactory.createTxClient();
                client.send("AmsPaperLessPrint", message_order);
                System.out.println("开始提交事务！");
                Thread.sleep(3000);
                client.commit();
                System.out.println("事务提交完成！");

                // 两个消息是同一个事务的话，relatedId要相同，csf通过relatedId查找事务
                // message_order.getHeaderMap().put("relatedId", "order001");
                // client.send("aaaa", message_order);
                // message_order1.getHeaderMap().put("relatedId", "order001");
                // client.send("aaaa", message_order1);
                // System.out.println("开始提交事务！");
                // Thread.sleep(10000);
                // client.commit();
                // System.out.println("事务提交完成！");

            } catch (Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Test
    public static void test(String[] args) throws Exception {
        // 发送事务消息
        MsgFTextMessage message_order1 = new MsgFTextMessage();
        message_order1.setText("事务消息测试1");
        message_order1.getHeaderMap().put("region", "filterTag1");
        MsgFTextMessage message_order2 = new MsgFTextMessage();
        message_order2.setText("事务消息测试2");
        message_order2.getHeaderMap().put("region", "filterTag2");
        MsgFTextMessage message_order3 = new MsgFTextMessage();
        message_order3.setText("事务消息测试3");
        message_order3.getHeaderMap().put("region", "filterTag3");
        MfProducerTxClient client = ClientFactory.createTxClient();
        client.send("PerfTest9", message_order1);
        client.send("PerfTest9", message_order2);
        client.send("PerfTest9", message_order3);
        client.commit();
        // client.rollback();

    }
}
