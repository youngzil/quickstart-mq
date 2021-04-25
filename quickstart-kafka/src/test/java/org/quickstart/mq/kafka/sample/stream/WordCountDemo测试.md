tar -xzvf kafka_2.13-2.7.0.tgz
cd kafka_2.13-2.7.0



bin/zookeeper-server-start.sh config/zookeeper.properties

bin/kafka-server-start.sh config/server.properties


接下来，我们创建一个输入主题“streams-plaintext-input”，和一个输出主题"streams-wordcount-output":

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-plaintext-input

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic streams-wordcount-output --config cleanup.policy=compact


bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe



启动 WordCountDemo.java


第一个终端发送消息

bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic streams-plaintext-input


第二个终端消费经过stream处理后的数据

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic streams-wordcount-output \
--from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer




参考 [Kafka Stream演示程序](https://www.orchome.com/936)


