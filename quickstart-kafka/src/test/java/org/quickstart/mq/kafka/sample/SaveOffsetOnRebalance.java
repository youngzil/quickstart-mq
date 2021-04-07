package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SaveOffsetOnRebalance implements ConsumerRebalanceListener {

    // 第一次加入消费者组的时候，rebalance会执行，也就是启动本地consumer的时候，当有其他消费者加入的时候也会调用。

    private Consumer<String, String> consumer;

    //初始化方法，传入consumer对象，否则无法调用外部的consumer对象，必须传入
    public SaveOffsetOnRebalance(Consumer<String, String> consumer) {
        this.consumer = consumer;
    }

    // 方法会在再均衡开始之前和消费者停止读取消息之后被调用。如果在这里提交偏移量，下一个接管分区的消费者就知道该从哪里读取了。
    // onPartitionRevoked会在rebalance操作之前调用，用于我们提交消费者偏移
    // 参数partitions表示那些仅仅需要回收的分区，其实也就是当前消费者消费的所有分区，当前分配给这个消费者的所有分区。
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

        //提交偏移量 主要是consumer.commitSync(toCommit); 方法
        System.out.println("*- in ralance:onPartitionsRevoked");
        //commitQueue 是我本地已消费消息的一个队列 是一个linkedblockingqueue对象
        /*while (!commitQueue.isEmpty()) {
            Map<TopicPartition, OffsetAndMetadata> toCommit = commitQueue.poll();
            consumer.commitSync(toCommit);
        }*/

        partitions.forEach(partition -> System.out.println("Revoked partition:" + partition));

    }

    // 方法会在重新分配分区之后和消费者开始读取消息之前被调用。
    // OnPartitionAssigned会在rebalance操作之后调用，用于我们拉取新的分配区的偏移量。
    // 参数partitions表示它所分配的分区结果，rebalance后分配后，再次分配给这个消费者的所有分区，后面消费的所有分区
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        //rebalance之后 获取新的分区，获取最新的偏移量，设置拉取分量
        System.out.println("*- in ralance:onPartitionsAssigned  ");
        for (TopicPartition partition : partitions) {
            System.out.println("Assigned partition:" + partition);

            //获取消费偏移量，实现原理是向协调者发送获取请求
            // OffsetAndMetadata committedOffset = consumer.committed(partition);
            Map<TopicPartition, OffsetAndMetadata> committedOffsetMap = consumer.committed(Collections.singleton(partition));
            OffsetAndMetadata committedOffset = committedOffsetMap.get(partition);
            System.out.println(committedOffset);
            //设置本地拉取分量，下次拉取消息以这个偏移量为准
            // consumer.seek(partition, offset.offset());

            // consumer.seek(partition, 21000L);

            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(Collections.singleton(partition));
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(Collections.singleton(partition));
            System.out.println("beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);

        }

    }

}

