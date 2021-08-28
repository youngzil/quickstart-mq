- [Presto介绍](#Presto介绍)
- [Trino介绍](#Trino介绍)
- [Presto和Trino的区别](#Presto和Trino的区别)
- [Presto安装部署](#Presto安装部署)


---------------------------------------------------------------------------------------------------------------------

## Presto介绍

Presto前身为PrestoDB

Distributed SQL Query Engine for Big Data

[Presto官网](https://prestodb.io/)  
[Presto Github](https://github.com/prestodb/presto)  
[Presto文档](https://prestodb.io/docs/current/)  




---------------------------------------------------------------------------------------------------------------------

## Trino介绍

PrestoSQL更名为Trino。Trino（前身为 PrestoSQL）

Presto SQL is now Trino

Trino, a query engine that runs at ludicrous speed
Fast distributed SQL query engine for big data analytics that helps you explore your data universe.

Official repository of Trino, the distributed SQL query engine for big data, formerly known as PrestoSQL 


[Trino官网](https://trino.io/)  
[Trino Github](https://github.com/trinodb/trino)  




---------------------------------------------------------------------------------------------------------------------

## Presto和Trino的区别


Presto项目有了两个分支：一个是由Facebook主导的PrestoDB项目，另一个是由Presto软件基金会维护的PrestoSQL项目。



2018 年，Martin、Dain 和 David 离开 Facebook，以新名称 PrestoSQL 全职致力于建立 Presto 开源社区。PrestoDB 旨在为 Facebook 和 Uber 等超大规模互联网公司提高查询效率，而 PrestoSQL 则是为更广泛的客户和用例而构建的。

2020 年 12 月，  PrestoSQL 更名为 Trino。Trino（前身为 PrestoSQL）将 Presto 的价值带给了处于不同云采用阶段的众多公司，这些公司需要更快地访问其所有数据。如今，LinkedIn、Lyft、Netflix、GrubHub、Slack、Comcast、FINRA、Condé Nast、Nordstrom 和其他数千家公司都在使用 Trino。


[The Differences Between Presto and Trino](https://www.starburst.io/learn/open-source-presto/prestosql-and-prestodb/)  
[Presto vs. Trino](https://ahana.io/presto-vs-trino/)  
[The Story Behind Presto DB](https://pandio.com/blog/presto-db-story/)  





---------------------------------------------------------------------------------------------------------------------

## Presto安装部署


[Presto服务端安装参考](https://prestodb.io/docs/current/installation/deployment.html)
[Presto CLI安装参考](https://prestodb.io/docs/current/installation/cli.html)
[Kafka Connector Tutorial](https://prestodb.io/docs/current/connector/kafka-tutorial.html)


[presto-server-0.260下载](https://repo1.maven.org/maven2/com/facebook/presto/presto-server/0.260/presto-server-0.260.tar.gz)
[presto-cli-0.260下载](https://repo1.maven.org/maven2/com/facebook/presto/presto-cli/0.260/presto-cli-0.260-executable.jar)


[presto的安装与部署对接kafka，MySQL](https://blog.csdn.net/u011418530/article/details/94598659)



首先创建一个专门的文件夹presto

1. 安装包准备
    ```aidl
   Kafka安装包并解压：kafka_2.13-2.8.0.tgz
   presto-server安装包并解压：presto-server-0.260.tar.gz
   Presto CLI安装包：presto-cli-0.260-executable.jar
   kafka-tpch文件：curl -o kafka-tpch https://repo1.maven.org/maven2/de/softwareforge/kafka_tpch_0811/1.0/kafka_tpch_0811-1.0.sh && chmod +x kafka-tpch
   数据文件夹：mkdir -p data/presto
   创建配置文件夹和配置文件，具体可以参考XXX
    ```
2. mv presto-cli-0.260-executable.jar presto && chmod +x presto
3. 启动ZK：bin/zookeeper-server-start.sh config/zookeeper.properties
4. 启动Kafka：bin/kafka-server-start.sh config/server.properties
5. Load data:./kafka-tpch load --brokers localhost:9092 --prefix tpch. --tpch-type tiny
6. Now start Presto:bin/launcher run
7. Start the Presto CLI:./presto --catalog kafka --schema tpch
8. Use live data： twistr tool







利用help命令可以查看launcher的详细用法
bin/launcher --help

以后台方式启动presto
bin/launcher start

一般启动方式，且输出并打印日志
bin/launcher run

停止presto
bin/launcher stop







---------------------------------------------------------------------------------------------------------------------
## Kafka Confluent使用JSON Schema模式

[JSON Schema Serializer and Deserializer](https://docs.confluent.io/platform/current/schema-registry/serdes-develop/serdes-json.html)

[Spring Boot Kafka JsonSerializer Example](https://howtodoinjava.com/kafka/spring-boot-jsonserializer-example/)
[How to create a JSON serializer for Kafka Producer](https://www.learningjournal.guru/article/kafka/how-to-create-a-json-serializer-for-kafka-producer/)


