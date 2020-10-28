package org.quickstart.msgframe.v2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfCreateClusterTps {

    static final Logger logger = LoggerFactory.getLogger(SelfCreateClusterTps.class);

    protected final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
    protected final static String separator = "@";
    protected final static String filePath = System.getProperty("user.home") + "/msgframe/clustertps.txt";

    public static void main(String[] args) {

        // 1、初始化开始时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -16);// 前面一天
        calendar.add(Calendar.HOUR, -8);// 1小时前
        calendar.add(Calendar.MINUTE, 30);// 3分钟之前的时间
        calendar.add(Calendar.SECOND, 0);// 往前20秒

        // 2、设置tps的范围
        // （类型）最小值+Random.nextInt((最大值-最小值 + 1))
        Random r = new Random();
        float min = 280000;
        float max = 310000;
        int decimalDigits = 10;// 小数点后多少位数

        BigDecimal inTPS = new BigDecimal(0);
        BigDecimal outTPS = new BigDecimal(0);

        // 3、设置生成的数据的小时数
        int hour = 8;
        int minute = 0;
        int second = 45;
        int count = ((hour * 60 + minute) * 60 + second) / 3;

        // 4、保存数据到文件
        // 时间每次增加3S+50内的随机毫秒数，tps是min和max的随机数
        for (int i = 0; i <= count; i++) {

            calendar.add(Calendar.SECOND, 3);// 增加3秒
            calendar.add(Calendar.MILLISECOND, r.nextInt(1000));// 生成0到1000的随机数

            BigDecimal db = new BigDecimal(Math.random() * (max - min + 1) + min);
            inTPS = db.setScale(decimalDigits, BigDecimal.ROUND_HALF_UP);// 保留30位小数并四舍五入

            String context = sdf.format(calendar.getTime()) + separator + inTPS.toString() + separator + outTPS.toString();// 2
            writeTxtFile(context);
        }

    }

    public static void writeTxtFile(String context) {
        FileWriter fw = null;
        try {
            // 如果文件存在，则追加内容；如果文件不存在，则创建文件
            File file = new File(filePath);
            if (!file.exists()) {
                // 如果路径不存在,则创建
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }

            fw = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);
        context = context + System.getProperty("line.separator");
        pw.println(context);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
