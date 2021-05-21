/**
 * 项目名称：quickstart-kafka 
 * 文件名：Dem.java
 * 版本信息：
 * 日期：2017年11月17日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.sample2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dem
 * 
 * @author：yangzl
 * @2017年11月17日 下午4:51:51
 * @since 1.0
 */
public class DateConver {

    public static void main(String[] args) {
        Date date = new Date();
        Long time = date.getTime();
        System.out.println(time);

        time = 1512453072249L;
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        System.out.println(sdf.format(d));
    }

}
