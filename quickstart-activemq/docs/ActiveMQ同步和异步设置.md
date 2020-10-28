Activemq发送的同步和异步设置
属性设置的三种方式：URL、ConnectionFactory、Connection

---------------------------------------------------------------------------------------------------------------------
ActiveMQ支持生产者以同步或异步模式发送消息。使用不同的模式对send方法的反应时间有巨大的影响，反映时间是衡量ActiveMQ吞吐量的重要因素，使用异步发送可以提高系统的性能。

###生产者发送消息 在默认大多数情况 下，AcitveMQ是以异步模式发送消息。
例外的情况： 没有使用事务并且 生产者以PERSISTENT传送模式发送消息。

在这种情况 下， send方法都是同步的，并且一直阻塞直到ActiveMQ发回确认消息：消息已经存储在持久性数据存储中。这种确认机制保证消息不会丢失，但会造成 生产者阻塞从而影响反应时间。

高性能的程序一般都能容忍在故障情况下丢失少量数据。如果编写这样的程序，可以通过使用异步发送来提高吞吐量（甚至在使用PERSISTENT传送模式的情况下）。


// 在不考虑事务的情况下：
// producer发送持久化消息是同步发送，发送是阻塞的，直到收到确认。同步发送肯定是有流量控制的。
// producer默认是异步发送，异步发送不会等待broker的确认， 所以就需要考虑流量控制了：
// ActiveMQConnectionFactory.setProducerWindowSize(int producerWindowSize)
// ProducerWindowSize的含义：producer每发送一个消息，统计一下发送的字节数，当字节数达到ProducerWindowSize值时，需要等待broker的确认，才能继续发送。




可靠同步发送 
原理：同步发送是指消息发送方发出数据后，会在收到接收方发回响应之后才发下一个数据包的通讯方式。 
应用场景：此种方式应用场景非常广泛，例如重要通知邮件、报名短信通知、营销短信系统等。 



可靠异步发送 

原理：异步发送是指发送方发出数据后，不等接收方发回响应，接着发送下个数据包的通讯方式。MQ 的异步发送，需要用户实现异步发送回调接口（SendCallback），在执行消息的异步发送时，应用不需要等待服务器响应即可直接返回，通过回调接口接收服务器响应，并对服务器的响应结果进行处理。 

1.在连接的URI中配置 
你可以使用连接的URI支持的参数来配置异步发送的模式，如下所示： 
cf = new ActiveMQConnectionFactory("tcp://locahost:61616?jms.useAsyncSend=true");  

2.在ConnectionFactory层配置 
你可以使用ActiveMQConnectionFactory对象实例，并通过下面的设置来使用异步模式： 
((ActiveMQConnectionFactory)connectionFactory).setUseAsyncSend(true);  

3.在Connection层配置 
在Connection层的配置，将覆盖在ConnectionFactory层的配置。 
你可以使用ActiveMQConnection对象实例，并通过下面的设置来使用异步模式： 
((ActiveMQConnection)connection).setUseAsyncSend(true);  

应用场景：异步发送一般用于链路耗时较长，对 RT 响应时间较为敏感的业务场景，例如用户视频上传后通知启动转码服务，转码完成后通知推送转码结果等。 







