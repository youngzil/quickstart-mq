package org.quickstart.msgframe.v2.consumer;

import java.io.Serializable;
import java.util.Random;

import org.quickstart.msgframe.v2.common.Stu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.common.exception.ConsumerException;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.common.message.MsgFObjectMessage;
import com.ai.aif.msgframe.consumer.facade.IConsumerProcessor;

public class ConsumerProcessorImpl implements IConsumerProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerProcessorImpl.class);

    private static final Random random = new Random();

    @SuppressWarnings("rawtypes")
    @Override
    public void process(MsgFMessage msg) throws ConsumerException {
        logger.info("receive message：{}", msg);
        
        Object ss = msg.getHeaderMap().get("");

        if (msg instanceof MsgFObjectMessage) {
            Stu stu = (Stu) ((MsgFObjectMessage) msg).getMsg();
            logger.info("MsgFObjectMessage,name={},age={}", stu.getName(), stu.getAge());
            logger.info(Thread.currentThread().getName() + " Receive New Messages: ");
        }
        
//        throw new RuntimeException();

        // try {
        // Thread.currentThread().sleep(60 * 1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }

        // throw new ConsumerException("ss");
        // return msg.getMsgId();
        // return new Boolean(false);

        // if (random.nextBoolean()) {
        // throw new ConsumerException("我是返回模拟异常");
        // }
        // return "我是返回模拟值：" + Integer.valueOf(((MsgFTextMessage) msg).getText());

        // 返回Boolean类型的false，代表消息消费失败，重新消费，其他表示消费成功
//        return new Boolean(true);
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
