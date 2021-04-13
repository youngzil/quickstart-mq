[Kafka权限管理](#Kafka权限管理)
[SASL/PLAIN方式](#SASL/PLAIN方式)
[SASL/SCRAM认证](#SASL/SCRAM认证)
[SASL/OAUTHBEARER](#SASL/OAUTHBEARER)



---------------------------------------------------------------------------------------------------------------------
## Kafka权限管理

## 1、Kafka的权限分类

1）、身份认证（Authentication）：
对client 与服务器的连接进行身份认证，brokers和zookeeper之间的连接进行Authentication（producer 和 consumer）、其他 brokers、tools与 brokers 之间连接的认证。

2）、权限控制（Authorization）：
实现对于消息级别的权限控制，clients的读写操作进行Authorization：（生产/消费/group)数据权限。



## 2、实现方式
自0.9.0.0版本开始Kafka社区添加了许多功能用于提高Kafka群集的安全性，Kafka提供SSL或者SASL两种安全策略。SSL方式主要是通过CA令牌实现，此文主要介绍SASL方式。

1）SASL验证:

验证方式	| Kafka版本	| 特点
-------- | ---    |------
SASL/PLAIN | 0.10.0.0 | 不能动态增加用户
SASL/SCRAM | 0.10.2.0 | 可以动态增加用户
SASL/Kerberos | 0.9.0.0 | 需要独立部署验证服务
SASL/OAUTHBEARER | 2.0.0 | 需自己实现接口实现token的创建和验证，需要额外Oauth服务

2）SSL加密:
使用SSL加密在代理和客户端之间，代理之间或代理和工具之间传输的数据。



[Kafka权限管理](https://www.jianshu.com/p/09129c9f4c80)  
[概念参考 GSSAPI介绍.md 中的JAAS/GSS-API/SASL/Kerberos简介](https://github.com/youngzil/notes/blob/master/docs/base/GSSAPI介绍.md)  


---------------------------------------------------------------------------------------------------------------------
## SASL/PLAIN方式


[配置SASL\PLAIN](https://www.cnblogs.com/rexcheny/articles/12884990.html)  
[kafka集群安全认证配置](https://gist.github.com/fuyuntt/945997c3647c640c97daf33e451cd7ae)  

[kafka用户认证与权限管理（SASL/PLAIN+ACL）](https://blog.csdn.net/langzitianya/article/details/103121973)  



---------------------------------------------------------------------------------------------------------------------
## SASL/SCRAM认证


[阿里云中kafka授权](https://www.alibabacloud.com/help/zh/doc-detail/67233.htm) ，也是必须配置一个默认的用户User:kafka


[部署参考single-sasl-scram-install.sh](install/single-sasl-scram-install.sh)  
[授权命令参考Kafka授权命令.sh](install/Kafka授权命令.sh)  
[配置文件参考](SASL/SCRAM)  


[Kafka SASL/SCRAM+ACL实现动态建立用户及权限控制](https://www.shangmayuan.com/a/c3aa5a5bfad04e9f8fac5020.html)  
[Kafka SASL/SCRAM+ACL实现动态创建用户及权限控制](https://blog.csdn.net/ashic/article/details/86661599)  
[kafka动态权限认证（SASL SCRAM + ACL）](https://www.pianshen.com/article/47852033243/)  
[kafka使用SASL/SCRAM认证](https://www.orchome.com/1946)  
[kafka动态权限认证（SASL SCRAM + ACL）](https://blog.csdn.net/weixin_45682234/article/details/109158975)  
[配置SASL SCRAM + ACL来实现如何动态增减用户](https://www.cnblogs.com/huxi2b/p/10437844.html)


[Kafka ACL控制，用户权限能控制](https://blog.csdn.net/zhangshenghang/article/details/90291813)  
[kafka acl配置](https://blog.csdn.net/ahzsg1314/article/details/54140909)  



---------------------------------------------------------------------------------------------------------------------
## SASL/OAUTHBEARER


[部署参考single-sasl-oauth2-install.sh](install/single-sasl-oauth2-install.sh)  
[授权命令参考Kafka授权命令.sh](install/Kafka授权命令.sh)  
[配置文件参考](SASL/OAUTHBEARER)  



[How to Configure OAuth2 Authentication for Apache Kafka Cluster using Okta](https://medium.com/egen/how-to-configure-oauth2-authentication-for-apache-kafka-cluster-using-okta-8c60d4a85b43)  
[Kafka 权限管理实战【很干】](https://cloud.tencent.com/developer/article/1491674)  
[Kafka 权限管理实战（最全整理）](https://www.cnblogs.com/felixzh/p/13695298.html)  
[如何为Kafka设置OAuth2安全机制？](https://www.jdon.com/51106)  
[Configuring OAUTHBEARER](https://docs.confluent.io/platform/current/kafka/authentication_sasl/authentication_sasl_oauth.html)  


[KIP-255 OAuth Authentication via SASL/OAUTHBEARER](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=75968876)


[kafka oauth2_将OAuth用于Kafka安全](https://blog.csdn.net/weixin_26722031/article/details/108136798)  
[Utilizing OAuth for Kafka Security](https://medium.com/blackrock-engineering/utilizing-oauth-for-kafka-security-5c1da9f3d3d)  


[Kafka Security : SASL OAUTHBEARER setup with Keycloak](https://dumisblog.wordpress.com/2020/06/04/kafka-security-sasl-oauthbearer-setup-with-keycloak/)  
[Kafka authentication using OAuth 2.0](https://strimzi.io/blog/2019/10/25/kafka-authentication-using-oauth-2.0/)
[OAuth 2.0 & OpenID Connect](https://www.novatec-gmbh.de/en/blog/kafka-security-behind-the-scenes/)  



Strimzi Operators：
Strimzi提供了容器映像和操作员，用于在Kubernetes上运行Kafka。Strimzi操作员是Strimzi运行的基础。Strimzi提供的操作员是专门构建的，具有专业的操作知识，可以有效地管理Kafka。

[Strimzi Operators使用](https://strimzi.io/docs/operators/in-development/using.html#assembly-oauth-str)  




示例代码  
[简单的Kafka OAuth代码](https://github.com/vishwavangari/kafka-oauth2)  
[kafka OAuth2示例代码](https://github.com/kafka-security/oauth)  
[kafka OAuth2示例](https://github.com/jairsjunior/oauth)  
[Kafka OAuth测试代码](https://github.com/jairsjunior/kafka-playground)  
[strimzi-kafka-oauth](https://github.com/strimzi/strimzi-kafka-oauth)  




