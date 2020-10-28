package org.quickstart.msgframe.v2.producer;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.message.MsgFObjectMessage;

public class StressSendMessageTest {

    private final static Map<Integer, byte[]> BYTES_MAP = new HashMap<Integer, byte[]>();

    private final static ExecutorService pool = Executors.newFixedThreadPool(5);

    // @org.junit.Test
    public void test() {
        for (int i = 0; i < 2; i++) {

            pool.submit(new Runnable() {
                public void run() {
                    try {
                        new StressSendMessageTest().messageStressTest("PerfTest", null, 1024);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
        }

        while (true) {

        }

    }

    /**
     * 压力测试需要注册的服务
     * 
     * @param subject 必选 服务的主题
     * @param filterTag 可选 服务的路由条件，可以为null
     * @param bytesNumber 消息的大小，字节数
     * 
     * @throws MsgFrameException
     * @see [类、类#方法、类#成员]
     */
    private void messageStressTest(String subject, String filterTag, int bytesNumber) throws Exception {
        MfProducerClient client = new MfProducerClient();
        MsgFObjectMessage message_order_a = new MsgFObjectMessage();
        message_order_a.setFilterTag(filterTag);
        message_order_a.setMsg(getByte(bytesNumber));

        try {
            client.send(subject, message_order_a);
        } catch (RemoteException e) {
            throw new Exception("消息发送网络发生异常", e);
        } catch (Exception e) {
            throw e;
        }
    }

    public static byte[] getByte(int bytesNumber) {
        byte[] bytes = null;
        if (!BYTES_MAP.containsKey(bytesNumber)) {

            synchronized (BYTES_MAP) {
                if (!BYTES_MAP.containsKey(bytesNumber)) {

                    bytes = new byte[bytesNumber];
                    for (byte b : bytes) {
                        b = 'a';
                    }
                    BYTES_MAP.put(bytesNumber, bytes);
                }
                bytes = BYTES_MAP.get(bytesNumber);
            }
        } else {
            bytes = BYTES_MAP.get(bytesNumber);
        }

        return bytes;
    }

}
