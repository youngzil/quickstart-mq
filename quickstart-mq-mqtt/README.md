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
   
   
   
   