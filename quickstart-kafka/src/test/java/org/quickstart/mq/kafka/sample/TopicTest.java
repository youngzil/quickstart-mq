package org.quickstart.mq.kafka.sample;

import kafka.admin.TopicCommand;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.AlterConfigsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.PartitionReassignment;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.kafka.clients.admin.ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG;

@Slf4j
public class TopicTest {

    // private static final String brokerList = "localhost:9092";
    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    private Admin adminClient;
    private Consumer consumer;

    @Before
    public void initialize() {
        // 获取KafkaAdminClient
        adminClient = KafkaAdminClientManager.getKafkaAdminClient(brokerList);
        consumer = KafkaAdminClientManager.createConsumer(brokerList);
    }

    @Test
    public void queryAllTopic() throws ExecutionException, InterruptedException {

        //查看topic列表
        Set<String> topics = adminClient.listTopics().names().get();
        System.out.println(topics);
    }

    @Test
    public void queryTopicDetail() throws ExecutionException, InterruptedException {

        // String topic = "lengfeng.topic.test";
        String topic = "druid.service.metrics";

        Map<String, List<PartitionInfo>> listMap = consumer.listTopics();
        System.out.println(listMap);

        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        List<TopicPartition> topicPartitions =
            partitionInfos.stream().map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
                .collect(Collectors.toList());

        Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(topicPartitions);
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(Collections.singleton(topic)).all().get();

        Map<TopicPartition, TopicDetail> topicDetailMap =
            Stream.of(beginningOffsets, endOffsets).flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(//
                Map.Entry::getKey,//
                value -> {
                    TopicDetail topicDetail = new TopicDetail();
                    topicDetail.setTopic(value.getKey().topic());
                    topicDetail.setPartition(value.getKey().partition());
                    topicDetail.setBeginningOffset(value.getValue());
                    return topicDetail;
                },//
                (v1, v2) -> {
                    v1.setEndOffset(v2.getBeginningOffset());
                    v1.setLag(v1.getBeginningOffset() - v1.getEndOffset());
                    return v1;
                }));

        System.out.println(topicDetailMap);

    }

    // 获取某个Topic的所有分区以及分区最新的Offset
    @Test
    public void getPartitionsForTopic() throws IOException {

        // 脚本方式获取
        // ./bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic test

        String topic = "lengfeng.topic.test";

        Collection<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        System.out.println("Get the partition info as below:");
        List<TopicPartition> tp = new ArrayList<>();
        partitionInfos.forEach(partitionInfo -> {
            System.out.println("Partition Info:" + partitionInfo);

            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            tp.add(topicPartition);
            consumer.assign(tp);
            consumer.seekToEnd(tp);

            System.out.println(topicPartition + " 's latest offset is '" + consumer.position(topicPartition));

            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(Collections.singleton(topicPartition));
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(Collections.singleton(topicPartition));
            System.out.println("topicPartition=" + topicPartition + ",beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);

        });

        System.in.read();
    }

    @Test
    public void topicQuery() throws ExecutionException, InterruptedException {

        // 查看topic列表
        //是否查看Internal选项
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(true);

        //ListTopicsResult listTopicsResult = adminClient.listTopics();
        ListTopicsResult listTopicsResult = adminClient.listTopics(options);
        Set<String> names = listTopicsResult.names().get();

        //打印names
        names.stream().forEach(System.out::println);

        Collection<TopicListing> topicListings = listTopicsResult.listings().get();
        //打印TopicListing
        topicListings.stream().forEach((topicList) -> {
            System.out.println(topicList.toString());
        });
    }

    // @Test
    public void createTopic(String brokerList, String topic, int partitions, int replicas) throws ExecutionException, InterruptedException {

        // 创建topic
        adminClient.createTopics(Arrays.asList(new NewTopic("topic01", 2, (short)1), // (名称，分区数，副本因子)
            new NewTopic("topic02", 2, (short)2), new NewTopic("topic03", 6, (short)3)));

        //创建topic
        CreateTopicsResult topicsResult = adminClient.createTopics(Arrays.asList(new NewTopic(topic, partitions, (short)replicas)));//(名称，分区数，副本因子)
        topicsResult.all().get();

    }

    /**
     * 增加partitions数量
     *
     * @param partitions
     * @throws Exception
     */
    // @Test
    public void incrPartitions(int partitions) throws Exception {
        String topic = "topic01";

        Map<String, NewPartitions> partitionsMap = new HashMap<>();
        NewPartitions newPartitions = NewPartitions.increaseTo(partitions);
        partitionsMap.put(topic, newPartitions);
        CreatePartitionsResult partitionsResult = adminClient.createPartitions(partitionsMap);
        partitionsResult.all().get();
    }

    // @Test
    public boolean alterTopicPartition(String brokerList, String topic, int partitions) {

        TopicCommand.TopicCommandOptions topicCommandOptions =
            new TopicCommand.TopicCommandOptions(new String[] {"--alter", "--topic", topic, "--partitions", "" + partitions});

        TopicCommand.AdminClientTopicService adminClientTopicService = new TopicCommand.AdminClientTopicService(adminClient);

        // Topic修改
        adminClientTopicService.alterTopic(topicCommandOptions);

        return true;
    }

    @Test
    public void topicDescribeConfig() throws ExecutionException, InterruptedException {

        String topic = "topic01";
        //TODO 这里做一个预留，集群时会讲到
        //ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER,TOPIC_NAME);

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
        Map<ConfigResource, Config> resourceConfigMap = describeConfigsResult.all().get();
        resourceConfigMap.forEach((key, value) -> {
            System.out.println(key + " " + value);
        });
    }

    /**
     * 修改配置信息 新版API
     *
     * @throws Exception
     */
    @Test
    public void alterConfig() throws Exception {
        String topic = "topic01";

        Map<ConfigResource, Collection<AlterConfigOp>> configMap = new HashMap<>();
        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        AlterConfigOp alterConfigOp = new AlterConfigOp(new ConfigEntry("preallocate", "false"), AlterConfigOp.OpType.SET);
        configMap.put(configResource, Arrays.asList(alterConfigOp));
        AlterConfigsResult alterConfigsResult = adminClient.incrementalAlterConfigs(configMap);
        alterConfigsResult.all().get();
    }

    /**
     * 修改配置信息 老版API
     *
     * @throws Exception
     */
    @Test
    public void alterConfig2() throws Exception {
        String topic = "topic01";

        Map<ConfigResource, Config> configMap = new HashMap<>();
        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        Config config = new Config(Arrays.asList(new ConfigEntry("preallocate", "true")));
        configMap.put(configResource, config);
        AlterConfigsResult alterConfigsResult = adminClient.alterConfigs(configMap);
        alterConfigsResult.all().get();
    }

    // @Test
    public void alterTopicConfigs(String brokerList, String topic, Map<String, String> paraMap) {

        List<String> params = new ArrayList<>();
        params.add("--describe");
        params.add("--topic");
        params.add(topic);
        for (Map.Entry entry : paraMap.entrySet()) {
            params.add("--config");
            params.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] commands = new String[params.size()];
        params.toArray(commands);

        TopicCommand.TopicCommandOptions topicCommandOptions = new TopicCommand.TopicCommandOptions(commands);

        TopicCommand.AdminClientTopicService adminClientTopicService = new TopicCommand.AdminClientTopicService(adminClient);

        adminClientTopicService.alterTopic(topicCommandOptions);
    }

    // @Test
    public Properties getTopicProperties(String brokerList, String topic) {

        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        DescribeConfigsResult result = adminClient.describeConfigs(Collections.singleton(resource));

        Properties props = new Properties();
        try {
            Config config = result.all().get().get(resource);

            props = config.entries().stream()//
                .filter(configEntry -> DYNAMIC_TOPIC_CONFIG == configEntry.source())//
                .collect(Properties::new, (m, v) -> m.put(v.name(), v.value()), Properties::putAll);
        } catch (InterruptedException | ExecutionException e) {
            log.warn("获取topic属性异常,topic={}", topic);
        }

        return props;
    }

    @Test
    public void queryAllTopic2() throws ExecutionException, InterruptedException {

        Set<String> topics = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(topics).all().get();

        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = topicDescriptionMap.values().stream()
            // .map(topicDescription -> topicDescription.partitions())
            // .flatMap(Collection::stream)
            .map(topicDescription -> {
                String topic = topicDescription.name();
                return topicDescription.partitions().stream().map(topicPartitionInfo -> new TopicPartition(topic, topicPartitionInfo.partition()))
                    .collect(Collectors.toList());

            }).flatMap(Collection::stream).collect(Collectors.toMap(TopicPartition -> TopicPartition, value -> new OffsetSpec()));

        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> ffff = adminClient.listOffsets(topicPartitionOffsets).all().get();
        System.out.println(ffff);

        Map<TopicPartition, PartitionReassignment> partitionPartitionReassignmentMap =
            adminClient.listPartitionReassignments(Collections.singleton(new TopicPartition("topic03", 0)))//
                .reassignments().get();
        System.out.println(partitionPartitionReassignmentMap);

    }

    @Test
    public void topicCRUD() throws ExecutionException, InterruptedException {

        // 查看topic列表
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> names = topicsResult.names().get();
        names.stream().forEach(name -> System.out.println(name));

        // 查看topic详细信息：主题的属性、主题的partition分区信息
        // name、partitions（partition、leader、replicas、isr）、authorizedOperations、internal
        DescribeTopicsResult topic = adminClient.describeTopics(Arrays.asList("topic01", "topic02", "topic03"));
        Map<String, TopicDescription> map = topic.all().get();
        System.out.println("TopicDescription:" + map);

        // 删除topic
        // adminClient.deleteTopics(Arrays.asList("topic01", "topic02", "topic03"));

        // 查看topic列表
        topicsResult = adminClient.listTopics();
        names = topicsResult.names().get();
        names.stream().forEach(System.out::println);

        // adminClient.listOffsets()
        // adminClient.listPartitionReassignments()

        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = new HashMap<>();
        topicPartitionOffsets.put(new TopicPartition("lengfeng.topic.test", 0), new OffsetSpec());
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> ffff = adminClient.listOffsets(topicPartitionOffsets).all().get();
        System.out.println(ffff);

        Map<TopicPartition, PartitionReassignment> partitionPartitionReassignmentMap =
            adminClient.listPartitionReassignments(Collections.singleton(new TopicPartition("topic03", 0)))//
                .reassignments().get();
        System.out.println(partitionPartitionReassignmentMap);

        // 关闭AdminClient
        adminClient.close();
    }

    @Data
    public class TopicDetail {
        private String topic;
        private int partition;
        private Long beginningOffset;
        private Long endOffset;
        private Long lag;
    }

}
