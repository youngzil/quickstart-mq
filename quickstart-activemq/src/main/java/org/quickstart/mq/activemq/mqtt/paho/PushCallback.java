/**
 * 项目名称：quickstart-activemq 
 * 文件名：PushCallback.java
 * 版本信息：
 * 日期：2016年12月22日
 * Copyright asiainfo Corporation 2016
 * 版权所有 *
 */
package org.quickstart.mq.activemq.mqtt.paho;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
/**
 * PushCallback 
 *  
 * @author：youngzil@163.com
 * @2016年12月22日 下午5:24:29 
 * @version 1.0
 */
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 * 发布消息的回调类
 * 
 * 必须实现MqttCallback的接口并实现对应的相关接口方法 ◦CallBack 类将实现 MqttCallBack。每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。在回调中，将它用来标识已经启动了该回调的哪个实例。 ◦必须在回调类中实现三个方法：
 * 
 * public void messageArrived(MqttTopic topic, MqttMessage message) 接收已经预订的发布。
 * 
 * public void connectionLost(Throwable cause) 在断开连接时调用。
 * 
 * public void deliveryComplete(MqttDeliveryToken token)) 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。 ◦由 MqttClient.connect 激活此回调。
 * 
 */
public class PushCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        // 连接丢失后，一般在这里面进行重连
        System.out.println("连接断开，可以做重连");
    }

    /* (non-Javadoc)
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // subscribe后得到的消息会执行到这里面
        System.out.println("接收消息主题:" + topic);
        System.out.println("接收消息Qos:" + message.getQos());
        System.out.println("接收消息内容:" + new String(message.getPayload()));

    }

    /* (non-Javadoc)
     * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // publish后会执行到这里
        System.out.println("deliveryComplete---------" + token.isComplete());

    }

}
