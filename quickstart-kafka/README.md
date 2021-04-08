[Kafka官网](http://kafka.apache.org/)  
[Kafka文档](http://kafka.apache.org/documentation.html)  
[Kafka Github](https://github.com/apache/kafka)  
[Kafka介绍](https://www.oschina.net/p/kafka)  
[Kafka中文文档](https://kafka.apachecn.org/documentation.html)  

[wurstmeister Kafka Docker Github](https://github.com/wurstmeister/kafka-docker)  
[wurstmeister Kafka Docker文档](http://wurstmeister.github.io/kafka-docker/)  



Kafka是由Apache软件基金会开发的一个开源流处理平台，由Scala和Java编写。


kafka是一种高吞吐量的分布式发布订阅消息系统，她有如下特性：
- 通过O(1)的磁盘数据结构提供消息的持久化，这种结构对于即使数以TB的消息存储也能够保持长时间的稳定性能。
- 高吞吐量：即使是非常普通的硬件kafka也可以支持每秒数十万的消息。
- 支持通过kafka服务器和消费机集群来分区消息。
- 支持Hadoop并行数据加载。

kafka的目的是提供一个发布订阅解决方案，它可以处理消费者规模的网站中的所有动作流数据。   
这种动作（网页浏览，搜索和其他用户的行动）是在现代网络上的许多社会功能的一个关键因素。   
这些数据通常是由于吞吐量的要求而通过处理日志和日志聚合来解决。   
对于像Hadoop的一样的日志数据和离线分析系统，但又要求实时处理的限制，这是一个可行的解决方案。  
kafka的目的是通过Hadoop的并行加载机制来统一线上和离线的消息处理，也是为了通过集群机来提供实时的消费。  


Kafka本身没有提供批量发送。不过由于消息发送是异步而且本身在内存中已经做了批量化处理，因此我们通常不需要关心发送时是否是批量的。  
至于高效的发送消息，Kafka producer提供了一些参数帮助你调优它的性能。常见的参数包括但不限于：batch.size, linger.ms, compression.type, buffer.memory等



Apache Kafka 在 Exactly-Once Semantics（EOS）上三种粒度的保证如下（来自 [Exactly-once Semantics in Apache Kafka](https://www.slideshare.net/ConfluentInc/exactlyonce-semantics-in-apache-kafka) ）：
1. Idempotent Producer：Exactly-once，in-order，delivery per partition；
2. Transactions：Atomic writes across partitions；
3. Exactly-Once stream processing across read-process-write tasks；





[Kafka AdminClient配置](https://www.orchome.com/677) 


[Kafka使用示例](https://github.com/fhussonnois/kafka-examples)  
[kafka消息队列2-生产者和消费者（Java客户端 数据的发送与接收）](https://blog.csdn.net/baidu_32689899/article/details/107475500)  

[Kafka介绍](https://blog.csdn.net/abc123lzf/category_9726815.html)  
[Kafka核心原理](https://www.kancloud.cn/nicefo71/kafka/1473381)  


[Kafka AdminClient 管理Kafka Offset代码实现](https://blog.csdn.net/lisi1129/article/details/72869194)  
[集群管理工具KafkaAdminClient——原理与示例](http://www.voidcn.com/article/p-rhfwdjtl-brz.html)  
 
[Apache Kafka 2.7.0 稳定版新功能介绍](https://blog.csdn.net/yangyijun1990/article/details/111874790)  


[Kafka的概念和常见模式](http://www.beyondthelines.net/computing/kafka-patterns/)  


[kafka中文教程](https://www.orchome.com/kafka/index)  
[kafka中文](https://xiaomingtongxie.gitbooks.io/kafka-tutorial-cn/content/)  
[Kafka管理](https://blog.51cto.com/9291927/2497842)  

[Kafka快速入门](https://blog.51cto.com/9291927/2497822)  

