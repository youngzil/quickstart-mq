#!/bin/bash

function printTopicV1() {
  topics=`bin/kafka-topics.sh --list --zookeeper 10.1.120.6:2181,10.1.120.7:2181,10.1.120.8:2181/kafka_log_2`

  for topic in ${topics[*]};
  do
    echo $topic >> allTopics.log
  done

}

cleanTopicV2
