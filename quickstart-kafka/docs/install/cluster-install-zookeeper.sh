#!/bin/bash


function deploy_zookeeper()
{
  cat config/hostlist | while read line
    do
      host=`echo "$line"|awk '{print $1}'`
      username=`echo "$line"|awk '{print $2}'`
      passwd=`echo "$line"|awk '{print $3}'`

      echo "zk" $host $username $passwd

  #    ssh -n ${username}@$host "hostname -i;
  #    cd /data/program/kafka_2.13-2.8.0;
  #    sh startZookeeper.sh"

  #    ssh -n ${username}@$host "hostname -i;
  #    cd /data/program/kafka_2.13-2.8.0;
  #    nohup /data/program/kafka_2.13-2.8.0/bin/zookeeper-server-start.sh /data/program/kafka_2.13-2.8.0/config/zookeeper.properties >/data/program/logs/zookeeper/zookeeper.log 2>&1 &"

    done
}
