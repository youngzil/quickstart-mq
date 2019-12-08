package org.quickstart.msgframe.v1.producer;

import com.ai.aif.msgframe.common.exception.MsgFrameClientException;
import com.ai.aif.msgframe.common.interfaces.IProsInjectionProcessor;
import com.ai.aif.msgframe.common.message.MsgFMessage;

public class ProInjectionImpl implements IProsInjectionProcessor {

    public void process(MsgFMessage msg) throws MsgFrameClientException {
        // 放入消息公共信息方式
        msg.setInjectInfo("我是注入进去的消息头信息");

        // 获取公共信息方式
        msg.getInjectInfo();
    }

}
