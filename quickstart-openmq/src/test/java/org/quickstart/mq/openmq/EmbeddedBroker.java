/**
 * 项目名称：quickstart-openmq 
 * 文件名：EmbeddedBroker.java
 * 版本信息：
 * 日期：2018年1月15日
 * Copyright youngzil Corporation 2018
 * 版权所有 *
 */
package org.quickstart.mq.openmq;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance;
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;
import com.sun.messaging.jmq.jmsserver.Globals;
import com.sun.messaging.jmq.jmsservice.BrokerEvent;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * EmbeddedBroker
 * 
 * @author：youngzil@163.com
 * @2018年1月15日 下午10:47:54
 * @since 1.0
 */
@Slf4j
public class EmbeddedBroker {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedBroker.class);

    private static final String IMQ_HOME = EmbeddedBroker.class.getClassLoader().getResource("openmq").getPath();
    private static final String IMQ_VAR_HOME = IMQ_HOME.concat("/var");
    private static final String IMQ_LIB_HOME = IMQ_HOME.concat("/lib");
    private static final String IMQ_INSTANCE_HOME = IMQ_VAR_HOME.concat("/instances");
    private static final String IMQ_INSTANCE_NAME = "imqbroker";

    public static BrokerInstance start() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClientRuntime clientRuntime = ClientRuntime.getRuntime();
        BrokerInstance brokerInstance = clientRuntime.createBrokerInstance();
        BrokerEventListener listener = new BrokerEventListener() {

            public void brokerEvent(BrokerEvent error) {}

            public boolean exitRequested(BrokerEvent event, Throwable thr) {
                return true;
            }
        };

        Properties properties = brokerInstance.parseArgs(getArguments());
        updateConfigurationForBroker();

        brokerInstance.init(properties, listener);
        brokerInstance.start();

        return brokerInstance;
    }

    private static String[] getArguments() {
        return new String[] {"-varhome", IMQ_VAR_HOME, "-libhome", IMQ_LIB_HOME, "-imqhome", IMQ_HOME};
    }

    private static void updateConfigurationForBroker() {
        try {
            Globals.getConfig().updateProperty("imq.home", IMQ_HOME);
            Globals.getConfig().updateProperty("imq.libhome", IMQ_LIB_HOME);
            Globals.getConfig().updateProperty("imq.varhome", IMQ_VAR_HOME);
            Globals.getConfig().updateProperty("imq.instanceshome", IMQ_INSTANCE_HOME);
            Globals.getConfig().updateProperty("imq.instancename", IMQ_INSTANCE_NAME);
            Globals.getConfig().updateBooleanProperty("imq.persist.file.newTxnLog.enabled", false, true);
            Globals.getConfig().updateBooleanProperty("imq.cluster.enabled", false, true);
            // Globals.getConfig().updateProperty("imq.autocreate.destination.maxNumMsgs", "100");
        } catch (Exception e) {
            EmbeddedBroker.logger.warn("Unable to set the configuration for the broker", e);
        }
    }
}
