package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerPartitionAssignor;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.CircularIterator;
import org.apache.kafka.common.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimplePartitionAssignor extends AbstractPartitionAssignor {

    @Override
    public Map<String/*instanceId*/, List<TopicPartition>/*TopicPartition的List集合*/> assign(Map<String/*topic*/, Integer/*partitionNum*/> partitionsPerTopic,
        Map<String/*instanceId*/, ConsumerPartitionAssignor.Subscription/*主要是里面的topics*/> subscriptions) {
        Map<String, List<TopicPartition>> assignment = new HashMap<>();
        List<MemberInfo> memberInfoList = new ArrayList<>();
        for (Map.Entry<String, ConsumerPartitionAssignor.Subscription> memberSubscription : subscriptions.entrySet()) {
            assignment.put(memberSubscription.getKey(), new ArrayList<>());
            memberInfoList.add(new MemberInfo(memberSubscription.getKey(), memberSubscription.getValue().groupInstanceId()));
        }

        CircularIterator<MemberInfo> assigner = new CircularIterator<>(Utils.sorted(memberInfoList));

        for (TopicPartition partition : allPartitionsSorted(partitionsPerTopic, subscriptions)) {
            final String topic = partition.topic();
            while (!subscriptions.get(assigner.peek().memberId).topics().contains(topic)) {
                assigner.next();
            }
            assignment.get(assigner.next().memberId).add(partition);
        }
        return assignment;
    }

    private List<TopicPartition> allPartitionsSorted(Map<String, Integer> partitionsPerTopic,
        Map<String, ConsumerPartitionAssignor.Subscription> subscriptions) {
        SortedSet<String> topics = new TreeSet<>();
        for (ConsumerPartitionAssignor.Subscription subscription : subscriptions.values()) {
            topics.addAll(subscription.topics());
        }

        List<TopicPartition> allPartitions = new ArrayList<>();
        for (String topic : topics) {
            Integer numPartitionsForTopic = partitionsPerTopic.get(topic);
            if (numPartitionsForTopic != null) {
                allPartitions.addAll(AbstractPartitionAssignor.partitions(topic, numPartitionsForTopic));
            }
        }
        return allPartitions;
    }

    @Override
    public String name() {
        return "simple";
    }

}


