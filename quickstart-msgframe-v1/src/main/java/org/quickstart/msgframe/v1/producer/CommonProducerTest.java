package org.quickstart.msgframe.v1.producer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.exception.MsgFrameClientException;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class CommonProducerTest {

    static final Logger logger = LoggerFactory.getLogger(CommonProducerTest.class);

    public static void main(String[] args) throws Exception {

        AtomicLong messageCount = new AtomicLong();

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss.SSS");

        int count = 0;

        MfProducerClient client = new MfProducerClient();
        while (true) {

            try {
                MsgFTextMessage message = new MsgFTextMessage();
                message.setText("test-" + messageCount.incrementAndGet());
                client.send("AmsSendUserAppBal", message);
                logger.info("send message:" + message.getMsgId() + ",date=" + sdf.format(new Date()));

                count++;

                // Thread.sleep(5);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.info("send message count:" + count + ",date=" + sdf.format(new Date()));

        }

    }

}
