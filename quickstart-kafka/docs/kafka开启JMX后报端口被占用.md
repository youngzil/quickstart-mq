kafka开启JMX
如果直接 修改kafka-run-class.sh脚本，第一行增加JMX_PORT=9988。 会导致消费者消费失败

Error: Exception thrown by the agent : java.rmi.server.ExportException: Port already in use: 9999; nested exception is:
java.net.BindException: Address already in use (Bind failed)



因为所有的服务最终都是调用kafka-run-class.sh脚本，所以不能修改kafka-run-class.sh脚本，也不能把JMX_PORT配置成全局变量

在kafka-server-start.sh  添加一条 export JMX_PORT=9988  即可
或者
新建启动脚本，在新的脚本里面添加一条 export JMX_PORT=9988
或者
[参考这个PR](https://github.com/apache/kafka/pull/1983/files) 这个PR没有被合并进去




背景：
kafka需要监控broker和topic的数据的时候，是需要开启jmx_port的，正常开启，是在脚本kafka-run-class.sh里面定义变量，定义完成后，在bin目录下面执行脚本会报错

原因：
原因是因为kafka-run-class.sh是个被调用脚本，当被其他脚本调用的同时，java会绑定该端口，这个时候就会报错端口占用了

解决：
1. 如果是supervisor启动的kafka，在supervisor的服务启动配置文件中加入一行配置：environment=JMX_PORT=9999
2. 如果是在bin目录启动，可以在启动的时候export jmx_port=9999或者直接在kafka-server-start.sh脚本上面指定





[kafka开启jmx_port后，报端口被占用](https://blog.csdn.net/weixin_37642251/article/details/90405635)
[开启JMX_PORT导致的问题](https://www.codenong.com/jsb17a60e1d494/)

