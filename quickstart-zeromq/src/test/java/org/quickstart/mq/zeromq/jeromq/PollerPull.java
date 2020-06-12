/**
 * 项目名称：quickstart-zeromq 
 * 文件名：PollerPull.java
 * 版本信息：
 * 日期：2017年9月9日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * PollerPull
 * 
 * @author：yangzl
 * @2017年9月9日 上午11:18:43
 * @since 1.0
 */
public class PollerPull {

    public static void main(String args[]) {
        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket pull1 = context.socket(ZMQ.PULL); // 创建一个pull
        pull1.connect("tcp://127.0.0.1:5555"); // 建立于push的连接
        ZMQ.Socket pull2 = context.socket(ZMQ.PULL);
        pull2.connect("tcp://127.0.0.1:5555");

        ZMQ.Poller poller = new ZMQ.Poller(2); // 创建一个大小为2的poller
        poller.register(pull1, ZMQ.Poller.POLLIN); // 分别将上述的pull注册到poller上，注册的事件是读
        poller.register(pull2, ZMQ.Poller.POLLIN);
        int i = 0;
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll();
            if (poller.pollin(0)) {
                while (null != pull1.recv(ZMQ.NOBLOCK)) { // 这里采用了非阻塞，确保一次性将队列中的数据读取完
                    i++;
                }

            }
            if (poller.pollin(1)) {
                while (null != pull2.recv(ZMQ.NOBLOCK)) {
                    i++;
                }

            }
            if (i % 10000000 == 0) {
                System.out.println(i);
            }
        }
        pull1.close();
        pull2.close();
        context.term();

    }

}
