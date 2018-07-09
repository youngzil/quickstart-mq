/**
 * 项目名称：quickstart-activemq 
 * 文件名：ActivemqReconnectPoolTask.java
 * 版本信息：
 * 日期：2017年8月8日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.activemq.jmx.test;

import org.apache.activemq.ActiveMQConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActivemqReconnectPoolTask
 * 
 * @author：yangzl@asiainfo.com
 * @2017年8月8日 下午10:52:28
 * @since 1.0
 */
public class ActivemqReconnectPoolTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ActivemqReconnectPoolTask.class);
    private ActivemqMonitorTest monitorTest;
    private String url;

    public ActivemqReconnectPoolTask(ActivemqMonitorTest monitor, String connUrl) {
        super();
        this.url = connUrl;
        this.monitorTest = monitor;
    }

    @Override
    public void run() {
        try {
            ActiveMQConnection conn = this.monitorTest.reCreateConnection(this.url);
            if (conn.isStarted()) {
                System.out.println("broker地址" + this.url + "超时的连接已经恢复连接");
                // 向监控表中插入恢复正常的数据信息，比如url，监控时间，mq类型(activemq)等信息
                // 如果监控表中已经有当前url的监控数据，则把表中此url的数据转移到历史表中，然后插入新数据
            }
        } catch (Exception e) {
            LOG.warn("添加broker失败，broker信息：" + url, e);
        }
    }
}
