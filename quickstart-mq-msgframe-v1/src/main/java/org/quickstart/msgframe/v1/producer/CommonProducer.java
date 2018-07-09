package org.quickstart.msgframe.v1.producer;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.exception.MsgFrameClientException;
import com.ai.aif.msgframe.common.message.MsgFMapMessage;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class CommonProducer {

    public static void main(String[] args) {

        try {
            MfProducerClient client = new MfProducerClient();

            for (int i = 0; i < 10; i++) {
                // while (true) {

                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("test_gg" + i);

                MsgFMapMessage messageMap = new MsgFMapMessage();
                messageMap.setMapmessage(new HashMap<String, String>());
                messageMap.getMapmessage().put("test_gg" + i, "haha" + i);

                client.send("topicTest", messageMap);
                // client.send("AmsPaperLessPrint", message);
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
