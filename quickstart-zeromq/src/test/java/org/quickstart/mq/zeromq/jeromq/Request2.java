/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Request2.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Request2
 * 
 * @author：yangzl
 * @2017年9月6日 下午7:26:44
 * @since 1.0
 */
public class Request2 {
    public static void main(String args[]) throws InterruptedException {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);

        // System.out.println("Connecting to hello world server...");
        socket.connect("tcp://localhost:9999");
        // socket.bind("tcp://localhost:9999"); //不行，查看bind和connect方法内部

        String requestString = "getSingle";
        // byte[] request = requestString.getBytes();
        socket.send(requestString, 0);
        Thread.sleep(100);
        byte[] reply = socket.recv(0);
        System.out.println("客户端接收的是: [" + new String(reply) + "]");
    }
}
