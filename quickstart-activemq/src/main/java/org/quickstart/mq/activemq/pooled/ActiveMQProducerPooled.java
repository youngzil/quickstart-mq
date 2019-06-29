/**
 * 项目名称：quickstart-activemq 文件名：ActiveMQProductHelper.java 版本信息： 日期：2019年6月27日 Copyright yangzl Corporation 2019 版权所有 *
 */
package org.quickstart.mq.activemq.pooled;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledSession;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActiveMQProductHelper
 *
 * @version 2.0
 * @author：youngzil@163.com
 * @2019年6月27日 上午12:02:33
 */
public class ActiveMQProducerPooled {

  public static final Logger LOG = LoggerFactory.getLogger(ActiveMQProducerPooled.class);
  private static PooledConnectionFactory poolFactory;

  public static void main(String[] args) throws JMSException {
    LOG.info("getPooledConnectionFactory");
    LOG.info("getPooledConnectionFactory create new");
    LOG.info("获取单例的PooledConnectionFactory");
    String url = "failover:(tcp://10.11.20.101:61616,tcp://10.11.20.103:61616,tcp://10.1.226.100:61616)";

    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, url);

    /*
     * ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(); ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
     * activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy); activeMQConnectionFactory.setUseAsyncSend(true); activeMQConnectionFactory.setUserName("XXX");
     * activeMQConnectionFactory.setPassword("XXXX"); activeMQConnectionFactory.setBrokerURL( "failover:(tcp://XXXXX:XXXXX,tcp://XXXXX:XXXXX)?randomize=true");
     */

    // poolFactory = new PooledConnectionFactory(url);

    poolFactory = new PooledConnectionFactory(factory);

//    poolFactory.setConnectionFactory(factory);


    // 池中借出的对象的最大数目
//    maxConnections表示的是LinkedList中connection的数目
    poolFactory.setMaxConnections(100);
    poolFactory.setMaximumActiveSessionPerConnection(50);
    // 后台对象清理时，休眠时间超过了3000毫秒的对象为过期
    poolFactory.setTimeBetweenExpirationCheckMillis(30000);
    poolFactory.setIdleTimeout(30);
    LOG.info("getPooledConnectionFactory create success");

    /**
     * 1.对象池管理connection和session,包括创建和关闭等 2.PooledConnectionFactory缺省设置MaxIdle为1， 官方解释Set max idle (not max active) since our connections always idle in the pool. *
     *
     * @return * @throws JMSException
     */
    // 1.直接调用池化连接工厂中的创建连接方法==>获取到的就是池化的连接
    PooledConnection pooledConnection = (PooledConnection) poolFactory.createConnection();
    pooledConnection.start();

    // false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    // Session session = pooledConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // 2.从池化连接中获取到才是池化的会话；
    PooledSession session = (PooledSession) pooledConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // 3.通过池化的会话创建目标和消费者
    Destination destination = session.createQueue("pooledqueueTest");

    MessageProducer producer = session.createProducer(destination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    MessageConsumer consumer = session.createConsumer(destination);

    // LOG.info("producer send msg: {} ", msg);
    /*
     * if (StringUtil.isEmpty(msg)) { LOG.warn("发送消息不能为空。"); return; }
     */
    String str = "thisvvvv---this";
    for (int i = 0; i < 3; i++) {
      str += str;

    }

    while (true) {
      try {

        // System.out.println("pooledConnection=" + pooledConnection + ",session="+ session) ;
        // LOG.info("create session");

        TextMessage textMessage = session.createTextMessage(str);

        // producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.send(textMessage);
        // LOG.info("create session success");
      } catch (JMSException e) {
        LOG.error(e.getMessage(), e);
      }
    }

  }
}
