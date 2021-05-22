- [Kafka Connect的实战测试](#Kafka-Connect的实战测试)

---------------------------------------------------------------------------------------------------------------------

## Kafka Connect的实战测试


### 构建jar

cd quickstart-kafka-connect/

mvn clean install -DskipTests -f pom.xml
mvn assembly:assembly

ls -l target



### 启动kafka集群

cd quickstart-kafka-connect/script文件夹下，启动容器

docker ps -a

docker compose -f docker-compose.yaml up -d

docker ps -a

docker exec -it kafka bash
docker exec -it  kafka-zookeeper bash
docker exec -it kafka-mysql bash
docker exec -it kafka-redis bash


docker exec -it kafka-redis bash
redis-cli
或者
输入命令：docker exec -it kafka-redis redis-cli -a 123456
或者 
docker exec -it kafka-redis redis-cli -h 127.0.0.1 -p 6379 -a 123456



### 数据库插入数据

docker exec -it kafka-mysql bash

mysql -u root -p
然后输入密码root

show databases;

CREATE DATABASE testkafka;

show databases;

use testkafka;

创建表
CREATE TABLE `test_user` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) DEFAULT NULL,
PRIMARY KEY (`id`)
) ;

select * from test_user;

INSERT INTO test_user(name) VALUES("lengfeng");
INSERT INTO test_user(name) VALUES("liming");
INSERT INTO test_user(name) VALUES("yuangzl");
INSERT INTO test_user(name) VALUES("zhangsan");
INSERT INTO test_user(name) VALUES("lisi");
INSERT INTO test_user(name) VALUES("wangwu");
INSERT INTO test_user(name) VALUES("zhaoliu");

select * from test_user;



### 把connect的jar包复制到kafka容器

docker exec -it kafka ls -l /opt

# 建文件夹
docker exec -it kafka mkdir -p /opt/connectors/

docker exec -it kafka ls -l /opt

cd ../

# 将连接器上传到kafka 容器中
docker cp target/kafka-connector-example-bin.jar kafka:/opt/connectors/

docker exec -it kafka ls -l /opt/connectors



### 修改配置并启动Worker

docker exec -it kafka bash

#在配置文件末尾追加 plugin.path=/opt/connectors
vi /opt/kafka/config/connect-distributed.properties


# 启动Worker
cd /opt/kafka
bin/connect-distributed.sh -daemon config/connect-distributed.properties

查看进程
ps -ef|grep java

新建配置source.json
docker cp script/source.json kafka:/opt/kafka/

docker exec -it kafka ls -l /opt/kafka/

docker exec -it kafka bash

cd /opt/kafka

向Worker 发送请求，创建连接器
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8083/connectors/ -d @source.json


确认数据是否写入Kafka

首先查看一下Worker中的运行状态，如果Task的state = RUNNING，代表Task没有抛出任何异常，平稳运行
bash-4.4# curl -X GET localhost:8083/connectors/example-source/status
{"name":"example-source","connector":{"state":"RUNNING","worker_id":"172.22.0.5:8083"},"tasks":[{"id":0,"state":"RUNNING","worker_id":"172.22.0.5:8083"}],"type":"source"}


查看kafka 中Topic 是否创建
bash-4.4# bin/kafka-topics.sh --list --zookeeper zookeeper:2181
__consumer_offsets: 记录所有Kafka Consumer Group的Offset
connect-configs: 存储连接器的配置，对应Connect 配置文件中config.storage.topic
connect-offsets: 存储Source 的Offset，对应Connect 配置文件中offset.storage.topic
connect-status: 连接器与Task的状态，对应Connect 配置文件中status.storage.topic


查看topic中数据，此时说明MySQL数据已经成功写入Kafka
bash-4.4# bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test_user --from-beginning





新建配置sink.json

docker cp script/sink.json kafka:/opt/kafka/

docker exec -it kafka ls -l /opt/kafka/

docker exec -it kafka bash

cd /opt/kafka


创建Sink Connector
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8083/connectors/ -d @sink.json


然后查看Sink Connector Status，这里我发现由于我的Redis端口只对localhost开发，所以这里我的Task Fail了，修改了Redis配置之后，重启Task curl -X POST localhost:8083/connectors/example-sink/tasks/0/restart

查看运行状态
curl -X GET localhost:8083/connectors/example-sink/status


在确认了Sink Status 为RUNNING 后，可以确认下Redis中是否有数据

如何查看Sink Offset消费情况
使用命令
bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group connect-example-sink

CURRENT-OFFSET和LOG-END-OFFSET都是7，7条数据都被消费完了




### 再次数据库插入数据

docker exec -it kafka-mysql bash

mysql -u root -p
然后输入密码root

use testkafka;

select * from test_user;

INSERT INTO test_user(name) VALUES("redis");
INSERT INTO test_user(name) VALUES("mysql");
INSERT INTO test_user(name) VALUES("kafka");
INSERT INTO test_user(name) VALUES("zookeeper");

select * from test_user;



### 再次查看redis中的数据
docker exec -it kafka-redis bash

redis-cli

keys *

get test_user_11




[Kafka Connect 实战 ---- 入门](https://segmentfault.com/a/1190000039395164)  
[示例源码地址参考](https://github.com/TavenYin/kafka-connect-example)  



---------------------------------------------------------------------------------------------------------------------





