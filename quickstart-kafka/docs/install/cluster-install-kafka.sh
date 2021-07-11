#!/bin/bash


function deploy_kafka()
{
  cat config/hostlist | while read line
    do
      host=`echo "$line"|awk '{print $1}'`
      username=`echo "$line"|awk '{print $2}'`
      passwd=`echo "$line"|awk '{print $3}'`

      echo "kafka" $host $username $passwd


  #    ssh -n ${username}@$host "hostname -i;
  #    cd /data/program/kafka_2.13-2.8.0;
  #    nohup sh bin/kafka-server-start.sh config/server.properties >../logs/kafka/kafka.log 2>&1 &"

    done
}
