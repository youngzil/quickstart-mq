package org.quickstart.mq.kafka.sample.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

/**
 * 自定义随机分区器
 * 1. 实现Partitioner接口
 * 2. 重写partition方法
 */
public class RandParitioner implements Partitioner {
    /**
     * 计算当前消息的分区号
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
        //先获取分区的数量
        Integer count = cluster.partitionCountForTopic(topic);
        //随机一个分区号，应该是一个0~count-1的自然数
        int number = (int) (Math.random() * count);
        return number;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
