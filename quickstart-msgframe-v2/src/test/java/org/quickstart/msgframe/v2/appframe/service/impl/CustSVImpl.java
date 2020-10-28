package org.quickstart.msgframe.v2.appframe.service.impl;

import org.quickstart.msgframe.v2.appframe.service.interfaces.ICustSV;

import com.ai.aif.msgframe.ClientFactory;
import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class CustSVImpl implements ICustSV {

    @Override
    public void doService() throws Exception {

        MfProducerTxClient client = ClientFactory.createTxClient();
        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("haha");
        client.send("topicTest", message);
        // client.sendOrderMsg("Test", message,"sadsa");
    }

}
