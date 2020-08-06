/**
 * 项目名称：quickstart-kafka 
 * 文件名：MyPartition.java
 * 版本信息：
 * 日期：2017年11月16日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.kafka.v1.sample2.partition;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http://blog.csdn.net/liuxiangke0210/article/details/71422508 使用./kafka-topics.sh 命令创建topic及partitions 分区数 bin/kafka-topics.sh --create --zookeeper 192.168.31.130:2181 --replication-factor 2
 * --partitions 3 --topic Topic-test MyPartition 实现org.apache.kafka.clients.producer.Partitioner 分区接口，以实现自定义的消息分区 备注： 要先用命令创建topic及partitions 分区数;否则在自定义的分区中如果有大于1的情况下，发送数据消息到kafka时会报expired due to
 * timeout while requesting metadata from brokers错误
 * 
 * @author：yangzl
 * @2017年11月16日 下午5:36:40
 * @since 1.0
 */
public class MyPartition implements Partitioner {

    private static Logger logger = LoggerFactory.getLogger(MyPartition.class);

    public MyPartition() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // TODO Auto-generated method stub

    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // TODO Auto-generated method stub
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        // cluster.availablePartitionsForTopic(topic);
        // int numPartitions = cluster.partitionCountForTopic("sssss");

        int partitionNum = 0;
        try {
            partitionNum = Integer.parseInt((String) key);
        } catch (Exception e) {
            partitionNum = key.hashCode();
        }

        System.out.println("the message sendTo topic:" + topic + " and the partitionNum:" + partitionNum + ",broker numPartitions=" + numPartitions);
        // logger.info("the message sendTo topic:" + topic + " and the partitionNum:" + partitionNum);
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
