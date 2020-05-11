参考文章：http://blog.csdn.net/suifeng3051/article/details/38321043
http://kafka.apache.org/082/documentation.html#quickstart

部署集群至少修改三个：config/server.properties
brokerid=2
port=9093
log.dir=/temp/kafka8-logs/broker2   
       
启动：
bin/zookeeper-server-start.sh config/zookeeper.properties &                 
bin/kafka-server-start.sh config/server.properties & 

nohup sh bin/zookeeper-server-start.sh config/zookeeper.properties &
nohup sh bin/kafka-server-start.sh -daemon config/server.properties &

停止：
bin/zookeeper-server-stop.sh
bin/kafka-server-stop.sh 



列出主题列表
bin/kafka-topics.sh --list --zookeeper 127.0.0.1:12181
bin/kafka-topics.sh --list --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181


查看主题详细信息
bin/kafka-topics.sh --describe --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181 --topic event


消费主题
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:12181 --topic behavior
bin/kafka-console-consumer.sh --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181 --topic alarm --from-beginning
bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:12181 --topic behavior


删除主题
bin/kafka-topics.sh --delete --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181 --topic alarm




创建topic：
#replication-factor 表示该topic需要在不同的broker中保存几份, partitions为几个分区
现在我们要创建一个含有两个Partition分区和2个备份的broker：
bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 2 --partitions 3 --topic test-topic  
bin/kafka-create-topic.sh --zookeeper localhost:2189 --replica 2 --partition 2 --topic testtopic


发送和消费测试：
bin/kafka-console-producer.sh --broker-list localhost:9092,localhost:9093 --topic othertopic                            
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic othertopic --from-beginning
bin/kafka-console-consumer.sh --zookeeper 10.1.130.138:21821,10.1.130.138:21822,10.1.130.138:21823 --topic test --from-beginning


Kafka操作命令：https://www.cnblogs.com/zcqdream/articles/6593875.html

1. Kafka下载：
wget https://archive.apache.org/dist/kafka/0.8.1/kafka_2.9.2-0.8.1.tgz
解压 tar zxvf kafka_2.9.2-0.8.1.tgz

启动zookeeper
nohup bin/zookeeper-server-start.sh config/zookeeper.properties &
或者 zkServer.sh start 启动

启动kafka
nohup bin/kafka-server-start.sh -daemon config/server.properties &

下线broker
./kafka-run-class.sh kafka.admin.ShutdownBroker --zookeeper127.0.0.1:2181--broker #brokerId# --num.retries3--retry.interval.ms60

shutdown broker

创建主题
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test 
bin/kafka-topics.sh --create --zookeeper master:2181 --replication-factor 2 --partitions 8 --topic DreamTopic 
bin/kafka-topics.sh --create --zookeeper 192.168.100.125:2181 --replication-factor 1 --partitions 1 --topic DreamTopic1
#replication-factor 表示该topic需要在不同的broker中保存几份, partitions为几个分区

创建topic–test
./bin/kafka-topics.sh --create --zookeeper 10.10.67.102:2181, 10.10.67.104:2181, 10.10.67.106:2181 --replication-factor 3 --partitions 3 --topic test


查看topic个数
查看指定的主题：　　bin/kafka-topics.sh --describe --zookeeper 10.1.243.23:52181 --topic topicTest 
　　bin/kafka-topics.sh --describe --zookeeper 192.168.100.125:2181 --topic DreamTopic1
　　bin/kafka-topics.sh --list --zookeeper master:2181,node2:2181,node1:2181
列出已创建的topic列表
./bin/kafka-topics.sh --list --zookeeper localhost:2181


为topic增加副本
./kafka-reassign-partitions.sh -zookeeper127.0.0.1:2181-reassignment-json-file json/partitions-to-move.json -execute

为topic增加partition
bin/kafka-topics.sh --zookeeper 10.1.243.23:52181 --alter --partitions 3 --topic topicTest

查看topic的生产者和消费者信息
./kafka-topics.sh --describe --zookeeper localhost:2181 --topic idoall_testTopic,DreamTopic
[root@linux-node2 bin]# ./kafka-topics.sh --describe --zookeeper localhost:2181 --topic dream
Topic:dream    PartitionCount:5    ReplicationFactor:2    Configs:
    Topic: dream    Partition: 0    Leader: 1    Replicas: 1,2    Isr: 1,2
    Topic: dream    Partition: 1    Leader: 2    Replicas: 2,3    Isr: 2,3
    Topic: dream    Partition: 2    Leader: 3    Replicas: 3,1    Isr: 3,1
    Topic: dream    Partition: 3    Leader: 1    Replicas: 1,3    Isr: 1,3
    Topic: dream    Partition: 4    Leader: 2    Replicas: 2,1    Isr: 2,1
leader:负责处理消息的读和写，leader是从所有节点中随机选择的.
Replicas:列出了所有的副本节点，不管节点是否在服务中.
Lsr:是正在服务中的节点.

生产者测试
bin/kafka-console-producer.sh --broker-list kafka1:9092 --topic test
bin/kafka-console-producer.sh --broker-list 192.168.100.125:9092 --topic DreamTopic1

模拟客户端去发送消息
./bin/kafka-console-producer.sh --broker-list 10.10.67.102:9092, 10.10.67.104:9092, 10.10.67.106:9092 --topic test

消费者测试
bin/kafka-console-consumer.sh --zookeeper 192.168.100.125:2181 --topic test --from-beginning
bin/kafka-console-consumer.sh --zookeeper kafka1:2181 --topic test --from-beginning
bin/kafka-console-consumer.sh --zookeeper 192.168.100.125:2181 --topic DreamTopic1 --from-beginning
模拟客户端去接受消息
./bin/kafka-console-consumer.sh --zookeeper 10.10.67.102:2181, 10.10.67.104:2181, 10.10.67.106:2181 --from-beginning --topic test


bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list 10.1.243.23:59092,10.1.243.23:59093,10.1.243.23:59094 --topic topicTest --time -1

bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --broker-info --group CONSUMER_TOPICTEST_ALL_MSGFRAME_CLUSTER_GROUP --topic topicTest --zookeeper 10.1.243.23:52181




kafka删除topic方法
1) bin/kafka-topics.sh --delete --zookeeper master:2181 --topic DreamTopic
如果删除后查看topic显示为:marked for deletion  则需要在每一台机器中的 config/server.properties 文件加入  delete.topic.enable=true，然后重启kafka
2) 删除kafka存储目录（server.properties文件log.dirs配置，默认为"/tmp/kafka-logs"）相关topic目录删除zookeeper "/brokers/topics/"目录下相关topic节点




