package org.quickstart.mq.jafka;

import io.jafka.api.FetchRequest;
import io.jafka.consumer.SimpleConsumer;
import io.jafka.message.MessageAndOffset;
import io.jafka.utils.Utils;
import java.io.IOException;

/**
 * @author yangzl
 * @description TODO
 * @createTime 2019/12/8 23:31
 */
public class ConsumerTest {

  public static void main(String[] args) throws IOException {
    SimpleConsumer consumer = new SimpleConsumer("127.0.0.1", 9092);
    //
    long offset = 0;
    while (true) {
      FetchRequest request = new FetchRequest("test", 0, offset);
      for (MessageAndOffset msg : consumer.fetch(request)) {
        System.out.println(Utils.toString(msg.message.payload(), "UTF-8"));
        offset = msg.offset;
      }
    }
  }

}
