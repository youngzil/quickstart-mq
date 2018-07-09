#!/bin/sh

# 邮件:guozq5@asiainfo.com
# 创建时间:2016-10-12
# 脚本目的:msgframe namesrv进程

# *************************************************************************
# JAVA_OPT - java启动选项
# JAVA_VM      - jvm选项
# MEM_ARGS     - 内存参数
# *************************************************************************

VARS=$#
if [ $VARS -lt 1 ];
then
        echo "必须传入1个参数:进程名称"
        exit 0;
fi

BASEBIN="${BASH_SOURCE-$0}"
BASEBIN="$(dirname "${BASEBIN}")"
BASE_APP_DIR="$(cd "${BASEBIN}"; pwd)"

MAIN=com.alibaba.rocketmq.namesrv.NamesrvStartup
SERVERNAME=$1

#判断进程是否重复启动
${BASE_APP_DIR}/monitor.sh ${MAIN} ${SERVERNAME} | read PROCESS_ALIVE_STATUS
if [ "$PROCESS_ALIVE_STATUS" = "PROCESS_EXIST" ];
then
        echo "此进程已经启动了,不能重复启动"
        exit 0;
fi
#判断进程是否重复启动结束

. ${BASE_APP_DIR}/setEnv.sh

echo "CLASSPATH=${CLASSPATH}"

MEM_ARGS="-server -Xms4g -Xmx4g -Xmn2g -XX:PermSize=256m -XX:MaxPermSize=512m"

echo "\n"
echo "MEM_ARGS=${MEM_ARGS}"

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled"
JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:${APP_HOME}/logs/rmq_srv_gc.log -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${APP_HOME}/lib"
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
echo "\n"
echo "JAVA_OPT=${JAVA_OPT}"
#启动命令行
java ${MEM_ARGS} -Dserver.name=${SERVERNAME} ${JAVA_OPT} -cp ${CLASSPATH} ${MAIN} > /dev/null &




echo "\n"
echo "启动完成,请查看日志"
