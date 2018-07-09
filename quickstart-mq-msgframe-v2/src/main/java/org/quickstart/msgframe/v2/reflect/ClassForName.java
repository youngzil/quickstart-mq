/**
 * 项目名称：quickstart-example 
 * 文件名：ClassForName.java
 * 版本信息：
 * 日期：2017年2月18日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.reflect;

import com.ai.aif.msgframe.common.exception.ConsumerException;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;
import com.ai.aif.msgframe.consumer.facade.IConsumerProcessor;

/**
 * ClassForName
 * 
 * @author：yangzl@asiainfo.com
 * @2017年2月18日 下午12:41:37
 * @version 1.0
 */
public class ClassForName {

    public static void main(String[] args) throws Exception {
        IConsumerProcessor processor = (IConsumerProcessor) Class.forName("org.quickstart.msgframe.reflect.SubscribeImpl1").newInstance();
        MsgFMessage msg = new MsgFTextMessage();
        msg.setMsgId("111");
        processor.process(msg);
    }

}
