Activemq顺序消息处理方案：
1、利用Activemq的高级特性：consumer之独有消费者（exclusive consumer）
2、利用Activemq的高级特性：Message Groups特性



---------------------------------------------------------------------------------------------------------------------
常mq可以保证先到队列的消息按照顺序分发给消费者消费来保证顺序，但是一个队列有多个消费者消费的时候，那将失去这个保证，因为这些消息被多个线程并发的消费。但是有的时候消息按照顺序处理是很重要的，那我们该如何来保证消息的顺序呢，下面将从activemq和rocketmq来看看，它们是如何来保证消息的顺序问题的？我们还可以有别的处理方案么？

Activemq处理方案

1、利用Activemq的高级特性：consumer之独有消费者（exclusive consumer）

ActiveMQ从4.x版本起开始支持Exclusive Consumer。 Broker会从多个consumers中挑选一个consumer来处理queue中

所有的消息，从而保证了消息的有序处理。如果这个consumer失效，那么broker会自动切换到其它的consumer。 

可以通过DestinationOptions 来创建一个Exclusive Consumer，如下：

queue = new ActiveMQQueue("TEST.QUEUE?consumer.exclusive=true");
consumer = session.createConsumer(queue);

[JMS学习十一(ActiveMQ Consumer高级特性之独有消费者（Exclusive Consumer）)](https://www.cnblogs.com/alter888/p/8978463.html)



在ActiveMQ4.x中可以采用Exclusive Consumer，broker会从queue中，一次发送消息给一个消费者，这样就避免了多个消费者并发消费的问题，从而保证顺序，配置如下：

queue = new ActiveMQQueue("TEST.QUEUE?consumer.exclusive=true");
consumer = session.createConsumer(queue);
当在接收信息的时候有一个或者多个备份接收消息者和一个独占消息者的同时接收时候，无论两者创建先后，在接收的时候，均为独占消息者接收。
当在接收信息的时候，有多个独占消费者的时候，只有一个独占消费者可以接收到消息。
当有多个备份消息者和多个独占消费者的时候，当所有的独占消费者均close的时候，只有一个备份消费者接到到消息。
当主消费者挂了话，会立刻启用故障切换转移到下一台消费者继续消费

独占消息就是在有多个消费者同时消费一个queue时，可以保证只有一个消费者可以消费消息，这样虽然保证了消息的顺序问题，不过也带来了一个问题，就是这个queue的所有消息将只会在这一个主消费者上消费，其他消费者将闲置，达不到负载均衡分配，而实际业务我们可能更多的是这样的场景，比如一个订单会发出一组顺序消息，我们只要求这一组消息是顺序消费的，而订单与订单之间又是可以并行消费的，不需要顺序，因为顺序也没有任何意义，有没有办法做到呢？答案是可以的，下面就来看看activemq的另一个高级特性之messageGroup。







2、利用Activemq的高级特性：messageGroups


Message Groups就是对消息分组，它是Exclusive Consumer功能的增强。

逻辑上，Message Groups 可以看成是一种并发的Exclusive Consumer。跟所有的消息都由唯一的consumer处理不同，JMS 消息属性JMSXGroupID 被用来区分message group。Message Groups特性保证所有具有相同JMSXGroupID的消息会被分发到相同的consumer（只要这个consumer保持active）。

另外一方面，Message Groups特性也是一种负载均衡的机制。在一个消息被分发到consumer之前，broker首先检查消息JMSXGroupID属性。如果存在，那么broker会检查是否有某个consumer拥有这个message group。如果没有，那么broker会选择一个consumer，并将它关联到这个message group。此后，这个consumer会接收这个message group的所有消息，直到：

  1：Consumer被关闭

  2：Message group被关闭，通过发送一个消息，并设置这个消息的JMSXGroupSeq为-1

[ActiveMQ（22）：Consumer高级特性之消息分组（Message Groups）](https://blog.51cto.com/1754966750/1924848)




Message Groups特性是一种负载均衡的机制。在一个消息被分发到consumer之前，broker首先检查消息JMSXGroupID属性。如果存在，那么broker会检查是否有某个consumer拥有这个message group。如果没有，那么broker会选择一个consumer，并将它关联到这个message group。此后，这个consumer会接收这个message group的所有消息，直到：

Consumer被关闭
Message group被关闭，通过发送一个消息，并设置这个消息的JMSXGroupSeq为-1

因此逻辑上消息组就像并行独占消费者。标准JMS头不是将所有消息发送到单个消费者，  JMSXGroupID而是用于定义消息所属的消息组。然后，消息组功能可确保将同一消息组的所有消息发送给同一 JMS使用者 - 同时该消费者保持活动状态。一旦消费者死亡，另一个将被选中。

解释消息组的另一种方法是它为消费者提供消息的粘性负载平衡; 其中  JMSXGroupID有点像HTTP会话ID或cookie值，消息代理就像HTTP负载均衡器。


activemq 消息顺序
ActiveMQ针对一个topic可以保证在只有一个发送者多个消费者的情况下消息的顺序性。 针对queue如果只有单个消费者和单个生产者的情况下也能保证消息的顺序性。

如果单个队列上有多个消费者，消费者会共同消费这些消息，ActiveMQ将在多个消费者之间进行负载均衡处理，因此顺序将不能得到保证。 关于该问题的背景和如何解决可以参考下面的内容：

独家消费者 一次只有一个消费者来消费一个queue的消息，以此来保证消息被顺序消费（不用担心消费节点的单点问题， 你可以部署多个消费节点，activemq会自动进行容灾的。具体参考链接的内容）。

消息组 将一个队列上的消息拆分为多个并行的虚拟排它队列，以确保到单个消息组（由JMSXGroupID头定义）的消息将保留其顺序，但不同的组将被负载均衡到不同的消费者。



https://segmentfault.com/a/1190000014512075
https://leokongwq.github.io/2017/01/23/jms-message-order.html
https://www.jianshu.com/p/f7a7105b3c27






