package org.quickstart.mq.jafka;

import java.util.Properties;

import io.jafka.producer.Producer;
import io.jafka.producer.ProducerConfig;
import io.jafka.producer.StringProducerData;
import io.jafka.producer.serializer.StringEncoder;

/**
 * @author yangzl
 * @description TODO
 * @createTime 2019/12/8 23:29
 */
public class ProducerTest {

  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.put("broker.list", "0:127.0.0.1:9092");
    props.put("serializer.class", StringEncoder.class.getName());
    //
    ProducerConfig config = new ProducerConfig(props);
    Producer<String, String> producer = new Producer<String, String>(config);
    //
    StringProducerData data = new StringProducerData("demo");
    for(int i=0;i<1000;i++) {
      data.add("Hello world #"+i);
    }
    //
    try {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 100; i++) {
        producer.send(data);
      }
      long cost = System.currentTimeMillis() - start;
      System.out.println("send 100000 message cost: "+cost+" ms");
    } finally {
      producer.close();
    }
  }

}
