package org.quickstart.msgframe.v1.consumer;

import com.ai.aif.msgframe.consumer.MfConsumerClient;

public class TagConsumerTest {
    public static void main(String[] args) {
        // 第一个参数一般传主题名称，-p后面传tag和物理主题名称（主题_tag_num），num就是destination_rule.xml中的tag配置的number数量，如配置为2，num可以为1和2
        String[] sample_array = new String[] {"tagTopic", "-p", "tag", "tag2", "tagTopic_tag_1", "tagTopic_tag2_1", "tagTopic_tag2_2"};
        MfConsumerClient.main(sample_array);
    }

}
