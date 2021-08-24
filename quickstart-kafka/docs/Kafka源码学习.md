- [Kafka2.X服务端启动源码](#Kafka2.X服务端启动源码)
- [Kafka日志文件](#Kafka日志文件)
- [](#)
- [](#)





---------------------------------------------------------------------------------------------------------------------

## Kafka2.X服务端启动源码

[Kafka服务端源码启动和调试](Kafka服务端源码启动和调试.md)

Kafka 服务端通过Kafka.scala的主函数main方法启动。KafkaServerStartable类提供读取配置文件、启动/停止服务的方法。而启动/停止服务最终调用的是KafkaServer的startup/shutdown方法。



[Kafka2.0服务端启动源码](https://blog.csdn.net/shenmeshia/article/details/96391321)  



[]()  
[消息中间件—简谈Kafka中的NIO网络通信模型](https://www.jianshu.com/p/a6b9e5342878)  
[Kafka源码分析-Content Table](https://www.jianshu.com/p/aa274f8fe00f)  
[]()  
[]()  
[如何获取kafka某一topic中最新的offset？](https://www.zhihu.com/question/48611929)  
[]()  



---------------------------------------------------------------------------------------------------------------------
## Kafka日志文件

服务端日志：
controller.log
kafka-authorizer.log
state-change.log
log-cleaner.log
kafka-request.log
server.log


GC日志：
kafkaServer-gc.log
zookeeper-gc.log


自定义的启动日志：
kafka.log
zookeeper.log


---------------------------------------------------------------------------------------------------------------------









---------------------------------------------------------------------------------------------------------------------



