Producer:
producer.send
1、执行this.interceptors.onSend(record)处理record之后，执行doSend，catch到Exception后执行this.interceptors.onSendError(record, tp, e);
2、在doSend时，首先进行校验、初始化、选择partition，构建回调函数interceptCallback，accumulator.append添加消息到缓存，判断已满或是新建，就唤醒sender，获取返回结果
3、accumulator.append时候，获取本分区的Deque<ProducerBatch>，tryAppend就是从Deque中获取peekLast的ProducerBatch，然后把消息追加进去



Broker：
启动：kafka.Kafka


Consumer：
1、consumer.poll(10000);，然后this.interceptors.onConsume，然后返回客户端

客户端（Producer和Consumer）的interceptors都可以在send和receive前改变消息






