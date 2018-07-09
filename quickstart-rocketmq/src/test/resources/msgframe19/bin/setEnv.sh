#!/bin/sh

# 邮件:liuqq@asiainfo.com
# 创建时间:2015-07-30
# 脚本目的:事件中心设置通用环境变量

# *************************************************************************
# JAVA_OPT - java启动选项
# JAVA_VM      - jvm选项
# MEM_ARGS     - 内存参数
# *************************************************************************

echo "初始化通用环境参数"

JAVA_HOME="/home/itframe/jdk"


echo "JAVA_HOME=${JAVA_HOME}"

APP_HOME="${HOME}/aimsg/msgframe"
#echo "APP_HOME=${APP_HOME}"

#使用ROCKETMQ时：ROCKETMQ_HOME 必须设置
ROCKETMQ_HOME=${APP_HOME}
export ROCKETMQ_HOME

COMMON_LIB_HOME="${APP_HOME}/lib"
export COMMON_LIB_HOME
#echo "COMMON_LIB_HOME=${COMMON_LIB_HOME}"

COMMON_CONFIG_HOME="${APP_HOME}/conf"
#echo "COMMON_CONFIG_HOME=${COMMON_CONFIG_HOME}"


#UNIX环境连接主lib目录下的每一个jar文件，windows环境请修改
CP=
for file in ${COMMON_LIB_HOME}/*;
do CP=${CP}:$file;
done



CLASSPATH="${COMMON_CONFIG_HOME}:${CP}"
export CLASSPATH

JAVA_OPT=" -Dfile.encoding=UTF-8  -Doracle.jdbc.V8Compatible=true -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=10 "

MEM_ARGS="-Xms512m -Xmx512m"


#echo $CLASSPATH

#echo "*************************************************"
#echo "\n"
