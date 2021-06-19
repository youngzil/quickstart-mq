- [Kafka配置源码类](#Kafka配置源码类)
- [Broker Configs](#Broker-Configs)
- [Topic Configs](#Topic-Configs)
- [Producer Configs](#Producer-Configs)
- [Consumer Configs](#Consumer-Configs)
- [AdminClient Configs](#AdminClient-Configs)
- [Kafka Connect Configs](#Kafka-Connect-Configs)
- [Kafka Streams Configs](#Kafka-Streams-Configs)



[Kafka官网配置说明文档](https://kafka.apache.org/documentation/#configuration)


---------------------------------------------------------------------------------------------------------------------

## Broker Configs

kafka.server.KafkaConfig

---------------------------------------------------------------------------------------------------------------------

## Topic Configs

---------------------------------------------------------------------------------------------------------------------

## Producer Configs

---------------------------------------------------------------------------------------------------------------------

## Consumer Configs

---------------------------------------------------------------------------------------------------------------------

## AdminClient Configs

---------------------------------------------------------------------------------------------------------------------

## Kafka Connect Configs

---------------------------------------------------------------------------------------------------------------------

## Kafka Streams Configs

---------------------------------------------------------------------------------------------------------------------

## Kafka配置源码类

大多数都是org.apache.kafka.common.config.AbstractConfig的子类


Broker
---------------
kafka.server.KafkaConfig kafka.admin.BrokerApiVersionsCommand.AdminConfig kafka.log.LogConfig




commomn
---------------
org.apache.kafka.common.config.TopicConfig

org.apache.kafka.common.config.SslConfigs org.apache.kafka.common.config.SecurityConfig
org.apache.kafka.common.config.SaslConfigs

org.apache.kafka.common.config.LogLevelConfig





client
---------------
org.apache.kafka.clients.producer.ProducerConfig org.apache.kafka.clients.consumer.ConsumerConfig
org.apache.kafka.clients.admin.AdminClientConfig




Connector
---------------
org.apache.kafka.connect.runtime.ConnectorConfig org.apache.kafka.connect.runtime.SinkConnectorConfig
org.apache.kafka.connect.runtime.SourceConnectorConfig

org.apache.kafka.connect.runtime.WorkerConfig org.apache.kafka.connect.runtime.TaskConfig

org.apache.kafka.connect.runtime.standalone.StandaloneConfig
org.apache.kafka.connect.runtime.distributed.DistributedConfig

org.apache.kafka.connect.storage.ConverterConfig org.apache.kafka.connect.storage.StringConverterConfig

org.apache.kafka.connect.json.JsonConverterConfig




Streams
---------------
org.apache.kafka.streams.StreamsConfig



---------------------------------------------------------------------------------------------------------------------

