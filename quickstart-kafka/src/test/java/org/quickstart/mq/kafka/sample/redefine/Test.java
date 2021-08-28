package org.quickstart.mq.kafka.sample.redefine;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.MyKafkaConsumer;
import org.apache.kafka.clients.consumer.MyKafkaConsumer2;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.checkerframework.checker.units.qual.K;
import org.quickstart.mq.kafka.sample.SaveOffsetOnRebalance;
import org.quickstart.mq.kafka.sample.SimpleConsumerInterceptor;
import org.quickstart.mq.kafka.sample.SimpleConsumerInterceptor2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class Test {

    private static final String brokerList = "172.16.48.179:9081,172.16.48.180:9081,172.16.48.181:9081";

    public static void main(String[] args) {

        ByteBuddyAgent.install();

        new ByteBuddy()//
            .redefine(MyKafkaConsumer.class)//
            .name(KafkaConsumer.class.getName())//
            .make()//
            // .load(Thread.currentThread().getContextClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            .load(KafkaConsumer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        String topic = "topic03";
        // String topic = "bkk.item.tradetgt.count";
        // String topic = "test.topic.7";
        String topic2 = "test.topic.8";
        String topic3 = "test.topic.9";

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);// 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");// 制定consumer group
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); // 自动确认offset的时间间隔
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "300");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);// key的序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // value的序列化类
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase(Locale.ROOT));
        // props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT));

        // 配置partition分配策略，可选配置
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, Collections.singletonList(SimplePartitionAssignor.class));

        // 2 构建滤器链
        List interceptors = new ArrayList<>();
        // interceptors.add(SimpleConsumerInterceptor.class);
        // interceptors.add(SimpleConsumerInterceptor2.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);

        // Consumer<String, String> consumer = new MyKafkaConsumer<String, String>(props);
        Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        // 使用消费者对象订阅这些主题
        consumer.subscribe(Arrays.asList(topic), new SaveOffsetOnRebalance(consumer));

        System.out.println(consumer);

    }


    // 构造方法无法重写
    // 构造方法无法重写
    // 构造方法无法重写
    @org.junit.Test
    public void test() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        Map<TypeDescription, File> ff = new ByteBuddy()
            .redefine(KafkaConsumer.class)
            .defineConstructor(Visibility.PUBLIC)
            .withParameters(Properties.class)
            // .withParameters(Map.class, Deserializer.class, Deserializer.class)
            .intercept(MethodDelegation.toConstructor(MyKafkaConsumer2.class))
            .make()
            .saveIn(new File("/Users/lengfeng/test.java"));
            // .load(KafkaConsumer.class.getClassLoader())
            // .getLoaded();


        ByteBuddyAgent.install();

        new ByteBuddy()//
            .redefine(MyKafkaConsumer.class)//
            .name(KafkaConsumer.class.getName())//
            .make()//
            // .load(Thread.currentThread().getContextClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            .load(KafkaConsumer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);// 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");// 制定consumer group
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); // 自动确认offset的时间间隔
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "300");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);// key的序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // value的序列化类
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase(Locale.ROOT));
        // props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT));

        // 配置partition分配策略，可选配置
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, Collections.singletonList(SimplePartitionAssignor.class));

        // 2 构建滤器链
        List interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class);
        interceptors.add(SimpleConsumerInterceptor2.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);


        // KafkaConsumer<String, String> consumer33  = ff.getConstructor(Properties.class).newInstance(props);
        // System.out.println(consumer33);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        System.out.println(consumer);

    }


    @org.junit.Test
    public void test2() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        ByteBuddyAgent.install();

        new ByteBuddy()
            .redefine(KafkaConsumer.class)
            .method(ElementMatchers.isConstructor().and(ElementMatchers.takesArgument(0,Properties.class)).and(ElementMatchers.takesArgument(1,Deserializer.class)).and(ElementMatchers.takesArgument(2,Deserializer.class)))
            .intercept(MethodDelegation.to(MyKafkaConsumer2.class))
            .make()
            // .load(KafkaConsumer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            .saveIn(new File("/Users/lengfeng/test.java"));
        // .load(KafkaConsumer.class.getClassLoader())
        // .getLoaded();

            // .load(Thread.currentThread().getContextClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
            // .load(KafkaConsumer.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);// 定义kakfa 服务的地址，不需要将所有broker指定上
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "lengfeng.consumer.group");// 制定consumer group
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 是否自动提交进度
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000"); // 自动确认offset的时间间隔
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "300");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);// key的序列化类
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class); // value的序列化类
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase(Locale.ROOT));
        // props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT));

        // 配置partition分配策略，可选配置
        // props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, Collections.singletonList(SimplePartitionAssignor.class));

        // 2 构建滤器链
        List interceptors = new ArrayList<>();
        interceptors.add(SimpleConsumerInterceptor.class);
        interceptors.add(SimpleConsumerInterceptor2.class.getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, interceptors);


        // KafkaConsumer<String, String> consumer33  = ff.getConstructor(Properties.class).newInstance(props);
        // System.out.println(consumer33);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        System.out.println(consumer);

    }

}
