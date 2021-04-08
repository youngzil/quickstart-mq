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


申请okta账号作为oauth2服务端，然后配置scope为kafka的地址、新建 kafkabroker, kafkaproducerapp and kafkaconsumerapp.

配置参考 [How to Configure OAuth2 Authentication for Apache Kafka Cluster using Okta](https://medium.com/egen/how-to-configure-oauth2-authentication-for-apache-kafka-cluster-using-okta-8c60d4a85b43)


然后测试okta作为oauth2服务端

在线Encode网站 https://www.base64encode.org/

模板连接
curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://<auth-server-url>/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic <encoded-clientId:clientsecret>'


kafkabroker
curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://dev-276677.okta.com/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic kafkabroker clientId:clientsecret  XXXX'

kafkaproducerapp
curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://dev-276677.okta.com/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic kafkaproducerapp clientId:clientsecret  XXXX'

kafkaconsumerapp
curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://dev-276677.okta.com/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic kafkaconsumerapp clientId:clientsecret  XXXX'




下载代码 ttps://github.com/vishwavangari/kafka-oauth2
根目录执行 gradle clean build
cd build/libs 找到kafka-oauth2–0.0.1.jar 复制到 kafka的 libs 目录下

cp <oauth2-project-dir>/build/libs/kafka-oauth2–0.0.1.jar <kafka-binary-dir>/libs



配置服务端配置文件
vi config/server.properties

##########SECURITY using OAUTHBEARER authentication ###############
sasl.enabled.mechanisms=OAUTHBEARER
sasl.mechanism.inter.broker.protocol=OAUTHBEARER
security.inter.broker.protocol=SASL_PLAINTEXT
listeners=SASL_PLAINTEXT://localhost:9093
advertised.listeners=SASL_PLAINTEXT://localhost:9093
#Authorizer for ACL
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
# 这里要修改
super.users=User:0oa8khq2ssvVWeKya357;
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required
################ OAuth Classes #####################
# 这里要根据自己实现的类需要的参数来配置
OAUTH_WITH_SSL=true
OAUTH_LOGIN_SERVER=dev-276677.okta.com
OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token'
OAUTH_LOGIN_GRANT_TYPE=client_credentials
# OAUTH_LOGIN_SCOPE=broker.kafka
OAUTH_LOGIN_SCOPE=kafka
OAUTH_AUTHORIZATION='Basic kafkabroker clientId:clientsecret XXXX'
OAUTH_INTROSPECT_SERVER=dev-276677.okta.com
OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect'
OAUTH_INTROSPECT_AUTHORIZATION='Basic kafkabroker clientId:clientsecret XXXX';
listener.name.sasl_plaintext.oauthbearer.sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
listener.name.sasl_plaintext.oauthbearer.sasl.server.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateValidatorCallbackHandler
########## SECURITY using OAUTHBEARER authentication ###############


vi config/kafka_server_jaas.conf

KafkaServer {
  org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required
  LoginStringClaim_sub="0oa8khq2ssvVWeKya357";
};

# 主要是-Djava.security.auth.login.config，其他的都是CallbackHandler实现类里面需要的
export KAFKA_OPTS="-Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_server_jaas.conf -DOAUTH_WITH_SSL=true -DOAUTH_LOGIN_SERVER=dev-276677.okta.com -DOAUTH_LOGIN_ENDPOINT=/oauth2/default/v1/token -DOAUTH_LOGIN_GRANT_TYPE=client_credentials -DOAUTH_LOGIN_SCOPE=kafka -DOAUTH_INTROSPECT_SERVER=dev-276677.okta.com -DOAUTH_INTROSPECT_ENDPOINT=/oauth2/default/v1/introspect -DOAUTH_AUTHORIZATION=Basic%20kafkabroker clientId:clientsecret XXXX -DOAUTH_INTROSPECT_AUTHORIZATION=Basic%20kafkabroker clientId:clientsecret XXXX"


启动Zookeeper和Kafka

bin/zookeeper-server-start.sh config/zookeeper.properties

bin/kafka-server-start.sh config/server.properties



生产者producer配置

vi config/sasl-oauth2-producerapp-config.properties

security.protocol=SASL_PLAINTEXT
sasl.mechanism=OAUTHBEARER
sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
OAUTH_LOGIN_SERVER=dev-276677.okta.com
OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token'
OAUTH_LOGIN_GRANT_TYPE=client_credentials
OAUTH_LOGIN_SCOPE=kafka
OAUTH_AUTHORIZATION='Basic kafkaproducerapp clientId:clientsecret  XXXX'
OAUTH_INTROSPECT_SERVER=dev-276677.okta.com
OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect'
OAUTH_INTROSPECT_AUTHORIZATION='Basic kafkaproducerapp clientId:clientsecret  XXXX'


bin/kafka-topics.sh --create --bootstrap-server localhost:9093  --command-config config/sasl-oauth2-producerapp-config.properties --replication-factor 1 --partitions 1 --topic oauth2-demo-topic

bin/kafka-console-producer.sh --broker-list localhost:9093 --topic oauth2-demo-topic --producer.config config/sasl-oauth2-producerapp-config.properties



消费者consumer配置

vi config/sasl-oauth2-consumerapp-config.properties

security.protocol=SASL_PLAINTEXT
sasl.mechanism=OAUTHBEARER
sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
OAUTH_LOGIN_SERVER=dev-276677.okta.com
OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token'
OAUTH_LOGIN_GRANT_TYPE=client_credentials
OAUTH_LOGIN_SCOPE=kafka
OAUTH_AUTHORIZATION='Basic kafkaconsumerapp clientId:clientsecret  XXXX'
OAUTH_INTROSPECT_SERVER=dev-276677.okta.com
OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect'
OAUTH_INTROSPECT_AUTHORIZATION='Basic kafkaconsumerapp clientId:clientsecret  XXXX';
group.id=oauth2-consumer-group


bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic oauth2-demo-topic --from-beginning --consumer.config config/sasl-oauth2-consumerapp-config.properties






--------------------------------------------------------



curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://dev-276677.okta.com/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic MG9hOGtocTJzc3ZWV2VLeWEzNTc6OFhtRnFpdEY5THU2MWgwYmZTa0V4b0hDVWZsaV9FS0FCVUM4TzNnRQ=='


curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://dev-276677.okta.com/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic MG9hOGtoYnI0eHIyVUJ2U1IzNTc6VWk1aUo0TTFMcjR1cmdELXFmTzRxdHlnMDF0REFhaWlqSUpMZS1Wbg=='


curl -i -H 'Content-Type: application/x-www-form-urlencoded' -X POST 'https://dev-276677.okta.com/oauth2/default/v1/token' -d 'grant_type=client_credentials&scope=kafka' -H 'Authorization: Basic MG9hOGtobmM0MkM0c1B2NkMzNTc6ZDctbDNYVldWaFMzX0tSUENibEc1R1hFVGlMaWxza2ZRQzJfdTVDbw=='



##########SECURITY using OAUTHBEARER authentication ###############
sasl.enabled.mechanisms=OAUTHBEARER
sasl.mechanism.inter.broker.protocol=OAUTHBEARER
security.inter.broker.protocol=SASL_PLAINTEXT
listeners=SASL_PLAINTEXT://localhost:9093
advertised.listeners=SASL_PLAINTEXT://localhost:9093
#Authorizer for ACL
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
super.users=User:0oa8khq2ssvVWeKya357;
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required
################ OAuth Classes #####################
OAUTH_WITH_SSL=true
OAUTH_LOGIN_SERVER=dev-276677.okta.com
OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token'
OAUTH_LOGIN_GRANT_TYPE=client_credentials
# OAUTH_LOGIN_SCOPE=broker.kafka
OAUTH_LOGIN_SCOPE=kafka
OAUTH_AUTHORIZATION='Basic MG9hOGtocTJzc3ZWV2VLeWEzNTc6OFhtRnFpdEY5THU2MWgwYmZTa0V4b0hDVWZsaV9FS0FCVUM4TzNnRQ=='
OAUTH_INTROSPECT_SERVER=dev-276677.okta.com
OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect'
OAUTH_INTROSPECT_AUTHORIZATION='Basic MG9hOGtocTJzc3ZWV2VLeWEzNTc6OFhtRnFpdEY5THU2MWgwYmZTa0V4b0hDVWZsaV9FS0FCVUM4TzNnRQ==';
listener.name.sasl_plaintext.oauthbearer.sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
listener.name.sasl_plaintext.oauthbearer.sasl.server.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateValidatorCallbackHandler
########## SECURITY using OAUTHBEARER authentication ###############


vi config/kafka_server_jaas.conf

KafkaServer {
org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required
LoginStringClaim_sub="0oa8khq2ssvVWeKya357";
};


export KAFKA_OPTS="-Djava.security.auth.login.config=/Users/lengfeng/Downloads/kafka_2.13-2.7.0/config/kafka_server_jaas.conf -DOAUTH_WITH_SSL=true -DOAUTH_LOGIN_SERVER=dev-276677.okta.com -DOAUTH_LOGIN_ENDPOINT=/oauth2/default/v1/token -DOAUTH_LOGIN_GRANT_TYPE=client_credentials -DOAUTH_LOGIN_SCOPE=kafka -DOAUTH_INTROSPECT_SERVER=dev-276677.okta.com -DOAUTH_INTROSPECT_ENDPOINT=/oauth2/default/v1/introspect -DOAUTH_AUTHORIZATION=Basic%20MG9hOGtocTJzc3ZWV2VLeWEzNTc6OFhtRnFpdEY5THU2MWgwYmZTa0V4b0hDVWZsaV9FS0FCVUM4TzNnRQ== -DOAUTH_INTROSPECT_AUTHORIZATION=Basic%20MG9hOGtocTJzc3ZWV2VLeWEzNTc6OFhtRnFpdEY5THU2MWgwYmZTa0V4b0hDVWZsaV9FS0FCVUM4TzNnRQ=="


bin/zookeeper-server-start.sh config/zookeeper.properties

bin/kafka-server-start.sh config/server.properties


 vi config/sasl-oauth2-producerapp-config.properties

security.protocol=SASL_PLAINTEXT
sasl.mechanism=OAUTHBEARER
sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
OAUTH_LOGIN_SERVER=dev-276677.okta.com
OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token'
OAUTH_LOGIN_GRANT_TYPE=client_credentials
OAUTH_LOGIN_SCOPE=kafka
OAUTH_AUTHORIZATION='Basic MG9hOGtoYnI0eHIyVUJ2U1IzNTc6VWk1aUo0TTFMcjR1cmdELXFmTzRxdHlnMDF0REFhaWlqSUpMZS1Wbg=='
OAUTH_INTROSPECT_SERVER=dev-276677.okta.com
OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect'
OAUTH_INTROSPECT_AUTHORIZATION='Basic MG9hOGtoYnI0eHIyVUJ2U1IzNTc6VWk1aUo0TTFMcjR1cmdELXFmTzRxdHlnMDF0REFhaWlqSUpMZS1Wbg=='


bin/kafka-topics.sh --create --bootstrap-server localhost:9093  --command-config config/sasl-oauth2-producerapp-config.properties --replication-factor 1 --partitions 1 --topic oauth2-demo-topic




bin/kafka-console-producer.sh --broker-list localhost:9093 --topic oauth2-demo-topic --producer.config config/sasl-oauth2-producerapp-config.properties



vi config/sasl-oauth2-consumerapp-config.properties

security.protocol=SASL_PLAINTEXT
sasl.mechanism=OAUTHBEARER
sasl.login.callback.handler.class=com.oauth2.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
OAUTH_LOGIN_SERVER=dev-276677.okta.com
OAUTH_LOGIN_ENDPOINT='/oauth2/default/v1/token'
OAUTH_LOGIN_GRANT_TYPE=client_credentials
OAUTH_LOGIN_SCOPE=kafka
OAUTH_AUTHORIZATION='Basic MG9hOGtobmM0MkM0c1B2NkMzNTc6ZDctbDNYVldWaFMzX0tSUENibEc1R1hFVGlMaWxza2ZRQzJfdTVDbw=='
OAUTH_INTROSPECT_SERVER=dev-276677.okta.com
OAUTH_INTROSPECT_ENDPOINT='/oauth2/default/v1/introspect'
OAUTH_INTROSPECT_AUTHORIZATION='Basic MG9hOGtobmM0MkM0c1B2NkMzNTc6ZDctbDNYVldWaFMzX0tSUENibEc1R1hFVGlMaWxza2ZRQzJfdTVDbw==';
group.id=oauth2-consumer-group


bin/kafka-console-consumer.sh --bootstrap-server localhost:9093 --topic oauth2-demo-topic --from-beginning --consumer.config config/sasl-oauth2-consumerapp-config.properties



--------------------------------------------------------





