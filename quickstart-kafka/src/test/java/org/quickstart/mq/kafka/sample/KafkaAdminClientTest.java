package org.quickstart.mq.kafka.sample;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AlterUserScramCredentialsResult;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.ScramCredentialInfo;
import org.apache.kafka.clients.admin.ScramMechanism;
import org.apache.kafka.clients.admin.UserScramCredentialAlteration;
import org.apache.kafka.clients.admin.UserScramCredentialUpsertion;
import org.apache.kafka.clients.admin.UserScramCredentialsDescription;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AccessControlEntryFilter;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourcePatternFilter;
import org.apache.kafka.common.resource.ResourceType;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KafkaAdminClientTest {

    private static final String brokerList = "localhost:9092";
    // private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";
    private static final String username = "admin";
    private static final String password = "admin";

    @Test
    public void broker() throws ExecutionException, InterruptedException {

        // broker的信息等

        // 消息的查询等

        // 生产者的信息，主题、发送速率等
        // 具体某个生产者的操作等

        // 获取KafkaAdminClient
        Admin adminClient = KafkaAdminClientManager.getKafkaAdminClient(brokerList);
        // KafkaAdminClient adminClient = KafkaAdminClientManager.createAdminClientWithScram(brokerList, username, password);

        DescribeClusterResult clusterResult = adminClient.describeCluster();

        ConfigResource resource = new ConfigResource(Type.BROKER, "0");
        Map<ConfigResource, Config> configMap = adminClient.describeConfigs(Collections.singleton(resource)).all().get();
        System.out.println(configMap);

        Map<ConfigResource, Config> topicConfigMap =
            adminClient.describeConfigs(Collections.singleton(new ConfigResource(Type.TOPIC, "lengfeng.test3.test"))).all().get();
        System.out.println(topicConfigMap);

        Map<ConfigResource, Config> brokerLoggerConfigMap =
            adminClient.describeConfigs(Collections.singleton(new ConfigResource(Type.BROKER_LOGGER, "0"))).all().get();
        System.out.println(brokerLoggerConfigMap);

        System.out.println("sss");

        // adminClient.describeFeatures();
        // adminClient.describeLogDirs()
        // adminClient.describeReplicaLogDirs()

    }

    @Test
    public void testScramCredential() throws IOException, ExecutionException, InterruptedException {

        // ACL控制和Token控制等

        // 获取KafkaAdminClient
        // KafkaAdminClient adminClient = createAdminClient();
        Admin adminClient = KafkaAdminClientManager.createAdminClientWithScram(brokerList, username, password);

        String username = "testuser";
        String password = "testpwd";

        ScramCredentialInfo credentialInfo = new ScramCredentialInfo(ScramMechanism.SCRAM_SHA_256, 4096);
        UserScramCredentialAlteration userScram = new UserScramCredentialUpsertion(username, credentialInfo, password);

        AlterUserScramCredentialsResult alterUserScramCredentialsResult = adminClient.alterUserScramCredentials(Collections.singletonList(userScram));

        // 阻塞直到成功
        alterUserScramCredentialsResult.all().get();

        List<String> users = alterUserScramCredentialsResult.values().keySet().stream().collect(Collectors.toList());

        Map<String, UserScramCredentialsDescription> userScramCredentialsDescriptionMap = adminClient.describeUserScramCredentials(users).all().get();
        System.out.println(userScramCredentialsDescriptionMap);

    }

    @Test
    public void testAcl() throws IOException, ExecutionException, InterruptedException {

        // ACL控制和Token控制等

        // 获取KafkaAdminClient

        // 只能使用权限认证过的客户端才能操作，所以部署的时候，只能受用脚本手动创建一个admin账户，配置了，然后才能使用API
        // KafkaAdminClient adminClient = KafkaAdminClientManager.createAdminClient(brokerList);
        Admin adminClient = KafkaAdminClientManager.createAdminClientWithScram(brokerList, username, password);

        String username = "testuser";

        String topic = "quickstart-events";
        ResourcePatternFilter patternFilter = new ResourcePatternFilter(ResourceType.TOPIC, topic, PatternType.LITERAL);
        AccessControlEntryFilter entryFilter = AccessControlEntryFilter.ANY;
        AclBindingFilter aclBindingFilter = new AclBindingFilter(patternFilter, entryFilter);

        Collection<AclBinding> aclBindings = adminClient.describeAcls(aclBindingFilter).values().get();
        System.out.println("----------------------------------------");
        aclBindings.forEach(System.out::println);

        String group = "test-group";
        patternFilter = new ResourcePatternFilter(ResourceType.GROUP, group, PatternType.LITERAL);
        aclBindingFilter = new AclBindingFilter(patternFilter, AccessControlEntryFilter.ANY);

        aclBindings = adminClient.describeAcls(aclBindingFilter).values().get();
        System.out.println("----------------------------------------");
        aclBindings.forEach(System.out::println);

        ResourcePattern pattern = new ResourcePattern(ResourceType.TOPIC, topic, PatternType.LITERAL);
        AccessControlEntry entry = new AccessControlEntry("User:" + username, "*", AclOperation.CREATE, AclPermissionType.ALLOW);
        AclBinding createAcl = new AclBinding(pattern, entry);

        ResourcePattern pattern2 = new ResourcePattern(ResourceType.GROUP, group, PatternType.LITERAL);
        AccessControlEntry entry2 = new AccessControlEntry("User:" + username, "*", AclOperation.READ, AclPermissionType.ALLOW);
        AclBinding readAcl = new AclBinding(pattern2, entry2);

        Collection<AclBinding> acls = new ArrayList<>();
        acls.add(createAcl);
        acls.add(readAcl);
        CreateAclsResult aclsResult = adminClient.createAcls(acls);
        aclsResult.all().get();

        System.out.println("----------------------------------------");
        aclsResult.values().keySet().forEach(System.out::println);

        topic = "quickstart-events";
        patternFilter = new ResourcePatternFilter(ResourceType.TOPIC, topic, PatternType.LITERAL);
        aclBindingFilter = new AclBindingFilter(patternFilter, AccessControlEntryFilter.ANY);

        aclBindings = adminClient.describeAcls(aclBindingFilter).values().get();
        System.out.println("----------------------------------------");
        aclBindings.forEach(System.out::println);

        group = "test-group";
        patternFilter = new ResourcePatternFilter(ResourceType.GROUP, group, PatternType.LITERAL);
        aclBindingFilter = new AclBindingFilter(patternFilter, AccessControlEntryFilter.ANY);

        aclBindings = adminClient.describeAcls(aclBindingFilter).values().get();
        System.out.println("----------------------------------------");
        aclBindings.forEach(System.out::println);

    }

}
