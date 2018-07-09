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
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * Server
 * 
 * @author：yangzl@asiainfo.com
 * @2017年9月6日 下午6:57:03
 * @since 1.0
 */
public class ResponseServer2 {

    public static void main(String[] args) {

        Context context = ZMQ.context(1);

        Socket clients = context.socket(ZMQ.ROUTER);
        clients.bind("tcp://*:5559");

        Socket workers = context.socket(ZMQ.DEALER);
        workers.bind("inproc://workers");

        for (int thread_nbr = 0; thread_nbr < 5; thread_nbr++) {
            Thread worker = new Worker(context, thread_nbr);
            worker.start();
        }
        // Connect work threads to client threads via a queue
        ZMQ.proxy(clients, workers, null);

        // We never get here but clean up anyhow
        clients.close();
        workers.close();
        context.term();
    }
}


class Worker extends Thread {
    private Context context;
    private int workerNum;

    Worker(Context context, int worker) {
        this.context = context;
        this.workerNum = worker;
    }

    @Override
    public void run() {
        ZMQ.Socket socket = context.socket(ZMQ.REP);
        socket.connect("inproc://workers");

        while (true) {

            // Wait for next request from client (C string)
            String request = socket.recvStr(0);
            System.out.println(Thread.currentThread().getName() + " Received request: [" + request + "]");

            // Do some 'work'
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            // Send reply back to client (C string)
            socket.send("work" + this.workerNum + "reply is: " + "world", 0);
        }
    }
}
