ConnectionFactory种类详解.md
一、ActiveMQ原生的连接工程：ActiveMQConnectionFactory
二、PooledConnectionFactory
三、SingleConnectionFactory
四、CachingConnectionFactory




---------------------------------------------------------------------------------------------------------------------
参考
https://www.cnblogs.com/zackzhuzi/p/10050506.html
https://isoftyh.iteye.com/blog/1830296
http://blog.sina.com.cn/s/blog_5f53615f0100py5w.html
https://blog.csdn.net/s296850101/article/details/52367106
https://www.programcreek.com/java-api-examples/index.php?api=org.apache.activemq.pool.PooledConnectionFactory


一、ActiveMQ原生的连接工程：ActiveMQConnectionFactory

默认的maxThreadPoolSize=1000，也就是每个connection的session线程池最大值为1000，可以根据自己应用定制。

我们一般不直接用这个连接工厂，原因是：这个connectionFactory不会复用connection、session、producer、consumer，每次连接都需要重新创建connection，再创建session，然后调用session的创建新的producer或者consumer的方法，然后用完之后依次关闭，比较浪费资源。

我们一般用这个连接工厂作为其他拥有更高级功能（缓存）的连接工厂的参数。



二、PooledConnectionFactory

　　PooledConnectionFactory会缓存connection，session，和producer，不会缓存consumer，更适合于发送者。

maxConnections为最大连接数；
maximumActiveSessionPerConnection为每个连接最大的会话数量。
可以自行设置。



三、SingleConnectionFactory

　　SingleConnectionFactory：对于建立JMS服务器链接的请求会一直返回同一个链接，并且会忽略Connection的close方法调用。 



四、CachingConnectionFactory

　　CachingConnectionFactory继承了SingleConnectionFactory(仅有一个Connection)，所以它拥有SingleConnectionFactory的所有功能，同时它还新增了缓存功能，它可以缓存Session、MessageProducer和MessageConsumer。spring2.5.3之后推出的首选方案。

　　默认情况下，cachingConnectionFactory默认只缓存一个session，针对低并发足够。sessionCacheSize =1. 默认缓存producer、consumer。



PooledConnectionFactory实现ConnectionFactory接口。为因JmsTemlate每次发送消息时都会重新创建连接，创建connection，session，创建productor。这是一个非常耗性能的地方，特别是大数据量的情况下。因此出现了PooledConnectionFactory。这个类只会缓存connection，session和productor，不会缓存consumer。因此只适合于生产者发送消息。那为什么不缓存consumer呢?官方解释是由于消费者一般是异步的，也就是说，broker代理会把生产者发送的消息放在一个消息者的预取缓存中。当消息者准备好的时候就会从这个预取缓存中取出来进行处理。我想，这个只是在要求消息处理的及时性不是特别高的情况下。如果希望处理能够提高速度，自然也可以从这部分提高效率，减小不断创建consumer的时间（大数据量的情况下）。

 

CachingConnectionFactory类扩展自SingleConnectionFactory，主要用于提供缓存JMS资源功能。具体包括MessageProducer、MessageConsumer和Session的缓存功能。


