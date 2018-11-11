/**
 * 项目名称：quickstart-zeromq 
 * 文件名：ServerPub.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import java.util.Random;

import org.zeromq.ZMQ;

/**
 * ServerPub
 * 
 * @author：youngzil@163.com
 * @2017年9月6日 下午7:05:16
 * @since 1.0
 */
public class ServerPub {

    public static void main(String[] args) throws Exception {
        // Prepare our context and publisher
        // 准备上下文和套接字
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:5556");

        // Initialize random number generator
        Random srandom = new Random(System.currentTimeMillis());
        while (!Thread.currentThread().isInterrupted()) {
            // Get values that will fool the boss
            int zipcode, temperature, relhumidity;
            zipcode = 10000 + srandom.nextInt(10000);
            temperature = srandom.nextInt(215) - 80 + 1;
            relhumidity = srandom.nextInt(50) + 10 + 1;

            // Send message to all subscribers
            String update = String.format("%05d %d %d", zipcode, temperature, relhumidity);
            publisher.send(update, 0);
            // publisher.send(("admin " + 1).getBytes(), ZMQ.NOBLOCK);
        }
        // 关闭套接字和上下文
        publisher.close();
        context.term();
    }

}
