/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Push.java
 * 版本信息：
 * 日期：2017年9月8日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Push
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月8日 上午8:42:56
 * @since 1.0
 */
public class Push {
    public static void main(String args[]) {

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket push = context.socket(ZMQ.PUSH);
        push.bind("ipc://fjs");
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            push.send(("hello" + i).getBytes());
        }

        System.out.println("send cost time" + (System.currentTimeMillis() - beginTime));
        push.close();
        context.term();

    }
}
