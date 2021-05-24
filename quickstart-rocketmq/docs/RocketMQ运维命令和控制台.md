
问题排查：
1、查看Producer情况：生产是否发送出去
2、查看Broker情况：broker或topic下是否存在消息，查询topic下消息是否增长，消息是否在broker上
3、查看Consumer情况：是否已经消费完（是否已经消费掉），消费的条件是否包含该消息（消费条件是否应该消费这个消息），是否拉取到消息（改源码打印拉取消息日志确认是框架问题还是业务问题），是否处理时异常，是否有重试和死信队列，
得到



Console控制台：
/rocketmq/nsaddr

/cluster/list.query


/topic/queryConsumerByTopic.query 参数String topic
/topic/queryTopicConsumerInfo.query 参数String topic
/topic/examineTopicConfig.query 参数String topic


/message/viewMessage.query 参数 String msgId
/message/queryMessageByTopicAndKey.query 参数String topic, String key
/message/queryMessageByTopic.query 参数 String topic, long begin, long end


/consumer/groupList.query
/consumer/group.query 参数String consumerGroup
/consumer/examineSubscriptionGroupConfig.query 参数String consumerGroup
/consumer/fetchBrokerNameList.query 参数String consumerGroup
/consumer/queryTopicByConsumer.query 参数String consumerGroup
/consumer/consumerRunningInfo.query 参数 String consumerGroup, String clientId, boolean jstack



运维命令（基于V4.2.0整理）：在代码包org.apache.rocketmq.tools.command中
org.apache.rocketmq.tools.command.MQAdminStartup
SubCommand接口的实现


cluster和namesrv：
1、获取集群中的broker信息：sh bin/mqadmin clusterList  -n 127.0.0.1:9876
2、namesrv的配置信息：sh mqadmin getNamesrvConfig  -n 10.11.20.102:9876
3、更新namesrv的配置信息：sh mqadmin updateNamesrvConfig  -n 10.11.20.102:9876 -k serverAsyncSemaphoreValue -v 128
4、Create or update KV config：sh mqadmin updateKvConfig  -n 10.11.20.102:9876 -s namespace值 -k key值 -v value值
5、Delete KV config：sh mqadmin deleteKvConfig  -n 10.11.20.102:9876 -s namespace值 -k key值 


Broker：
1、查询broker配置：sh mqadmin getBrokerConfig -b 10.11.20.102:10911
2、更新Broker配置：sh mqadmin updateBrokerConfig -n namesrvAddr -c clusterName -b brokerAddr -k key值 -v value值
3、查看Broker 统计信息：sh mqadmin brokerStatus -b 10.11.20.102:10911
4、清除Broker写权限：sh mqadmin wipeWritePerm -n 10.251.19.106:9876;10.251.19.107:9876 -b brokerName 
5、查询broker的消费情况：sh mqadmin brokerConsumeStats -n 10.11.20.102:9876 -b brokerAddr（10.11.20.102:10911）


Topic：
1、查询Topic列表：sh mqadmin topicList  -n 10.11.20.102:9876
2、查看Topic消息信息：sh mqadmin topicStatus -n  10.11.20.102:9876 -t topicName
3、topic的路由配置信息：sh mqadmin topicRoute -n  10.11.20.102:9876 -t topicName
4、查询主题在哪些集群上： ./mqadmin topicClusterList -n  10.11.20.102:9876 -t topicName
5、Update or create topic：sh mqadmin updateTopic -n 10.11.20.102:9876 -c clusterName（或-b brokerAddr） -t topicName -r 10 -w 10 -p permission(intro[2:W 4:R; 6:RW]) -o order(true|false)
6、更新主题权限：sh mqadmin updateTopicPerm -n 10.11.20.102:9876 -c clusterName（或-b brokerAddr） -t topicName -p permission(intro[2:W 4:R; 6:RW]) 
7、删除主题：sh mqadmin deleteTopic -n 10.11.20.102:9876 -c clusterName -t topicName
8、查询Topic的消费tps：sh mqadmin statsAll -n 10.11.20.102:9876 （-t topicName）


Message：
queryMsgById         Query Message by Id
   queryMsgByKey        Query Message by Key
   queryMsgByOffset     Query Message by offset
   queryMsgByUniqueKey  Query Message by Unique key
   printMsg             Print Message Detail
   printMsgByQueue      Print Message Detail
   sendMsgStatus        send msg to broker.


Consumer：
1、查询消费组消费主题和进度：sh mqadmin consumerProgress -n 10.11.20.102:9876 -g groupName  
2、查询消费状态：sh mqadmin consumerStatus -n 10.11.20.102:9876 -g groupName  
3、重置消费进度通过时间：sh mqadmin resetOffsetByTime -n 10.11.20.102:9876 -g groupName -t topicName -s timestamp(long类型时间或者yyyy-MM-dd#HH:mm:ss:SSS型字符串)
4、查询消费者连接：sh mqadmin consumerConnection -n 10.11.20.102:9876 -g groupName  

5、新增消费组：sh mqadmin updateSubGroup 
6、删除消费组：sh mqadmin deleteSubGroup -n 10.11.20.102:9876 -g groupName  

   cloneGroupOffset     clone offset from other group.


Producer：
1、查询生产者连接：sh mqadmin producerConnection -n 10.11.20.102:9876 -g groupName -t topicName
  
  
  
sh mqadmin startMonitoring -n 10.11.20.102:9876

   updateOrderConf      Create or update or delete order conf
   cleanExpiredCQ       Clean expired ConsumeQueue on broker.
   cleanUnusedTopic     Clean unused topic on broker.
   startMonitoring      Start Monitoring
   allocateMQ           Allocate MQ
   checkMsgSendRT       check message send response time
   clusterRT            List All clusters Message Send RT
   queryCq              Query cq command.
   
   
   
   
   
   