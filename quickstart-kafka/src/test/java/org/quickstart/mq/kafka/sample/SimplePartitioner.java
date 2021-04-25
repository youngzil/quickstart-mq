package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;

public class SimplePartitioner implements Partitioner {

    @Override
    public void configure(Map<String, ?> configs) {
        // TODO Auto-generated method stub

    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int partition = 0;
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        String stringKey = (String)key;
        int offset = stringKey.lastIndexOf('.');
        if (offset > 0) {
            partition = Integer.parseInt(stringKey.substring(offset + 1)) % numPartitions;
        }

        return partition;

        /* *//**
         *由于我们按key分区，在这里我们规定：key值不允许为null。在实际项目中，key为null的消息*，可以发送到同一个分区。
         *//*
        if(keyBytes == null) {
            throw new InvalidRecordException("key cannot be null");
        }
        if(key.equals("1")) {
            return 1;
        }
        //如果消息的key值不为1，那么使用hash值取模，确定分区。
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;*/

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
