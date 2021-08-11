- [Kafka自带的压力测试脚本](#Kafka自带的压力测试脚本)
- [Linux查看实时网卡流量和网卡支持的速率](#Linux查看实时网卡流量和网卡支持的速率)
- [Kafka参数调优](#Kafka参数调优)

---------------------------------------------------------------------------------------------------------------------

## Kafka自带的压力测试脚本


bin/kafka-topics.sh --create --replication-factor 3 --partitions 4 --topic bkk.item.tradetgt.count --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094
bin/kafka-topics.sh --delete --topic bkk.item.tradetgt.count --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094


bin/kafka-producer-perf-test.sh  --topic bkk.item.tradetgt.count --record-size 2000 --num-records 100000000 --throughput -1 --producer-props bootstrap.servers=127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094
bin/kafka-consumer-perf-test.sh --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094 --topic bkk.item.tradetgt.count --fetch-size 500 --messages 1000000


bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic bkk.item.tradetgt.count --time -1 --broker-list 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094
bin/kafka-consumer-groups.sh --group lengfeng.consumer.group  --describe  --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094




[kafka项目经验之如何进行Kafka压力测试、如何计算Kafka分区数、如何确定Kaftka集群机器数量](https://www.cnblogs.com/sunbr/p/14334718.html)
[kafka 压力测试](https://www.jianshu.com/p/1dd19b9628e3)  
[性能测试工具 Kafka 消息迁移工具的压测与调优](https://testerhome.com/topics/25609)  
[Kafka压力测试(写入MQ消息压测和消费MQ消息压测）](https://blog.csdn.net/laofashi2015/article/details/81111466)  
[]()  




---------------------------------------------------------------------------------------------------------------------
## Linux查看实时网卡流量和网卡支持的速率

sar –n DEV 1 2
命令后面1 2 意思是：每一秒钟取1次值，取2次。


ethtool ethx 查看支持的速率


[详解Linux查看实时网卡流量的几种方式](https://www.jb51.net/article/112965.htm)  
[linux查看网卡支持的速率](https://blog.csdn.net/whatday/article/details/89337431)  
[]()



---------------------------------------------------------------------------------------------------------------------
## Kafka参数调优

[Kafka参数调优](https://blog.csdn.net/yangshengwei230612/article/details/109485458)  
[千亿级数据量kafka集群性能调优实战总结](https://gaofeng.blog.csdn.net/article/details/107103505)  
[kafka消费者配置参数调优](https://blog.csdn.net/a822631129/article/details/109532993)  



---------------------------------------------------------------------------------------------------------------------


