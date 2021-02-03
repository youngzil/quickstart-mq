- [Kafka的Rebalance](#Kafka的Rebalance)
    - [Kafka的Rebalance条件](#Kafka的Rebalance条件)
    - [Kafka的Rebalance方案](#Kafka的Rebalance方案)
    - [0.9之前kafka的Rebalance算法](#0.9之前kafka的Rebalance算法)
    - [0.9后kafka对Rebalance过程进行了改进](#0.9后kafka对Rebalance过程进行了改进)
    - [如何避免不必要的Rebalance](#如何避免不必要的Rebalance)
- [Kafka高级API和低级API](#Kafka高级API和低级API)


---------------------------------------------------------------------------------------------------------------------
### Kafka的Rebalance


### Kafka的Rebalance条件
- 条件1：有新的consumer加入
- 条件2：旧的consumer挂了（consumer离开）
- 条件3：coordinator挂了，集群选举出新的coordinator（0.10 特有的）
- 条件4：订阅topic的partition新加
- 条件5：consumer调用unsubscrible()，取消topic的订阅（订阅的Topic个数发生变化）



### Kafka的Rebalance方案

Kafka的Consumer Rebalance方案是基于Zookeeper的Watcher来实现的。consumer启动的时候，在zk下都维护一个”/consumers/[group_name]/ids”路径，在此路径下，使用临时节点记录属于此cg的消费者的Id，该Id信息由对应的consumer在启动时创建。

每个consumer都会在此路径下简历一个watcher，当有节点发生变化时，就会触发watcher，然后触发Rebalance过程。



### 0.9之前kafka的Rebalance算法



### 0.9后kafka对Rebalance过程进行了改进

Group Coordinator是一个服务，每个Broker在启动的时候都会启动一个该服务。  
Group Coordinator的作用是用来存储Group的相关Meta信息，并将对应Partition的Offset信息记录到Kafka内置Topic(__consumer_offsets)中。

Kafka在0.9之前是基于Zookeeper来存储Partition的Offset信息(consumers/{group}/offsets/{topic}/{partition})，因为ZK并不适用于频繁的写操作，所以在0.9之后通过内置Topic的方式来记录对应Partition的Offset。

rebalance本质上是一组协议。group与coordinator共同使用它来完成group的rebalance。

目前kafka提供了5个协议来处理与consumer group coordination相关的问题：
- Heartbeat请求：consumer需要定期给coordinator发送心跳来表明自己还活着
- LeaveGroup请求：主动告诉coordinator我要离开consumer group
- SyncGroup请求：group leader把分配方案告诉组内所有成员
- JoinGroup请求：成员请求加入组
- DescribeGroup请求：显示组的所有信息，包括成员信息，协议名称，分配方案，订阅信息等



rebalance过程分为2步：Join和Sync

1 Join， 顾名思义就是加入组。这一步中，所有成员都向coordinator发送JoinGroup请求，请求入组。一旦所有成员都发送了JoinGroup请求，coordinator会从中选择一个consumer担任leader的角色，并把组成员信息以及订阅信息发给leader——注意leader和coordinator不是一个概念。leader负责消费分配方案的制定。

2 Sync，这一步leader开始分配消费方案，即哪个consumer负责消费哪些topic的哪些partition。一旦完成分配，leader会将这个方案封装进SyncGroup请求中发给coordinator，非leader也会发SyncGroup请求，只是内容为空。coordinator接收到分配方案之后会把方案塞进SyncGroup的response中发给各个consumer。这样组内的所有成员就都知道自己应该消费哪些分区了。



### 如何避免不必要的Rebalance

1. 第一类非必要 Rebalance 是因为未能及时发送心跳，导致 Consumer 被 “踢出”Group 而引发的。这种情况下我们可以设置 session.timeout.ms 和 heartbeat.interval.ms 的值，来尽量避免rebalance的出现。
2. 第二类非必要 Rebalance 是 Consumer 消费时间过长导致的。此时，max.poll.interval.ms 参数值的设置显得尤为关键。如果要避免非预期的 Rebalance，你最好将该参数值设置得大一点，比你的下游最大处理时间稍长一点。


[kafka的Rebalance问题分析](https://blog.csdn.net/chenwiehuang/article/details/103434600)  
[Kafka源码系列之分组消费的再平衡策略](https://cloud.tencent.com/developer/article/1032490)  
[Kafka Rebalance 客户端原理](https://zhmin.github.io/2019/03/18/kafka-consumer-coordinator/)  
[Kafka Consumer 的 Rebalance 机制](https://juejin.cn/post/6844904000479821832)  


---------------------------------------------------------------------------------------------------------------------
## Kafka高级API和低级API
Kafka High Level API vs. Low Level API

kafka提供了两套consumer API：高级Consumer API和低级API。

根据Kafka提供的API不同，可以讲Consumer划分为：High Level Consumer和Low Level Consumer（也叫Simple Consumer）。虽然说0.9版本开始讲两种Consumer合二为一了，但在API上还是有assign和subscribe的区分的。


高级API优点：
1. 高级API 写起来简单
2. 不需要自行去管理offset，系统通过zookeeper自行管理。
3. 不需要管理分区，副本等情况，.系统自动管理。
4. 消费者断线会自动根据上一次记录在zookeeper中的offset去接着获取数据（默认设置1分钟更新一下zookeeper中存的offset）。

高级API缺点：
1. 不能细化控制如分区、副本、zk等。
2. 不能自行控制offset（对于某些特殊需求来说）。


低级API优点：
1. 能够让开发者自己控制offset，想从哪里读取就从哪里读取。
2. 自行控制连接分区，对分区自定义进行负载均衡
3. 对zookeeper的依赖性降低（如：offset不一定非要靠zk存储，自行存储offset即可，比如存在文件或者内存中）

低级API缺点
1. 太过复杂，需要自行控制offset，连接哪个分区，找到分区leader 等。


[从0开始学Kafka（下）](https://zhuanlan.zhihu.com/p/93547373)  
[Kafka高级API和低级API](https://blog.csdn.net/weixin_37766087/article/details/103681307)  
[Kafka High Level API vs. Low Level API](https://blog.csdn.net/yjgithub/article/details/78559094)  
[Kafka:High level consumer vs. Low level consumer](https://blog.csdn.net/WangQYoho/article/details/78358715)  

---------------------------------------------------------------------------------------------------------------------

[消费者配置官方文档](http://kafka.apache.org/documentation.html#consumerconfigs)  













