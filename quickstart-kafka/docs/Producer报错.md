
---------------------------------------------------------------------------------------------------------------------

## Kafka生产者消息分区机制

Partitioner接口
- DefaultPartitioner随机策略
- RoundRobinPartitioner轮询策略
- UniformStickyPartitioner黏性分区策略


DefaultPartitioner随机策略

key不为null时，对key进行hash（基于murmurHash2算法），根据最终得到的hash值计算分区号，有相同key的消息会被写入同样的分区；key为null时，2.3版本使用轮询分区策略，2.4版本使用黏性分区策略。


UniformStickyPartitioner，黏性分区策略。

黏性分区策略通过“黏贴”到分区直到批记录已满（或在linger.ms启动时发送），与轮询策略相比，我们可以创建更大的批记录并减少系统中的延迟。即使在linger.ms为零立即发送的情况下，也可以看到改进的批处理和减少的延迟。在创建新批处理时更改粘性分区，随着时间的流逝，记录应该在所有分区之间是平均分配的。2.4版本key=null时默认使用黏性分区策略

如果使用默认的轮询partition策略，可能会造成一个大的batch被轮询成多个小的batch的情况。鉴于此，kafka2.4的时候推出一种新的分区策略，即Sticky Partitioning Strategy，Sticky Partitioning Strategy会随机地选择另一个分区并会尽可能地坚持使用该分区——即所谓的粘住这个分区。

鉴于小batch可能导致延时增加，之前对于无Key消息的分区策略效率很低。社区于2.4版本引入了黏性分区策略（Sticky Partitioning Strategy）。该策略是一种全新的策略，能够显著地降低给消息指定分区过程中的延时。

使用Sticky Partitioner有助于改进消息批处理，减少延迟，并减少broker的负载。





[kafka生产者消息分区机制](https://huagetai.github.io/posts/fabbb24d/)  
[kafka能够从follower副本读数据](https://zhuanlan.zhihu.com/p/324497008)  
[KafkaProducer之Partitioner](https://www.jianshu.com/p/d8abcd93b4b9)  
[]()  
[]()  



---------------------------------------------------------------------------------------------------------------------

### 生产者FailedToSendMessageException问题

Exception in thread "main" kafka.common.FailedToSendMessageException: Failed to send messages after 3 tries.
at kafka.producer.async.DefaultEventHandler.handle(DefaultEventHandler.scala:90)
at kafka.producer.Producer.send(Producer.scala:76)
at kafka.javaapi.producer.Producer.send(Producer.scala:33)
at com.tuan55.kafka.test.TestP.main(TestP.java:20)

这是生产者的代码 在向服务器发起连接后，在kafka的服务器配置中有zookeeper.connect=xx.xx.xx.xx：2181的配置 这时候kafka会查找zookeeper。

那么如果我们的hosts 中没有做hosts的配置 kafka经多次尝试连接不上就会报上面的错误。

kafka的server.properties这个配置文件是有配置这两项的地方。 里面也有zookeeper的连接路径，也可以改一下。因为我在同一台机器上跑的，跟默认localhost一样，没必要改。

host.name=172.x.x.x    //这里是腾讯云的内网地址
advertised.host.name=211.159.160.xxx    //这里是腾讯云的外网IP


解决办法：配置hosts文件 做zookeeper服务器的映射配置。
出现此种错误 还有一种情况
# Hostname the broker will advertise to producers and consumers. If not set, it uses the
# value for "host.name" if configured.  Otherwise, it will use the value returned from
# java.net.InetAddress.getCanonicalHostName().
advertised.host.name=192.168.1.118
远程连接的话 是根据这个配置来找broker的，默认是localhost ，所以如果不是本机运行的话 应该设置此值 来确保通信畅通。



参考  
https://blog.csdn.net/qq_38872310/article/details/80091581

---------------------------------------------------------------------------------------------------------------------
