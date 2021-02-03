


---------------------------------------------------------------------------------------------------------------------

### 生产者FailedToSendMessageException问题

Exception in thread "main" kafka.common.FailedToSendMessageException: Failed to send messages after 3 tries.
at kafka.producer.async.DefaultEventHandler.handle(DefaultEventHandler.scala:90)
at kafka.producer.Producer.send(Producer.scala:76)
at kafka.javaapi.producer.Producer.send(Producer.scala:33)
at com.tuan55.kafka.test.TestP.main(TestP.java:20)

这是生产者的代码 在向服务器发起连接后，在kafka的服务器配置中有zookeeper.connect=xx.xx.xx.xx：2181的配置 这时候kafka会查找zookeeper。

那么如果我们的hosts 中没有做hosts的配置 kafka经多次尝试连接不上就会报上面的错误。

kafka的server.properties这个配置文件是有配置这两项的地方。 里面也有zookeeper的连接路径，也可以改一下。因为我在同一台机器上跑的，跟默认localhost一样，没必要改。

host.name=172.x.x.x    //这里是腾讯云的内网地址
advertised.host.name=211.159.160.xxx    //这里是腾讯云的外网IP


解决办法：配置hosts文件 做zookeeper服务器的映射配置。
出现此种错误 还有一种情况
# Hostname the broker will advertise to producers and consumers. If not set, it uses the
# value for "host.name" if configured.  Otherwise, it will use the value returned from
# java.net.InetAddress.getCanonicalHostName().
advertised.host.name=192.168.1.118
远程连接的话 是根据这个配置来找broker的，默认是localhost ，所以如果不是本机运行的话 应该设置此值 来确保通信畅通。



参考  
https://blog.csdn.net/qq_38872310/article/details/80091581

---------------------------------------------------------------------------------------------------------------------
