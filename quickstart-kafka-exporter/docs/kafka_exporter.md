[kafka_exporter Github](https://github.com/danielqsj/kafka_exporter)


Kafka_exporter安装

#下载tar包
kafka_exporter-1.3.1.linux-amd64.tar.gz

#解压
tar -xzvf kafka_exporter-1.3.1.linux-amd64.tar.gz

进入到kafka_exporter目录，启动kafka监控端口：
cd kafka_exporter-1.3.1.linux-amd64

./kafka_exporter --kafka.server=kafkaIP或者域名:9092 &
./kafka_exporter --kafka.server=172.16.49.6:9093 &



比如：其实一个集群只要配置一个节点地址也是一个效果，下面其实就是一样的

./kafka_exporter --kafka.server=172.16.48.179:9081 --kafka.server=172.16.48.180:9081 --kafka.server=172.16.48.181:9081 &

./kafka_exporter --kafka.server=172.16.48.179:9081 &
./kafka_exporter --kafka.server=10.21.32.1:9092 &


./kafka_exporter --kafka.server=10.1.25.57:9092 &
/data/program/kafka/kafka_exporter-1.3.1.linux-amd64/kafka_exporter --kafka.server=10.1.25.57:9092 &

curl http://10.1.25.57:9308/metrics




[kafka exporter调研与改进](https://cloud.tencent.com/developer/article/1794971)
[Prometheus+Grafana+kafka_exporter监控kafka](https://blog.csdn.net/An1090239782/article/details/102994930)  
[Prometheus监控Kafka](https://blog.csdn.net/baidu_31618421/article/details/106774838)  




kafka-exporter 9308端口
1. 可以采集到使用者经常关注的指标

2. broker数量
   kafka_brokers 集群的broker数量

3. 消费组相关
   kafka_consumergroup_current_offset{consumergroup="lengfeng.consumer.group",partition="0",topic="topic03"} 1.574722e+06  消费进度的当前offset
   kafka_consumergroup_current_offset_sum{consumergroup="lengfeng.consumer.group",topic="topic03"} 1.574722e+06
   kafka_consumergroup_lag{consumergroup="lengfeng.consumer.group",partition="0",topic="topic03"} 1.792037e+06  消费进度的LAG
   kafka_consumergroup_lag_sum{consumergroup="lengfeng.consumer.group",topic="topic03"} 1.792037e+06
   kafka_consumergroup_members{consumergroup="lengfeng.consumer.group"} 1 消费组的消费者个数

4. topic partition相关的
   kafka_topic_partitions{topic="topic03"} 1  topic的partition数量
   kafka_topic_partition_oldest_offset{partition="0",topic="topic03"} 0  topic的partition的最老的offset
   kafka_topic_partition_current_offset{partition="0",topic="topic03"} 300  topic的partition的当前offset

kafka_topic_partition_in_sync_replica{partition="0",topic="topic03"} 3  在in_sync_replica的消费者个数
kafka_topic_partition_leader{partition="0",topic="topic03"} 0 leader的brokerId
kafka_topic_partition_leader_is_preferred{partition="0",topic="topic03"} 1
# HELP kafka_topic_partition_replicas Number of Replicas for this Topic/Partition
kafka_topic_partition_replicas{partition="0",topic="topic03"} 3 topic的partition的副本数
# HELP kafka_topic_partition_under_replicated_partition 1 if Topic/Partition is under Replicated
kafka_topic_partition_under_replicated_partition{partition="0",topic="topic03"} 0 如果是1，说明topic的partition是处于under Replicated状态




