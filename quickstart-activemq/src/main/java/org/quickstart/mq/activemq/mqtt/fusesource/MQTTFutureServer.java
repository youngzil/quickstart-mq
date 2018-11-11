/**
 * 项目名称：quickstart-activemq 
 * 文件名：MQTTFutureServer.java
 * 版本信息：
 * 日期：2016年12月22日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.mqtt.fusesource;

/**
 * MQTTFutureServer 
 *  
 * @author：youngzil@163.com
 * @2016年12月22日 下午4:15:34 
 * @version 1.0
 */
import java.net.URISyntaxException;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.fusesource.mqtt.client.Tracer;
import org.fusesource.mqtt.codec.MQTTFrame;

/**
 * 采用Future模式 发布主题
 *
 */
public class MQTTFutureServer {

    private final static String CONNECTION_STRING = "tcp://10.20.16.209:21881";
    private final static boolean CLEAN_START = true;
    private final static String CLIENT_ID = "server";
    private final static short KEEP_ALIVE = 30;// 低耗网络，但是又需要及时获取数据，心跳30s

    public static Topic[] topics = {new Topic("mqtt/aaa", QoS.EXACTLY_ONCE), new Topic("mqtt/bbb", QoS.AT_LEAST_ONCE), new Topic("mqtt/ccc", QoS.AT_MOST_ONCE)};

    public final static long RECONNECTION_ATTEMPT_MAX = 6;
    public final static long RECONNECTION_DELAY = 2000;

    public final static int SEND_BUFFER_SIZE = 2 * 1024 * 1024;// 发送最大缓冲为2M

    public static void main(String[] args) {
        MQTT mqtt = new MQTT();
        try {
            // ==MQTT设置说明
            // 设置服务端的ip
            mqtt.setHost(CONNECTION_STRING);
            // 连接前清空会话信息 ,若设为false，MQTT服务器将持久化客户端会话的主体订阅和ACK位置，默认为true
            mqtt.setCleanSession(CLEAN_START);
            // 设置心跳时间 ,定义客户端传来消息的最大时间间隔秒数，服务器可以据此判断与客户端的连接是否已经断开，从而避免TCP/IP超时的长时间等待
            mqtt.setKeepAlive(KEEP_ALIVE);
            // 设置客户端id,用于设置客户端会话的ID。在setCleanSession(false);被调用时，MQTT服务器利用该ID获得相应的会话。
            // 此ID应少于23个字符，默认根据本机地址、端口和时间自动生成
            mqtt.setClientId(CLIENT_ID);
            // 服务器认证用户名
            // mqtt.setUserName("admin");
            // 服务器认证密码
            // mqtt.setPassword("admin");

            /*
            //设置“遗嘱”消息的内容，默认是长度为零的消息
            mqtt.setWillMessage("willMessage");
            //设置“遗嘱”消息的QoS，默认为QoS.ATMOSTONCE
            mqtt.setWillQos(QoS.AT_LEAST_ONCE);
            //若想要在发布“遗嘱”消息时拥有retain选项，则为true
            mqtt.setWillRetain(true);
            //设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
            mqtt.setWillTopic("willTopic");
            */

            // ==失败重连接设置说明
            // 设置重新连接的次数 ,客户端已经连接到服务器，但因某种原因连接断开时的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
            mqtt.setReconnectAttemptsMax(RECONNECTION_ATTEMPT_MAX);
            // 设置重连的间隔时间 ,首次重连接间隔毫秒数，默认为10ms
            mqtt.setReconnectDelay(RECONNECTION_DELAY);
            // 客户端首次连接到服务器时，连接的最大重试次数，超出该次数客户端将返回错误。-1意为无重试上限，默认为-1
            // mqtt.setConnectAttemptsMax(10L);
            // 重连接间隔毫秒数，默认为30000ms
            // mqtt.setReconnectDelayMax(30000L);
            // 设置重连接指数回归。设置为1则停用指数回归，默认为2
            // mqtt.setReconnectBackOffMultiplier(2);

            // == Socket设置说明
            // 设置socket接收缓冲区大小，默认为65536（64k）
            // mqtt.setReceiveBufferSize(65536);
            // 设置socket发送缓冲区大小，默认为65536（64k）
            mqtt.setSendBufferSize(SEND_BUFFER_SIZE);
            //// 设置发送数据包头的流量类型或服务类型字段，默认为8，意为吞吐量最大化传输
            mqtt.setTrafficClass(8);

            // ==带宽限制设置说明
            mqtt.setMaxReadRate(0);// 设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
            mqtt.setMaxWriteRate(0);// 设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制

            // ==选择消息分发队列
            // 若没有调用方法setDispatchQueue，客户端将为连接新建一个队列。如果想实现多个连接使用公用的队列，显式地指定队列是一个非常方便的实现方法
            // mqtt.setDispatchQueue(Dispatch.createQueue("mqtt/aaa"));

            // ==设置跟踪器
            /* mqtt.setTracer(new Tracer(){
                 @Override
                 public void onReceive(MQTTFrame frame) {
                     System.out.println("recv: "+frame);
                 }
                 @Override
                 public void onSend(MQTTFrame frame) {
                     System.out.println("send: "+frame);
                 }
                 @Override
                 public void debug(String message, Object... args) {
                     System.out.println(String.format("debug: "+message, args));
                 }
             });*/

            // 使用Future创建连接
            final FutureConnection connection = mqtt.futureConnection();
            connection.connect();
            int count = 1;
            while (true) {
                count++;
                // 用于发布消息，目前手机段不需要向服务端发送消息
                // 主题的内容
                String message = "Hello " + count + " MQTT...";
                String topic = "mqtt/bbb";
                connection.publish(topic, message.getBytes(), QoS.AT_LEAST_ONCE, false);
                System.out.println("MQTTFutureServer.publish Message " + "Topic Title :" + topic + " context :" + message);

            }
            // 使用回调式API
            /* final CallbackConnection callbackConnection=mqtt.callbackConnection();
            //连接监听
            callbackConnection.listener(new Listener() {
            //接收订阅话题发布的消息
            @Override
            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
            System.out.println("=============receive msg================"+new String(body.toByteArray()));
            ack.run();
            }
            //连接失败
            @Override
            public void onFailure(Throwable value) {
             System.out.println("===========connect failure===========");
             callbackConnection.disconnect(null);
            }
               //连接断开
            @Override
            public void onDisconnected() {
             System.out.println("====mqtt disconnected=====");
            }
            //连接成功
            @Override
            public void onConnected() {
             System.out.println("====mqtt connected=====");
            }
            });
            //连接
            callbackConnection.connect(new Callback<Void>() {
              //连接失败
                public void onFailure(Throwable value) {
                    System.out.println("============连接失败："+value.getLocalizedMessage()+"============");
                }
                // 连接成功
                public void onSuccess(Void v) {
                    //订阅主题
                    Topic[] topics = {new Topic("mqtt/bbb", QoS.AT_LEAST_ONCE)};
                    callbackConnection.subscribe(topics, new Callback<byte[]>() {
                        //订阅主题成功
                     public void onSuccess(byte[] qoses) {
                            System.out.println("========订阅成功=======");
                        }
                     //订阅主题失败
                        public void onFailure(Throwable value) {
                         System.out.println("========订阅失败=======");
                         callbackConnection.disconnect(null);
                        }
                    });
                     //发布消息
                    callbackConnection.publish("mqtt/bbb", ("Hello ").getBytes(), QoS.AT_LEAST_ONCE, true, new Callback<Void>() {
                        public void onSuccess(Void v) {
                          System.out.println("===========消息发布成功============");
                        }
                        public void onFailure(Throwable value) {
                         System.out.println("========消息发布失败=======");
                         callbackConnection.disconnect(null);
                        }
                    });
            
                }
            });
            */

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
