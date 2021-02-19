package org.quickstart.mq.kafka.sample;

import kafka.admin.TopicCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static org.apache.kafka.clients.admin.ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG;

/**
 * @Title TDOD
 * @Description: TODO
 * @Author yuren
 * @Date 2018/5/25 上午10:53
 * @Version V2.0.0
 */
@Slf4j
public class TopicUtil {

    private static ConcurrentMap<String/*brokerList*/, KafkaAdminClient> adminClientMap = new ConcurrentHashMap<>();

    public static KafkaAdminClient createAdminClient(String brokerList) {

        KafkaAdminClient adminClient = adminClientMap.get(brokerList);
        if (null == adminClient) {

            Properties props = new Properties();
            //配置kafka的服务连接
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
            props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);

            //KafkaUtils.getTopicNames(zkAddress)

            //创建KafkaAdminClient
            KafkaAdminClient newAdminClient = (KafkaAdminClient)KafkaAdminClient.create(props);
            KafkaAdminClient oldAdminClient = adminClientMap.putIfAbsent(brokerList, newAdminClient);
            if (null != oldAdminClient) {
                newAdminClient.close();
            }
        }

        return adminClientMap.get(brokerList);
    }

    public static Set<String> listTopics(String brokerList) {

        Set<String> topics = new HashSet<>();
        try {
            //获取KafkaAdminClient
            KafkaAdminClient adminClient = createAdminClient(brokerList);

            //查看topic列表
            ListTopicsResult topicsResult = adminClient.listTopics();
            topics = topicsResult.names().get();

        } catch (InterruptedException | ExecutionException e) {
            log.warn("查询topic异常,brokerList={}", brokerList);
        }

        return topics;

    }

    public static boolean createTopic(String brokerList, String topic, int partitions, int replicas) {

        //获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient(brokerList);

        //创建topic
        CreateTopicsResult topicsResult =
            adminClient.createTopics(Arrays.asList(new NewTopic(topic, partitions, (short)replicas)));//(名称，分区数，副本因子)
        try {
            topicsResult.all().get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            log.warn("创建topic异常,topic={}", topic);
            return false;
        }

    }

    public static boolean alterTopicPartition(String brokerList, String topic, int partitions) {

        //获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient(brokerList);

        TopicCommand.TopicCommandOptions topicCommandOptions = new TopicCommand.TopicCommandOptions(
            new String[] {"--alter", "--topic", topic, "--partitions", "" + partitions});

        TopicCommand.AdminClientTopicService adminClientTopicService =
            new TopicCommand.AdminClientTopicService(adminClient);

        adminClientTopicService.alterTopic(topicCommandOptions);

        return true;
    }

    public static void alterTopicConfigs(String brokerList, String topic, Map<String, String> paraMap) {
        //获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient(brokerList);

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

        TopicCommand.AdminClientTopicService adminClientTopicService =
            new TopicCommand.AdminClientTopicService(adminClient);

        adminClientTopicService.alterTopic(topicCommandOptions);
    }

    public static Properties getTopicProperties(String brokerList, String topic) {
        //获取KafkaAdminClient
        KafkaAdminClient adminClient = createAdminClient(brokerList);

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

    public static void main(String[] args) {

        String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
        System.out.println(getTopicProperties(brokerList, "test"));
    }

}
