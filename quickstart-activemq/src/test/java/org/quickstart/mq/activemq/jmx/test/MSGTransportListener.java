/**
 * 项目名称：quickstart-activemq 
 * 文件名：MSGTransportListener.java
 * 版本信息：
 * 日期：2017年8月8日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.jmx.test;

import java.io.IOException;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.transport.TransportListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MSGTransportListener
 * 
 * @author：yangzl
 * @2017年8月8日 下午10:52:51
 * @since 1.0
 */
public class MSGTransportListener implements TransportListener {
    private ActivemqMonitorTest MonitorTest;
    private ActiveMQConnection connection;
    private static final Logger LOG = LoggerFactory.getLogger(MSGTransportListener.class);

    public MSGTransportListener(ActiveMQConnection conn, ActivemqMonitorTest monitor) {
        super();
        this.connection = conn;
        this.MonitorTest = monitor;
    }

    @Override
    public void onCommand(Object arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onException(IOException arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void transportInterupted() {
        // 向监控表中插入消息组件出现异常的数据信息，比如url，监控时间，异常发生时间，mq类型(activemq)等信息
        // 如果监控表中已经有当前url的监控数据，则把表中此url的数据转移到历史表中，然后插入新数据
        LOG.error("transportInterupted,生产者与服务器" + connection.getTransport() + "连接发生中断......");

    }

    @Override
    public void transportResumed() {
        // 向监控表中插入消息组件恢复异常的数据信息，比如url，监控时间，异常发生时间，mq类型(activemq)等信息
        // 如果监控表中已经有当前url的监控数据，则把表中此url的数据转移到历史表中，然后插入新数据
        LOG.error("transportInterupted,生产者与服务器" + connection.getTransport() + "重连成功.");

    }

}
