/**
 * 项目名称：quickstart-zeromq 
 * 文件名：ClientSub2.java
 * 版本信息：
 * 日期：2017年9月6日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.zeromq.jeromq;

import java.io.IOException;
import java.util.StringTokenizer;

import org.zeromq.ZMQ;

/**
 * ClientSub2
 * 
 * @author：youngzil@163.com
 * @2017年9月6日 下午7:06:21
 * @since 1.0
 */
public class ClientSub2 {

    public static void main(String[] args) throws IOException {
        ZMQ.Context context = ZMQ.context(1);
        // Socket to talk to server
        System.out.println("Collecting updates from weather server");
        ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:5556");

        // Subscribe to zipcode, default is NYC, 10001
        String filter = (args.length > 0) ? args[0] : "10002";
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
        subscriber.close();
        context.term();
    }

}
