package org.quickstart.mq.kafka.sample.partition;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义一个轮询分区器
 */
public class RoundPartitioner implements Partitioner {
    //获取一个全局的计数器
    private AtomicInteger atomic = new AtomicInteger();

    //计算当前消息的分区号
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        //获取主题的分区数量
        Integer count = cluster.partitionCountForTopic(topic);
        //获取计数器上的自增值
        int andIncrement = atomic.getAndIncrement();
        //对分区数量取模运算，就可以得到 0，1，2，0，1，2.....轮询的效果
        int i = andIncrement % count;
        return i;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}