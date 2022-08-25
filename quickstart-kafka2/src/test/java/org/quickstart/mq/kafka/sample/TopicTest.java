package org.quickstart.mq.kafka.sample;

import kafka.admin.TopicCommand;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.config.ConfigResource;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TopicTest {

    // private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.182:9011,172.16.48.182:9012,172.16.48.183:9011";
    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    // private static final String brokerList = "172.16.49.6:9093,172.16.49.12:9093,172.16.49.10:9093";

    private Admin adminClient;

    public static Admin createAdminClient(String brokerList) {

        Properties props = new Properties();
        // 配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);

        // 创建KafkaAdminClient
        Admin adminClient = Admin.create(props);
        return adminClient;
    }

    @Before
    public void initialize() {
        // 获取KafkaAdminClient
        adminClient = createAdminClient(brokerList);
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
    public void alterTopicConfigs() {

        String topic = "topic01";
        Map<String, String> paraMap = new HashMap<>();

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
        TopicCommand.AdminClientTopicService adminClientTopicService = new TopicCommand.AdminClientTopicService(adminClient);
        adminClientTopicService.alterTopic(topicCommandOptions);
    }

}
