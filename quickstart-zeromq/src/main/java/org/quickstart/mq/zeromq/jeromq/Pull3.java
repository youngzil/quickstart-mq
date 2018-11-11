/**
 * 项目名称：quickstart-zeromq 
 * 文件名：Pull3.java
 * 版本信息：
 * 日期：2017年9月9日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import org.zeromq.ZMQ;

/**
 * Pull3
 * 
 * @author：youngzil@163.com
 * @2017年9月9日 上午11:13:26
 * @since 1.0
 */
public class Pull3 {

    public static void main(String args[]) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket pull = context.socket(ZMQ.PULL);

        pull.bind("ipc://fjs");

        int number = 0;
        while (true) {
            String message = new String(pull.recv());
            number++;
            if (number % 1000000 == 0) {
                System.out.println(number);
            }
        }
    }

}
