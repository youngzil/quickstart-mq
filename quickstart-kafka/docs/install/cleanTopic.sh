#!/bin/bash

function printTopicV1() {
  topics=`bin/kafka-topics.sh --list --zookeeper 10.1.120.6:2181,10.1.120.7:2181,10.1.120.8:2181/kafka_log_2`
  echo $topics > test.log

  for topic in ${topics[*]};
  do
    topicPartitionMinOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -2 --broker-list 10.1.120.6:9094,10.1.120.7:9094,10.1.120.8:9094`
    topicPartitionMaxOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -1 --broker-list 10.1.120.6:9094,10.1.120.7:9094,10.1.120.8:9094`
    if [ "$topicPartitionMinOffset" != "$topicPartitionMaxOffset" ];then
      echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset"
      if [[ $topic =~ ^__.* ]]; then
        echo "system topic $topic"
      fi
      echo "--------------------------------------------------------------------------"
    fi
  done

}


function cleanTopicV1() {
  topics=`bin/kafka-topics.sh --list --zookeeper 10.1.120.6:2181,10.1.120.7:2181,10.1.120.8:2181/kafka_log_2`
  echo $topics > test.log

  for topic in ${topics[*]};
  do
    topicPartitionMinOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -2 --broker-list 10.1.120.6:9094,10.1.120.7:9094,10.1.120.8:9094`
    topicPartitionMaxOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -1 --broker-list 10.1.120.6:9094,10.1.120.7:9094,10.1.120.8:9094`

    echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset"

    if [ "$topicPartitionMinOffset" = "$topicPartitionMaxOffset" ];then
      if [[ $topic =~ ^__.* ]]; then
        echo "system topic $topic"
      else
        echo "delete topic $topic"
        deleteTopic=`bin/kafka-topics.sh --delete --topic $topic --zookeeper 10.1.120.6:2181,10.1.120.7:2181,10.1.120.8:2181/kafka_log_2`
      fi
    fi

    echo "--------------------------------------------------------------------------"
  done

}



function cleanTopicV2() {

  cd /data/program/kafka/kafka_2.13-2.8.0

  topics=`bin/kafka-topics.sh --list --bootstrap-server 172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081`
  echo $topics > test.log

#  echo $topics | while read topic
  for topic in ${topics[*]};
  do
    topicPartitionMinOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -2 --broker-list 172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081`
    topicPartitionMaxOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic $topic --time -1 --broker-list 172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081`

    echo "topic=$topic,topicPartitionMinOffset=$topicPartitionMinOffset,topicPartitionMaxOffset=$topicPartitionMaxOffset"

    if [ "$topicPartitionMinOffset" = "$topicPartitionMaxOffset" ];then
      if [[ $topic =~ ^__.* ]]; then
        echo "system topic $topic"
      else
        echo "delete topic $topic"
        deleteTopic=`bin/kafka-topics.sh --delete --topic $topic --bootstrap-server 172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081`
      fi
    fi

    echo "--------------------------------------------------------------------------"

  done
    
}

cleanTopicV2


#topicPartitionOffset=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic wke.alert.metrics --time -2 --broker-list 172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081`
#
#declare -A topicPartitionOffsetMap
#
#echo $check_results | while read line
#do
#  topic=`echo "$line" | awk -F ':' '{print $1}'`
#  partition=`echo "$line" | awk -F ':' '{print $2}'`
#  offset=`echo "$line" | awk -F ':' '{print $3}'`
#  topicPartitionOffsetMap["$topic$partition"]="$offset"
#done
#
#
#check_results=`bin/kafka-run-class.sh kafka.tools.GetOffsetShell --topic wke.alert.metrics --time -1 --broker-list 172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081`
#
#echo $check_results | while read line
#do
#  topic=`echo "$line" | awk -F ':' '{print $1}'`
#  partition=`echo "$line" | awk -F ':' '{print $2}'`
#  offset=`echo "$line" | awk -F ':' '{print $3}'`
#  topicPartitionOffsetMap["$topic$partition"]="$offset"
#done
#
#
#for key in ${!topicPartitionOffsetMap[@]}
#do
# echo "key:"$key
# echo "value:"${topicPartitionOffsetMap[$key]}
#done

