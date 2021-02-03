brokers
config
consumers
admin
controller
controller_epoch




A),消费者目录,获取子节点就可以获取所有的消费者
/consumers/group.id/ids/
/consumers/group.id/offsets/
/consumers/group.id/owners/

B),topic的目录，可以获取topic，分区及副本信息
/brokers/topics/topicName
/brokers/ids/topicName



kafka在Zookeeper上的节点如下图：

![kafka在Zookeeper上的节点信息](images/kafka-zookeeper-path.jpg "ReferencePicture")



具体见网址：
http://blog.csdn.net/lkforce/article/details/77864472


