[ZeroMQ官网](https://zeromq.org/)  
[ZeroMQ文档](https://zeromq.org/get-started/)  
[ZeroMQ Github](https://github.com/zeromq/jeromq)  



ØMQ （也拼写作ZeroMQ，0MQ或ZMQ)是一个为可伸缩的分布式或并发应用程序设计的高性能异步消息库。它提供一个消息队列, 但是与面向消息的中间件不同，ZeroMQ的运行不需要专门的消息代理（message broker）。该库设计成常见的套接字风格的API。



ZeroMQ（简称ZMQ）是一个基于消息队列的多线程网络库，其对套接字类型、连接处理、帧、甚至路由的底层细节进行抽象，提供跨越多种传输协议的套接字。

ZMQ是网络通信中新的一层，介于应用层和传输层之间（按照TCP/IP划分），其是一个可伸缩层，可并行运行，分散在分布式系统间。

ZMQ不是单独的服务，而是一个嵌入式库，它封装了网络通信、消息队列、线程调度等功能，向上层提供简洁的API，应用程序通过加载库文件，调用API函数来实现高性能网络通信。


参考  
[消息队列库——ZeroMQ](https://www.cnblogs.com/chenny7/p/6245236.html)



