/**
 * 项目名称：quickstart-zeromq 
 * 文件名：DealerResponse.java
 * 版本信息：
 * 日期：2017年9月9日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * DealerResponse
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月9日 上午11:25:01
 * @since 1.0
 */
public class DealerResponse {

    public static void main(String args[]) {
        final ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket router = context.socket(ZMQ.ROUTER);
        ZMQ.Socket dealer = context.socket(ZMQ.DEALER);

        router.bind("ipc://fjs1");
        dealer.bind("ipc://fjs2");

        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    ZMQ.Socket response = context.socket(ZMQ.REP);
                    response.connect("ipc://fjs2");
                    while (!Thread.currentThread().isInterrupted()) {
                        response.recv();
                        response.send("hello".getBytes());
                        try {
                            Thread.currentThread().sleep(1);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    response.close();
                }

            }).start();
        }
        ZMQ.proxy(router, dealer, null);
        router.close();
        dealer.close();
        context.term();
    }

}
