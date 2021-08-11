#!/bin/bash

#chmod +x queryTopic.sh
#crontab -e
#0 2 * * * /data/program/kafka/queryTopic.sh

#KAFKA_HOME="/data/program/kafka/kafka_2.12-1.0.0"
#ZK_SERVER="10.1.120.6:2181,10.1.120.7:2181,10.1.120.8:2181/kafka_log_2"
#BROKER_SERVER="10.1.120.6:9094,10.1.120.7:9094,10.1.120.8:9094"

KAFKA_HOME="/data/program/kafka/kafka_2.13-2.8.0"
ZK_SERVER="172.16.48.179:2181,172.16.48.180:2181,172.16.48.181:2181/kfk1"
BROKER_SERVER="172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081"

function printTopicV1() {
  export JAVA_HOME=/data/program/java
  time=$(date "+%Y%m%d-%H%M%S")

  topics=`$KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper $ZK_SERVER`

  for topic in ${topics[*]};
  do
    echo $topic >> /data/program/kafka/allTopics$time.log

    topicPartitionMinOffset=`$KAFKA_HOME/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -2 --broker-list $BROKER_SERVER`
    topicPartitionMaxOffset=`$KAFKA_HOME/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -1 --broker-list $BROKER_SERVER`

    echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset" >> /data/program/kafka/allTopicsOffset$time.log

    if [ "$topicPartitionMinOffset" = "$topicPartitionMaxOffset" ];then
      if [[ $topic =~ ^__.* ]]; then
        echo "system topic $topic"
      else
        echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset" >> /data/program/kafka/noMessageTopics$time.log
      fi
    fi

  done

}

function printTopicV2() {
  export JAVA_HOME=/data/program/java
  time=$(date "+%Y%m%d-%H%M%S")

  topics=`$KAFKA_HOME/bin/kafka-topics.sh --list --bootstrap-server $BROKER_SERVER`

  for topic in ${topics[*]};
  do
    echo $topic >> /data/program/kafka/allTopics$time.log

    topicPartitionMinOffset=`$KAFKA_HOME/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -2 --broker-list $BROKER_SERVER`
    topicPartitionMaxOffset=`$KAFKA_HOME/bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -1 --broker-list $BROKER_SERVER`

    echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset" >> /data/program/kafka/allTopicsOffset$time.log

    if [ "$topicPartitionMinOffset" = "$topicPartitionMaxOffset" ];then
      if [[ $topic =~ ^__.* ]]; then
        echo "system topic $topic"
      else
        echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset" >> /data/program/kafka/noMessageTopics$time.log
      fi
    fi

  done

}

printTopicV2
