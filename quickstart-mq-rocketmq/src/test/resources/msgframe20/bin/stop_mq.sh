#!/bin/sh

# 邮件:liuqq@asiainfo.com
# 创建时间:2015-07-30
# 脚本目的:停止进程
case $1 in
    broker)

    pid=`ps ax | grep -i 'com.alibaba.rocketmq.broker.BrokerStartup' |grep java | grep -v grep | awk '{print $1}'`
    if [ -z "$pid" ] ; then
            echo "No mqbroker running."
            exit -1;
    fi

    echo "The mqbroker(${pid}) is running..."

    kill ${pid}

    echo "Send shutdown request to mqbroker(${pid}) OK"
    ;;
    namesrv)

    pid=`ps ax | grep -i 'com.alibaba.rocketmq.namesrv.NamesrvStartup' |grep java | grep -v grep | awk '{print $1}'`
    if [ -z "$pid" ] ; then
            echo "No mqnamesrv running."
            exit -1;
    fi

    echo "The mqnamesrv(${pid}) is running..."

    kill ${pid}

    echo "Send shutdown request to mqnamesrv(${pid}) OK"
    ;;
    *)
    echo "Useage: mqshutdown broker | namesrv"
esac

