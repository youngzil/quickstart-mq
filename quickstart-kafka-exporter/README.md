

配置启动类
--kafka-host localhost

访问
http://localhost:7979/metrics




创建数据源prometheus

使用主机IP
http://172.16.113.4:9090

选择模板721，选择prometheus数据源即可



参考  

[kafka-lag-prometheus-exporter示例代码](https://github.com/skalogs/kafka-lag-prometheus-exporter)  
[消费kafka topic数据到prometheus](https://github.com/ogibayashi/kafka-topic-exporter)  

[Monitor Kafka Consumer Group Latency with Kafka Lag Exporter](https://github.com/lightbend/kafka-lag-exporter)  






jmx_prometheus_javaagent-0.16.1.jar
1. 只能在Kafka服务端采集kafka.server的指标
2. 在客户端按照server一样部署，采集不到任何数据

可以采集的主要指标：
1. topic相关：开始和结束offset、segment数量、文件字节大小
   kafka_log_log_logstartoffset{topic="topic03",partition="0",} 0.0
   kafka_log_log_logendoffset{topic="topic03",partition="0",} 2000000.0
   kafka_log_log_numlogsegments{topic="topic03",partition="0",} 1.0
   kafka_log_log_size{topic="topic03",partition="0",} 1.26257873E8
2. broker相关：发送消息数量，生产消费字节数，生产消费请求数
   kafka_server_brokertopicmetrics_messagesin_total{topic="topic03",} 2000000.0
   kafka_server_brokertopicmetrics_bytesout_total{topic="topic03",} 6.325713E7
   kafka_server_brokertopicmetrics_bytesin_total{topic="topic03",} 1.26257873E8
   kafka_server_brokertopicmetrics_totalfetchrequests_total{topic="topic03",} 490.0
   kafka_server_brokertopicmetrics_totalproducerequests_total{topic="topic03",} 7726.0





kafka-exporter
1. 可以采集到使用者经常关注的指标

1. broker数量
   kafka_brokers 1

2. 消费组相关
   kafka_consumergroup_current_offset{consumergroup="lengfeng.consumer.group",partition="0",topic="topic03"} 1.574722e+06
   kafka_consumergroup_current_offset_sum{consumergroup="lengfeng.consumer.group",topic="topic03"} 1.574722e+06
   kafka_consumergroup_lag{consumergroup="lengfeng.consumer.group",partition="0",topic="topic03"} 1.792037e+06
   kafka_consumergroup_lag_sum{consumergroup="lengfeng.consumer.group",topic="topic03"} 1.792037e+06
   kafka_consumergroup_members{consumergroup="lengfeng.consumer.group"} 1

3. topic partition相关的
   kafka_topic_partition_oldest_offset{partition="0",topic="topic03"} 0
   kafka_topic_partition_current_offset{partition="0",topic="topic03"} 3.366759e+06
   kafka_topic_partitions{topic="topic03"} 1

kafka_topic_partition_in_sync_replica{partition="0",topic="topic03"} 1
kafka_topic_partition_leader{partition="0",topic="topic03"} 0
kafka_topic_partition_leader_is_preferred{partition="0",topic="topic03"} 1
kafka_topic_partition_replicas{partition="0",topic="topic03"} 1
kafka_topic_partition_under_replicated_partition{partition="0",topic="topic03"} 0






---------------------------------------------------------------------------------------------------------------------









