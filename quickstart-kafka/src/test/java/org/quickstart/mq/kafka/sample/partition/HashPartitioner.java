package org.quickstart.mq.kafka.sample.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class HashPartitioner implements Partitioner {
    /**
     * 使用key的hash值来计算消息的分区号
     * @param topic
     * @param key
     * @param keyBytes
     * @param value
     * @param valueBytes
     * @param cluster
     * @return
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //获取分区数量
        Integer count = cluster.partitionCountForTopic(topic);
        //获取key的hash执行取模运算
        int number = key.toString().hashCode() % count;
        return number;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}