package org.quickstart.msgframe.v2.jmeter;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.aif.msgframe.MfProducerClient;
import com.ai.aif.msgframe.common.CompletionListener;
import com.ai.aif.msgframe.common.message.MsgFMessage;
import com.ai.aif.msgframe.common.message.MsgFTextMessage;

/**
 * 4.1.1 通讯模式(同步、异步、oneway模式生产消息)
 **/

public class SendMessageTest {

	static final Logger logger = LoggerFactory.getLogger(SendMessageTest.class);

	private static String model = "oneway";
	private static int count = 10000;// 默认1W条

	public void sendMessage() {
		try {

			MfProducerClient client = new MfProducerClient();
			for (int i = 0; i < count; i++) {

				MsgFTextMessage message = new MsgFTextMessage();
				message.setText("test send message " + i);
				/**
				 * client.send(topic, msg); topic 要发送的主题 msg 要发送的消息体
				 */
				// 同步模式生产消息
				if ("sync".equals(model)) {
					logger.info(model + "模式生产消息");
					client.send("topicTest", message);
				} else if ("async".equals(model)) {
					logger.info(model + "模式生产消息");
					// 异步模式生产消息
					client.asyncSend("topicTest", message, new CompletionListener() {
						@Override
						public void onCompletion(MsgFMessage message) {
							// logger.info("发送成功" + message.getMsgId());
						}

						@Override
						public void onException(MsgFMessage message, Exception exception) {
							// logger.error("发送消息异常");
						}
					});
				} else if ("oneway".equals(model)) {
					logger.info(model + "模式生产消息");
					// oneway模式生产消息
					client.sendOneway("topicTest", message);
				} else {
					logger.error("不支持的模式");
				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

}
