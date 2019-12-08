/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Push3.java
 * 版本信息：
 * 日期：2017年9月9日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Push3
 * 
 * @author：youngzil@163.com
 * @2017年9月9日 上午11:12:20
 * @since 1.0
 */
public class Push3 {

    public static void main(String args[]) {
        for (int j = 0; j < 3; j++) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ZMQ.Context context = ZMQ.context(1);
                    ZMQ.Socket push = context.socket(ZMQ.PUSH);

                    push.connect("ipc://fjs");

                    for (int i = 0; i < 10000000; i++) {
                        push.send("hello".getBytes());
                        System.out.println(i);
                    }
                    push.close();
                    context.term();
                }

            }).start();;
        }
    }

}
