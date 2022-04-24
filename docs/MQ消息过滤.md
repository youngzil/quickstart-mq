# MQ消息过滤

## RocketMQ实现

- Tag过滤（默认过滤方式）：发送者设置一个tag，消费者可以订阅多个tag，过滤逻辑在client
- SQL属性过滤

[RocketMQ消息过滤](https://help.aliyun.com/document_detail/29543.html)

[消息过滤](https://cloud.tencent.com/developer/article/1163426)



## spring-integration-kafka实现

使用的是Client注册一个RecordFilterStrategy来过滤，过滤逻辑也是在Client




