package org.quickstart.msgframe.v1.producer;

import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;
import com.ai.aif.msgframe.facade.impl.MfHalfmessageClient;

public class TestHalfMessageProducer {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        MsgFTextMessage crm_product = new MsgFTextMessage();
        crm_product.setText("产品发送内容yyyyyyyyyy：_" + 1);

        // 第一步：发送half消息
        MfHalfmessageClient mfc = new MfHalfmessageClient(new MfProducerTxClient());
        mfc.send("open_quene_request", crm_product);
        System.out.println("发送half消息成功！");
        Thread.sleep(5000);
        // 第二步提交half消息
        mfc.commit();
        System.out.println("提交half消息成功！");

    }

}
