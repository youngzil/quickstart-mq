- [Kafka的介绍](#Kafka的介绍)
- [Kafka的架构](#Kafka的架构)
- [Kafka的概念名词](#Kafka的概念名词)



---------------------------------------------------------------------------------------------------------------------
## Kafka的介绍

Kafka由 linked-in 开源  
生产者生产（produce）各种信息，消费者消费（consume）（处理分析）这些信息  
kafka-即是解决上述这类问题的一个框架，它实现了生产者和消费者之间的无缝连接。  
kafka-高产出的分布式消息系统(A high-throughput distributed messaging system)  


Kafka的特性
- 高吞吐量、低延迟：kafka每秒可以处理几十万条消息，它的延迟最低只有几毫秒
- 可扩展性：kafka集群支持热扩展
- 持久性、可靠性：消息被持久化到本地磁盘，并且支持数据备份防止数据丢失
- 容错性：允许集群中节点失败（若副本数量为n,则允许n-1个节点失败）
- 高并发：支持数千个客户端同时读写




## Kafka应用场景

- 日志收集：一个公司可以用Kafka可以收集各种服务的log，通过kafka以统一接口服务的方式开放给各种consumer，例如hadoop、Hbase、Solr等。
- 消息系统：解耦和生产者和消费者、缓存消息等。
- 用户活动跟踪：Kafka经常被用来记录web用户或者app用户的各种活动，如浏览网页、搜索、点击等活动，这些活动信息被各个服务器发布到kafka的topic中，然后订阅者通过订阅这些topic来做实时的监控分析，或者装载到hadoop、数据仓库中做离线分析和挖掘。
- 运营指标：Kafka也经常用来记录运营监控数据。包括收集各种分布式应用的数据，生产各种操作的集中反馈，比如报警和报告。
- 流式处理：比如spark streaming和storm
- 事件源




## Kafka的概念名词

- 消息状态：在Kafka中，消息的状态被保存在consumer中，broker不会关心哪个消息被消费了被谁消费了，只记录一个offset值（指向partition中下一个要被消费的消息位置），这就意味着如果consumer处理不好的话，broker上的一个消息可能会被消费多次。
- 消息持久化：Kafka中会把消息持久化到本地文件系统中，并且保持极高的效率。
- 消息有效期：Kafka会长久保留其中的消息，以便consumer可以多次消费，当然其中很多细节是可配置的。
- 批量发送：Kafka支持以消息集合为单位进行批量发送，以提高push效率。
- push-and-pull : Kafka中的Producer和consumer采用的是push-and-pull模式，即Producer只管向broker push消息，consumer只管从broker pull消息，两者对消息的生产和消费是异步的。
- Kafka集群中broker之间的关系：不是主从关系，各个broker在集群中地位一样，我们可以随意的增加或删除任何一个broker节点。
- 负载均衡方面： Kafka提供了一个 metadata API来管理broker之间的负载（对Kafka0.8.x而言，对于0.7.x主要靠zookeeper来实现负载均衡）。
- 同步异步：Producer采用异步push方式，极大提高Kafka系统的吞吐率（可以通过参数控制是采用同步还是异步方式）。
- 分区机制partition：Kafka的broker端支持消息分区，Producer可以决定把消息发到哪个分区，在一个分区中消息的顺序就是Producer发送消息的顺序，一个主题中可以有多个分区，具体分区的数量是可配置的。分区的意义很重大，后面的内容会逐渐体现。
- 离线数据装载：Kafka由于对可拓展的数据持久化的支持，它也非常适合向Hadoop或者数据仓库中进行数据装载。
- 插件支持：现在不少活跃的社区已经开发出不少插件来拓展Kafka的功能，如用来配合Storm、Hadoop、flume相关的插件。




## Kafka的架构

Kafka中发布订阅的对象是topic。我们可以为每类数据创建一个topic，  
把向topic发布消息的客户端称作producer，  
从topic订阅消息的客户端称作consumer。  
Producers和consumers可以同时从多个topic读写数据。  
一个kafka集群由一个或多个broker服务器组成，它负责持久化和备份具体的kafka消息。


- topic：消息存放的目录即主题。Kafka中的topic是以partition的形式存放的，每一个topic都可以设置它的partition数量  
  每条发布到 Kafka 集群的消息都有一个类别，这个类别被称为 Topic。（物理上不同 Topic 的消息分开存储，逻辑上一个 Topic 的消息虽然保存于一个或多个 broker 上但用户只需指定消息的 Topic 即可生产或消费数据而不必关心数据存于何处）
- Producer：生产消息到topic的一方,负责发布消息到 Kafka broker
- Consumer：订阅topic消费消息的一方,向 Kafka broker 读取消息的客户端。
- Consumer Group：每个 Consumer 属于一个特定的 Consumer Group（可为每个 Consumer 指定 group name，若不指定 group name 则属于默认的 group）。  
  消费组ConsumerGroup：各个consumer可以组成一个组，每个消息只能被组中的一个consumer消费，如果一个消息可以被多个consumer消费的话，那么这些consumer必须在不同的组。
- Broker：Kafka的服务实例就是一个broker,Kafka 集群包含一个或多个服务器，这种服务器被称为 broker
- Topic Partition：每个Partition中的消息都是有序的，生产的消息被不断追加到Partition log上，其中的每一个消息都被赋予了一个唯一的offset值。  
  Parition 是物理上的概念，每个 Topic 包含一个或多个 Partition.  
- Topic Partition的offset范围，每个消息有一个唯一的offset值。
- 消费组ConsumerGroup有个当前消费的offset值，记录该消费组ConsumerGroup消费的位置
- 一个partition只能被一个消费者消费（一个消费者可以同时消费多个partition），因此，如果设置的partition的数量小于consumer的数量，就会有消费者消费不到数据。所以，推荐partition的数量一定要大于同时运行的consumer的数量。 
- 另外一方面，建议partition的数量大于集群broker的数量，这样leader partition就可以均匀的分布在各个broker中，最终使得集群负载均衡。  
  为了更好的做负载均衡，Kafka 尽量将所有的 Partition 均匀分配到整个集群上。一个典型的部署方式是一个 Topic 的 Partition 数量大于 Broker 的数量。
- 同时为了提高 Kafka 的容错能力，也需要将同一个 Partition 的 Replica 尽量分散到不同的机器。




## kafka消息保证机制

有这么几种可能的 delivery guarantee：
- At most once 消息可能会丢，但绝不会重复传输
- At least one 消息绝不会丢，但可能会重复传输
- Exactly once 每条消息肯定会被传输一次且仅传输一次，很多时候这是用户所想要的。

总之，Kafka 默认保证 At least once，并且允许通过设置 Producer 异步提交来实现 At most once。而 Exactly once 要求与外部存储系统协作，幸运的是 Kafka 提供的 offset 可以非常直接非常容易得使用这种方式。





### Kafka High Availability
- 一个partition只能被一个消费者消费（一个消费者可以同时消费多个partition），因此，如果设置的partition的数量小于consumer的数量，就会有消费者消费不到数据。所以，推荐partition的数量一定要大于同时运行的consumer的数量。
- 另外一方面，建议partition的数量大于集群broker的数量，这样leader partition就可以均匀的分布在各个broker中，最终使得集群负载均衡。  
  为了更好的做负载均衡，Kafka 尽量将所有的 Partition 均匀分配到整个集群上。一个典型的部署方式是一个 Topic 的 Partition 数量大于 Broker 的数量。
- 同时为了提高 Kafka 的容错能力，也需要将同一个 Partition 的 Replica 尽量分散到不同的机器。


Kafka 分配 Replica 的算法如下：
- 将所有 Broker（假设共 n 个 Broker）和待分配的 Partition 排序
- 将第 i 个 Partition 分配到第（i mod n）个 Broker 上
- 将第 i 个 Partition 的第 j 个 Replica 分配到第（(i + j) mode n）个 Broker 上


Data Replication：  
- Leader Election 算法：ISR 模式  
  Kafka 在 ZooKeeper 中动态维护了一个 ISR（in-sync replicas），这个 ISR 里的所有 Replica 都跟上了 leader，只有 ISR 里的成员才有被选为 Leader 的可能。



如何处理所有 Replica 都不工作

上文提到，在 ISR 中至少有一个 follower 时，Kafka 可以确保已经 commit 的数据不丢失，但如果某个 Partition 的所有 Replica 都宕机了，就无法保证数据不丢失了。这种情况下有两种可行的方案：
- 等待 ISR 中的任一个 Replica“活”过来，并且选它作为 Leader
- 选择第一个“活”过来的 Replica（不一定是 ISR 中的）作为 Leader





参考  
Kafka的[README.md](../README.md)  
[Kafka 设计与原理详解](https://blog.csdn.net/suifeng3051/article/details/48053965)  
[Kafka系列文章InfoQ地址](https://www.infoq.cn/article/kafka-analysis-part-1) ：共八篇，修改URL就可以一一查看  
[Kafka系列文章](http://www.jasongj.com/tags/Kafka/)  

[Kafka 总结](https://zhuanlan.zhihu.com/p/72328153)  
[Kafka学习笔记](https://zhmin.github.io/categories/kafka/)  
[]()  
[]()  




[从0开始学Kafka（上）](https://zhuanlan.zhihu.com/p/93403426)  
[从0开始学Kafka（下）](https://zhuanlan.zhihu.com/p/93547373)  


[分布式消息Kafka的原理、基础架构、使用场景](https://aijishu.com/a/1060000000080308)  



