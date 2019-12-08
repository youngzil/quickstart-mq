/**
 * 项目名称：quickstart-zeromq 
 * 文件名：ClientSub1.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import java.io.IOException;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;

/**
 * ClientSub1
 * 
 * @author：youngzil@163.com
 * @2017年9月6日 下午7:06:21
 * @since 1.0
 */
public class ClientSub1 {

    public static void main(String[] args) throws IOException {
        // 准备上下文

        ZMQ.Context context = ZMQ.context(1);
        // Socket to talk to server
        System.out.println("Collecting updates from weather server");
        // 套接字连接至服务器
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:5556");

        // 订阅任何主题（An empty 'option_value' of length zero shall subscribe to all incoming messages）
        // subscriber.subscribe("".getBytes());

        // Subscribe to zipcode, default is NYC, 10001
        String filter = (args.length > 0) ? args[0] : "10001 ";
        subscriber.subscribe(filter.getBytes());

        // Process 100 updates
        int update_nbr;
        long total_temp = 0;
        for (update_nbr = 0; update_nbr < 100; update_nbr++) {
            // Use trim to remove the tailing '0' character
            String string = subscriber.recvStr(0).trim();
            StringTokenizer sscanf = new StringTokenizer(string, " ");
            int zipcode = Integer.valueOf(sscanf.nextToken());
            int temperature = Integer.valueOf(sscanf.nextToken());
            int relhumidity = Integer.valueOf(sscanf.nextToken());

            total_temp += temperature;
        }
        System.out.println("Average temperature for zipcode '" + filter + "' was " + (int) (total_temp / update_nbr));
        System.out.println("...");
        System.in.read();

        // 关闭套接字和上下文
        subscriber.close();
        context.term();
    }

}
