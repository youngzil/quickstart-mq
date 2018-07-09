activemq开启jmx监控：
1.${ACTIVEMQ_HOME}/conf/activemq.xml 中的 broker 节点增加  useJmx="true" 属性

2.${ACTIVEMQ_HOME}/conf/activemq.xml 中的 managementContext 节点修改成如下样子
<managementContext>   
<managementContext createConnector="true" connectorPort="1099" />
</managementContext> 

启动
${MQ_HOME}/bin/activemq start
tail -f ${MQ_HOME}/data/activemq.log

停止：./activemq stop


