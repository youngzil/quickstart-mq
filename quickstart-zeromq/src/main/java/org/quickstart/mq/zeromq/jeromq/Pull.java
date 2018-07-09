/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Pull.java
 * 版本信息：
 * 日期：2017年9月8日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Pull
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月8日 上午8:43:25
 * @since 1.0
 */
public class Pull {
    public static void main(String args[]) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pull = context.socket(ZMQ.PULL);

        // pull.bind("ipc://fjs");
        pull.connect("ipc://fjs");

        int number = 0;
        long beginTime = System.currentTimeMillis();
        while (true) {
            String message = new String(pull.recv());

            // System.out.println("received " + message);
            number++;
            if (10000000 == number) {
                System.out.println("receive cost time" + (System.currentTimeMillis() - beginTime));
            }
        }
    }
}
