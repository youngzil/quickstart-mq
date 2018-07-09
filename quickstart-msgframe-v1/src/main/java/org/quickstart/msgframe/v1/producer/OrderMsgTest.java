package org.quickstart.msgframe.v1.producer;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.exception.MsgFrameClientException;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class OrderMsgTest {

    public static void main(String[] args) {

        try {
            MfProducerClient client = new MfProducerClient();
            for (int i = 0; i < 10; i++) {
                MsgFTextMessage message_order = new MsgFTextMessage();
                // message_order.getHeaderMap().put("region", "sa_center_req_01");
                message_order.setText("ordefdgdfdsdddrmsg" + i);
                // 顺序消息
                client.send("AmsPaperLessPrint", message_order);
                System.out.println("发送成功");
                // Thread.currentThread().sleep(3000);
            }
        } catch (MsgFrameClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
