/**
 * 项目名称：quickstart-kafka 
 * 文件名：CounterInterceptor.java
 * 版本信息：
 * 日期：2017年10月21日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.sample2.interceptor.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * CounterInterceptor
 * 
 * @author：youngzil@163.com
 * @2017年10月21日 上午11:32:58
 * @since 1.0
 */
public class CounterInterceptor implements ConsumerInterceptor<String, String> {

    /* (non-Javadoc)
     * @see org.apache.kafka.common.Configurable#configure(java.util.Map)
     */
    @Override
    public void configure(Map<String, ?> configs) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.kafka.clients.consumer.ConsumerInterceptor#onConsume(org.apache.kafka.clients.consumer.ConsumerRecords)
     */
    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        System.out.println("consumer.CounterInterceptor.onConsume");
        return records;// 不作处理，直接返回原始数据
    }

    /* (non-Javadoc)
     * @see org.apache.kafka.clients.consumer.ConsumerInterceptor#onCommit(java.util.Map)
     */
    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {
        System.out.println("consumer.CounterInterceptor.onCommit");

    }

    /* (non-Javadoc)
     * @see org.apache.kafka.clients.consumer.ConsumerInterceptor#close()
     */
    @Override
    public void close() {
        System.out.println("consumer.CounterInterceptor.close");

    }

}
