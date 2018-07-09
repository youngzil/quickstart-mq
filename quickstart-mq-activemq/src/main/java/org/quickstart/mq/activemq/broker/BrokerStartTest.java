/**
 * 项目名称：quickstart-activemq 
 * 文件名：BrokerStartTest.java
 * 版本信息：
 * 日期：2017年8月22日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.broker;

import java.net.URI;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.broker.region.policy.PolicyMap;
import org.apache.activemq.command.ActiveMQQueue;

/**
 * BrokerStartTest
 * 
 * @author：yangzl@asiainfo.com
 * @2017年8月22日 下午3:37:20
 * @since 1.0
 */
public class BrokerStartTest {

    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        broker.setBrokerName("testName");// 如果启动多个Broker时，必须为Broker设置一个名称
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(true);
        // broker.setUseJmx(false); // 启用JMX监控
        // 启用Advisory指定队列的消息监控
        PolicyMap policy = new PolicyMap();
        PolicyEntry entry = new PolicyEntry();
        entry.setAdvisoryForConsumed(true);
        policy.put(new ActiveMQQueue(">"), entry);
        // broker.setDestinationPolicy(policy);
        broker.start();

        // BrokerService broker2 = BrokerFactory.createBroker(new URI("broker:tcp://localhost:61616"));
        // broker2.start();

        while (true) {
            Thread.sleep(100000);
        }
    }

}
