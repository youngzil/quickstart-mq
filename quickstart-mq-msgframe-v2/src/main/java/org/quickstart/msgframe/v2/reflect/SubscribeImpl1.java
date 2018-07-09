package org.quickstart.msgframe.v2.reflect;

import java.io.Serializable;

import com.ai.aif.msgframe.common.exception.ConsumerException;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.consumer.facade.IConsumerProcessor;

public class SubscribeImpl1 implements IConsumerProcessor {
    private static String desc;

    static {
        desc = "haha";
    }

    @Override
    public void process(MsgFMessage msg) throws ConsumerException {
        System.out.println(msg.getMsgId());
        System.out.println(desc);
//        return msg.getMsgId();
    }

    /* (non-Javadoc)
     * @see com.ai.aif.msgframe.consumer.facade.IConsumerProcessor#processNeedReturn(com.ai.aif.msgframe.common.message.MsgFMessage)
     */
    @Override
    public Serializable processNeedReturn(MsgFMessage arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
