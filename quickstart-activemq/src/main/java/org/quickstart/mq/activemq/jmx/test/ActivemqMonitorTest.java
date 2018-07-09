/**
 * 项目名称：quickstart-activemq 
 * 文件名：ActivemqMonitorTest.java
 * 版本信息：
 * 日期：2017年8月8日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.jmx.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.TransportFilter;
import org.apache.activemq.transport.failover.FailoverTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActivemqMonitorTest 对activemq组件的健康性的一个检测 这个main方法在整个工程调用一次就可以了
 * 
 * @author：yangzl@asiainfo.com
 * @2017年8月8日 下午10:51:42
 * @since 1.0
 */
public class ActivemqMonitorTest {
    private static final Logger LOG = LoggerFactory.getLogger(ActivemqMonitorTest.class);
    private static final ExecutorService ACTIVEMQ_RECONNECTPOOL = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private Map<String, Connection> map = new HashMap<String, Connection>();
    private final static ActivemqMonitorTest monitorClient = new ActivemqMonitorTest();

    public static void main(String[] args) {
        // 在这个地方先在数据库从activemq中间件地址表中查询所有的mq地址，
        // 循环调用即可
        String url1 = "failover:(tcp://10.21.20.135:61616)";
        String url2 = "failover:(tcp://10.21.20.135:61616)?initialReconnectDelay=1&maxReconnectDelay=2";
        String url3 = "failover:(tcp://10.20.16.210:20003,tcp://10.20.16.211:20003,tcp://10.20.16.209:20001)?initialReconnectDelay=1";
        String url4 = "failover:(tcp://10.20.16.210:20001)?initialReconnectDelay=1&maxReconnectDelay=2";
        String[] urls = {url1};

        for (String url : urls) {
            try {
                // 在这个地方先在数据库从activemq中间件地址表中查询所有的mq地址，
                // 循环调用即可

                monitorClient.createConnection(url);
            } catch (JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private ActiveMQConnection createConnection(String url) throws JMSException, Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection conn = (ActiveMQConnection) connectionFactory.createConnection();

        Transport transport = conn.getTransport();

        while (true) {
            if (transport instanceof FailoverTransport) {
                ((FailoverTransport) transport).setTimeout(6000);
                break;
            }
            transport = ((TransportFilter) transport).getNext();
        }
        try {
            conn.start();
            conn.addTransportListener(new MSGTransportListener(conn, this));
            System.out.println("连接成功");
            // 向监控表中插入mq运行正常的数据信息，比如url，监控时间，mq类型(activemq)等信息
            // 如果监控表中已经有当前url的监控数据，则把表中此url的数据转移到历史表中，然后插入新数据
            map.put(url, conn);

            service.scheduleAtFixedRate(new ActivemqReconnectPoolTask(this, url), 0, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOG.error("连接超时", e);
            // 向监控表中插入mq运行异常的数据信息，比如url，监控时间，mq类型(activemq)等信息
            // 如果监控表中已经有当前url的监控数据，则把表中此url的数据转移到历史表中，然后插入新数据
            conn.close();
            ACTIVEMQ_RECONNECTPOOL.execute(new ActivemqReconnectPoolTask(this, url));
            // service.scheduleAtFixedRate(new ActivemqReconnectPoolTask(this,url), 0, 1, TimeUnit.SECONDS);
            throw e;
        }
        return conn;
    }

    public ActiveMQConnection reCreateConnection(String url) throws JMSException, Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
        ActiveMQConnection conn = (ActiveMQConnection) connectionFactory.createConnection();
        try {
            conn.start();
            conn.addTransportListener(new MSGTransportListener(conn, this));
            System.out.println("连接成功");

            map.put(url, conn);
        } catch (Exception e) {
            LOG.error("连接超时", e);
            throw e;
        }
        return conn;
    }
}
