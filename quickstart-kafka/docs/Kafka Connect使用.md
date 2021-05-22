- [Kafka Connect概述](#Kafka-Connect概述)
- [Kafka Connect功能包括](#Kafka-Connect功能包括)
- [Kafka Connect的关键概念](#Kafka-Connect的关键概念)
    - [Connectors](#Connectors)
    - [Task](#Task)
    - [Worker](#Worker)
    - [Converters](#Converters)
    - [Transforms](#Transforms)
    - [Dead Letter Queue](#Dead-Letter-Queue)
- [Kafka Connect的实战测试](../../quickstart-kafka-connect/Kafka%20Connect的实战测试.md)
- [Kafka Connect源码](#Kafka-Connect源码)

---------------------------------------------------------------------------------------------------------------------

## Kafka Connect概述

Kafka Connect 是一个可扩展、可靠的在Kafka和其他系统之间流传输的数据工具。

它可以通过connectors（连接器）简单、快速的将大集合数据导入和导出kafka。

Kafka Connect可以接收整个数据库或收集来自所有的应用程序的消息到Kafka Topic。使这些数据可用于低延迟流处理。

导出可以把topic的数据发送到secondary storage（辅助存储也叫二级存储）也可以发送到查询系统或批处理系统做离线分析。




## Kafka Connect功能包括

- Kafka连接器通用框架：Kafka Connect 规范了kafka与其他数据系统集成，简化了connector的开发、部署和管理。

- 分布式和单机模式 - 扩展到大型支持整个organization的集中管理服务，也可缩小到开发，测试和小规模生产部署。

- REST 接口 - 使用REST API来提交并管理Kafka Connect集群。

- 自动的offset管理 - 从connector获取少量的信息，Kafka Connect来管理offset的提交，所以connector的开发者不需要担心这个容易出错的部分。

- 分布式和默认扩展 - Kafka Connect建立在现有的组管理协议上。更多的工作可以添加扩展到Kafka Connect集群。

- 流/批量集成 - 利用kafka现有的能力，Kafka Connect是一个桥接流和批量数据系统的理想解决方案。




Kafka Connect目前支持两种执行模式：独立（单进程）和分布式。

在独立模式下，所有的工作都在一个单进程中进行的。这样易于配置，在一些情况下，只有一个在工作是好的（例如，收集日志文件），但它不会从kafka Connection的功能受益，如容错。

分布式的模式会自动平衡。允许你动态的扩展（或缩减），并在执行任务期间和配置、偏移量提交中提供容错保障




## Kafka Connect的关键概念

## Connectors
Connector（连接器）：在Kafka和其他系统之间复制数据，用户创建自定义的从系统中pull数据或push数据到系统的Connector（连接器）。

Connector有两种形式：
- SourceConnectors从其他系统导入数据（如：JDBCSourceConnector将导入一个关系型数据库到Kafka）和
- SinkConnectors导出数据（如：HDFSSinkConnector将kafka主题的内容导出到HDFS文件）。


Kafka Connnect 有两个核心概念：Source 和 Sink。  
Source 负责导入数据到 Kafka，Sink 负责从 Kafka 导出数据，它们都被称为 Connector。

连接器，分为两种 Source（从源数据库拉取数据写入Kafka），Sink（从Kafka消费数据写入目标数据）

连接器其实并不参与实际的数据copy，连接器负责管理Task。连接器中定义了对应Task的类型，对外提供配置选项（用户创建连接器时需要提供对应的配置信息）。并且连接器还可以决定启动多少个Task线程。




## Task
实际进行数据传输的单元，和连接器一样同样分为 Source和Sink

Task的配置和状态存储在Kafka的Topic中，config.storage.topic和status.storage.topic。我们可以随时启动，停止任务，以提供弹性、可扩展的数据管道




## Worker
刚刚我们讲的Connectors 和Task 属于逻辑单元，而Worker 是实际运行逻辑单元的进程，Worker 分为两种模式，单机模式和分布式模式

单机模式：比较简单，但是功能也受限，只有一些特殊的场景会使用到，例如收集主机的日志，通常来说更多的是使用分布式模式

分布式模式：为Kafka Connect提供了可扩展和故障转移。相同group.id的Worker，会自动组成集群。当新增Worker，或者有Worker挂掉时，集群会自动协调分配所有的Connector 和 Task（这个过程称为Rebalance）

当使用Worker集群时，创建连接器，或者连接器Task数量变动时，都会触发Rebalance 以保证集群各个Worker节点负载均衡。但是当Task 进入Fail状态的时候并不会触发 Rebalance，只能通过Rest Api 对Task进行重启




## Converters
Kafka Connect 通过 Converter 将数据在Kafka（字节数组）与Task（Object）之间进行转换

默认支持以下Converter
- AvroConverter io.confluent.connect.avro.AvroConverter: 需要使用 Schema Registry
- ProtobufConverter io.confluent.connect.protobuf.ProtobufConverter: 需要使用 Schema Registry
- JsonSchemaConverter io.confluent.connect.json.JsonSchemaConverter: 需要使用 Schema Registry
- JsonConverter org.apache.kafka.connect.json.JsonConverter (无需 Schema Registry): 转换为json结构
- StringConverter org.apache.kafka.connect.storage.StringConverter: 简单的字符串格式
- ByteArrayConverter org.apache.kafka.connect.converters.ByteArrayConverter: 不做任何转换

Converters 与 Connector 是解耦的，下图展示了在Kafka Connect中，Converter 在何时进行数据转换




## Transforms
连接器可以通过配置Transform 实现对单个消息（对应代码中的Record）的转换和修改，可以配置多个Transform 组成一个链。例如让所有消息的topic加一个前缀、sink无法消费source 写入的数据格式，这些场景都可以使用Transform 解决

Transform 如果配置在Source 则在Task之后执行，如果配置在Sink 则在Task之前执行




## Dead Letter Queue
与其他MQ不同，Kafka 并没有死信队列这个功能。但是Kafka Connect提供了这一功能。

当Sink Task遇到无法处理的消息，会根据errors.tolerance配置项决定如何处理，默认情况下(errors.tolerance=none) Sink 遇到无法处理的记录会直接抛出异常，Task进入Fail 状态。开发人员需要根据Worker的错误日志解决问题，然后重启Task，才能继续消费数据

设置 errors.tolerance=all，Sink Task 会忽略所有的错误，继续处理。Worker中不会有任何错误日志。可以通过配置errors.deadletterqueue.topic.name = <dead-letter-topic-name> 让无法处理的消息路由到 Dead Letter Topic





[Kafka Connect 实战 ---- 入门](https://segmentfault.com/a/1190000039395164)  
[Kafka Connect用户指南](https://www.orchome.com/344)  
[Kafka Connector开发指南](https://www.orchome.com/345)  
[基于 Kafka Connect 的流数据同步服务实现和监控体系搭建](https://www.infoq.cn/article/zxst2zq8rwsamqjusqbg)  
[]()  
[]()  
[]()  
[Confluent社区Connector Developer Guide](https://docs.confluent.io/platform/current/connect/devguide.html)  
[]()  
[Kafka Connect简介与部署](https://code-monkey.top/2020/03/10/Kafka-Connect%E7%AE%80%E4%BB%8B%E4%B8%8E%E9%83%A8%E7%BD%B2/)  
[Connector开发指南](https://www.jianshu.com/p/08f8ae15a861)  
[kafka connector 使用总结以及自定义connector开发](https://www.cnblogs.com/laoqing/p/11927958.html)  
[]()  
[]()  
[]()  
[]()  

---------------------------------------------------------------------------------------------------------------------
## Kafka Connect源码

启动脚本
bin/connect-distributed.sh -daemon config/connect-distributed.properties


启动main类
org.apache.kafka.connect.cli.ConnectDistributed


