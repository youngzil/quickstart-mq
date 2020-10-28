client发送消息时候，每个消息请求时候，都有对应的类型，比如CONNECT、PUBLISH、SUBSCRIBE、UNSUBSCRIBE、PINGREQ、DISCONNECT等
server回应时候，要根据client的类型作对应的回应，比如CONNACK、PUBACK、SUBACK、UNSUBACK、PINGRESP等（netty中的MqttMessageType枚举类和MqttMessageFactory类）



全部类型如下：
CONNECT(1),
   CONNACK(2),
   PUBLISH(3),
   PUBACK(4),
   PUBREC(5),
   PUBREL(6),
   PUBCOMP(7),
   SUBSCRIBE(8),
   SUBACK(9),
   UNSUBSCRIBE(10),
   UNSUBACK(11),
   PINGREQ(12),
   PINGRESP(13),
   DISCONNECT(14);




 车联网遇到的问题：
 
 1、启动只建立连接，初始化5个连接，实际5个连接都超时关闭了，但是只有2个连接在连接超时关闭的时候，执行了MqttCallback.connectionLost
 2、先启动连接，启动订阅，关闭连接，再次订阅的时候，5个有3个显示连接未断的异常（这3个用isConnected是已经断开，但是在重连的时候，判断还在连接，所以报异常）
 3、先close全部连接，再connect连接，报错
 已断开连接 (32109) - java.io.EOFException
 	at org.eclipse.paho.client.mqttv3.internal.CommsReceiver.run(CommsReceiver.java:181)
 	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
 	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
 	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180)
 	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293)
 	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
 	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
 	at java.lang.Thread.run(Thread.java:748)
 Caused by: java.io.EOFException
 	at java.io.DataInputStream.readByte(DataInputStream.java:267)
 	at org.eclipse.paho.client.mqttv3.internal.wire.MqttInputStream.readMqttWireMessage(MqttInputStream.java:92)
 	at org.eclipse.paho.client.mqttv3.internal.CommsReceiver.run(CommsReceiver.java:133)
 	... 7 more
 4、同一个Client通道并发问题：3个并发publish消息，只有一个成功，另外两个夯住，close也会，
  
   
   
   