#!/bin/sh

# 邮件:guozq5@asiainfo.com
# 创建时间:2016-12-05
# 脚本目的:停止进程

pid=`ps ax | grep -i 'com.alibaba.rocketmq.broker.BrokerStartup' |grep java |grep msg_broker_master_19 | grep -v grep | awk '{print$1}'`
if [ -z "$pid" ] ; then
   echo "No mqbroker running."
   exit -1;
fi

echo "The mqbroker(${pid}) is running..."

kill ${pid}

echo "Send shutdown request to mqbroker(${pid}) OK"
