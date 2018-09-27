package org.quickstart.msgframe.v2.transaction;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

import com.ai.aif.msgframe.MfProducerTxClient;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

public class ClientExecutorThread implements Callable<String> {

    private CyclicBarrier cyclicBarrier;
    private MfProducerTxClient client;

    ClientExecutorThread(CyclicBarrier cyclicBarrier, MfProducerTxClient client) {
        this.cyclicBarrier = cyclicBarrier;
        this.client = client;
    }

    @Override
    public String call() throws Exception {

        MsgFTextMessage message = new MsgFTextMessage();
        message.setText("hello");
        client.send("testqueue", message);

        cyclicBarrier.await();

        client.commit();

        return "";
        // return Thread.currentThread().getId() + "";

    }

}
