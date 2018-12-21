/**
 * 项目名称：quickstart-activemq 文件名：ActiveMQMonitorTest.java 版本信息： 日期：2017年8月8日 Copyright asiainfo
 * Corporation 2017 版权所有 *
 */
package org.quickstart.mq.activemq.jmx;

import java.util.Collection;
import java.util.List;

import javax.jms.Message;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.ConnectionViewMBean;
import org.apache.activemq.broker.jmx.ConnectorViewMBean;
import org.apache.activemq.broker.jmx.ProducerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.web.BrokerFacadeSupport;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.config.SystemPropertiesConfiguration;

/**
 * ActiveMQMonitorTest 通过jmx对activemq进行监控
 * 
 * @author：youngzil@163.com
 * @2017年8月8日 下午9:48:42
 * @since 1.0
 */
public class ActiveMQMonitorTest {

    public static void main(String[] args) throws Exception {

        // String surl = "service:jmx:rmi:///jndi/rmi://10.21.20.154:1099/jmxrmi";
        String surl = "service:jmx:rmi:///jndi/rmi://188.102.2.90:1099/jmxrmi";

        // String surl = "service:jmx:rmi:///jndi/rmi://20.26.25.39:1097/jmxrmi";
        // String surl = "service:jmx:rmi:///jndi/rmi://20.26.25.38:1098/jmxrmi";
        // String surl = "service:jmx:rmi:///jndi/rmi://20.26.25.38:1099/jmxrmi";

        System.out.println("url=" + surl);
        String url = "failover:(tcp://10.11.20.101:61616)";

        String user = "admin";
        String password = "admin";

        RemoteJMXBrokerFacade createConnector = new RemoteJMXBrokerFacade();
        System.setProperty("webconsole.jmx.url", surl);
        System.setProperty("webconsole.jmx.user", user);
        System.setProperty("webconsole.jmx.password", password);
        // 创建配置
        SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
        // 创建链接
        createConnector.setConfiguration(configuration);

        /****************************** 开始对broker的信息监控****start ***********************************************/
        // 对broker的监控
        BrokerViewMBean brokerBean = createConnector.getBrokerAdmin();

        // brokerBean.addQueue("test1");
        // brokerBean.removeQueue("javatest");

        // 这个计算的是错误的，getTotalEnqueueCount他把 topics里面jms的Advisory入队数也全算进去了
        // 如果想计算这个broker所有入队数，可以累加所有queue的入队数来计算
        long totalDequeueCount = 0;
        Collection<QueueViewMBean> queueList = createConnector.getQueues();
        for (QueueViewMBean queueViewMBean : queueList) {
            totalDequeueCount += queueViewMBean.getEnqueueCount();
        }

        System.out.println("整个broker的Name" + brokerBean.getBrokerName());
        System.out.println("整个broker的Version" + brokerBean.getBrokerVersion());
        System.out.println("整个broker的ID" + brokerBean.getBrokerId());
        System.out.println("整个broker的Uptime" + brokerBean.getUptime());

        // brokerBean.stop();
        // brokerBean.start();//不能使用，只能先启动，jmx才可以连到broker
        // brokerBean.restart();

        System.out.println("整个broker消息的积压数(待消费):" + brokerBean.getTotalMessageCount());
        System.out.println("整个broker的入队数:" + totalDequeueCount);
        System.out.println("整个broker的入队数:" + brokerBean.getTotalEnqueueCount()); // 这个取的有问题
        System.out.println("整个broker的出队数" + brokerBean.getTotalDequeueCount());
        System.out.println("整个broker所有的消费者数量:" + brokerBean.getTotalConsumerCount());
        System.out.println("整个broker所有的生产者数量:" + brokerBean.getTotalProducerCount());
        System.out.println("整个broker所有的 brokerBean.getCurrentConnectionsCount():" + brokerBean.getCurrentConnectionsCount());
        System.out.println("整个broker所有的 brokerBean.getTotalConnectionsCount():" + brokerBean.getTotalConnectionsCount());
        ObjectName[] queueConsumerList2 = brokerBean.getQueueSubscribers();
        ObjectName[] queueProducerList2 = brokerBean.getQueueProducers();
        /****************************** 结束对broker的信息监控****end ***********************************************/
        // 上面 brokerBean里面还有很多的可监控信息，比如版本、id broker名称等等，这里就不写了，开发的时候你们自己去写
        /****************************** 开始对队列的信息监控****start ***********************************************/

        Collection<QueueViewMBean> queueVwList = createConnector.getQueues();
        for (QueueViewMBean queueWvbean : queueVwList) {
            System.out.println("------------");
            System.out.println("queueName:" + queueWvbean.getName());
            System.out.println("队列消息积压数(待消费数):" + queueWvbean.getQueueSize());
            System.out.println("队列入队消息数:" + queueWvbean.getEnqueueCount());
            System.out.println("队列出队消息数:" + queueWvbean.getDequeueCount());
            System.out.println("当前队列的消费者数量:" + queueWvbean.getConsumerCount());
            System.out.println("当前队列的生产者数量:" + queueWvbean.getProducerCount());

            /****************************** 开始对Consumers的信息监控****start ***********************************************/

            // 获取每个队列的消费者集合
            Collection<SubscriptionViewMBean> queueConsumerList = createConnector.getQueueConsumers(queueWvbean.getName());

            for (SubscriptionViewMBean subConsumerBean : queueConsumerList) {

                System.out.println(subConsumerBean.getEnqueueCounter());
                System.out.println(subConsumerBean.getDequeueCounter());
                System.out.println(subConsumerBean.getDispatchedCounter());
                System.out.println(subConsumerBean.getDispatchedQueueSize());
                System.out.println(subConsumerBean.getPrefetchSize());
                System.out.println(subConsumerBean.getMaximumPendingMessageLimit());

                System.out.println(subConsumerBean.isExclusive());
                System.out.println(subConsumerBean.isDurable());
                System.out.println(subConsumerBean.isRetroactive());
                System.out.println(subConsumerBean.getPriority());
                System.out.println(subConsumerBean.getPriority());

                subConsumerBean.getConnectionId();
                subConsumerBean.getClientId();
                subConsumerBean.getEnqueueCounter();
                subConsumerBean.getDequeueCounter();
            }
            /****************************** 结束对Consumers的信息监控****end ***********************************************/

            /****************************** 开始对Producers的信息监控****start ***********************************************/

            // 获取每个队列的生产者连接
            Collection<ProducerViewMBean> queueProducerList = createConnector.getQueueProducers(queueWvbean.getName());
            for (ProducerViewMBean producerBean : queueProducerList) {
                producerBean.getConnectionId();
                producerBean.getClientId();
                producerBean.getSessionId();
                // 下面的很据需要自己写来展示

            }
            /****************************** 结束对Producers的信息监控****end ***********************************************/

            /*TabularData tabularData = queueWvbean.browseAsTable();
            System.out.println("tabularData===========" + tabularData.size());
            
            List<Message> messageList = (List<Message>) queueWvbean.browseMessages();
            System.out.println(messageList.size());
            
            CompositeData[] messageArray = queueWvbean.browse();
            for (int i = 0; i < messageArray.length; i++) {
                messageArray[i].values().toString();
                // 这个里面是每条消息；messageArray[i].values()是个TreeMap,你们activemq的控制台来解析
                // 每个values值是个消息体
                // System.out.println(messageArray[i].values().toString());
            }
            
            for (int i = 0; i < messageArray.length; i++) {
            
                // System.out.println(messageArray[i].get("Text"));
            
                CompositeData data = messageArray[i];
            
                System.out.println(data.toString());
                System.out.println(data.get("JMSPriority"));
            
                String value = "";
                if (data.containsKey("Text")) {
                    value = (String) data.get("Text");
                } else if (data.containsKey("ContentMap")) {
                    value = (String) data.get("ContentMap");
                }
                System.out.println(value);
            
            }*/

        }
        /****************************** 结束对队列的信息监控****end ***********************************************/
        /****************************** 开始对topic的信息监控****start ***********************************************/
        /**
         * 主题列表页面、每个主题的消息列表界面、每个消息的详细界面
         */
        Collection<TopicViewMBean> topicVwList = createConnector.getTopics();
        for (TopicViewMBean topicWvbean : topicVwList) {
            System.out.println("queueName:" + topicWvbean.getName());
            System.out.println("队列消息积压数(待消费数):" + topicWvbean.getQueueSize());
            System.out.println("队列入队消息数:" + topicWvbean.getEnqueueCount());
            System.out.println("队列出队消息数:" + topicWvbean.getDequeueCount());
            System.out.println("当前队列的消费者数量:" + topicWvbean.getConsumerCount());
            System.out.println("当前队列的生产者数量:" + topicWvbean.getProducerCount());

            /****************************** 开始对Producers的信息监控****start ***********************************************/
            Collection<ProducerViewMBean> topicProducerList = createConnector.getTopicProducers(topicWvbean.getName());
            for (ProducerViewMBean producerBean : topicProducerList) {
                producerBean.getConnectionId();
                producerBean.getClientId();
                producerBean.getSessionId();
                // 下面的很据需要自己写来展示

            }

            /****************************** 结束对Producers的信息监控****end ***********************************************/

            /****************************** 开始对Subscribers的信息监控****start ***********************************************/
            Collection<SubscriptionViewMBean> topicConsumerList = createConnector.getTopicSubscribers(topicWvbean.getName());
            for (SubscriptionViewMBean subConsumerBean : topicConsumerList) {

                System.out.println(subConsumerBean.getEnqueueCounter());
                System.out.println(subConsumerBean.getDequeueCounter());
                System.out.println(subConsumerBean.getDispatchedCounter());
                System.out.println(subConsumerBean.getDispatchedQueueSize());
                System.out.println(subConsumerBean.getPrefetchSize());
                System.out.println(subConsumerBean.getMaximumPendingMessageLimit());

                System.out.println(subConsumerBean.isExclusive());
                System.out.println(subConsumerBean.isDurable());
                System.out.println(subConsumerBean.isRetroactive());
                System.out.println(subConsumerBean.getPriority());
                System.out.println(subConsumerBean.getPriority());

                subConsumerBean.getConnectionId();
                subConsumerBean.getClientId();
                subConsumerBean.getEnqueueCounter();
                subConsumerBean.getDequeueCounter();
            }
            /****************************** 结束对Subscribers的信息监控****end ***********************************************/

        }

        /*createConnector.getConsumersOnConnection(connectionName)
        createConnector.getQueueConsumers(queueName)
        createConnector.getQueueProducers(queueName)
        createConnector.getTopicProducers(topicName)
        createConnector.getTopicSubscribers(topicName)*/

        /****************************** 结束对topic的信息监控****end ***********************************************/
        /****************************** 开始对连接的的信息监控****start ***********************************************/

        Collection<String> ssCollection = createConnector.getConnectors();

        Collection<ConnectionViewMBean> connwList = createConnector.getConnections();
        for (ConnectionViewMBean conVwBean : connwList) {
            System.out.println("连接的客户端地址为:" + conVwBean.getRemoteAddress());
            System.out.println("连接的客户端ID为:" + conVwBean.getClientId());
            System.out.println("连接的客户端name为:" + conVwBean.getUserName());
            System.out.println("连接的客户端是否已激活:" + conVwBean.isActive());
            System.out.println("连接的客户端是否阻塞:" + conVwBean.isBlocked());
            // 其它的监控信息自己看api即可
        }

        System.out.println("========================Connections====================================");
        Collection<String> connections = createConnector.getConnections("openwire");
        for (String s : connections) {
            System.out.println("Connections为:" + s);
            // 其它的监控信息自己看api即可
        }

        // ConnectionViewMBean connw= createConnector.getConnection("ID:Lenovo-PC-50534-1476698389120-0:1");
        // System.out.println("连接的客户端地址为:"+connw.getRemoteAddress());
        // System.out.println("连接的客户端ID为:"+connw.getClientId());
        // System.out.println("连接的客户端name为:"+connw.getUserName());
        // System.out.println("连接的客户端是否已激活:"+connw.isActive());
        // System.out.println("连接的客户端是否阻塞:"+connw.isBlocked());

        System.out.println("==============================Connections==============================");

        Collection<String> createConnectors = createConnector.getConnectors();
        for (String createConnectorName : createConnectors) {
            System.out.println("createConnector=====" + createConnectorName);
            ConnectorViewMBean connectorViewMBean = createConnector.getConnector(createConnectorName);
            System.out.println("createConnectorName-->connectorViewMBean.connectionCount()==" + connectorViewMBean.connectionCount());
            Collection<String> connectionNames = createConnector.getConnections(createConnectorName);
            for (String connectionName : connectionNames) {

                System.out.println("connectionNames--->" + connectionName);

                ConnectionViewMBean conVwBean = createConnector.getConnection(connectionName);
                System.out.println("==========start==============" + connectionName);
                System.out.println("连接的客户端地址为:" + conVwBean.getRemoteAddress());
                System.out.println("连接的客户端ID为:" + conVwBean.getClientId());
                System.out.println("连接的客户端name为:" + conVwBean.getUserName());
                System.out.println("连接的客户端是否已激活:" + conVwBean.isActive());
                System.out.println("连接的客户端是否阻塞:" + conVwBean.isBlocked());
                System.out.println("==========end==============" + connectionName);
            }
        }

        System.out.println("monitor end");
        /****************************** 结束对连接的的信息监控****end ***********************************************/

        createConnector.shutdown();
    }
}
