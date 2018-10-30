http://activemq.apache.org/
https://github.com/apache/activemq

JMS：
https://github.com/eclipse-ee4j/jms-api


JMS的基本组成：
连接工厂，连接，会话，目的地（queue，topic），生产者，消费者，消息（TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage）

ConnectionFactory，Connection，Session，Destination（queue，topic），Producer，Consumer，Message（TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage）



JMS的基本构件
连接工厂：连接工厂是客户用来创建连接的对象，例如ActiveMQ提供的ActiveMQConnectionFactory
连接： JMS Connection封装了JMS 客户端到JMS Provider 的连接与JMS提供者之间的一个虚拟的连接
会话： JMS Session是生产和消费消息的一个单线程上下文，会话用于创建消息的生产者（producer），消费者（consumer），消息（message）等。会话是一个事务性的上下文，消息的生产和消费不能包含在同一个事务中。
生产者：MessageProducer 由Session 对象创建的用来发送消息的对象
消费者：MessageConsumer 由Session 对象创建的用来接收消息的对象
消息：Message jms消息包括消息头和消息体以及其它的扩展属性。JMS定义的消息类型TextMessage、MapMessage、BytesMessage、StreamMessage和ObjectMessage。
目的地：Destination 消息的目的地，是用来指定生产的消息的目标和它消费的消息的来源的对象
消息队列：Queue 点对点的消息队列
消息主题：Tipic 发布订阅的消息队列



http://www.liuhaihua.cn/archives/511431.html



与Spring整合
quickstart-spring-framework-activemq

与SpringBoot整合
quickstart-spring-boot-activemq






