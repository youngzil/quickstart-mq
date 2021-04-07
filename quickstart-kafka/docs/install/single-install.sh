#!/bin/bash

#export BASE_PATH=`cd $(dirname -- $0)/..; pwd`
export BASE_PATH=`cd $(dirname -- $0); pwd`
#export BASE_PATH=`pwd`

echo "BASE_PATH=$BASE_PATH"

#解压Kafka
cd $BASE_PATH
tar -xzvf kafka_2.13-2.5.0.tgz
cd kafka_2.13-2.5.0


# Start the ZooKeeper service
# Note: Soon, ZooKeeper will no longer be required by Apache Kafka.
nohup sh bin/zookeeper-server-start.sh config/zookeeper.properties >/dev/null 2>&1 &


# Start the Kafka broker service
nohup sh bin/kafka-server-start.sh config/server.properties >/dev/null 2>&1 &

#STEP 3: CREATE A TOPIC TO STORE YOUR EVENTS
bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092

#查询Topic信息
bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092


#STEP 4: WRITE SOME EVENTS INTO THE TOPIC
#发送消息
#You can stop the producer client with Ctrl-C at any time.
#bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092


#STEP 5: READ THE EVENTS
#消费消息
#You can stop the consumer client with Ctrl-C at any time.
#bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092


# bin/kafka-server-stop.sh
# bin/zookeeper-server-stop.sh

