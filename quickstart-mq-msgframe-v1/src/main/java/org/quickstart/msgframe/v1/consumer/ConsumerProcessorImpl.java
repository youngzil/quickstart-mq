package org.quickstart.msgframe.v1.consumer;

import java.io.Serializable;

import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.consumer.facade.IConsumerProcessor;

public class ConsumerProcessorImpl implements IConsumerProcessor {

    @Override
    public void process(MsgFMessage msg) throws Exception {
        System.out.println("receive message：" + msg);

    }

    @Override
    public Serializable processNeedReturn(MsgFMessage paramMsgFMessage) {
        // TODO Auto-generated method stub
        return null;
    }

}
