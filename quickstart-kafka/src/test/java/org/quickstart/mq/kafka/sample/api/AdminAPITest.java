package org.quickstart.mq.kafka.sample.api;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AdminAPITest {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    Properties props = new Properties();
    Admin adminClient = null;

    @Before
    public void setup() {

        // 配置kafka的服务连接
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        props.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 10000);

        // KafkaUtils.getTopicNames(zkAddress)

        // 创建KafkaAdminClient
        // Admin adminClient2 = Admin.create(props);
        // adminClient = (KafkaAdminClient)KafkaAdminClient.create(props);
        adminClient = Admin.create(props);
    }

    @Test
    public void testTopic() throws ExecutionException, InterruptedException {
        CreateTopicsResult createTopicsResult = adminClient.createTopics(Arrays.asList(//
            new NewTopic("topic01", 1, (short)1),//
            new NewTopic("topic2", 1, (short)1),//
            new NewTopic("topic3", 1, (short)1)//
        ));

        // 阻塞直到完成
        // createTopicsResult.all().get();

        ListTopicsResult listTopicsResult = adminClient.listTopics();
        listTopicsResult.listings().get().forEach(System.out::println);

        Map newPartitions = new HashMap<>();
        // 增加到2个
        newPartitions.put("topic1", NewPartitions.increaseTo(2));
        CreatePartitionsResult rs = adminClient.createPartitions(newPartitions);
        rs.all().get();

        listTopicsResult = adminClient.listTopics();
        listTopicsResult.listings().get().forEach(System.out::println);

    }

}
