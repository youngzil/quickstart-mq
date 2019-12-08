/**
 * 项目名称：quickstart-zeromq 
 * 文件名：ProxyTest.java
 * 版本信息：
 * 日期：2017年9月7日
 * Copyright youngzil Corporation 2017
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
public class ProxyTest2 {

    public static void main(String[] args) {
        // Prepare our context and sockets
        Context context = ZMQ.context(1);
        // Socket facing clients
        Socket frontend = context.socket(ZMQ.ROUTER);
        frontend.bind("tcp://*:5559");
        // Socket facing services
        Socket backend = context.socket(ZMQ.DEALER);
        backend.bind("tcp://*:5560");

        // Start the proxy
        ZMQ.proxy(frontend, backend, null);

        // We never get here but clean up anyhow
        frontend.close();
        backend.close();
        context.term();
    }

}
