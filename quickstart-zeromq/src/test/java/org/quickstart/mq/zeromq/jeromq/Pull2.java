/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Pull.java
 * 版本信息：
 * 日期：2017年9月8日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import java.util.concurrent.atomic.AtomicInteger;

import org.zeromq.ZMQ;

/**
 * Pull
 * 
 * @author：yangzl
 * @2017年9月8日 上午8:43:25
 * @since 1.0
 */
public class Pull2 {
    public static void main(String args[]) {
        final AtomicInteger number = new AtomicInteger(0);
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                private int here = 0;

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    ZMQ.Context context = ZMQ.context(1);
                    ZMQ.Socket pull = context.socket(ZMQ.PULL);
                    pull.connect("ipc://fjs");
                    // pull.connect("ipc://fjs");
                    while (true) {
                        String message = new String(pull.recv());
                        int now = number.incrementAndGet();
                        here++;
                        if (now % 1000000 == 0) {
                            System.out.println(now + "  here is : " + here);
                        }
                    }
                }

            }).start();

        }
    }
}
