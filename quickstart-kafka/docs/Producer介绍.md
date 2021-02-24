




---------------------------------------------------------------------------------------------------------------------
[生产者配置官方文档](http://kafka.apache.org/documentation.html#producerconfigs)  



Kafka Producer

在开发生产的时候，先简单的介绍下kafka各种配置说明：
bootstrap.servers： kafka的地址。
acks:消息的确认机制，默认值是0。
acks=0：如果设置为0，生产者不会等待kafka的响应。
acks=1：这个配置意味着kafka会把这条消息写到本地日志文件中，但是不会等待集群中其他机器的成功响应。
acks=all：这个配置意味着leader会等待所有的follower同步完成。这个确保消息不会丢失，除非kafka集群中所有机器挂掉。这是最强的可用性保证。
retries：配置为大于0的值的话，客户端会在消息发送失败时重新发送。
batch.size:当多条消息需要发送到同一个分区时，生产者会尝试合并网络请求。这会提高client和生产者的效率。
key.serializer: 键序列化，默认org.apache.kafka.common.serialization.StringDeserializer。
value.deserializer:值序列化，默认org.apache.kafka.common.serialization.StringDeserializer。

还有更多配置，可以去查看官方文档，这里就不在说明了。





---------------------------------------------------------------------------------------------------------------------


[kafka的使用示例](https://github.com/cocowool/sh-valley/tree/master/java/java-kafka)  







