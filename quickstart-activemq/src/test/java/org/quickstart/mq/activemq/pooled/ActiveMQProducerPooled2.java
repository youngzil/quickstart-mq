/**
 * 项目名称：quickstart-activemq 文件名：ActiveMQProductHelper.java 版本信息： 日期：2019年6月27日 Copyright yangzl Corporation 2019 版权所有 *
 */
package org.quickstart.mq.activemq.pooled;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.*;

/**
 * ActiveMQProductHelper
 * 
 * @author：yangzl
 * @2019年6月27日 上午12:02:33
 * @version 2.0
 */
public class ActiveMQProducerPooled2 {
  public static final Logger LOG = LoggerFactory.getLogger(ActiveMQProducerPooled2.class);
  private static PooledConnectionFactory poolFactory;

  /**
   * 获取单例的PooledConnectionFactory
   * 
   * @return
   */
  private static synchronized PooledConnectionFactory getPooledConnectionFactory() {
    LOG.info("getPooledConnectionFactory");
    if (poolFactory != null)
      return poolFactory;
    LOG.info("getPooledConnectionFactory create new");
    String url = "failover:(tcp://10.11.20.101:61616,tcp://10.11.20.103:61616,tcp://10.1.226.100:61616)";

    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);
    poolFactory = new PooledConnectionFactory(factory);
    // 池中借出的对象的最大数目
    poolFactory.setMaxConnections(100);
    poolFactory.setMaximumActiveSessionPerConnection(50);
    // 后台对象清理时，休眠时间超过了3000毫秒的对象为过期
    poolFactory.setTimeBetweenExpirationCheckMillis(3000);
    LOG.info("getPooledConnectionFactory create success");
    return poolFactory;
  }

  /**
   * 1.对象池管理connection和session,包括创建和关闭等 2.PooledConnectionFactory缺省设置MaxIdle为1， 官方解释Set max idle (not max active) since our connections always idle in the pool. *
   * 
   * @return * @throws JMSException
   */
  public static Session createSession() throws JMSException {
    PooledConnectionFactory poolFactory = getPooledConnectionFactory();
    PooledConnection pooledConnection = (PooledConnection) poolFactory.createConnection();
    // false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    return pooledConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  public static void produce(String subject, String msg) {
    LOG.info("producer send msg: {} ", msg);
    /*
     * if (StringUtil.isEmpty(msg)) { LOG.warn("发送消息不能为空。"); return; }
     */
    try {
      Session session = createSession();
      LOG.info("create session");
      TextMessage textMessage = session.createTextMessage(msg);
      Destination destination = session.createQueue(subject);
      MessageProducer producer = session.createProducer(destination);
      // producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      producer.send(textMessage);
      LOG.info("create session success");
    } catch (JMSException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  public static void main(String[] args) {
    ActiveMQProducerPooled2.produce("test.subject", "hello");
  }
}
