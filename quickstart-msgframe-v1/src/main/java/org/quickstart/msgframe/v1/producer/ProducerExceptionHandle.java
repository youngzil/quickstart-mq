package org.quickstart.msgframe.v1.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.common.ex.exception.IExceptionPersitence;
import com.ai.aif.msgframe.common.message.MsgFMessage;

public class ProducerExceptionHandle implements IExceptionPersitence {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerExceptionHandle.class);

    public void processException(MsgFMessage message, String centerName, String topic, Exception exception) throws Exception {
        LOG.error("processException处理发送异常消息:" + message.getMsgId());
        
        
        String ss = message.getHeaderMap().get("");

    }
}
