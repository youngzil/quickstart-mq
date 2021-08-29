[Pulsar主页](https://pulsar.apache.org/)  
[Pulsar文档](https://pulsar.apache.org/docs/zh-CN/standalone/)
[Pulsar Github](https://github.com/apache/pulsar)  

[Pulsar Java client使用](https://pulsar.apache.org/docs/zh-CN/client-libraries-java/)



Pulsar - distributed pub-sub messaging system 
Pulsar是一个分布式pub-sub消息平台，具有非常灵活的消息传递模型和直观的客户端API。

Apache Pulsar is a cloud-native, distributed messaging and streaming platform originally created at Yahoo! and now a top-level Apache Software Foundation project

Apache Pulsar是最初由Yahoo!创建的云原生分布式消息传递和流平台。现在是顶级Apache Software Foundation项目





下载好压缩文件后，解压缩并使用 cd 命令进入文件所在位置：
$ tar xvfz apache-pulsar-2.8.0-bin.tar.gz
$ cd apache-pulsar-2.8.0

启动单机模式 Pulsar
$ bin/pulsar standalone


Consume 一条消息
在 first-subscription 订阅中 consume 一条消息到 my-topic 的命令如下所示：
$ bin/pulsar-client consume my-topic -s "first-subscription"


Produce 一条消息
向名称为 my-topic 的 topic 发送一条简单的消息 hello-pulsar，命令如下所示：
$ bin/pulsar-client produce my-topic --messages "hello-pulsar"





【Docker启动报错，起不来，docker-compose.yml也不行，不用测试了，里面的两个镜像都有1.5G大小，下载很难的】
在 Docker 中启动 Pulsar
MacOS、Linux、Windows 用户：

$ docker run -it --name pulsar \
-p 6650:6650 \
-p 8080:8080 \
--mount source=pulsardata,target=/pulsar/data \
--mount source=pulsarconf,target=/pulsar/conf \
apachepulsar/pulsar:2.8.0 \
bin/pulsar standalone




