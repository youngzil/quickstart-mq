https://coderwall.com/p/koctbg/embedded-openmq-5-0-without-installation


It is possible to run openMQ 5.0 embedded without referencing a complete installation of openMQ. This can be usefull when creating tests. It does however initially require an installation, since some files need to be copied to your project.

Do the following:

Download and install openMQ 5.0
In your project create in the 'src/test/resources' eg. an 'openmq' folder
In the 'openmq' folder create a folder named 'lib/props/broker'
Copy default.properties and install.properties from your openMQ installation here
In the 'openmq' folder create a folder named 'var/instances/imqbroker/etc'
Copy accesscontrol.properties and passwd from your openMQ installation here
In your pom.xml add the following dependency:

<dependency>
    <groupId>org.glassfish.main.extras</groupId>
    <artifactId>glassfish-embedded-all</artifactId>
    <version>4.0</version>
    <scope>test</scope>
</dependency>

Now you can create a (Java) class which creates an embedded broker. This looks like:

package my.package;

import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance;
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;
import com.sun.messaging.jmq.jmsserver.Globals;
import com.sun.messaging.jmq.jmsservice.BrokerEvent;
import com.sun.messaging.jmq.jmsservice.BrokerEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class EmbeddedBroker {
    private static final String IMQ_HOME = EmbeddedBroker.class.getClassLoader().getResource("openmq").getPath();
    private static final String IMQ_VAR_HOME = IMQ_HOME.concat("/var");
    private static final String IMQ_LIB_HOME = IMQ_HOME.concat("/lib");
    private static final String IMQ_INSTANCE_HOME = IMQ_VAR_HOME.concat("/instances");
    private static final String IMQ_INSTANCE_NAME = "imqbroker";

    public static BrokerInstance start() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClientRuntime clientRuntime = ClientRuntime.getRuntime();
        BrokerInstance brokerInstance = clientRuntime.createBrokerInstance();
        BrokerEventListener listener = new BrokerEventListener() {

            public void brokerEvent(BrokerEvent error) {
            }

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
        return new String[]{
                  "-varhome", IMQ_VAR_HOME,
                  "-libhome", IMQ_LIB_HOME,
                  "-imqhome", IMQ_HOME
        };
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
//          Globals.getConfig().updateProperty("imq.autocreate.destination.maxNumMsgs", "100");
        } catch (Exception e) {
            EmbeddedBroker.log.warn("Unable to set the configuration for the broker", e);
        }
    }
}

By calling the EmbeddedBroker.start() method, the broker will be started and you can do whatever you want to do, like creating queues/topics and send/receive JMS messages.