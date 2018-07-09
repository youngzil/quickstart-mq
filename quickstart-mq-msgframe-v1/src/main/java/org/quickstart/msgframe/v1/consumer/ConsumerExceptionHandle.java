package org.quickstart.msgframe.v1.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.common.ex.exception.IExceptionPersitence;
import com.ai.aif.msgframe.common.message.MsgFMessage;

public class ConsumerExceptionHandle implements IExceptionPersitence {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerExceptionHandle.class);

    public void processException(MsgFMessage message, String centerName, String topic, Exception e) throws Exception {
        logger.error("ConsumerExceptionHandle处理消费异常消息:message={},centerName={},topic={},", message, centerName, topic, e);
    }

}
