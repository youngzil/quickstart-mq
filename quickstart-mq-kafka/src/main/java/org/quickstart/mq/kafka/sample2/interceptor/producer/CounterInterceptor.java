/**
 * 项目名称：quickstart-kafka 
 * 文件名：CounterInterceptor.java
 * 版本信息：
 * 日期：2017年10月21日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.sample2.interceptor.producer;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * CounterInterceptor
 * 
 * @author：yangzl@asiainfo.com
 * @2017年10月21日 上午10:07:39
 * @since 1.0
 */
public class CounterInterceptor implements ProducerInterceptor<String, String> {

    private int errorCounter = 0;
    private int successCounter = 0;

    @Override
    public void configure(Map<String, ?> configs) {}

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        System.out.println("CounterInterceptor.onSend");

        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        System.out.println("CounterInterceptor.onAcknowledgement");
        if (exception == null) {
            successCounter++;
        } else {
            errorCounter++;
        }
    }

    @Override
    public void close() {
        System.out.println("CounterInterceptor.close");
        // 保存结果
        System.out.println("Successful sent: " + successCounter);
        System.out.println("Failed sent: " + errorCounter);
    }
}
