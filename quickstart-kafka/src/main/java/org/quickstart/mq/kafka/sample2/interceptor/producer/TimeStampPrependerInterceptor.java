/**
 * 项目名称：quickstart-kafka 
 * 文件名：TimeStampPrependerInterceptor.java
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
 * TimeStampPrependerInterceptor
 * 
 * @author：youngzil@163.com
 * @2017年10月21日 上午9:45:31
 * @since 1.0
 */
public class TimeStampPrependerInterceptor implements ProducerInterceptor<String, String> {
    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public ProducerRecord onSend(ProducerRecord record) {
        System.out.println("TimeStampPrependerInterceptor.onSend");
        return new ProducerRecord(record.topic(), record.partition(), record.timestamp(), record.key(), System.currentTimeMillis() + "," + record.value().toString());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        System.out.println("TimeStampPrependerInterceptor.onAcknowledgement");

    }

    @Override
    public void close() {
        System.out.println("TimeStampPrependerInterceptor.close");
    }
}
