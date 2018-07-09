/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Client.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Client
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月6日 下午6:58:18
 * @since 1.0
 */
public class RequestClient {

    public static void main(String[] args) {

        /*
         * 传统的多线程并发模式一般会采用锁，临界区，信号量等技术来控制，而ZeroMQ给出的建议是：在创建IO时不要超出CPU核数。
        当我们创建一个上下文时都会有这么一句代码“Context context = ZMQ.context(1);”这里就指定了IO线程数。
        通常来说一个线程足矣。但是如果希望创建多个IO线程，最好不要超出CPU核数，因为此时ZeroMQ会将工作线程其实就是那个Poller绑定到每一个核
        ，免除了线程上下文切换带来的开销。
         */

        // inproc://, ipc://, tcp://这三个通讯方案

        ZMQ.Context context = ZMQ.context(1);
        // Socket to talk to server
        System.out.println("Connecting to hello world server…");
        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://localhost:5555");
        for (int requestNbr = 0; requestNbr != 100; requestNbr++) {
            String request = "Hello";
            System.out.println("Sending Hello " + requestNbr);
            // 发送
            requester.send(request.getBytes(), 0);
            // requester.send(request.getBytes(), ZMQ.NOBLOCK);
            // 获得返回值
            // 接收response发送回来的数据
            // 正在request/response模型中，send之后必须要recv之后才能继续send，这可能是为了保证整个request/response的流程走完
            byte[] reply = requester.recv(0);
            System.out.println("Received " + new String(reply) + " " + requestNbr);
            System.out.println("Received reply : [" + new String(reply) + "]");
        }
        requester.close();
        context.term();
    }

}
