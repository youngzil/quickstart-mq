nohup sh mqnamesrv &
nohup sh mqbroker &

sh mqshutdown namesrv
sh mqshutdown broker



重新部署：
1、检查配置文件：properties文件
2、删除namesrv中的topic、消费组、消费进度consumerOffset等信息：路径为：user.home/store/config文件夹：topics.json、subscriptionGroup.json
3、删除broker中的消息、消费组信息：配置文件中配置的路径：consumerOffset.json、delayOffset.json


主要涉及三个路径：
1、项目日志路径和GC日志路径：
${user.home}/logs/rocketmqlogs/
${user.home}/logs/rocketmqlogs/otherdays/

GC日志：
${user.home}/rmq_srv_gc.log
${user.home}/rmq_bk_gc.log

2、namesrv配置：store/config/
BrokerPathConfigHelper类中：topics.json（主题配置）、subscriptionGroup.json（消费组订阅配置）

3、commitlog路径：配置文件中配置的路径:storePathRootDir和storePathCommitLog配置的路径
MessageStoreConfig类和StorePathConfigHelper类
1、abort文件：一个标识last shutdown是不是正常关闭的标识文件，大小为0，如果启动服务时，存在此文件，此上次是非正常关闭，如果不存在，说明上次是正常关闭，在关闭的destroy()或者shutdown()方法时删除了此文件
2、checkpoint文件：StoreCheckpoint类，记录消息存储时间戳（physicMsgTimestamp）、consumequeue存储的时间戳（logicsMsgTimestamp）、index文件夹中的IndexFile的时间戳（indexMsgTimestamp）
3、commitlog文件夹：日志文件是在mapedFile = this.mapedFileQueue.getLastMapedFile();，第一次接收消息的时候，就会创建两个文件
4、config文件夹：consumerOffset.json（消费组消费进度配置）、delayOffset.json（定时任务消息，主题SCHEDULE_TOPIC_XXXX的消息，作用是？？？？）
5、consumequeue文件夹：ConsumeQueue类，记录mq的offset文件
6、index文件夹：IndexFile类，fileName是当前毫秒级时间戳，根据message的UniqKey和Keys来建立message的索引，查询消息时候使用




Broker日志设置：
$ROCKETMQ_HOME/conf下面的logback文件，log level直接在logback文件中设置，
日志路径：
当前日志：${user.home}/logs/rocketmqlogs/
备份日志：${user.home}/logs/rocketmqlogs/otherdays/
logback_broker.xml
logback_filtersrv.xml
logback_namesrv.xml
logback_tools.xml

Client日志配置：
logback_rocketmq_client.xml
其中日志路径、maxIndex、logLevel都可以使用如下jdk的-D参数设置，默认为${user.home}/logs/rocketmqlogs、10、INFO
public static final String CLIENT_LOG_ROOT = "rocketmq.client.logRoot";
public static final String CLIENT_LOG_MAXINDEX = "rocketmq.client.logFileMaxIndex";
public static final String CLIENT_LOG_LEVEL = "rocketmq.client.logLevel";

GC日志：
mqbroker是runbroker.sh中的gc日志配置，rmq_bk_gc.log
mqnamesrv和mqfiltersrv是runserver.sh中的gc日志配置，rmq_srv_gc.log




部署后检查：
sh mqadmin topicList  -n 10.251.19.106:9876;10.251.19.107:9876 
sh mqadmin clusterList  -n 10.251.19.106:9876;10.251.19.107:9876 

Clone & Build
  > git clone -b develop https://github.com/apache/incubator-rocketmq.git
  > cd incubator-rocketmq
  > mvn -Prelease-all -DskipTests clean install -U
  > cd distribution/target/apache-rocketmq

nohup sh bin/mqnamesrv &
nohup sh bin/mqbroker -c conf/broker-master.properties &

sh bin/mqshutdown broker
sh bin/mqshutdown namesrv

发送和接收消息
export NAMESRV_ADDR=localhost:9876
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer



开启远程debug
JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"


本机启动RocketMQ
本地rocketmq debug环境构建：
NamesrvStartup中添加：namesrvConfig.setRocketmqHome("/Users/yangzl/src/rocketmq-rocketmq-all-4.2.0");
BrokerStartup中添加如下：brokerConfig.setRocketmqHome("/Users/yangzl/src/rocketmq-rocketmq-all-4.2.0");
brokerConfig.setNamesrvAddr("localhost:9876");







