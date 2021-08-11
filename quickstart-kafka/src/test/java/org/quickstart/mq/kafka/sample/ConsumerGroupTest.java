package org.quickstart.mq.kafka.sample;

import lombok.Data;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AlterConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ConsumerGroupTest {

    // private static final String brokerList = "localhost:9092";
    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    // private static final String brokerList = "172.16.49.66:9092,172.16.49.68:9092,172.16.49.72:9092";

    private Admin adminClient;
    private Consumer consumer;

    @Before
    public void initialize() {
        // 获取KafkaAdminClient
        adminClient = KafkaAdminClientManager.getKafkaAdminClient(brokerList);
        consumer = KafkaAdminClientManager.createConsumer(brokerList);
    }

    @Test
    public void queryConsumerGroupAndTopic() throws ExecutionException, InterruptedException {

        // 消费组信息
        Collection<String> groups =
            adminClient.listConsumerGroups().all().get().stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());
        System.out.println(groups);

        // 消费组信息
        Map<String, ConsumerGroupDescription> consumerGroupDescriptionMap = adminClient.describeConsumerGroups(groups).all().get();
        // System.out.println(consumerGroupDescriptionMap);

        List<Map<String, List<TopicPartitionDetail>>> offsetsMapList = groups.stream().map(groupId -> {
                // 消费组的消费进度信息
                Map<String, List<TopicPartitionDetail>> map = new HashMap<>();
                List<TopicPartitionDetail> list = new ArrayList<>();
                try {
                    Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap =
                        adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();

                    list = offsetAndMetadataMap.entrySet().stream().map(entry -> {
                            TopicPartitionDetail topicPartitionDetail = new TopicPartitionDetail();
                            topicPartitionDetail.setTopic(entry.getKey().topic());
                            topicPartitionDetail.setPartition(entry.getKey().partition());
                            topicPartitionDetail.setOffset(entry.getValue().offset());
                            return topicPartitionDetail;
                        })//
                        .collect(Collectors.toList());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                map.put(groupId, list);

                return map;

            })//
            .filter(offsetsMap -> null != offsetsMap && offsetsMap.entrySet() != null)//
            .collect(Collectors.toList());

        Map<String, List<TopicPartitionDetail>> result = offsetsMapList.stream().map(map -> map.entrySet())//
            .flatMap(Collection::stream)//
            .collect(Collectors.toMap(//
                Map.Entry::getKey,//
                Map.Entry::getValue,//
                (value1, value2) -> value1));

        System.out.println(result);

    }

    @Test
    public void queryConsumerGroup() throws ExecutionException, InterruptedException {

        // 消费组信息
        Collection<ConsumerGroupListing> groups = adminClient.listConsumerGroups().all().get();
        System.out.println(groups);

    }

    @Test
    public void queryConsumerGroupDetail() throws ExecutionException, InterruptedException {

        String groupId = "legion-object-direct-1";

        // 消费组信息
        Map<String, ConsumerGroupDescription> consumerGroupDescriptionMap =
            adminClient.describeConsumerGroups(Collections.singleton(groupId)).all().get();
        System.out.println(consumerGroupDescriptionMap);

        // 消费组的消费进度信息
        Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap =
            adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();
        System.out.println(offsetAndMetadataMap);

        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(new TopicPartition("bkk.item.tradetgt.count", 0), new OffsetAndMetadata(2000));
        AlterConsumerGroupOffsetsResult result = adminClient.alterConsumerGroupOffsets(groupId, offsets);
        result.all().get();


    }

    @Test
    public void listAllConsumerGroups() throws IOException, ExecutionException, InterruptedException {

        // 消费者的信息、主题等、消费速率、拉取消息配置策略等
        // 具体某个消费者的操作：暂停消费、开始消费、消费重置、重置到最新、回溯消费
        // 消费的offset信息

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        String topic = "lengfeng.test3.test";

        // 使用消费者对象订阅这些主题
        // consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));
        // consumer.poll(1000);

        // 删除消费组
        /*adminClient.deleteConsumerGroups(Collections.singleton("lengfeng.consumer.group")).all().whenComplete(
            (Void,throwable)->{
                if (null != throwable) {
                    System.out.println("删除消费者Exception" + throwable.getMessage());
                    try {
                        throw throwable;
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    return;
                }
                System.out.println("删除消费者成功");
            }
        );*/

        // adminClient.deleteConsumerGroupOffsets("lengfeng.consumer.group",Collections.singleton(new TopicPartition(topic,0))).all().get();

        ListConsumerGroupsResult consumerGroupsResult = adminClient.listConsumerGroups();

        consumerGroupsResult.all().thenApply(consumerGroupList -> {
            consumerGroupList.stream().forEach(group -> {
                System.out.println("groupId=" + group.groupId());

                // 消费组信息
                /*Map<String, ConsumerGroupDescription> consumerGroupDescriptionMap = null;
                try {
                    consumerGroupDescriptionMap = adminClient.describeConsumerGroups(Collections.singleton(group.groupId())).all().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println(consumerGroupDescriptionMap);*/

                // 消费组的消费进度信息
                ListConsumerGroupOffsetsResult consumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(group.groupId());

                consumerGroupOffsetsResult.partitionsToOffsetAndMetadata().whenComplete((topicPartitionOffsetMap, throwable) -> {
                    if (null != throwable) {
                        System.out.println("Exception" + throwable.getMessage());
                        return;
                    }

                    topicPartitionOffsetMap.forEach((topicPartition, offsetMetadata) -> {
                        System.out.printf("group=%s, topic = %s, partition = %d, offset = %d%n", group.groupId(), topicPartition.topic(),
                            topicPartition.partition(), offsetMetadata.offset());

                        Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(Collections.singleton(topicPartition));
                        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(Collections.singleton(topicPartition));
                        System.out.println(
                            "topicPartition=" + topicPartition + ",beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);

                    });
                });

            });
            return null;
        });

        //        adminClient.alterConsumerGroupOffsets();
        //        adminClient.deleteConsumerGroupOffsets();

        //        adminClient.listConsumerGroups();
        //        adminClient.deleteConsumerGroups();
        //        adminClient.describeConsumerGroups();

        //        adminClient.removeMembersFromConsumerGroup();

        System.in.read();
    }

    @Test
    public void testConsumerGroup() throws ExecutionException, InterruptedException {

        // 消费者的信息、主题等、消费速率、拉取消息配置策略等
        // 具体某个消费者的操作：暂停消费、开始消费、消费重置、重置到最新、回溯消费
        // 消费的offset信息

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        String groupId = "legion-object";

        // 消费组信息
        Map<String, ConsumerGroupDescription> consumerGroupDescriptionMap =
            adminClient.describeConsumerGroups(Collections.singleton(groupId)).all().get();
        System.out.println(consumerGroupDescriptionMap);

        // 消费组的消费进度信息
        Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap =
            adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();
        System.out.println(offsetAndMetadataMap);

        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        offsets.put(new TopicPartition("lengfeng.test4.test", 0), new OffsetAndMetadata(2000));
        // AlterConsumerGroupOffsetsResult result = adminClient.alterConsumerGroupOffsets(groupId, offsets);
        // result.all().get();

        long startTime = System.currentTimeMillis();
        boolean running = false;
        while (running) {
            // 消费组的消费进度信息
            Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap2 =
                adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata().get();
            System.out.println(offsetAndMetadataMap2);

            System.out.println("time=" + (System.currentTimeMillis() - startTime));
            TimeUnit.MILLISECONDS.sleep(10);
        }

        // adminClient.listConsumerGroups();
        // adminClient.deleteConsumerGroups();
        // adminClient.describeConsumerGroups();

        // adminClient.listConsumerGroupOffsets
        // adminClient.alterConsumerGroupOffsets();
        // adminClient.deleteConsumerGroupOffsets();

        // adminClient.removeMembersFromConsumerGroup();

    }

    @Data
    public class TopicPartitionDetail {
        private String topic;
        private int partition;
        private Long offset;
    }

}
