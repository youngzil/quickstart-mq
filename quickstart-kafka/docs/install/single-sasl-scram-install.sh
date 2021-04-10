#!/bin/bash

#export BASE_PATH=`cd $(dirname -- $0)/..; pwd`
export BASE_PATH=`cd $(dirname -- $0); pwd`
#export BASE_PATH=`pwd`

echo "BASE_PATH=$BASE_PATH"

#解压Kafka
cd $BASE_PATH
tar -xzvf kafka_2.13-2.7.0.tgz
cd kafka_2.13-2.7.0


# Start the ZooKeeper service
# Note: Soon, ZooKeeper will no longer be required by Apache Kafka.
nohup sh bin/zookeeper-server-start.sh config/zookeeper.properties >/dev/null 2>&1 &


# Start the Kafka broker service
nohup sh bin/kafka-server-start.sh config/server.properties >/dev/null 2>&1 &

#STEP 3: CREATE A TOPIC TO STORE YOUR EVENTS
#bin/kafka-topics.sh --create --topic quickstart-events2 --bootstrap-server localhost:9092

#查询Topic信息
#bin/kafka-topics.sh --describe --topic quickstart-events3 --bootstrap-server localhost:9092


#STEP 4: WRITE SOME EVENTS INTO THE TOPIC
#发送消息
#You can stop the producer client with Ctrl-C at any time.
#bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092


#STEP 5: READ THE EVENTS
#消费消息
#You can stop the consumer client with Ctrl-C at any time.
#bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --bootstrap-server localhost:9092


# bin/kafka-server-stop.sh
# bin/zookeeper-server-stop.sh


创建用户方式一

bin/kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-256=[password=admin],SCRAM-SHA-512=[password=admin]' --entity-type users --entity-name admin

bin/kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-256=[iterations=8192,password=writer-pwd],SCRAM-SHA-512=[password=writer-pwd]' --entity-type users --entity-name writer

bin/kafka-configs.sh --zookeeper localhost:2181 --alter --add-config 'SCRAM-SHA-256=[password=reader-pwd],SCRAM-SHA-512=[password=reader-pwd]' --entity-type users --entity-name reader

bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type users --entity-name admin
bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type users --entity-name writer
bin/kafka-configs.sh --zookeeper localhost:2181 --describe --entity-type users --entity-name reader

bin/kafka-configs.sh --zookeeper localhost:2182 --alter --delete-config 'SCRAM-SHA-512' --entity-type users --entity-name alice


或者创建用户方式二

bin/kafka-configs.sh --bootstrap-server localhost:9092 --alter --add-config 'SCRAM-SHA-256=[password=admin],SCRAM-SHA-512=[password=admin]' --entity-type users --entity-name admin

bin/kafka-configs.sh --bootstrap-server localhost:9092 --alter --add-config 'SCRAM-SHA-256=[iterations=8192,password=writer-pwd],SCRAM-SHA-512=[password=writer-pwd]' --entity-type users --entity-name writer

bin/kafka-configs.sh --bootstrap-server localhost:9092 --alter --add-config 'SCRAM-SHA-256=[password=reader-pwd],SCRAM-SHA-512=[password=reader-pwd]' --entity-type users --entity-name reader

bin/kafka-configs.sh --bootstrap-server localhost:9092 --describe --entity-type users --entity-name admin
bin/kafka-configs.sh --bootstrap-server localhost:9092 --describe --entity-type users --entity-name writer
bin/kafka-configs.sh --bootstrap-server localhost:9092 --describe --entity-type users --entity-name reader


配置服务端的配置文件

vi config/kafka_server_jaas.conf
KafkaServer {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="admin"
    password="admin";
};


配置脚本
vi ~/.bash_profile

export KAFKA_PLAIN_PARAMS="-Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_server_jaas.conf"
export KAFKA_OPTS="$KAFKA_PLAIN_PARAMS $KAFKA_OPTS"

source ~/.bash_profile

配置服务端配置文件
vi config/server.properties

# ACL配置
allow.everyone.if.no.acl.found=false
# 启用ACL
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer

# 设置本例中admin为超级用户
super.users=User:admin
# super.users=User:admin;User:ANONYMOUS

# 认证配置
# 启用SCRAM机制，采用SCRAM-SHA-512算法
sasl.enabled.mechanisms=SCRAM-SHA-256
# sasl.enabled.mechanisms=SCRAM-SHA-512

# 为broker间通讯开启SCRAM机制，采用SCRAM-SHA-512算法
sasl.mechanism.inter.broker.protocol=SCRAM-SHA-256
# sasl.mechanism.inter.broker.protocol=SCRAM-SHA-512

# broker间通讯使用PLAINTEXT，本例中不演示SSL配置
security.inter.broker.protocol=SASL_PLAINTEXT
# security.inter.broker.protocol=SASL_SSL

# 配置listeners使用SASL_PLAINTEXT
listeners=SASL_PLAINTEXT://localhost:9092
# listeners=SASL_SSL:///localhost:9092

# 配置advertised.listeners
advertised.listeners=SASL_PLAINTEXT://localhost:9092



重启Kafka、Zookeeper



配置客户端的配置文件

vi config/kafka_client_jaas.conf
KafkaClient {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="admin"
    password="admin";
};

创建config/client-sasl.properties 文件
vim config/client-sasl.properties

security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-256

把KAFKA_OPTS清空
export KAFKA_OPTS=""



修改Topic脚本
vi bin/kafka-topics.sh

# exec $(dirname $0)/kafka-run-class.sh kafka.admin.TopicCommand "$@"
exec $(dirname $0)/kafka-run-class.sh -Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_client_jaas.conf kafka.admin.TopicCommand "$@"

# 创建Topic
bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092 --command-config config/client-sasl.properties
# bin/kafka-topics.sh --create --topic quickstart-events --partitions 1 --replication-factor 1 --zookeeper localhost:2181
# bin/kafka-topics.sh --create --topic quickstart-events --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092
#bin/kafka-topics.sh --create --topic oauth2-demo-topic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9093  --command-config config/sasl-oauth2-producerapp-config.properties


# 查询Topic信息
bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092 --command-config config/client-sasl.properties



修改生产者producer脚本
vi bin/kafka-console-producer.sh

# exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleProducer "$@"
exec $(dirname $0)/kafka-run-class.sh -Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_client_jaas.conf kafka.tools.ConsoleProducer "$@"

bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092 --producer.config config/client-sasl.properties



修改消费者consumer脚本
vi bin/kafka-console-consumer.sh

# exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleConsumer "$@"
exec $(dirname $0)/kafka-run-class.sh -Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_client_jaas.conf kafka.tools.ConsoleConsumer "$@"

bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --group test-group --bootstrap-server localhost:9092 --consumer.config config/client-sasl.properties





重新创建客户端的配置文件，使用特定的writer和reader用户

vi config/kafka_producer_jaas.conf
KafkaClient {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="writer"
    password="writer-pwd";
};

vi config/kafka_consumer_jaas.conf
KafkaClient {
    org.apache.kafka.common.security.scram.ScramLoginModule required
    username="reader"
    password="reader-pwd";
};


修改生产者producer脚本
vi bin/kafka-console-producer.sh

# exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleProducer "$@"
exec $(dirname $0)/kafka-run-class.sh -Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_producer_jaas.conf kafka.tools.ConsoleProducer "$@"


修改消费者consumer脚本
vi bin/kafka-console-consumer.sh

# exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleConsumer "$@"
exec $(dirname $0)/kafka-run-class.sh -Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_consumer_jaas.conf kafka.tools.ConsoleConsumer "$@"


启动发送脚本
bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092 --producer.config config/client-sasl.properties

或者消费脚本
bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --group test-group --bootstrap-server localhost:9092 --consumer.config config/client-sasl.properties

都会报错
org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [quickstart-events]




给writer配置发送produce权限
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --allow-principal User:writer --producer --topic quickstart-events --add

给reader配置消费consume权限
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --allow-principal User:reader --consumer --group test-group --topic quickstart-events --add


启动发送脚本
bin/kafka-console-producer.sh --topic quickstart-events --bootstrap-server localhost:9092 --producer.config config/client-sasl.properties

消费脚本
bin/kafka-console-consumer.sh --topic quickstart-events --from-beginning --group test-group --bootstrap-server localhost:9092 --consumer.config config/client-sasl.properties




zkCli -server 127.0.0.1:2181

#权限用户信息
ls /config/users
get /config/users/admin
get /config/users/reader
get /config/users/writer

#ACL权限信息
ls /kafka-acl/Group
get /kafka-acl/Group/test-group

ls /kafka-acl/Topic
get /kafka-acl/Topic/quickstart-events



