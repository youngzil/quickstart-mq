/**
 * 项目名称：quickstart-mqtt 
 * 文件名：NettyMqttServerHandler.java
 * 版本信息：
 * 日期：2017年10月25日
 * Copyright asiainfo Corporation 2017
 * 版权所有 *
 */
package org.quickstart.mq.mqtt.netty.server;

import java.util.ArrayList;
import java.util.List;

import org.quickstart.mq.mqtt.netty.server.config.MqttManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttConnAckVariableHeader;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageFactory;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubAckPayload;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.internal.ConcurrentSet;

/**
 * NettyMqttServerHandler
 * 
 * @author：yangzl@asiainfo.com
 * @2017年10月25日 下午6:30:59
 * @since 1.0
 */
public class NettyMqttServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyMqttServerHandler.class);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.debug("连上可用连接" + " ,channel:" + ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.debug("连接断开" + " ,channel:" + ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        MqttMessage msg = (MqttMessage) message;

        if (msg != null && msg.fixedHeader() != null) {

            MqttMessageType msgType = msg.fixedHeader().messageType();

            MqttMessage messageAck = null;

            switch (msgType) {
                case CONNECT:

                    messageAck = getMqttMessageByType(MqttMessageType.CONNACK);
                    break;

                case CONNACK:

                case SUBSCRIBE:

                    MqttSubscribeMessage subscribeMessage = (MqttSubscribeMessage) message;
                    List<MqttTopicSubscription> topicSubscriptions = subscribeMessage.payload().topicSubscriptions();

                    ArrayList grantedQoSLevels = new ArrayList(topicSubscriptions.size());

                    if (null != topicSubscriptions && !topicSubscriptions.isEmpty()) {
                        for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
                            MqttManager.registerTopic(topicSubscription.topicName(), ctx.channel());

                            grantedQoSLevels.add(Integer.valueOf(topicSubscription.qualityOfService().value()));

                        }
                    }

                    // messageAck = getMqttMessageByType(MqttMessageType.SUBACK);

                    MqttSubAckPayload subAckPayload = new MqttSubAckPayload(grantedQoSLevels);

                    MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
                    MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(((MqttMessageIdVariableHeader) msg.variableHeader()).messageId());
                    // return (MqttPubAckMessage)MqttMessageFactory.newMessage(fixedHeader, variableHeader, (Object) null);
                    messageAck = MqttMessageFactory.newMessage(fixedHeader, variableHeader, new MqttSubAckPayload());

                    break;

                case SUBACK:

                case UNSUBSCRIBE:

                    MqttUnsubscribeMessage unsubscribeMessage = (MqttUnsubscribeMessage) message;
                    List<String> topics = unsubscribeMessage.payload().topics();

                    if (null != topics && !topics.isEmpty()) {
                        for (String topic : topics) {
                            MqttManager.unregisterTopic(topic, ctx.channel());
                        }
                    }

                    messageAck = getMqttMessageByType(MqttMessageType.UNSUBACK);

                    break;

                case UNSUBACK:

                case PUBLISH:

                    MqttPublishMessage publishMessage = (MqttPublishMessage) message;
                    String topic = publishMessage.variableHeader().topicName();
                    ConcurrentSet<Channel> channels = MqttManager.getTopictable().get(topic);

                    if (null != channels && !channels.isEmpty()) {
                        // 把流读完，就没有数据了，放在循环外面，否则第二次越界
                        // publishMessage.payload().readSlice(12);
                        for (Channel channel : channels) {
                            if (ctx.channel() != channel && channel.isOpen()) {

                                System.out.println(publishMessage.payload().capacity());

                                // 正确用法，copy
                                channel.writeAndFlush(publishMessage.copy());

                                // 每次读6个，12个可以发送2个客户端
                                // MqttFixedHeader fixedHeader2 = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0);
                                // MqttPublishVariableHeader variableHeader2 = new MqttPublishVariableHeader("sensor", 23);
                                // channel.writeAndFlush(MqttMessageFactory.newMessage(fixedHeader2, variableHeader2, publishMessage.payload().readSlice(6)));// 每次读6个，12个可以发送2个客户端

                                // 只能发送一个客户端
                                // publishMessage.payload().readSlice(11);//把流读完，就没有数据了，放在循环外面，否则第二次越界
                                // channel.writeAndFlush(publishMessage);// 读一次就要重置，否则就是空

                            }
                        }
                    }

                    messageAck = getMqttMessageByType(MqttMessageType.PUBACK);
                    break;

                case PUBACK:
                case PUBREC:
                case PUBREL:
                case PUBCOMP:

                case PINGREQ:

                    messageAck = getMqttMessageByType(MqttMessageType.PINGRESP);
                    break;

                case PINGRESP:

                case DISCONNECT:

                default:
                    throw new IllegalArgumentException("unknown message type: " + msgType);
            }

            if (messageAck != null) {

                if (messageAck.fixedHeader().messageType() == MqttMessageType.DISCONNECT) {
                    ctx.write(messageAck).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(messageAck).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            }

            // MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
            // MqttConnAckVariableHeader variableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true);
            // MqttConnAckMessage message3 = (MqttConnAckMessage)MqttMessageFactory.newMessage(fixedHeader, variableHeader, (Object)null);

        }

    }

    private MqttMessage getMqttMessageByType(MqttMessageType msgType) {

        MqttFixedHeader fixedHeader = new MqttFixedHeader(msgType, false, MqttQoS.AT_MOST_ONCE, false, 0);

        if (MqttMessageType.PUBACK == msgType || msgType == MqttMessageType.SUBACK || msgType == MqttMessageType.UNSUBACK) {

            MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(234);
            // return (MqttPubAckMessage)MqttMessageFactory.newMessage(fixedHeader, variableHeader, (Object) null);
            return MqttMessageFactory.newMessage(fixedHeader, variableHeader, (Object) null);

        } else {
            MqttConnAckVariableHeader variableHeader = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true);
            // return (MqttConnAckMessage)MqttMessageFactory.newMessage(fixedHeader, variableHeader, (Object) null);
            return MqttMessageFactory.newMessage(fixedHeader, variableHeader, (Object) null);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            if (cause.getCause() instanceof ReadTimeoutException) {
                MqttFixedHeader t = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
                MqttMessage message = MqttMessageFactory.newMessage(t, (Object) null, (Object) null);
                ctx.write(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } catch (Throwable arg4) {
            arg4.printStackTrace();
        }

        cause.printStackTrace();
        ctx.close();
    }

}
