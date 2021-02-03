package org.quickstart.mq.kafka.sample3;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

/**
 * @author Administrator
 *
 */
public class SimplePartitioner2 implements Partitioner {

    @Override
    public void configure(Map<String, ?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public int partition(String topic, Object key, byte[] arg2, Object value, byte[] arg4, Cluster arg5) {
        // TODO Auto-generated method stub
        /*
         * Cluster arg5打印出来是这个样子
         * Cluster(nodes = [172.17.11.11:9092 (id: 0 rack: null), 172.17.11.13:9092 (id: 1 rack: null), 172.17.11.15:9092 (id: 2 rack: null)], partitions = [Partition(topic = TOPIC-20160504-1200, partition = 1, leader = 2, replicas = [0,1,2,], isr = [2,1,0,], Partition(topic = TOPIC-20160504-1200, partition = 2, leader = 0, replicas = [0,1,2,], isr = [0,2,1,], Partition(topic = TOPIC-20160504-1200, partition = 0, leader = 1, replicas = [0,1,2,], isr = [1,0,2,]])
        */
        /*
         * byte[] arg2以字节码的格式存储key
         * System.out.println(new String(arg2));
         * System.out.println(key.toString());二者输出相同，都是key
         * byte[] arg4和Object value同理
         */
        /*
         * 返回值指定了分区值
         */
        int partition = Integer.parseInt(key.toString().split("\\.")[3]);// 分割 . 需要转义\\.
        if (partition % 2 == 0) {
            return 1;
        } else {
            return 2;
        }
    }

}
