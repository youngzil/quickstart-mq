- [Kafka的安装部署](#Kafka的安装部署)
    - [一、单节点单broker实例的配置](#一、单节点单broker实例的配置)
    - [二、单节点运行多broker实例](#二、单节点运行多broker实例)
    - [三、集群模式（多节点多实例）](#三、集群模式（多节点多实例）)
    - [四、Linux在线安装部署](#四、Linux在线安装部署)
    - [使用Docker部署kafka集群](#使用Docker部署kafka集群)
- [Kafka操作命令](#Kafka操作命令)
    - [查看kafka的zookeeper上的数据](#查看kafka的zookeeper上的数据)



---------------------------------------------------------------------------------------------------------------------
## Kafka的安装部署

[安装脚本](install)

Kafka集群配置比较简单，主要是下面三种部署配置
- 单节点：一个broker的集群
- 单节点：多个broker的集群
- 多节点：多broker集群


### 一、单节点单broker实例的配置


zookeeper配置文件的一些重要属性:
```
# Data directory where the zookeeper snapshot is stored.
dataDir=/tmp/zookeeper
# The port listening for client request
clientPort=2181
默认情况下，zookeeper服务器会监听 2181端口，更详细的信息可去zookeeper官网查阅。
```

broker配置文件中的重要属性：
```
# broker的id. 每个broker的id必须是唯一的.
broker.id=0
# 存放log的目录
log.dir=/tmp/kafka8-logs
# Zookeeper 连接串
zookeeper.connect=localhost:2181
```

启动Zooleeper和Kafka

1. 首先启动zookeeper服务  
    bin/zookeeper-server-start.sh config/zookeeper.properties

2. 启动Kafka broker  
    bin/kafka-server-start.sh config/server.properties

3. 创建一个仅有一个Partition的topic  
   bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 1 --partition 1 --topic kafkatopic
    
4. 用Kafka提供的生产者客户端启动一个生产者进程来发送消息  
   bin/kafka-console-producer.sh --broker-list  localhost:9092 --topic kafkatopic

    其中有两个参数需要注意：
   - broker-list:定义了生产者要推送消息的broker地址，以<IP地址:端口>形式
   - topic：生产者发送给哪个topic
   
5. 启动一个Consumer实例来消费消息  
   bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic kafkatopic --from-beginning

   和消费者相关的属性配置存放在Consumer.properties文件中，重要的属性有：
   ```
    # consumer的group id (A string that uniquely identifies a set of consumers
    # within the same consumer group) 
      groupid=test-consumer-group
    # zookeeper 连接串
      zookeeper.connect=localhost:2181
   ```



### 二、单节点运行多broker实例

1. 启动zookeeper和上面的一样

2. 启动Kafka的broker

    部署集群至少修改三个：config/server.properties  
    ```
    brokerid=2  
    port=9093  
    log.dir=/temp/kafka8-logs/broker2  
    zookeeper.connect=localhost:2181
    ```
   
   要想在一台机器上启动多个broker实例，只需要准备多个server.properties文件即可，比如我们要在一台机器上启动两个broker：
   首先我们要准备两个server.properties配置文件

    server-1
    ```
    brokerid=1
    port=9092
    log.dir=/temp/kafka8-logs/broker1
    ```
    
    server-2
    ```
    brokerid=2
    port=9093
    log.dir=/temp/kafka8-logs/broker2
    ```

3. 创建一个topic

    现在我们要创建一个含有两个Partition分区和2个备份的broker：
    
    [root@localhost kafka-0.8]# bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 2 --partition 2 --topic othertopic

4. 启动Producer发送消息
    如果我们要用一个Producer发送给多个broker，唯一需要改变的就是在broker-list属性中指定要连接的broker：  
    [root@localhost kafka-0.8]# bin/kafka-console-producer.sh --broker-list localhost:9092,localhost:9093 --topic othertopic  
    如果我们要让不同的Producer发送给不同的broker，我们也仅仅需要为每个Producer配置响应的broker-list属性即可。

5. 启动一个消费者来消费消息
    和之前的命令一样 
    [root@localhost kafka-0.8]# bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic othertopic --from-beginning 
   


### 三、集群模式（多节点多实例）

介绍了上面两种配置方法，再理解集群配置就简单了，比如我们要配置如下图所示集群：
- zookeeper配置文件（zookeeper.properties）：不变
- broker的配置配置文件(server.properties)：按照单节点多实例配置方法在一个节点上启动两个实例，不同的地方是zookeeper的连接串需要把所有节点的zookeeper都连接起来
```
# Zookeeper 连接串
zookeeper.connect=node1:2181,node2:2181
```


### 四、Linux在线安装部署
1. Kafka下载：
   wget https://archive.apache.org/dist/kafka/0.8.1/kafka_2.9.2-0.8.1.tgz
   解压 tar zxvf kafka_2.9.2-0.8.1.tgz

启动zookeeper
```
nohup bin/zookeeper-server-start.sh config/zookeeper.properties &
或者 
zkServer.sh start
```

启动kafka
```
nohup bin/kafka-server-start.sh -daemon config/server.properties &
```

下线broker
```
./kafka-run-class.sh kafka.admin.ShutdownBroker --zookeeper127.0.0.1:2181--broker #brokerId# --num.retries3--retry.interval.ms60

shutdown broker
```



参考  
[Kafka详解二、如何配置Kafka集群](https://blog.csdn.net/suifeng3051/article/details/38321043)  
[官方文档apache kafka quickstart](http://kafka.apache.org/quickstart)  
[Kafka操作命令](https://www.cnblogs.com/zcqdream/articles/6593875.html)  

---------------------------------------------------------------------------------------------------------------------
## Kafka操作命令

启动：

```
bin/zookeeper-server-start.sh config/zookeeper.properties &

bin/kafka-server-start.sh config/server.properties &
```
或者
```
nohup sh bin/zookeeper-server-start.sh config/zookeeper.properties &

nohup sh bin/kafka-server-start.sh -daemon config/server.properties &
```

停止：
```
bin/zookeeper-server-stop.sh

bin/kafka-server-stop.sh
```


创建主题Topic
```
#replication-factor 表示该topic需要在不同的broker中保存几份, partitions为几个分区
现在我们要创建一个含有两个Partition分区和2个备份的broker：

bin/kafka-create-topic.sh --zookeeper localhost:2181 --replica 2 --partition 2 --topic kafkatopic

bin/kafka-topics.sh --create --zookeeper localhost:2189 --replication-factor 2 --partitions 2 --topic testTopic  

bin/kafka-topics.sh --create --zookeeper 10.10.67.102:2181,10.10.67.104:2181,10.10.67.106:2181 --replication-factor 3 --partitions 3 --topic test

```

列出主题列表
```
bin/kafka-topics.sh --list --zookeeper 127.0.0.1:12181

bin/kafka-topics.sh --list --zookeeper master:2181,node2:2181,node1:2181
bin/kafka-topics.sh --list --zookeeper 10.1.120.6:2181,10.1.120.7:2181,10.1.120.8:2181
```

查看主题详细信息
```
bin/kafka-topics.sh --describe --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181 --topic event

bin/kafka-topics.sh --describe --zookeeper 10.1.243.23:52181 --topic topicTest 


[root@linux-node2 bin]# bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic dream,DreamTopic
Topic:dream    PartitionCount:5    ReplicationFactor:2    Configs:
    Topic: dream    Partition: 0    Leader: 1    Replicas: 1,2    Isr: 1,2
    Topic: dream    Partition: 1    Leader: 2    Replicas: 2,3    Isr: 2,3
    Topic: dream    Partition: 2    Leader: 3    Replicas: 3,1    Isr: 3,1
    Topic: dream    Partition: 3    Leader: 1    Replicas: 1,3    Isr: 1,3
    Topic: dream    Partition: 4    Leader: 2    Replicas: 2,1    Isr: 2,1
leader:负责处理消息的读和写，leader是从所有节点中随机选择的.
Replicas:列出了所有的副本节点，不管节点是否在服务中.
Lsr:是正在服务中的节点.

```


查看topic指定分区offset的最大值或最小值
```
time为-1时表示最大值，为-2时表示最小值：

kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list 127.0.0.1:9092 --topic hive-mdatabase-hostsltable --time -1 --partitions 0 


bin/kafka-run-class.sh kafka.tools.GetOffsetShell --broker-list 10.1.243.23:59092,10.1.243.23:59093,10.1.243.23:59094 --topic topicTest --time -1


```


为topic增加副本

```
为topic增加partition

bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic test --partitions 5 

./kafka-reassign-partitions.sh -zookeeper127.0.0.1:2181-reassignment-json-file json/partitions-to-move.json -execute


如何增加__consumer_offsets的副本数？其他Topic主题也是一样
可使用kafka-reassign-partitions.sh来增加__consumer_offsets的副本数，方法如下：

构造一JSON文件reassign.json：
    {
    "version":1,
    "partitions":[
    {"topic":"__consumer_offsets","partition":0,"replicas":[1,2,3]},
    {"topic":"__consumer_offsets","partition":1,"replicas":[2,3,1]},
    {"topic":"__consumer_offsets","partition":2,"replicas":[3,1,2]},
    {"topic":"__consumer_offsets","partition":3,"replicas":[1,2,3]},
    ...
    {"topic":"__consumer_offsets","partition":100,"replicas":[2,3,1]}
    ]
    }

然后执行：
kafka-reassign-partitions.sh --zookeeper localhost:2181/kafka --reassignment-json-file reassign.json --execute
“[1,2,3]”中的数字为broker.id值。

```

删除主题
```
bin/kafka-topics.sh --delete --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181 --topic alarm

bin/kafka-topics.sh --zookeeper localhost:2181/kafka --topic test --delete 

bin/kafka-run-class.sh kafka.admin.DeleteTopicCommand --zookeeper localhost:2181 --topic test 


kafka删除topic方法
1) bin/kafka-topics.sh --delete --zookeeper master:2181 --topic DreamTopic
如果删除后查看topic显示为:marked for deletion  则需要在每一台机器中的 config/server.properties 文件加入  delete.topic.enable=true，然后重启kafka

2) 删除kafka存储目录（server.properties文件log.dirs配置，默认为"/tmp/kafka-logs"）相关topic目录删除zookeeper "/brokers/topics/"目录下相关topic节点

```


生产者Producer发送消息
```
bin/kafka-console-producer.sh --broker-list  localhost:9092 --topic kafkatopic

bin/kafka-console-producer.sh --broker-list localhost:9092,localhost:9093 --topic othertopic


```

消费者Consumer消费主题
```
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic kafkatopic --from-beginning

bin/kafka-console-consumer.sh --zookeeper 127.0.0.1:12181 --topic behavior

bin/kafka-console-consumer.sh --zookeeper 10.112.56.90:2181,10.112.56.91:2181,10.112.56.94:2181 --topic alarm --from-beginning


1) 从头开始 

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning 

2) 从尾部开始 

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --offset latest 

3) 指定分区 

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --offset latest --partition 1 

4) 取指定个数 

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --offset latest --partition 1 --max-messages 1 

5) 新消费者（ver>=0.9） 

kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --new-consumer --from-beginning --consumer.config config/consumer.properties 


```



查看有哪些消费者Group
```
1) 分ZooKeeper方式（老）

kafka-consumer-groups.sh --zookeeper 127.0.0.1:2181/kafka --list

2) API方式（新）

kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9092 --list
```


查看Group详情
```
kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9092 --group test --describe
```


获取指定Consumer Group的位移信息
```
需consumer.properties中设置exclude.internal.topics=false：

1) 0.11.0.0版本之前： 

kafka-simple-consumer-shell.sh --topic __consumer_offsets --partition 11 --broker-list localhost:9091,localhost:9092,localhost:9093 --formatter "kafka.coordinator.GroupMetadataManager\$OffsetsMessageFormatter" 

2) 0.11.0.0版本以后(含)： 

kafka-simple-consumer-shell.sh --topic __consumer_offsets --partition 11 --broker-list localhost:9091,localhost:9092,localhost:9093 --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" 

```


查看内部主题__consumer_offsets（保存Consumer Group消费位移信息的Topic）

```
需consumer.properties中设置exclude.internal.topics=false：

1) 0.11.0.0之前版本 

kafka-console-consumer.sh --topic __consumer_offsets --zookeeper localhost:2181 --formatter "kafka.coordinator.GroupMetadataManager\$OffsetsMessageFormatter" --consumer.config config/consumer.properties --from-beginning 

2) 0.11.0.0之后版本(含) 

kafka-console-consumer.sh --topic __consumer_offsets --zookeeper localhost:2181 --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" --consumer.config config/consumer.properties --from-beginning 
```



如何增加__consumer_offsets的副本数？其他Topic主题也是一样
```
可使用kafka-reassign-partitions.sh来增加__consumer_offsets的副本数，方法如下：

构造一JSON文件reassign.json：
    {
    "version":1,
    "partitions":[
    {"topic":"__consumer_offsets","partition":0,"replicas":[1,2,3]},
    {"topic":"__consumer_offsets","partition":1,"replicas":[2,3,1]},
    {"topic":"__consumer_offsets","partition":2,"replicas":[3,1,2]},
    {"topic":"__consumer_offsets","partition":3,"replicas":[1,2,3]},
    ...
    {"topic":"__consumer_offsets","partition":100,"replicas":[2,3,1]}
    ]
    }

然后执行：
kafka-reassign-partitions.sh --zookeeper localhost:2181/kafka --reassignment-json-file reassign.json --execute
“[1,2,3]”中的数字为broker.id值。


__consumer_offsets
__consumer_offsets是kafka内置的Topic，在0.9.0.0之后的Kafka，将topic的offset 信息由之前存储在zookeeper上改为存储到内置的__consumer_offsets中。

server.properties中的配置项num.partitions和default.replication.factor对__consumer_offsets无效，而是受offsets.topic.num.partitions和offsets.topic.replication.factor两个控制。 
```



设置修改Consumer Group的offset

```
执行zkCli.sh进入zookeeper命令行界面，假设需将group为testgroup的topic的offset设置为2018，则：set /consumers/testgroup/offsets/test/0 2018

如果kakfa在zookeeper中的根目录不是“/”，而是“/kafka”，则： 

set /kafka/consumers/testgroup/offsets/test/0 2018 

另外，还可以使用kafka自带工具kafka-run-class.sh kafka.tools.UpdateOffsetsInZK修改，命令用法： 

kafka.tools.UpdateOffsetsInZK$ [earliest | latest] consumer.properties topic 

从用法提示可以看出，只能修改为earliest或latest，没有直接修改zookeeper灵活。 

```


删除Group
```
老版本的ZooKeeper方式可以删除Group，新版本则自动删除，当执行：

kafka-consumer-groups.sh --new-consumer --bootstrap-server 127.0.0.1:9092 --group test --delete 

输出如下提示： 
```



查看新消费者详情
```
仅支持offset存储在zookeeper上的：

kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --zkconnect localhost:2181 --group test 

bin/kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --broker-info --group CONSUMER_TOPICTEST_ALL_MSGTEST_CLUSTER_GROUP --topic topicTest --zookeeper 10.1.243.23:52181

```




### 查看kafka的zookeeper上的数据

1) 查看Kakfa在zookeeper的根目录
   ls /kafka

2) 查看brokers
    ls /kafka/brokers

3) 查看有哪些brokers（214和215等为server.properties中配置的broker.id值）：
   ls /kafka/brokers/ids

4) 查看broker 214，下列数据显示该broker没有设置JMX_PORT：
   get /kafka/brokers/ids/214

5) 查看controller，下列数据显示broker 214为controller：
   get /kafka/controller

6) 查看kafka集群的id：
    get /kafka/cluster/id

7) 查看有哪些topics：
    ls /kafka/brokers/topics

8) 查看topic下有哪些partitions：
    ls /kafka/brokers/topics/__consumer_offsets/partitions

9) 查看“partition 0”的状态：
    get /kafka/brokers/topics/__consumer_offsets/partitions/0/state

23. 如何增加__consumer_offsets的副本数？
    可使用kafka-reassign-partitions.sh来增加__consumer_offsets的副本数，方法如下：

    构造一JSON文件reassign.json：

{
"version":1,
"partitions":[
{"topic":"__consumer_offsets","partition":0,"replicas":[1,2,3]},
{"topic":"__consumer_offsets","partition":1,"replicas":[2,3,1]},
{"topic":"__consumer_offsets","partition":2,"replicas":[3,1,2]},
{"topic":"__consumer_offsets","partition":3,"replicas":[1,2,3]},
...
{"topic":"__consumer_offsets","partition":100,"replicas":[2,3,1]}
]
}
然后执行：

kafka-reassign-partitions.sh --zookeeper localhost:2181/kafka --reassignment-json-file reassign.json --execute
“[1,2,3]”中的数字为broker.id值。


__consumer_offsets
__consumer_offsets是kafka内置的Topic，在0.9.0.0之后的Kafka，将topic的offset 信息由之前存储在zookeeper上改为存储到内置的__consumer_offsets中。

 	server.properties中的配置项num.partitions和default.replication.factor对__consumer_offsets无效，而是受offsets.topic.num.partitions和offsets.topic.replication.factor两个控制。 





[Kafka常用命令收录](https://cloud.tencent.com/developer/article/1350788)  
[Kafka操作命令](https://www.cnblogs.com/zcqdream/articles/6593875.html)  

---------------------------------------------------------------------------------------------------------------------


## 使用Docker部署kafka集群

[本文讲解如何使用Docker部署kafka集群](https://jasonkayzk.github.io/2020/04/26/%E4%BD%BF%E7%94%A8Docker%E9%83%A8%E7%BD%B2kafka%E9%9B%86%E7%BE%A4/)  
[DockerCompose配置](https://github.com/JasonkayZK/docker_repo/tree/kafka-v2.4.1-cluster)  








