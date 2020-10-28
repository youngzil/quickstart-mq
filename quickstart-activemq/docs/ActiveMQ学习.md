- ActiveMQ部署安装
    - [部署启动](部署启动.md)  
    - [共享文件方式部署](共享文件方式部署.md)  
        ActiveMQ的共享文件方式部署手册：kahaDB存储
    - [ActiveMQ的共享文件方式部署手册](ActiveMQ的共享文件方式部署手册.docx)  
    - [LevelDB部署](LevelDB部署.md)  
        ActiveMQ+LevelDB+Zookeeper部署
- [JMS规范](#JMS规范)  
- [JMS消息组成](JMS消息组成.md) ：JMS消息由3部分组成：消息头、属性和消息体  
- [ActiveMQ开启JMX监控](ActiveMQ开启JMX监控.md)  
- [ActiveMQ消息持久化](ActiveMQ消息持久化.md)  
    ActiveMQ的消息持久化机制有JDBC，KahaDB和LevelDB，AMQ（废弃）
- [ActiveMQ连接器](ActiveMQ连接器.md)  
    在ActiveMQ中，连接器(connector)共分为以下两类：
    1、传输连接器(transport connector)：用于客户端和服务端之间( client-to-broker)的通信。
    2、网络连接器(network connector)：用户集群中多个服务端之间(broker-to-broker)的通信。
- [ConnectionFactory种类详解](ConnectionFactory种类详解.md)  
    一、ActiveMQ原生的连接工程：ActiveMQConnectionFactory
    二、PooledConnectionFactory
    三、SingleConnectionFactory
    四、CachingConnectionFactory
- [ActiveMQ同步和异步设置](ActiveMQ同步和异步设置.md)  
    Activemq发送的同步和异步设置  
    属性设置的三种方式：URL、ConnectionFactory、Connection  
- [ActiveMQ顺序消息](ActiveMQ顺序消息.md)  
    ActiveMQ顺序消息：consumer之独有消费者（exclusive consumer）、 Message Groups特性
    Activemq顺序消息处理方案：
    1、利用Activemq的高级特性：consumer之独有消费者（exclusive consumer）
    2、利用Activemq的高级特性：Message Groups特性
- [使用常见问题](#使用常见问题)


---------------------------------------------------------------------------------------------------------------------
## JMS规范

JMS：
[jms-api](https://github.com/eclipse-ee4j/jms-api)



## JMS的基本组成
连接工厂，连接，会话，目的地（queue，topic），生产者，消费者，消息（TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage）

ConnectionFactory，Connection，Session，Destination（queue，topic），Producer，Consumer，Message（TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage）


JMS的基本构件
- 连接工厂：连接工厂是客户用来创建连接的对象，例如ActiveMQ提供的ActiveMQConnectionFactory
- 连接： JMS Connection封装了JMS 客户端到JMS Provider 的连接与JMS提供者之间的一个虚拟的连接
- 会话： JMS Session是生产和消费消息的一个单线程上下文，会话用于创建消息的生产者（producer），消费者（consumer），消息（message）等。会话是一个事务性的上下文，消息的生产和消费不能包含在同一个事务中。
- 生产者：MessageProducer 由Session 对象创建的用来发送消息的对象
- 消费者：MessageConsumer 由Session 对象创建的用来接收消息的对象
- 消息：Message jms消息包括消息头和消息体以及其它的扩展属性。JMS定义的消息类型TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage。
- 目的地：Destination 消息的目的地，是用来指定生产的消息的目标和它消费的消息的来源的对象
- 消息队列：Queue 点对点的消息队列
- 消息主题：Tipic 发布订阅的消息队列



[JMS中间件ActiveMQ详解](http://www.liuhaihua.cn/archives/511431.html)


---------------------------------------------------------------------------------------------------------------------
https://www.jianshu.com/p/31239f2b5651

## 使用常见问题
1、连接复用：不然没有关闭，就会oom，关闭了效率低
2、KahaDB无法及时清理db-*.log，Activemq的生产消费正常，但是消息持久化文件一直增加，不会删除
3、MQ服务器上出现大量CLOSE_WAIT连接
4、服务挂掉，消息积压到文件最大限制，持久化消息生产者可以恢复，非持久消息生产者消费者都假死，不能生产消费
5、丢消息。非持久化消息，MQ服务端接收的消息积压在缓存里，这时候生产者关闭TCP连接，服务端发送心跳给生产者，导致发生了java.net.SocketException异常，把缓存里的数据作废了，没处理的消息全部丢失。
6、持久化消息非常慢。开启事务异步发送，事务不会影响发送效率，相反还可能更快
7、消息的不均匀消费：ActiveMQ的prefetch机制
8、死信队列。重试6次后，ActiveMQ认为这条消息是“有毒”的，将会把消息丢到死信队列里



1、连接复用：不然没有关闭，就会oom，关闭了效率低
connection、session、producer最好复用，
或者
使用PooledConnectionFactory、PooledConnection、并且复用producer


在程序里复用producer，由于client需要持续往三个队列发送消息，因此使用了hashmap来存放建好的producer，map里键是目的地，值则是MessageProducer对象。
每次发送消息时，需要检查是否producer可用，主要是检查会话是否正常。


2、KahaDB无法及时清理db-*.log，Activemq的生产消费正常，但是消息持久化文件一直增加，不会删除

重建连接可以解决，比如重启生产者（重建连接） 或者 Broker发生主备切换（重建连接）


KahaDB无法及时清理db-*.log
问题原因已经找到，是ActiveMQ的一个bug：

ActiveMQ使用KahaDB进行消息的持久化存储，在KahaDB的db-.log文件中，存放的是消息和消息被消费者接收的ack数据（messages and acknowledgement），只有一个db-.log文件中所有的消息都被消费（都存在ack）后，这个db-*.log才会被清理。

那么现在存在一种情况，如果MQ上堆积了一条消息，许久没被消费。那么这条消息所在的db-1.log就不会被清理，也就是说db-1.log中所有其他已经被消费的消息也都不会被清理。如果db-2.log,db-3.log中存在db-1.log中的已经被消费的消息的ack数据，这个ack数据也是不能被清理的。原因是如果清理了ack数据，那么在MQ宕机重启后，db-1.log中的被消费的消息又会被置为未消费。因此就会产生一个连锁反应，只要有一条消息卡在MQ上，就可能后续的数据文件均无法被删除。当然连锁反应也有可能在某个环节被打破，比如某个文件刚好包含了自己所有消息的ack和上一个文件的消息的ack数据，但是如果一直连锁下去，文件系统的确是会被撑爆。

这个bug在ActiveMQ-5.14.0版本中被解决，解决方法是把第一个数据文件中消息的ack给迁移到某个数据文件中。我也在测试环境搭了5.14.1版本的ActiveMQ进行了测试。

解决方法是：
1、尽量减少MQ上的消息堆积的情况，比如设置消息的超时时间，定时清理等。如果堆积了要及时处理，不能让MQ上长期堆积相同的消息。
2、减小db-*.log的文件大小，理论上可以使脏数据的影响范围降低
3、设置KahaDB多实例，让队列之间的数据文件互不干扰。
4、升级MQ



3、MQ服务器上出现大量CLOSE_WAIT连接
CLOSE_WAIT表示一个连接被被动关闭了，服务器没有检测到，因此程序忽略了继续进行连接的关闭。最麻烦的是CLOSE_WAIT的连接会占用amq的总连接数，因此如果要清理，只能重启mq来解决。
有一篇文章对TIME_WAIT和CLOSE_WAIT解释得很详细。
http://www.cnblogs.com/sunxucool/p/3449068.html
出现这个问题后，可能直接导致activemq报出timer already cancelled，连接超限，无法建立线程OOM等报错。（推测）
该问题在JIRA上已经被关闭，见AMQ-5543和AMQ-5251。
经过一段时间的研究，发现是我的代码触发了MQ 的一个bug代码中建立了过多的JMX连接，导致服务器无法建立新的线程，从而导致连接无法被正常处理，而后连接被客户端关闭，就出现了close_wait。
这个bug连接的是InactivityMonitor中建立ReadChecker和WriteCheck的bug，出现问题时会导致MQ上报出大量的Timer Already Cancelled报错，因为无法建立新的checker线程了，但是代码中仍然进行了checker的Timer类的配置，所以抛出了timer already cancelled的异常。在AMQ 5.13及以后的版本已修复该问题。




4、服务挂掉，消息积压到文件最大限制，持久化消息生产者可以恢复，非持久消息生产者消费者都假死，不能生产消费

这得从ActiveMQ的储存机制说起。在通常的情况下，非持久化消息是存储在内存中的，持久化消息是存储在文件中的，它们的最大限制在配置文件的<systemUsage>节点中配置。但是，在非持久化消息堆积到一定程度，内存告急的时候，ActiveMQ会将内存中的非持久化消息写入临时文件中，以腾出内存。虽然都保存到了文件里，但它和持久化消息的区别是，重启后持久化消息会从文件中恢复，非持久化的临时文件会直接删除。

那如果文件增大到达了配置中的最大限制的时候会发生什么？我做了以下实验：

设置2G左右的持久化文件限制，大量生产持久化消息直到文件达到最大限制，此时生产者阻塞，但消费者可正常连接并消费消息，等消息消费掉一部分，文件删除又腾出空间之后，生产者又可继续发送消息，服务自动恢复正常。

设置2G左右的临时文件限制，大量生产非持久化消息并写入临时文件，在达到最大限制时，生产者阻塞，消费者可正常连接但不能消费消息，或者原本慢速消费的消费者，消费突然停止。整个系统可连接，但是无法提供服务，就这样挂了。

具体原因不详，解决方案：尽量不要用非持久化消息，非要用的话，将临时文件限制尽可能的调大。

详细配置信息见文档：http://activemq.apache.org/producer-flow-control.html




5、丢消息。非持久化消息，MQ服务端接收的消息积压在缓存里，这时候生产者关闭TCP连接，服务端发送心跳给生产者，导致发生了java.net.SocketException异常，把缓存里的数据作废了，没处理的消息全部丢失。

这得从java的java.net.SocketException异常说起。简单点说就是当网络发送方发送一堆数据，然后调用close关闭连接之后。这些发送的数据都在接收者的缓存里，接收者如果调用read方法仍旧能从缓存中读取这些数据，尽管对方已经关闭了连接。但是当接收者尝试发送数据时，由于此时连接已关闭，所以会发生异常，这个很好理解。不过需要注意的是，当发生SocketException后，原本缓存区中数据也作废了，此时接收者再次调用read方法去读取缓存中的数据，就会报Software caused connection abort: recv failed错误。

通过抓包得知，ActiveMQ会每隔10秒发送一个心跳包，这个心跳包是服务器发送给客户端的，用来判断客户端死没死。如果你看过上面第一条，就会知道非持久化消息堆积到一定程度会写到文件里，这个写的过程会阻塞所有动作，而且会持续20到30秒，并且随着内存的增大而增大。当客户端发完消息调用connection.close()时，会期待服务器对于关闭连接的回答，如果超过15秒没回答就直接调用socket层的close关闭tcp连接了。这时客户端发出的消息其实还在服务器的缓存里等待处理，不过由于服务器心跳包的设置，导致发生了java.net.SocketException异常，把缓存里的数据作废了，没处理的消息全部丢失。

解决方案：用持久化消息，或者非持久化消息及时处理不要堆积，或者启动事务，启动事务后，commit()方法会负责任的等待服务器的返回，也就不会关闭连接导致消息丢失了。

关于java.net.SocketException请看我的详细研究：http://blog.163.com/_kid/blog/static/3040547620160231534692/



6、持久化消息非常慢。开启事务异步发送，事务不会影响发送效率，相反还可能更快

默认的情况下，非持久化的消息是异步发送的，持久化的消息是同步发送的，遇到慢一点的硬盘，发送消息的速度是无法忍受的。但是在开启事务的情况下，消息都是异步发送的，效率会有2个数量级的提升。所以在发送持久化消息时，请务必开启事务模式。其实发送非持久化消息时也建议开启事务，因为根本不会影响性能。




7、消息的不均匀消费：ActiveMQ的prefetch机制
  
有时在发送一些消息之后，开启2个消费者去处理消息。会发现一个消费者处理了所有的消息，另一个消费者根本没收到消息。
原因在于ActiveMQ的prefetch机制。当消费者去获取消息时，不会一条一条去获取，而是一次性获取一批，默认是1000条。
这些预获取的消息，在还没确认消费之前，在管理控制台还是可以看见这些消息的，但是不会再分配给其他消费者，此时这些消息的状态应该算作“已分配未消费”，如果消息最后被消费，则会在服务器端被删除，如果消费者崩溃，则这些消息会被重新分配给新的消费者。
但是如果消费者既不消费确认，又不崩溃，那这些消息就永远躺在消费者的缓存区里无法处理。
更通常的情况是，消费这些消息非常耗时，你开了10个消费者去处理，结果发现只有一台机器吭哧吭哧处理，另外9台啥事不干。
  
解决方案：将prefetch设为1，每次处理1条消息，处理完再去取，这样也慢不了多少。

详细文档：http://activemq.apache.org/what-is-the-prefetch-limit-for.html





8、死信队列。重试6次后，ActiveMQ认为这条消息是“有毒”的，将会把消息丢到死信队列里
consumer.receive()方法，在消息返回给调用者之后就自动被确认了
  listener回调函数，在有消息到达时，会调用listener接口的onMessage方法。在这种情况下，在onMessage方法执行完毕后，消息才会被确认，此时只要在方法中抛出异常，该消息就不会被确认
  
如果你想在消息处理失败后，不被服务器删除，还能被其他消费者处理或重试，可以关闭AUTO_ACKNOWLEDGE，将ack交由程序自己处理。那如果使用了AUTO_ACKNOWLEDGE，消息是什么时候被确认的，还有没有阻止消息确认的方法？有！

消费消息有2种方法，一种是调用consumer.receive()方法，该方法将阻塞直到获得并返回一条消息。这种情况下，消息返回给方法调用者之后就自动被确认了。另一种方法是采用listener回调函数，在有消息到达时，会调用listener接口的onMessage方法。在这种情况下，在onMessage方法执行完毕后，消息才会被确认，此时只要在方法中抛出异常，该消息就不会被确认。那么问题来了，如果一条消息不能被处理，会被退回服务器重新分配，如果只有一个消费者，该消息又会重新被获取，重新抛异常。就算有多个消费者，往往在一个服务器上不能处理的消息，在另外的服务器上依然不能被处理。难道就这么退回--获取--报错死循环了吗？

在重试6次后，ActiveMQ认为这条消息是“有毒”的，将会把消息丢到死信队列里。如果你的消息不见了，去ActiveMQ.DLQ里找找，说不定就躺在那里。

详细文档：http://activemq.apache.org/redelivery-policy.html

http://activemq.apache.org/message-redelivery-and-dlq-handling.html





9、





10、



---------------------------------------------------------------------------------------------------------------------


参考
https://blog.csdn.net/jwdstef/article/details/17380471
https://segmentfault.com/a/1190000014108398



---------------------------------------------------------------------------------------------------------------------




