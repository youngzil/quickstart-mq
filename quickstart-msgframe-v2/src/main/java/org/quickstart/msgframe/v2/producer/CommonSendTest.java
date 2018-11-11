/**
 * 项目名称：quickstart-mq-msgframe-v2 
 * 文件名：CommonSendTest.java
 * 版本信息：
 * 日期：2018年7月3日
 * Copyright asiainfo Corporation 2018
 * 版权所有 *
 */
package org.quickstart.msgframe.v2.producer;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.ClientFactory;
import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.CompletionListener;
import com.ai.aif.msgframe.common.message.MsgFMapMessage;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;
import com.ai.aif.msgframe.facade.IMsgForTxProducer;

/**
 * CommonSendTest
 * 
 * @author：youngzil@163.com
 * @2018年7月3日 下午1:59:52
 * @since 1.0
 */
public class CommonSendTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonSendTest.class);

    static MfProducerClient client = new MfProducerClient();
    MfProducerTxClient txClient = new MfProducerTxClient();
    String topic = "topicTest";

    @Test
    public void testSend() throws RemoteException, Exception {
        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("commonMessage");

        MsgFMapMessage messageMap = new MsgFMapMessage();
        messageMap.setMapmessage(new HashMap<String, String>());
        messageMap.getMapmessage().put("mapKey", "mapValue");
        client.send(topic, messageMap);
    }

    @Test
    public void testAsyncSend() throws RemoteException, Exception {
        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("AsyncSendMessage");
        client.asyncSend(topic, message, new CompletionListener() {

            public void onCompletion(MsgFMessage message) {
                logger.info("发送成功" + message.getMsgId());
            }

            public void onException(MsgFMessage message, Exception e) {
                logger.error("发送消息异常：message={}", message, e);
            }

        });
    }

    @Test
    public void testSendOneway() throws RemoteException, Exception {
        MsgFTextMessage message = new MsgFTextMessage();
        // message.setCompressMsgBodyOverHowmuch(5);//设置消息体最大值（超过压缩）
        // message.setDelay("5");// 设置延时消息的延时level
        // message.setFilterTag("nn");
        message.setText("OneWayMessage");

        client.sendOneway(topic, message);
    }

    @Test
    public void testSendOrder() throws Exception {
        String orderId = "2013";// orderId对于同一组顺序消息必须一致
        for (int i = 0; i < 3; i++) {
            MsgFTextMessage message = new MsgFTextMessage();
            message.setText("orderMessage" + i);
            client.sendOrderMsg("OrderTest", message, orderId);
        }
    }

    @Test
    public void testSendTagMessage() throws Exception {
        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("tagMessage");
        message.setFilterTag("nn");
        client.send(topic, message);
    }

    @Test
    public void testSendTxMessage() throws Exception {
        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("txMessageForActiveMQ");
        this.txClient.send(topic, message);
        
//        this.txClient.sendOrderMsg(topic, message, "20013");

        this.txClient.commit();
        logger.info("发送成功，" + message);
    }

    @Test
    public void testSendTxMessage2() throws Exception {

        IMsgForTxProducer client = ClientFactory.createTxClient();

        for (int i = 0; i < 10; i++) {

            MsgFTextMessage message = new MsgFTextMessage();
            message.setText("txMessageForActiveMQ2" + i);

            // client.send("topicTest", message);
            client.sendOrderMsg("topicTest", message, "sadsa");

            client.commit();

            logger.info("发送成功，" + message);

        }

    }

    @Test
    public void testSendTxMessageForRocketMQ() throws Exception {

        LocalTransactionExecuter excutor = new LocalTransactionExecuter() {
            public LocalTransactionState executeLocalTransactionBranch(Message message, Object obj) {
                try {
                    logger.info("业务处理成功,message={}", message);
                } catch (Exception e) {
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        };

        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("txMessageForRocketMQ");

        this.txClient.sendMessageInTransaction("topicTest", message, excutor);

        logger.info("发送成功，" + message);
    }

    @Before
    public void before() {
        System.out.println("@Before");
    }

    @After
    public void after() {
        System.out.println("@After");
    }

    @BeforeClass
    public static void beforeClass() {
        System.out.println("@BeforeClass");
    };

    @AfterClass
    public static void afterClass() {
        System.out.println("@AfterClass");
    };

}
