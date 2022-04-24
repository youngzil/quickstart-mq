- [Kafka的安装部署](#Kafka的安装部署)
    - [一、单节点单broker实例的配置](#一、单节点单broker实例的配置)
    - [二、单节点运行多broker实例](#二、单节点运行多broker实例)
    - [三、集群模式（多节点多实例）](#三、集群模式（多节点多实例）)
    - [四、Linux在线安装部署](#四、Linux在线安装部署)
    - [使用Docker部署kafka集群](#使用Docker部署kafka集群)


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


[Kafka集群环境搭建及使用教程](http://www.lijiahong.com/?post=80)  
[Kafka2.8.x安装与配置](https://blog.csdn.net/Exception_sir/article/details/116229537)



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


## 使用Docker部署kafka集群

[本文讲解如何使用Docker部署kafka集群](https://jasonkayzk.github.io/2020/04/26/%E4%BD%BF%E7%94%A8Docker%E9%83%A8%E7%BD%B2kafka%E9%9B%86%E7%BE%A4/)  
[DockerCompose配置](https://github.com/JasonkayZK/docker_repo/tree/kafka-v2.4.1-cluster)  


[Guide to Setting Up Apache Kafka Using Docker](https://www.baeldung.com/ops/kafka-docker-setup)  





