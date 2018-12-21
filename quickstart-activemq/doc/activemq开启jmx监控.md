activemq开启jmx监控：
1.${ACTIVEMQ_HOME}/conf/activemq.xml 中的 broker 节点增加  useJmx="true" 属性

2.${ACTIVEMQ_HOME}/conf/activemq.xml 中的 managementContext 节点修改成如下样子
<managementContext>   
<managementContext createConnector="true" connectorPort="1099" connectorHost="10.1.1.101"/>
</managementContext> 

启动
${MQ_HOME}/bin/activemq start
tail -f ${MQ_HOME}/data/activemq.log

停止：./activemq stop

默认情况下，ActiveMQ使用useJmx后，jmx的url为
service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
这时，jmx的MBean server被绑死在localhost上，无法在broker所在机器以外的机器访问。
其实ActiveMQ提供了jmx相关的几个配置，不仅可以调整这个url中的ip和端口，还可以使用其他的MBean server。

属性名称	默认值	描述
useMBeanServer	true	为true则避免创建一个MBeanServer，使用jvm中已有的MBeanServer
jmxDomainName	org.apache.activemq	jmx域，所有ObjectName的前缀
createMBeanServer	true	为true则在需要时创建一个MBeanServer
createConnector	true	为true则创建一个JMX connector
connectorPort	1099	 JMX connector的端口
connectorHost	localhost	 JMX connector和RMI server(rmiServerPort>0)的host 
rmiServerPort	0	 RMI server的端口(便于穿过防火墙)
connectorPath	/jmxrmi	JMX connector注册的路径
findTigerMBeanServer	true	启用或禁用查找Java 5 平台的 MBeanServer
1、可以通过修改connectorHost和connectorPort，修改掉url中的localhost:1099

--------------------- 
作者：kimmking 
来源：CSDN 
原文：https://blog.csdn.net/KimmKing/article/details/9220009 
版权声明：本文为博主原创文章，转载请附上博文链接！



