package org.apache.kafka.clients.consumer;

import org.apache.kafka.common.serialization.Deserializer;
import org.quickstart.mq.kafka.sample.SimpleConsumerInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyConsumerConfig {

    public static ConsumerConfig getConsumerConfig(Map<String, Object> configs, Deserializer keyDeserializer, Deserializer valueDeserializer) {

        System.out.println(configs);
        List interceptorList = (List)configs.getOrDefault(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, new ArrayList<>());

        System.out.println(interceptorList);
        interceptorList.add(SimpleConsumerInterceptor.class.getName());

        interceptorList = (List)interceptorList.stream().map(interceptor -> {
            if (interceptor instanceof Class) {
                return ((Class<?>)interceptor).getName();
            }
            return interceptor;

        }).distinct().collect(Collectors.toList());

        configs.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptorList);

        ConsumerConfig config = new ConsumerConfig(ConsumerConfig.appendDeserializerToConfig(configs, keyDeserializer, valueDeserializer));
        return config;
    }

}
