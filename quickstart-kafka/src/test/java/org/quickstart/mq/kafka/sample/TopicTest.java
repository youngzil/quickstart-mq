package org.quickstart.mq.kafka.sample;

import kafka.admin.TopicCommand;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
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
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
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
    // private static final String brokerList = "172.16.48.182:9011,172.16.48.182:9012,172.16.48.183:9011";
    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    // private static final String brokerList = "172.16.49.125:9092,172.16.49.131:9092,172.16.49.133:9092";
    // private static final String brokerList = "172.16.112.232:9092,172.16.112.232:9093,172.16.112.232:9094";
    // private static final String brokerList = "172.16.49.6:9093,172.16.49.12:9093,172.16.49.10:9093";
    // private static final String brokerList = "127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094";
    private Admin adminClient;
    private Consumer consumer;

    @Before
    public void initialize() {
        // 获取KafkaAdminClient
        adminClient = KafkaAdminClientManager.getKafkaAdminClient(brokerList);
        consumer = KafkaAdminClientManager.createConsumer(brokerList);
    }

    @Test
    public void queryTopic() throws ExecutionException, InterruptedException {

        // 查看topic列表
        //是否查看Internal选项
        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(true);

        //ListTopicsResult listTopicsResult = adminClient.listTopics();
        ListTopicsResult listTopicsResult = adminClient.listTopics(options);
        Set<String> names = listTopicsResult.names().get();

        //打印names
        names.stream().forEach(System.out::println);

        String keyword = "test";
        Set<String> filterNames = names.stream().filter(topicName -> topicName.contains(keyword)).collect(Collectors.toSet());
        System.out.println("filterNames=" + filterNames);

        Collection<TopicListing> topicListings = listTopicsResult.listings().get();
        //打印TopicListing
        topicListings.stream().forEach((topicList) -> {
            System.out.println(topicList.toString());
        });

        //查看topic列表
        Set<String> topics = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(topics).all().get();

        List<TopicDetail> topicList = topicDescriptionMap.values().stream()//
            .map(topicDescription -> {
                TopicDetail topicDetail = new TopicDetail();
                topicDetail.setTopic(topicDescription.name());
                topicDetail.setPartitions(topicDescription.partitions().size());
                topicDetail.setReplicas(topicDescription.partitions().get(0).replicas().size());
                return topicDetail;
            })//
            .collect(Collectors.toList());


       /* topicList.stream().forEach(topicDetail -> {
            //创建topic
            CreateTopicsResult topicsResult = adminClient.createTopics(
                Arrays.asList(new NewTopic(topicDetail.getTopic(), topicDetail.getPartitions(), (short)topicDetail.getReplicas())));//(名称，分区数，副本因子)
            try {
                topicsResult.all().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });*/
    }

    @Test
    public void queryTopicPartitionOffset() throws ExecutionException, InterruptedException {

        Set<String> topics = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(topics).all().get();

        List<TopicPartition> topicPartitionList = topicDescriptionMap.values().stream()
            // .map(topicDescription -> topicDescription.partitions())
            .map(topicDescription -> {
                String topic = topicDescription.name();
                return topicDescription.partitions().stream().map(topicPartitionInfo -> new TopicPartition(topic, topicPartitionInfo.partition()))
                    .collect(Collectors.toList());

            })//
            .flatMap(Collection::stream)//
            .collect(Collectors.toList());

        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = topicPartitionList.stream()//
            .collect(Collectors.toMap(topicPartition -> topicPartition, topicPartition -> OffsetSpec.earliest(), (k1, k2) -> k1));

        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> offsetsResultInfoMap =
            adminClient.listOffsets(topicPartitionOffsets).all().get();

        Map<TopicPartition, TopicPartitionDetail> offsetsMap =
            offsetsResultInfoMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
                TopicPartition topicPartition = e.getKey();
                TopicPartitionDetail topicPartitionDetail = new TopicPartitionDetail();
                topicPartitionDetail.setTopic(topicPartition.topic());
                topicPartitionDetail.setPartition(topicPartition.partition());
                topicPartitionDetail.setBeginningOffset(e.getValue().offset());
                return topicPartitionDetail;
            }));

        topicPartitionOffsets = topicPartitionList.stream()//
            .collect(Collectors.toMap(topicPartition -> topicPartition, topicPartition -> OffsetSpec.latest(), (k1, k2) -> k1));

        offsetsResultInfoMap = adminClient.listOffsets(topicPartitionOffsets).all().get();
        offsetsResultInfoMap.entrySet().stream().forEach(entry -> {
            TopicPartitionDetail topicPartitionDetail = offsetsMap.get(entry.getKey());
            topicPartitionDetail.setEndOffset(entry.getValue().offset());
        });

        System.out.println(offsetsMap);

    }

    // 获取某个Topic的所有分区以及分区最新的Offset
    @Test
    public void queryTopicPartitionOffset2() throws IOException {

        // 脚本方式获取
        // ./bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic test

        String topic = "lengfeng.direct.test";

        Collection<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        System.out.println("Get the partition info as below:");

        Set<TopicPartition> tps = partitionInfos.stream().map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
            .collect(Collectors.toSet());
        consumer.assign(tps);

        // 方法二
        Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(tps);
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(tps);
        System.out.println("beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);

        // 方法三
        consumer.seekToBeginning(tps);
        beginningOffsets = tps.stream()
            .collect(Collectors.toMap(topicPartition -> topicPartition, topicPartition -> consumer.position(topicPartition), (k1, k2) -> k1));

        consumer.seekToEnd(tps);
        endOffsets = tps.stream()
            .collect(Collectors.toMap(topicPartition -> topicPartition, topicPartition -> consumer.position(topicPartition), (k1, k2) -> k1));
        System.out.println("beginningOffsets=" + beginningOffsets + ",endOffsets=" + endOffsets);

        System.in.read();
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

        Map<TopicPartition, TopicPartitionDetail> topicDetailMap =
            Stream.of(beginningOffsets, endOffsets).flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(//
                Map.Entry::getKey,//
                value -> {
                    TopicPartitionDetail topicDetail = new TopicPartitionDetail();
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

    @Test
    public void createTopic() throws ExecutionException, InterruptedException {

        String topic = "test";
        int partitions = 5;
        short replicas = 3;

        // 创建topic
        CreateTopicsResult topicsResult = adminClient.createTopics(Arrays.asList(new NewTopic(topic, partitions, replicas), // (名称，分区数，副本因子)
            new NewTopic("topic02", 2, (short)2), new NewTopic("topic03", 6, (short)3)));

        //创建topic
        // CreateTopicsResult topicsResult = adminClient.createTopics(Arrays.asList(new NewTopic(topic, partitions, replicas)));//(名称，分区数，副本因子)
        topicsResult.all().get();

    }

    /**
     * 增加partitions数量
     *
     * @throws Exception
     */
    // @Test
    public void incrPartitions() throws Exception {

        int partitions = 10;
        String topic = "topic01";

        Map<String, NewPartitions> partitionsMap = new HashMap<>();
        NewPartitions newPartitions = NewPartitions.increaseTo(partitions);
        partitionsMap.put(topic, newPartitions);
        CreatePartitionsResult partitionsResult = adminClient.createPartitions(partitionsMap);
        partitionsResult.all().get();
    }

    @Test
    public void testTopicDetail() throws ExecutionException, InterruptedException {

        //查看topic列表
        Set<String> topics = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(topics).all().get();

        List<TopicDetail> topicList = topicDescriptionMap.values().stream()//
            .map(topicDescription -> {
                TopicDetail topicDetail = new TopicDetail();
                topicDetail.setTopic(topicDescription.name());
                topicDetail.setPartitions(topicDescription.partitions().size());
                topicDetail.setReplicas(topicDescription.partitions().get(0).replicas().size());
                return topicDetail;
            })//
            .collect(Collectors.toList());
    }

    @Test
    public void testTopicCommand() {

        String[] options = new String[] {//
            "--zookeeper", "172.16.48.183:2181/kfk1_1",
            // "--bootstrap-server", "172.16.48.182:9011,172.16.48.182:9012,172.16.48.183:9011",//
            "--alter",//
            "--replication-factor", "3",//
            "--partitions", "1",//
            "--topic", "db.192_168_5_14_3319_wac_trinity.position"//
        };
        kafka.admin.TopicCommand.main(options);

    }

    @Test
    public void queryReplicasEqualOne() throws ExecutionException, InterruptedException {

        //查看topic列表
        Set<String> topics = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(topics).all().get();

        List<TopicPartitionInfo> topicPartitionInfos = topicDescriptionMap.values().stream()//
            .map(topicDescription -> topicDescription.partitions())//
            .flatMap(Collection::stream)//
            .filter(topicPartitionInfo -> 1 == topicPartitionInfo.replicas().size())//副本数为1的
            .collect(Collectors.toList());

        Map<String, List<TopicPartitionInfo>> topicPartitionsMap = topicDescriptionMap.values().stream()//
            .collect(Collectors.toMap(TopicDescription::name, topicDescription -> topicDescription.partitions().stream()//
                .filter(topicPartitionInfo -> 1 == topicPartitionInfo.replicas().size())//副本数为1的
                .collect(Collectors.toList())//
            ));

        // System.out.println(topicPartitionsMap);

        // TopicCommand.TopicCommandOptions topicCommandOptions =
        //     new TopicCommand.TopicCommandOptions(new String[] {"--alter", "--topic", "db.wac_loan_account.loan_app_contract", "--partitions", "2" ,"--replication-factor", "3"});

        // TopicCommand.AdminClientTopicService adminClientTopicService = new TopicCommand.AdminClientTopicService(adminClient);

        // Topic修改
        // adminClientTopicService.alterTopic(topicCommandOptions);

        /*String[] options = new String[] {//
            "--zookeeper", "172.16.48.183:2181/kfk1_1",
            // "--bootstrap-server", "172.16.48.182:9011,172.16.48.182:9012,172.16.48.183:9011",//
            "--alter",//
            "--replication-factor", "3",//
            "--partitions", "1",//
            "--topic", "db.192_168_5_14_3319_wac_trinity.position"//
        };
        kafka.admin.TopicCommand.main(options);*/

        System.out.println(topicPartitionInfos);
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

            })//
            .flatMap(Collection::stream)//
            .collect(Collectors.toMap(topicPartition -> topicPartition, key -> new OffsetSpec()));

        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> ffff = adminClient.listOffsets(topicPartitionOffsets).all().get();
        System.out.println(ffff);

        Map<TopicPartition, PartitionReassignment> partitionPartitionReassignmentMap =
            adminClient.listPartitionReassignments(Collections.singleton(new TopicPartition("topic03", 0)))//
                .reassignments().get();
        System.out.println(partitionPartitionReassignmentMap);

    }

    @Test
    public void topicDescribeConfig() throws ExecutionException, InterruptedException {

        String topic = "topic01";
        //TODO 这里做一个预留，集群时会讲到
        //ConfigResource configResource = new ConfigResource(ConfigResource.Type.BROKER,TOPIC_NAME);

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
        Map<ConfigResource, Config> resourceConfigMap = describeConfigsResult.all().get();
        resourceConfigMap.forEach((key, value) -> System.out.println(key + " " + value));
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
        // This operation is supported by brokers with version 2.3.0 or higher.
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

        Properties props = getTopicProperties(topic);

        Map<ConfigResource, Config> configMap = new HashMap<>();
        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        Config config = new Config(Arrays.asList(new ConfigEntry("preallocate", "false")));
        configMap.put(configResource, config);
        // This operation is supported by brokers with version 0.11.0.0 or higher.
        AlterConfigsResult alterConfigsResult = adminClient.alterConfigs(configMap);
        alterConfigsResult.all().get();

        Properties props2 = getTopicProperties(topic);
        System.out.println(props2);
    }

    @Test
    public void alterTopicConfigs() {

        String topic = "topic01";

        Properties props = getTopicProperties(topic);

        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("preallocate", "true");

        List<String> params = new ArrayList<>();
        params.add("--alert");
        params.add("--topic");
        params.add(topic);
        for (Map.Entry entry : paraMap.entrySet()) {
            params.add("--config");
            params.add(entry.getKey() + "=" + entry.getValue());
        }
        String[] commands = new String[params.size()];
        params.toArray(commands);

        // 这个是错误的，要改成ConfigCommand来修改
        // bin/kafka-configs.sh --alter --topic topic03 --add-config max.message.bytes=20480000 --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094

        TopicCommand.TopicCommandOptions topicCommandOptions = new TopicCommand.TopicCommandOptions(commands);
        TopicCommand.TopicService adminClientTopicService = new TopicCommand.TopicService(adminClient);
        adminClientTopicService.alterTopic(topicCommandOptions);

        Properties props2 = getTopicProperties(topic);
        System.out.println(props2);
    }

    @Test
    public void kafkaConfigs() {

        // ProducerConfig.configDef().defaultValues()
        // ProducerConfig.configDef().groups()
        // ProducerConfig.configDef().names()

        // ProducerConfig.configDef().validate()
        // ProducerConfig.configDef().validateAll()

        Set<String> producerConfigKeys1 = ProducerConfig.configNames();
        Set<String> consumerConfigKeys1 = ConsumerConfig.configNames();
        Set<String> adminClientConfigKeys1 = AdminClientConfig.configNames();

        Map<String, Object> map = ProducerConfig.configDef().defaultValues();
        List<String> groups = ProducerConfig.configDef().groups();
        Set<String> names = ProducerConfig.configDef().names();

        Field[] fields = TopicConfig.class.getDeclaredFields();
        Field[] fields2 = ProducerConfig.class.getDeclaredFields();
        Field[] fields3 = ConsumerConfig.class.getDeclaredFields();
        Field[] fields4 = AdminClientConfig.class.getDeclaredFields();

        // 35
        Set<String> producerConfigKeys = Arrays.stream(fields2).filter(field -> field.getName().endsWith("_CONFIG")).map(field -> {
            try {
                return field.get(TopicConfig.class).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).filter(str -> !"".equals(str)).collect(Collectors.toSet());

        // 41
        Set<String> consumerConfigKeys = Arrays.stream(fields3).filter(field -> field.getName().endsWith("_CONFIG")).map(field -> {
            try {
                return field.get(TopicConfig.class).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).filter(str -> !"".equals(str)).collect(Collectors.toSet());

        // 21
        Set<String> adminClientConfigKeys = Arrays.stream(fields4).filter(field -> field.getName().endsWith("_CONFIG")).map(field -> {
            try {
                return field.get(TopicConfig.class).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).filter(str -> !"".equals(str)).collect(Collectors.toSet());

        // 27
        Set<String> topicConfigKeys = Arrays.stream(fields).filter(field -> field.getName().endsWith("_CONFIG")).map(field -> {
            try {
                return field.get(TopicConfig.class).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).filter(str -> !"".equals(str)).collect(Collectors.toSet());

    }

    @Test
    public void getTopicProperties() {
        String topic = "topic01";
        Properties props = getTopicProperties(topic);
    }

    @Test
    public void getBrokerProperties() throws ExecutionException, InterruptedException {

        ConfigResource resource = new ConfigResource(ConfigResource.Type.BROKER, "1");
        DescribeConfigsResult result = adminClient.describeConfigs(Collections.singleton(resource));

        Map<ConfigResource, Config> map = result.all().get();

        Properties props = new Properties();
        Config config = result.all().get().get(resource);

        props = config.entries().stream()//
            .filter(configEntry -> DYNAMIC_TOPIC_CONFIG == configEntry.source())//
            .collect(Properties::new, (m, v) -> m.put(v.name(), v.value()), Properties::putAll);


    }

    public Properties getTopicProperties(String topic) {

        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topic);
        DescribeConfigsResult result = adminClient.describeConfigs(Collections.singleton(resource));

        Properties props = new Properties();
        try {
            Config config = result.all().get().get(resource);

            props = config.entries().stream()//
                // .filter(configEntry -> DYNAMIC_TOPIC_CONFIG == configEntry.source())//
                .collect(Properties::new, (m, v) -> m.put(v.name(), v.value()), Properties::putAll);
        } catch (InterruptedException | ExecutionException e) {
            log.warn("获取topic属性异常,topic={}", topic);
        }

        return props;
    }

    @Test
    public void topicCRUD() throws ExecutionException, InterruptedException {

        // 查看topic列表
        ListTopicsResult topicsResult = adminClient.listTopics();
        Set<String> names = topicsResult.names().get();
        names.stream().forEach(name -> System.out.println(name));

        // 查看topic详细信息：主题的属性、主题的partition分区信息
        // name、partitions（partition、leader、replicas、isr）、authorizedOperations、internal
        DescribeTopicsResult topic = adminClient.describeTopics(Arrays.asList("test.add.topic"));
        Map<String, TopicDescription> map = topic.all().get();
        System.out.println("TopicDescription:" + map.get("test.add.topic"));

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
    public class TopicPartitionDetail {
        private String topic;
        private int partition;
        private Long beginningOffset;
        private Long endOffset;
        private Long lag;
    }

    @Data
    public class TopicDetail {
        private String topic;
        private int replicas;
        private int partitions;
    }

}
