/**
 * 项目名称：quickstart-zeromq 
 * 文件名：ProxyTest.java
 * 版本信息：
 * 日期：2017年9月7日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * ProxyTest
 * 
 * @author：youngzil@163.com
 * @2017年9月7日 下午10:30:50
 * @since 1.0
 */
public class ProxyTest {

    public static void main(String[] args) {
        // Prepare our context and sockets
        Context context = ZMQ.context(1);
        // This is where the weather server sits
        Socket frontend = context.socket(ZMQ.SUB);
        frontend.connect("tcp://192.168.55.210:5556");
        // This is our public endpoint for subscribers
        Socket backend = context.socket(ZMQ.PUB);
        backend.bind("tcp://10.1.1.0:8100");
        // Subscribe on everything
        frontend.subscribe("".getBytes());
        // Run the proxy until the user interrupts us
        ZMQ.proxy(frontend, backend, null);
        frontend.close();
        backend.close();
        context.term();
    }

}
