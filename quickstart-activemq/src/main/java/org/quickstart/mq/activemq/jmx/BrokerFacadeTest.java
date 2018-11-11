/**
 * 项目名称：quickstart-activemq 
 * 文件名：BrokerFacadeTest.java
 * 版本信息：
 * 日期：2017年8月8日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.jmx;

import java.util.Collection;
import java.util.Set;

import javax.management.ObjectName;

import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.commons.lang3.StringUtils;

/**
 * BrokerFacadeTest
 * 
 * @author：youngzil@163.com
 * @2017年8月8日 下午10:42:47
 * @since 1.0
 */
public class BrokerFacadeTest extends RemoteJMXBrokerFacade {

    @Override
    public Collection<SubscriptionViewMBean> getQueueConsumers(String queueName) throws Exception {
        String brokerName = getBrokerName();
        queueName = StringUtils.replace(queueName, "\"", "_");
        ObjectName query = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + brokerName + ",destinationType=Queue,destinationName=*,endpoint=Consumer,*");
        Set<ObjectName> queryResult = queryNames(query, null);
        return getManagedObjects(queryResult.toArray(new ObjectName[queryResult.size()]), SubscriptionViewMBean.class);
    }

    public static void main(String[] args) {
        System.out.println(StringUtils.replace("*", "\"", "_"));
    }

}
