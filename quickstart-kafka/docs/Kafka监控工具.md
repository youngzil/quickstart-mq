- [Kafka Metrics监控](#Kafka-Metrics监控)
- [Prometheus监控Kafka](#Prometheus监控Kafka)
- [Kafka监控程序](#Kafka监控程序)

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

[Kafka Monitoring Using Prometheus](https://www.metricfire.com/blog/kafka-monitoring-using-prometheus)  


[prometheuns JMX配置文件](https://github.com/confluentinc/jmx-monitoring-stacks/tree/6.1.0-post/shared-assets/jmx-exporter)



[Monitor Apache Kafka with Prometheus and Grafana](https://computingforgeeks.com/monitor-apache-kafka-with-prometheus-and-grafana/)  
[Monitoring Kafka with Kafka exporter + Prometheus + Grafana](https://danielmrosa.medium.com/monitoring-kafka-b97d2d5a5434)  
[Monitoring Your Event Streams: Integrating Confluent with Prometheus and Grafana](https://www.confluent.io/blog/monitor-kafka-clusters-with-prometheus-grafana-and-confluent/)  

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
## Kafka监控程序

- [CMAK(以前叫[Kafka Manager])](#CMAK)
- [Offset Explorer](#Offset-Explorer)
- [Kafka Eagle（目前使用中）](#)
- [Kafka Center（最近才开源，没有做测试）](#)
- [Kafka Web Conslole(retired已经退休了)](#Kafka-Web-Conslole)
- [KafkaOffsetMonitor(Github上已经404了)](#KafkaOffsetMonitor) ：监控消费者和延迟的队列


- [Kafka Monitoring](#)
- [JmxTool](#)
- [kafka-monitor](#)
- [Cruise-control](#)
- [Doctorkafka](#)
- [Burrow](#)


[Kafka监控工具汇总](https://juejin.cn/post/6844903922612568078)




## CMAK

[Cluster Manager for Apache Kafka](https://github.com/yahoo/CMAK) ：以前叫[Kafka Manager](https://github.com/yahoo/kafka-manager)  

CMAK is a tool for managing Apache Kafka clusters

CMAK (Cluster Manager for Apache Kafka, previously known as Kafka Manager)

kafka管理器kafka-manager部署安装  
http://blog.csdn.net/lsshlsw/article/details/47300145




## Offset Explorer

[Offset Explorer (formerly Kafka Tool)](https://www.kafkatool.com/index.html)  

UI Tool for Apache Kafka  

Offset Explorer (formerly Kafka Tool) is a GUI application for managing and using Apache Kafka ® clusters. It provides an intuitive UI that allows one to quickly view objects within a Kafka cluster as well as the messages stored in the topics of the cluster. It contains features geared towards both developers and administrators. Some of the key features include
Offset Explorer（以前称为 Kafka Tool）是一个用于管理和使用 Apache Kafka ® 集群的 GUI 应用程序。 它提供了一个直观的 UI，允许人们快速查看 Kafka 集群中的对象以及存储在集群主题中的消息。 它包含面向开发人员和管理员的功能。 一些主要功能包括




## Kafka Web Conslole
[Kafka Web Conslole](https://github.com/claudemamo/kafka-web-console)  
A web console for Apache Kafka (retired)



## KafkaOffsetMonitor

[KafkaOffsetMonitor](https://github.com/quantifind/KafkaOffsetMonitor)  
Github上已经404了，[其他的拷贝地址](https://github.com/Morningstar/kafka-offset-monitor)





[华为云连接和查看Kafka Manager](https://support.huaweicloud.com/usermanual-kafka/kafka-ug-180801002.html)
[如何查看kafka消息](https://www.it-swarm.cn/zh/apache-kafka/%E5%A6%82%E4%BD%95%E6%9F%A5%E7%9C%8Bkafka%E6%B6%88%E6%81%AF/831961531/)

如果您希望通过编程方式执行此操作，则可以编写包装Kafka客户端（几乎每种语言都有一个）的应用程序，或使用内置kafka-console-consumer工具或 Kafkacat 稍微灵活一些（但缺点是您必须下载单独的工具，而kafka-console-consumer与Kafka捆绑在一起）。

如果只需要显示主题中最后几条消息的GUI，则可以使用 Kafdrop 或 Kafka Tool 。前者是基于Web的（springboot应用程序），而后者是基于Swing的桌面应用程序。


如果您正在寻找一种简单直观的方式来查看和搜索Apache Kafka消息，则应尝试 KaDeck 。该社区版本是完全免费的，它支持Win，Mac OS和Linux。

为了在企业环境中使用，还提供了一个企业版，其中包括一个Web服务。


关于Kafka的以下线程，对SO工具进行了不错的讨论：
[kafka是否具有任何默认的Web UI](https://stackoverflow.com/questions/49397126/does-kafka-have-any-default-web-ui/56333254)
[Kafka主题查看器？](https://stackoverflow.com/questions/54236902/kafka-topic-viewer/56334673)

如果您使用的是基于Web的工具，请尝试 Kafdrop （这是原始Kafdrop的复活版，现在大部分时间处于休眠状态）。它使您可以查看主题并为您提供集群配置（但这不是完整的管理工具）。这是一个简单的spring boot应用程序，并带有Docker构建。 （温和的免责声明：我是撰稿人之一，但我不是原始作者。）

对于命令行工具，请尝试 Kafkacat 用于浏览主题和发布消息。在撰写本文时，它还支持打印消息标题（与Kafka的内置工具不同）。



[Kafkacat](https://github.com/edenhill/kafkacat)  
[Kafdrop](https://github.com/obsidiandynamics/Kafdrop)  
[Kafka Tool](https://www.kafkatool.com/)  
[KaDeck](https://www.xeotek.com/)

KaDeck for Apache Kafka and Amazon Kinesis

[Amazon Kinesis Streams](https://docs.amazonaws.cn/zh_cn/aws/latest/userguide/kinesis.html)

Kinesis是一个流式传输数据的平台Amazon，可提供托管服务，让您能够轻松地加载和分析流数据，同时还可让您根据具体需求来构建自定义流数据应用程序。

Amazon Kinesis流是一项在实时处理大规模的数据流时可弹性扩展的托管服务。该服务收集大规模的数据记录流，随后供多个可在 EC2 实例上运行的数据处理应用程序实时使用。






kafka监控  
https://www.oschina.net/p/kafka-manager  
https://www.oschina.net/p/kafka-monitor  


客户端可视化工具  
https://blog.csdn.net/Dongguabai/article/details/86526299  


相关文章：  
http://blog.csdn.net/dabokele/article/details/52373960  
http://blog.csdn.net/chuntian_feng/article/details/51871648  
https://www.iteblog.com/archives/1084.html  
http://blog.csdn.net/lsshlsw/article/details/47300145  



---------------------------------------------------------------------------------------------------------------------


