/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Response2.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

/**
 * Response2
 * 
 * @author：youngzil@163.com
 * @2017年9月6日 下午7:25:52
 * @since 1.0
 */
public class Response2 {
    public static void main(String[] args) throws InterruptedException {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        String url = "tcp://*:9999";
        try {
            socket.bind(url);// 绑定地址
        } catch (ZMQException e) {
            throw e;
        }
        boolean wait = true;
        while (wait) {// 服务器一直循环
            byte[] request;
            try {
                request = socket.recv(0);// 接收的客户端数据
                String getData = new String(request);
                if (getData.equals("getSingle")) {
                    socket.send("OK".toString(), 1);
                } else {
                    socket.send("error".toString(), 1);
                }

            } catch (ZMQException e) {
                throw e;
            }
        } // while(wait)
    }
}
