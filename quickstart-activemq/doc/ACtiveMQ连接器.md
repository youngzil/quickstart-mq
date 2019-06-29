在ActiveMQ中，连接器(connector)共分为以下两类：
1、传输连接器(transport connector)：用于客户端和服务端之间( client-to-broker)的通信。
2、网络连接器(network connector)：用户集群中多个服务端之间(broker-to-broker)的通信。


---------------------------------------------------------------------------------------------------------------------
在ActiveMQ中有3个重要的角色：Broker、Producer、Consumer。
Broker为消息代理，它是ActiveMQ服务端角色，接受客户端的连接并提供消息通信的核心服务。
Producer是消息生产者，客户端角色。
Consumer是消息消费者，客户端角色。

客户端怎样和服务端通信，该选择哪种网络协议？ActiveMQ定义的连接器(connector)就是用来约定ActiveMQ的节点之间如何通信。

在ActiveMQ中，连接器(connector)共分为以下两类：
1、传输连接器(transport connector)：用于客户端和服务端之间( client-to-broker)的通信。
2、网络连接器(network connector)：用户集群中多个服务端之间(broker-to-broker)的通信。


https://www.jianshu.com/p/eca6a2894eb8
https://blog.csdn.net/zcf9916/article/details/84827940
https://blog.csdn.net/manzhizhen/article/details/52606722

