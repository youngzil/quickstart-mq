#!/bin/bash

#BASE_DIR=$(dirname $0)
#echo $BASE_DIR

BASE_DIR=/Users/lengfeng/kafka
#KAFKA_VERSION=kafka_2.13-2.8.0
KAFKA_VERSION=kafka_2.12-1.0.0
KAFKA_HOME=/Users/lengfeng/kafka/$KAFKA_VERSION

function installSingleNode()
{
  mkdir -p $BASE_DIR/config
  mkdir -p $BASE_DIR/data/zookeeper1
  mkdir -p $BASE_DIR/data/zookeeper2
  mkdir -p $BASE_DIR/data/zookeeper3
  mkdir -p $BASE_DIR/data/kafka1
  mkdir -p $BASE_DIR/data/kafka2
  mkdir -p $BASE_DIR/data/kafka3
  mkdir -p $BASE_DIR/logs1
  mkdir -p $BASE_DIR/logs2
  mkdir -p $BASE_DIR/logs3

  cp config/zookeeper.properties $BASE_DIR/config/zookeeper1.properties
  cp config/zookeeper.properties $BASE_DIR/config/zookeeper2.properties
  cp config/zookeeper.properties $BASE_DIR/config/zookeeper3.properties
  echo "1" > $BASE_DIR/data/zookeeper1/myid
  echo "2" > $BASE_DIR/data/zookeeper2/myid
  echo "3" > $BASE_DIR/data/zookeeper3/myid

  cp config/server.properties $BASE_DIR/config/server1.properties
  cp config/server.properties $BASE_DIR/config/server2.properties
  cp config/server.properties $BASE_DIR/config/server3.properties

  cd $BASE_DIR

  # mac下sed命令有''
  sed -i '' "s/2181/2181/g" config/zookeeper1.properties
  sed -i '' "s/2181/2182/g" config/zookeeper2.properties
  sed -i '' "s/2181/2183/g" config/zookeeper3.properties

  sed -i '' "s/zookeeper/zookeeper1/g" config/zookeeper1.properties
  sed -i '' "s/zookeeper/zookeeper2/g" config/zookeeper2.properties
  sed -i '' "s/zookeeper/zookeeper3/g" config/zookeeper3.properties

  sed -i '' "s/logs/logs1/g" config/zookeeper1.properties
  sed -i '' "s/logs/logs2/g" config/zookeeper2.properties
  sed -i '' "s/logs/logs3/g" config/zookeeper3.properties

  # mac下sed命令有''
  sed -i '' "s/9092/9092/g" config/server1.properties
  sed -i '' "s/9092/9093/g" config/server2.properties
  sed -i '' "s/9092/9094/g" config/server3.properties

  sed -i '' "s/broker.id=0/broker.id=1/g" config/server1.properties
  sed -i '' "s/broker.id=0/broker.id=2/g" config/server2.properties
  sed -i '' "s/broker.id=0/broker.id=3/g" config/server3.properties

  sed -i '' "s/data\/kafka/data\/kafka1/g" config/server1.properties
  sed -i '' "s/data\/kafka/data\/kafka2/g" config/server2.properties
  sed -i '' "s/data\/kafka/data\/kafka3/g" config/server3.properties

  tar -xzvf $KAFKA_VERSION.tgz

  ## 指定kafka的日志路径
  export LOG_DIR=$BASE_DIR/logs
#  export JAVA_HOME=/data/program/java

  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $BASE_DIR/config/zookeeper1.properties > ${LOG_DIR}1/zookeeper-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $BASE_DIR/config/zookeeper2.properties > ${LOG_DIR}2/zookeeper-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $BASE_DIR/config/zookeeper3.properties > ${LOG_DIR}3/zookeeper-start.log 2>&1 &

  sleep 5

  nohup $KAFKA_HOME/bin/kafka-server-start.sh $BASE_DIR/config/server1.properties > ${LOG_DIR}1/kafka-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/kafka-server-start.sh $BASE_DIR/config/server2.properties > ${LOG_DIR}2/kafka-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/kafka-server-start.sh $BASE_DIR/config/server3.properties > ${LOG_DIR}3/kafka-start.log 2>&1 &

}


function installSingleNode2()
{
  cd $BASE_DIR


  ## 指定kafka的日志路径
  export LOG_DIR=$BASE_DIR/logs
#  export JAVA_HOME=/data/program/java

  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $BASE_DIR/config/zookeeper1.properties > ${LOG_DIR}1/zookeeper-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $BASE_DIR/config/zookeeper2.properties > ${LOG_DIR}2/zookeeper-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/zookeeper-server-start.sh $BASE_DIR/config/zookeeper3.properties > ${LOG_DIR}3/zookeeper-start.log 2>&1 &

  sleep 5

  nohup $KAFKA_HOME/bin/kafka-server-start.sh $BASE_DIR/config/server1.properties > ${LOG_DIR}1/kafka-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/kafka-server-start.sh $BASE_DIR/config/server2.properties > ${LOG_DIR}2/kafka-start.log 2>&1 &
  nohup $KAFKA_HOME/bin/kafka-server-start.sh $BASE_DIR/config/server3.properties > ${LOG_DIR}3/kafka-start.log 2>&1 &

  nohup /Users/lengfeng/kafka/kafka_2.12-1.0.0/bin/kafka-server-start.sh /Users/lengfeng/kafka/config/server3.properties > /Users/lengfeng/kafka/logs3/kafka-start.log 2>&1 &

}


index=0

function installManyNodes()
{
  cat config/hostlist | while read line
    do
      host=`echo "$line"|awk '{print $1}'`
      username=`echo "$line"|awk '{print $2}'`
      passwd=`echo "$line"|awk '{print $3}'`

      ((index++));

      echo $index $host $username $passwd

      cp config/server.properties config/server$index.properties
      cp config/zookeeper.properties config/zookeeper$index.properties

      # mac下sed命令有''
      sed -i '' "s/127.0.0.1/$host/g" config/server$index.properties
      sed -i '' "s/broker.id=0/broker.id=$index/g" config/server$index.properties
#      sed -i "s/127.0.0.1/$host/g" config/server.properties

      scp config/kafka_2.13-2.8.0.tgz ${username}@$host:$BASE_DIR
      scp config/zookeeper.properties ${username}@$host:$BASE_DIR/zookeeper.properties
      scp config/server$index.properties ${username}@$host:$BASE_DIR/server.properties

      scp config/startZookeeper.sh ${username}@$host:$BASE_DIR
      scp config/startKafka.sh ${username}@$host:$BASE_DIR

      rm -rf config/server$index.properties

      ssh -n ${username}@$host "cd $BASE_DIR;
      tar -xzvf $KAFKA_VERSION.tgz;
      mkdir -p $BASE_DIR/data/zookeeper;
      mkdir -p $BASE_DIR/data/kafka;
      mkdir -p $BASE_DIR/logs;
      mv zookeeper.properties $KAFKA_VERSION/config;
      mv server.properties $KAFKA_VERSION/config;
      mv startZookeeper.sh $KAFKA_VERSION;
      mv startKafka.sh $KAFKA_VERSION;
      echo $index > $BASE_DIR/data/zookeeper/myid"

    done

}

source ./cluster-install-kafka.sh
source ./cluster-install-zookeeper.sh

#deployKafka
#deployZookeeper

installSingleNode
#installSingleNode2

# bin/kafka-server-stop.sh
# bin/zookeeper-server-stop.sh

#bin/kafka-topics.sh --list --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094
#bin/kafka-topics.sh --create --replication-factor 3 --partitions 6 --topic topic03 --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094

#bin/kafka-console-producer.sh --topic topic03 --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094

#bin/kafka-console-consumer.sh --topic alarm --from-beginning --bootstrap-server 127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094

