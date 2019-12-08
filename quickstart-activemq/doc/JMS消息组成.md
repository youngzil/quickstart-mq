JMS消息由3部分组成：消息头、属性和消息体



---------------------------------------------------------------------------------------------------------------------

JMS消息由3部分组成：消息头、属性和消息体

消息头：包含消息的识别信息和路由信息，消息头包含一些标准的属性如下：
1.JMSDestination：消息发送的目的地：主要指Queue和Topic，自动分配。
2.JMSDeliveryMode：传送模式。有两种：持久模式和非持久模式。一条持久性的消息应该被传送“一次仅仅一次”，这就意味着如果JMS提供者出现故障，该消息并不会丢失，它会在服务器恢复之后再次传递。一条非持久的消息最多会传送一次，这意味着服务器出现故障，该消息将永久丢失。自动分配。
3.JMSExpiration：消息过期时间，等于Destination的send方法中的timeToLive值加上发送时刻的GMT时间值。如果timeToLive值等于零，则JMSExpiration被设为零，表示该消息永不过期。如果发送后，在消息过期时间之后消息还没有被发送到目的地，则该消息被清除。自动分配。
4.JMSPriority：消息优先级，从0-9十个级别，0-4是普通消息，5-9是加急消息。JMS不要求JMS Provider严格按照这十个优先级发送消息，但必须保证加急消息要先于普通消息到达。默认是4级。自动分配。
5.JMSMessageID：唯一识别每个消息的标识，由JMS Provider产生。自动分配。
6.JMSTimestamp：一个JMS Provider在调用send()方法时自动设置的。它是消息被发送和消费者实际接收的时间差。自动分配。
7.JMSCorrelationID：用来连接到另外一个消息，典型的应用是在回复消息中连接到原消息。在大多数情况下，JMSCorrelationID
8.JMSReplyTo：提供本消息回复消息的目的地址。由开发者设置。
9.JMSType
10.JMSRedelivered


消息体：JMS API定义了5种消息体格式，也叫消息类型，可以使用不同形式发送接收数据，并可以兼容现有的消息格式。包括：TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage。


属性设置方式：
1.在连接的URI中配置 
2.在ConnectionFactory层配置 
3.在Connection层配置 

消息属性：包含以下三种类型的属性：
1.应用程序设置和添加的属性，比如：
Message.setStringProperty("username",username);
2.JMS定义的属性
使用“JMSX”作为属性名的前缀，connection.getMetaData().getJMSXPropertyNames()，方法返回所有连接支持的JMSX属性的名字。
3.JMS供应商特定的属性
--------------------- 
作者：csdn_kenneth 
来源：CSDN 
原文：https://blog.csdn.net/csdn_kenneth/article/details/82080252?utm_source=copy 
版权声明：本文为博主原创文章，转载请附上博文链接！



JMS消息的组成：

消息头
JMSDestinanion： 比如是QUEUE或者是TOPIC
 destination = session.createQueue("first_queue");   //点对点
 producer = session.createProducer(destination);

JMSDeliveryMode: 持久模式和非持久模式

JMSExpiration:  消息过期时间
message.setJMSExpiration(0);  //0消息永不过期

JMSPriorty  : 消息优先级别（0-9） 默认是4；  注意：JMS只保证加急消息(5~9)比普通消息(0~4)更早到达，

JMSMessageID ;

JMSTimestamp：

JMSReplyTo:

消息属性
通过Message.setStringProperty()来设置
message.setStringProperty("key","hello world");     //消息属性

消息体

JMS consumer




/**
             Session javax.jms.Connection.createSession(boolean transacted, int acknowledgeMode) throws JMSException
             1.transacted事务，事务成功commit,才会将消息发送到mom中
             2.acknowledgeMode消息确认机制
             1）、带事务的session
             如果session带有事务，并且事务成功提交，则消息被自动签收。如果事务回滚，则消息会被再次传送。
             消息事务是在生产者producer到broker或broker到consumer过程中同一个session中发生的，
             保证几条消息在发送过程中的原子性。
             在支持事务的session中，producer发送message时在message中带有transactionID。
             broker收到message后判断是否有transactionID，如果有就把message保存在transaction store中，
             等待commit或者rollback消息。
             2）、不带事务的session
             不带事务的session的签收方式，取决于session的配置。
             Activemq支持一下三種模式：
             Session.AUTO_ACKNOWLEDGE  消息自动签收
             Session.CLIENT_ACKNOWLEDGE  客戶端调用acknowledge方法手动签收
             Session.DUPS_OK_ACKNOWLEDGE 不是必须签收，消息可能会重复发送。在第二次重新传送消息的时候，消息
             头的JmsDelivered会被置为true标示当前消息已经传送过一次，客户端需要进行消息的重复处理控制。
             代码示例如下：
             session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
             textMsg.acknowledge();
             */

--------------------- 
作者：u014401141 
来源：CSDN 
原文：https://blog.csdn.net/u014401141/article/details/54772847?utm_source=copy 
版权声明：本文为博主原创文章，转载请附上博文链接！