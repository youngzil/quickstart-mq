第一步；拉去zookeeper镜像
```
docker pull wurstmeister/zookeeper
```

第二步：拉取kafka
```
docker pull wurstmeister/kafka
```


第三步：启动zookeeper镜像
```
docker run -d --name zookeeper -p 2181:2181 -v /etc/localtime:/etc/localtime wurstmeister/zookeeper
或
docker run -d --name zookeeper --publish 2181:2181 --volume /etc/localtime:/etc/localtime wurstmeister/zookeeper
```


第四步：启动kafka镜像（切记 192.168.31.131是我虚拟机ip，各位需要更换为自己的kafka镜像所在主机的ip）
```
docker run -d --name kafka --publish 9092:9092 --link zookeeper --env KAFKA_BROKER_ID=0 --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181/kafka --env KAFKA_ADVERTISED_HOST_NAME=172.16.185.76 --env KAFKA_ADVERTISED_PORT=9092 --env KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://172.16.185.76:9092 --env KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -v /etc/localtime:/etc/localtime wurstmeister/kafka
或者
docker run -d --name kafka -p 9092:9092 -e KAFKA_BROKER_ID=0 -e KAFKA_ZOOKEEPER_CONNECT=47.111.184.102:2181/kafka -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://172.16.185.76:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 -v /etc/localtime:/etc/localtime wurstmeister/kafka
```

参数说明
```
-e KAFKA_BROKER_ID=0  在kafka集群中，每个kafka都有一个BROKER_ID来区分自己

-e KAFKA_ZOOKEEPER_CONNECT=192.168.155.56:2181/kafka 配置zookeeper管理kafka的路径192.168.155.56:2181/kafka

-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://192.168.155.56:9092  把kafka的地址端口注册给zookeeper

-e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 配置kafka的监听端口

-v /etc/localtime:/etc/localtime 容器时间同步虚拟机的时间

```


第五步：测试Kafka

```
docker exec -it kafka /bin/bash
```

第六步：进入容器里进行生产和消费消息（具体进入/opt/kafkaxxxx 要ls一下 查看自己拉取的哪个版本的镜像产生的容器）
```
cd /opt/kafka_2.13-2.7.0/
```

创建一个主题名为netmusic：
```
bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic netmusic
```

查看主题信息
```
bin/kafka-topics.sh --describe --topic netmusic --bootstrap-server localhost:9092
```

当创建的replication-factor=2时，因为zookeeper的zoo.cfg配置文件中tickTime=2000会报链接超时，把这个值调大一些，重启zookeeper，再创建topic正常。


运行一个生产者：
```
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic netmusic
```

运行kafka消费者接收消息
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic netmusic --from-beginning
```


第七步：查看kafka注册信息

1：进入zookeeper容器内，可以看到kafka注册信息
```
docker exec -it zookeeper /bin/bash
```

运行zkCli.sh进入zookeeper客户端  
bin/zkCli.sh

可以查看zookeeper根节点下都挂了那些目录  
ls /  

可以看到我建立的topic叫netmusic主题的partitions信息  
ls /kafka/brokers/topics/netmusic/partitions 

get命令会显示该节点的节点数据内容和属性信息  
get /kafka/brokers/topics/netmusic

ls2命令会显示该节点的子节点信息和属性信息  
ls2 /kafka/brokers/topics/netmusic

删除topic 使用命令：

若 delete.topic.enable=true  
直接彻底删除该 Topic。

若 delete.topic.enable=false  
如果当前 Topic 没有使用过即没有传输过信息：可以彻底删除。

如果当前 Topic 有使用过即有过传输过信息：并没有真正删除 Topic 只是把这个 Topic 标记为删除(marked for deletion)，重启 Kafka Server 后删除。

我的kafka版本是最新的，在service.config文件中是找不到delete.topic.enable=true，系统默认是true.

进入kafka容器，cd /opt/kafka_2.13-2.7.0/

bin/kafka-topics.sh --delete --zookeeper 192.168.155.56:2181/kafka --topic netmusic

命令可以删除容器中的topic数据，还有zookeeper中的topic目录。

可以在zookeeper中查看目录是否已经删除掉了

进入zookeeper容器，运行bin/zkCli.sh

ls /kafka/brokers/topics

docker kafka 数据文件保存的路径：
在配置文件service.config中配置的，log.dirs配置保存路径。

进入kafka容器,找到配置文件路径 cd /opt/kafka_2.13-2.7.0/config

vi service.config 

默认配置在/kafka/kafka-logs-4eaa3ff7f59d下




参考  
https://www.jianshu.com/p/e8c29cba9fae  
https://juejin.cn/post/6844903829624848398  
https://segmentfault.com/a/1190000021746086  
https://my.oschina.net/u/4117203/blog/3084920  

[wurstmeister Kafka Docker Github](https://github.com/wurstmeister/kafka-docker)  
[wurstmeister Kafka Docker文档](http://wurstmeister.github.io/kafka-docker/)  


