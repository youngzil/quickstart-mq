## Kafka Streams概述


流式计算框架

目前业界比较成熟的的框架包括： Apache Spark、Storm、Flink等。

这些框架的特点是拥有强大的计算能力，例如 Spark Streaming 上已经包含 Graph Compute，MLLib 等适合迭代计算库，在特定场景中非常好用。

然而，这些框架也有通用的一个缺点：使用复杂、集群资源消耗较大。



Kafka Streams是一个客户端程序库，用于处理和分析存储在Kafka中的数据，并将得到的数据写回Kafka或发送到外部系统。

Kafka Stream 的亮点：
- 设计一个简单的、轻量级的客户端库，可以很容易地嵌入在任何java应用程序与任何现有应用程序封装集成。
- Apache Kafka本身作为内部消息层，没有外部系统的依赖，还有，它使用kafka的分区模型水平扩展处理，并同时保证有序。
- 支持本地状态容错，非常快速、高效的状态操作（如join和窗口的聚合）。
- 采用 one-recored-at-a-time（一次一个消息） 处理以实现低延迟，并支持基于事件时间(event-time)的窗口操作。
- 提供必要的流处理原语(primitive)，以及一个 高级别的Steram DSL 和 低级别的Processor API。


Kafka Streams DSL (Domain Specific Language)

领域特定语言（英语：domain-specific language、DSL）指的是专注于某个应用程序领域的计算机语言。又译作领域专用语言。




## Kafka Streams的关键概念

Stream处理拓扑
- 流是Kafka Stream提出的最重要的抽象概念：它表示一个无限的，不断更新的数据集。流是一个有序的，可重放（反复的使用），不可变的容错序列，数据记录的格式是键值对（key-value）。
- 处理器拓扑：通过Kafka Streams编写一个或多个的计算逻辑的处理器拓扑。其中处理器拓扑是一个由流（边缘）连接的流处理（节点）的图。
- 流处理器：是处理器拓扑中的一个节点；它表示一个处理的步骤，用来转换流中的数据（从拓扑中的上游处理器一次接受一个输入消息，并且随后产生一个或多个输出消息到其下游处理器中）。


在拓扑中有两个特别的处理器：
- 源处理器（Source Processor）：源处理器是一个没有任何上游处理器的特殊类型的流处理器。它从一个或多个kafka主题生成输入流。通过消费这些主题的消息并将它们转发到下游处理器。
- Sink处理器：sink处理器是一个没有下游流处理器的特殊类型的流处理器。它接收上游流处理器的消息发送到一个指定的Kafka主题。


Kafka streams提供2种方式来定义流处理器拓扑：
- Kafka Streams DSL提供了更常用的数据转换操作，如map和filter；
- 低级别Processor API允许开发者定义和连接自定义的处理器，以及和状态仓库交互。




## Kafka Streams处理器拓扑Topology构建方式

final StreamsBuilder builder = new StreamsBuilder();
// builder.addGlobalStore()
// builder.addStateStore()
// builder.globalTable()
// builder.table()
// builder.stream()


final Topology builder = new Topology();
// builder.addGlobalStore()
// builder.addProcessor()
// builder.addSink()
// builder.addSource()
// builder.addStateStore()
// builder.describe()





参考  
[KAFKA STREAMS官方文档](http://kafka.apache.org/27/documentation/streams/)  
[Kafka Streams开发者指南](https://www.orchome.com/335)  
[流式计算之 Kafka Stream](https://juejin.cn/post/6844904087100620814)  
[最简单流处理引擎——Kafka Streams简介](https://juejin.cn/post/6844903934557945870)  



---------------------------------------------------------------------------------------------------------------------



