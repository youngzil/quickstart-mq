/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Server.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Server
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月6日 下午6:57:03
 * @since 1.0
 */
public class ResponseServer {
    public static void main(String[] args) throws Exception {
        ZMQ.Context context = ZMQ.context(1);
        // Socket to talk to clients
        ZMQ.Socket responder = context.socket(ZMQ.REP);
        responder.bind("tcp://*:5555");
        // responder.bind("tcp://192.168.124.130:5555");
        // while (!Thread.currentThread().isInterrupted()) {
        while (true) {
            // Wait for next request from the client
            byte[] request = responder.recv(0);
            System.out.println("Received request: [" + new String(request) + "]");
            // Do some 'work'
            Thread.sleep(1000);
            // Send reply back to client
            String reply = "World";
            // 向request端发送数据 ，必须要要request端返回数据，没有返回就又recv，将会出错，这里可以理解为强制要求走完整个request/response流程
            responder.send(reply.getBytes(), 0);
            // responder.send(reply, ZMQ.NOBLOCK);
            System.out.println("Sending " + reply);

            if (new String(request).equals("quit")) {
                break;
            }
        }
        System.out.println("===========server end=============");
        responder.close();
        context.term();
    }
}
