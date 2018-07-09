官网http://www.rabbitmq.com
下载http://www.rabbitmq.com/download.html
官方文档http://www.rabbitmq.com/documentation.html

https://github.com/rabbitmq/rabbitmq-server
java客户端：http://www.rabbitmq.com/java-client.html
客户端github：https://github.com/rabbitmq/rabbitmq-java-client
MQTT：https://github.com/rabbitmq/rabbitmq-mqtt

Erlang：
http://www.erlang.org/
http://erlang.org/doc/
https://github.com/erlang/otp

https://www.oschina.net/p/rabbitmq

RabbitMQ 3.7.0 发布，正式支持 Erlang/OTP 20
（OTP 20.1.4 已发布，OTP (Open Telecom Platform) 是一个开源的 Erlang 分发和一个用 Erlang 编写的应用服务器。）

开放电信平台 OTP
OTP (Open Telecom Platform) 是一个开源的 Erlang 分发和一个用 Erlang 编写的应用服务器，由爱立信开发。

OTP 包含：
一个 Erlang 解释器
一个 Erlang 编译器
服务器之间的通讯协议
一个 Corba 对象请求代理
名为 Dialyzer 的静态分析工具
一个分布式的数据库服务 Mnesia
大量的开发库

https://www.oschina.net/p/otp

官网：http://www.erlang.org/
文档：http://erlang.org/doc/design_principles/users_guide.html
下载：http://www.erlang.org/downloads

RabbitMQ 是由 LShift 提供的一个 Advanced Message Queuing Protocol (AMQP) 的开源实现，由以高性能、健壮以及可伸缩性出名的 Erlang 写成，因此也是继承了这些优点。

AMQP 里主要要说两个组件：Exchange 和 Queue （在 AMQP 1.0 里还会有变动），如下图所示，绿色的 X 就是 Exchange ，红色的是 Queue ，这两者都在 Server 端，又称作 Broker ，这部分是 RabbitMQ 实现的，而蓝色的则是客户端，通常有 Producer 和 Consumer 两种类型



RabbitMQ：

ConnectionFactory、Connection、Channel
ConnectionFactory、Connection、Channel都是RabbitMQ对外提供的API中最基本的对象。Connection是RabbitMQ的socket链接，它封装了socket协议相关部分逻辑。ConnectionFactory为Connection的制造工厂。
Channel是我们与RabbitMQ打交道的最重要的一个接口，我们大部分的业务操作是在Channel这个接口中完成的，包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等。

ProDucer发送消息（发送指定一个routing key）到Exchange （RabbitMQ常用的Exchange Type有fanout、direct、topic、headers这四种），当binding key与routing key相匹配时，消息将会被路由到对应的Queue中（RabbitMQ中通过Binding将Exchange与Queue关联起来，在绑定（Binding）Exchange与Queue的同时，一般会指定一个binding key），

publisher将消息首先发布给Exchange，由Exchange负责发送到一个或多个Queue中，发送的规则称为binging。最后发送给consumer或者consumer来pull。




