package org.quickstart.msgframe.v1.producer;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.exception.MsgFrameClientException;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class TagProducer {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        while (true) {
            try {
                MfProducerClient client = new MfProducerClient();
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("test");
                message.setTag("tag2");
                try {
                    client.send("tagTopic", message);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (MsgFrameClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
