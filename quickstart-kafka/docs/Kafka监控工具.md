- [Kafka Metrics监控](#Kafka-Metrics监控)
- [Prometheus监控Kafka](#Prometheus监控Kafka)
- [主流的三种kafka监控程序](#主流的三种kafka监控程序)

---------------------------------------------------------------------------------------------------------------------
## Kafka Metrics监控


[Kafka官方监控文档](https://kafka.apache.org/documentation.html#monitoring)  
[]()  
[]()  
[]()  
[Monitoring Kafka performance metrics](https://www.datadoghq.com/blog/monitoring-kafka-performance-metrics/#monitor-your-kafka-deployment)  
[Monitoring Kafka](https://docs.confluent.io/platform/current/kafka/monitoring.html)  
[Kafka Metrics模块解析](https://blog.csdn.net/u010952362/article/details/103808259)  
[Kafka学习之监控](https://blog.csdn.net/damacheng/article/details/84612384)  
[Confluent Metrics Reporter](https://docs.confluent.io/platform/current/kafka/metrics-reporter.html)  
[]()  
[]()  
[]()  
[Default JMX Metrics for Apache Kafka Backends](https://docs.appdynamics.com/21.4/en/application-monitoring/tiers-and-nodes/monitor-jmx/default-jmx-metrics-for-apache-kafka-backends)  
[Kafka monitoring](https://www.dynatrace.com/support/help/technology-support/application-software/other-technologies/supported-out-of-the-box/kafka/)  
[](https://docs.signalfx.com/en/latest/integrations/integrations-reference/integrations.kafka.html)  
[Kafka monitoring integration](https://docs.newrelic.com/docs/integrations/host-integrations/host-integrations-list/kafka-monitoring-integration/)  
[Monitoring Kafka](https://www.instana.com/docs/ecosystem/kafka/)  
[Kafka Metrics to Monitor](https://sematext.com/blog/kafka-metrics-to-monitor/#toc-network-request-rate-2)  
[Kafka Metrics](https://techdocs.broadcom.com/us/en/ca-enterprise-software/it-operations-management/dx-apm-saas/SaaS/implementing-agents/infrastructure-agent/kafka-monitoring/kafka-metrics.html)  
[Apache Kafka JMX Metrics](https://docs.sysdig.com/en/apache-kafka-jmx-metrics.html)  
[kafka-manage-metrics](https://docs.cloudera.com/runtime/7.2.9/kafka-managing/topics/kafka-manage-metrics.html)  
[Kafka Metrics](https://github.com/amient/kafka-metrics)  



---------------------------------------------------------------------------------------------------------------------
## Prometheus监控Kafka

[Prometheus node_exporter介绍分类](https://prometheus.io/docs/instrumenting/exporters/)

[Kafka exporter for Prometheus](https://github.com/danielqsj/kafka_exporter)  
[kafka-exporter docker hub](https://hub.docker.com/r/danielqsj/kafka-exporter)  

[Monitor Apache Kafka with Prometheus and Grafana](https://computingforgeeks.com/monitor-apache-kafka-with-prometheus-and-grafana/)  
[Monitoring Kafka with Kafka exporter + Prometheus + Grafana](https://danielmrosa.medium.com/monitoring-kafka-b97d2d5a5434)  
[Monitoring Your Event Streams: Integrating Confluent with Prometheus and Grafana](https://www.confluent.io/blog/monitor-kafka-clusters-with-prometheus-grafana-and-confluent/)  
[Kafka Monitoring Using Prometheus](https://www.metricfire.com/blog/kafka-monitoring-using-prometheus)  
[Kafka Monitoring via Prometheus-Grafana](https://dzone.com/articles/kafka-monitoring-via-prometheus-amp-grafana)  
[Monitoring Kafka with Prometheus and Grafana](https://blog.knoldus.com/monitoring-kafka-with-prometheus-and-grafana/)  
[Kafka - Monitor producer metrics using JMX, Prometheus and Grafana](https://www.linkedin.com/pulse/kafka-monitor-producer-metrics-using-jmx-prometheus-hari-ramesh/?trk=read_related_article-card_title)  
[Monitoring Apache Kafka with Prometheus](https://banzaicloud.com/blog/monitoring-kafka-prometheus/)  
[Monitoring Kafka with Prometheus](https://www.robustperception.io/monitoring-kafka-with-prometheus)  
[]()  
[通过阿里云Prometheus监控Kafka应用](https://help.aliyun.com/document_detail/141108.html)  
[]()  
[]()  


[Kakfa dashboards模板1](https://grafana.com/grafana/dashboards/7589)  
[Kakfa dashboards模板2](https://grafana.com/grafana/dashboards/721)

[Monitoring Kafka with Prometheus](https://www.robustperception.io/monitoring-kafka-with-prometheus)



[消费kafka topic数据到prometheus](https://github.com/ogibayashi/kafka-topic-exporter)  
[kafka-lag-prometheus-exporter示例代码](https://github.com/skalogs/kafka-lag-prometheus-exporter)  



[使用 prometheus jmx_exporter监控kafka](https://github.com/jianzhiunique/kafka-jmx-exporter)  
[prometheus jmx_exporter地址](https://github.com/prometheus/jmx_exporter)  


[Kafka Lag Exporter地址](https://github.com/lightbend/kafka-lag-exporter)  
[Kafka Lag Exporter部署使用](https://github.com/cspinetta/kafka-lag-exporter-standalone)  
[Monitor Kafka Consumer Group Latency with Kafka Lag Exporter](https://www.lightbend.com/blog/monitor-kafka-consumer-group-latency-with-kafka-lag-exporter)  





在docs/install/docker/image下面构建镜像
docker build -t quickstart/kafka:2.0 .


配置
docker-compose-single.yml
和
prometheus-single.yml

访问
http://localhost:7071/

http://localhost:9308/
http://localhost:9308/metrics

http://localhost:9090/

http://localhost:3000


http://172.16.113.4:9090



配置host
127.0.0.1 kafka


private static final String brokerList = "localhost:9092";













集群监控：

配置
docker-compose-cluster.yml
和
prometheus2.yml

访问
http://localhost:7071/
http://localhost:7072/
http://localhost:7073/


http://localhost:9308/
http://localhost:9308/metrics


http://localhost:9090/


http://localhost:3000/



127.0.0.1	kafka1
127.0.0.1	kafka2
127.0.0.1	kafka3

private static final String brokerList = "localhost:9092,localhost:9093,localhost:9094";


http://172.16.113.4:9090

---------------------------------------------------------------------------------------------------------------------
## 主流的三种kafka监控程序

通过研究，发现主流的三种kafka监控程序分别为：

[Kafka Web Conslole](https://github.com/claudemamo/kafka-web-console)  
[Cluster Manager for Apache Kafka](https://github.com/yahoo/CMAK) ：以前叫[Kafka Manager](https://github.com/yahoo/kafka-manager)  
[KafkaOffsetMonitor](https://github.com/quantifind/KafkaOffsetMonitor)  




一、Kafka Monitoring

二、JmxTool

三、Kafka-Manager

四、kafka-monitor

五、Kafka Offset Monitor

六、Cruise-control

七、Doctorkafka

八、Burrow


[Kafka监控工具汇总](https://juejin.cn/post/6844903922612568078)  



KafkaOffsetMonitor：监控消费者和延迟的队列



kafka监控  
https://www.oschina.net/p/kafka-manager  
https://www.oschina.net/p/kafka-monitor  


kafka管理器kafka-manager部署安装  
http://blog.csdn.net/lsshlsw/article/details/47300145


客户端可视化工具  
https://blog.csdn.net/Dongguabai/article/details/86526299  



相关文章：  
http://blog.csdn.net/dabokele/article/details/52373960  
http://blog.csdn.net/chuntian_feng/article/details/51871648  
https://www.iteblog.com/archives/1084.html  
http://blog.csdn.net/lsshlsw/article/details/47300145  






---------------------------------------------------------------------------------------------------------------------


