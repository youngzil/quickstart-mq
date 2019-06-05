RocketMQ系统部署架构：Namesrv 和 Broker（Master、Slave）
RocketMQ生产者模型、消费者模型
RocketMQ存储模型:offset-->commitlog-->mmap
1、存储文件夹和文件说明

生产存储消费流程：
1、消息发送到存储再到消费流程
2、broker查找消息过程
3、RocketMQ消费组上线问题

broker消息存储过程、创建文件机制、刷盘机制：
1、消息的格式
2、存储流程
3、创建文件机制
4、刷盘机制
5、MMAP(Memory Mapped Files，内存映射文件)技术

Broker常见问题：
1、下线一个Broker节点
2、监控数据
3、本机启动RocketMQ
RocketMQ部署.md


生产端注意事项：
1、生产者属性
2、发送失败处理
3、事务消息


消费端注意事项：
1、广播消费重复消费
2、消息重复原因
3、消费幂等的必要性
4、消息查询功能
5、消费失败处理
6、广播消费
RocketMQ消费组负载均衡.md


Topic主题：
1、线上关闭topic自动创建
2、topic保存在broker的存储结构
3、topic的读写队列

运维监控：
1、Console提供的功能
2、Console提供三种查询消息方式
RocketMQ运维命令和控制台.md

常见问题：
1、事务的回查
2、消费端要做好幂等性
3、rocketmq的内存占用
4、刷盘方式


生产者发送流程
发送普通消息负载
发送顺序消息
发送事务消息


消费者问题：
1、多个消费者只有一个消费者消费：instanceId相同的问题
2、全新的消费组第一次上线，设置从尾部开始消费，但是实际从头开始消费，如果是老的消费组重新上线，就从上次消费过的位置继续消费
3、消费组中的消费者负载均衡


---------------------------------------------------------------------------------------------------------------------
RocketMQ系统部署架构
Namesrv：多个项目独立，不进行通信
Broker（Master、Slave）：目前不支持主备切换



RocketMQ生产者模型、消费者模型
生产者、生产组，消费者、消费组，组内集群消费，组间广播消费



常见问题



---------------------------------------------------------------------------------------------------------------------
RocketMQ存储模型
1、存储文件夹和文件说明


RocketMQ存储模型
所有的消息存储在同一个文件，顺序写，随机读


存储文件夹和文件说明：

Commitlog：存储消息文件
consumequeue：主题和offset偏移量
index：是消息key和消息offset偏移量关系

config：topics.json【主题的元信息、subscriptionGroup.json【订阅组的元信息、consumerOffset.json【消费组消费进度信息

lock文件：防止多个服务使用同一个路径
checkpoint文件：主要记录三个时间，校验消息的时候使用


RocketMQ的文件：
topic的发送和接收都是依据mq为单位，send时候需要选Select一个mq，拉取消息（pull或push）也是拉取某个mq中的信息，topic的mq保存在所有的nameSrv中，所有的nameSrv地位是相等的。

topic、mq都是逻辑单元，没有物理单元，只有commitlog、topic offsetconsume offset等是对应物理文件


ConfigManager负责持久化数据或者加载，查看（和子类）里面的persist()或load() 
BrokerPathConfigHelper里面是持久化文件的名字
StorePathConfigHelper 存储相关的持久化文件的名字
在properties中配置跟存储路径storePathRootDir，在它下面会有
文件：abort、checkpoint、lock
文件夹：commitlog、config、consumequeue、index、

abort文件：仅仅是创建，目前没有使用

lock文件：启动时候锁定改文件，退出释放，一个存储路径只能启动一个服务，防止多个服务使用同一个路径

checkpoint文件：主要记录三个时间
physicMsgTimestamp【物理消息刷盘时间，上面的commitlog文件夹】
logicsMsgTimestamp【逻辑消息时间，上面的consumequeue文件夹】
indexMsgTimestamp【index消息时间，上面的index文件夹】



1、commitlog文件夹【消息日志文件，大小默认1G大小，文件名为文件起始字节数，20位，不足前面补0：第二个文件名为1073741824=1024*1024*1024】 

2、config文件夹：【topics、subscriptionGroup、consumerOffset、delayOffset、consumerFilter，都有bak文件】   
topics.json【主题的元信息：topicName、readQueueNums、writeQueueNums、perm、order】
subscriptionGroup.json【订阅组的元信息：】
consumerOffset.json【消费组消费进度信息："TESTTEST@CONSUMER_TEST_GROUP":{0:2073,1:2074,2:2073,3:2074}】
delayOffset.json【】
consumerFilter.json【】


topics.json.bak
consumerOffset.json.bak
delayOffset.json.bak
consumerFilter.json.bak

3、consumequeue文件夹：【主题下的队列和commitlog的offset对应关系等元信息】一级目录是主题名称，二级目录是queueId，下面是文件（名称和commitlog的文件对应，大小默认固定6M），存储和commitlog的offset对应关系等元信息
4、index文件夹：【名字为yyyyMMddhhmmssSSS格式】保存消息的Key和commitlog的offset对应关系等元信息，根据Key来查询消息使用

---------------------------------------------------------------------------------------------------------------------
学习网站
https://github.com/a2888409/RocketMQ-Learning
https://blog.csdn.net/a19881029/article/details/34446629
https://blog.csdn.net/StrideBin/article/details/78338686

生产存储消费流程：
1、消息发送到存储再到消费流程
2、broker查找消息过程
3、RocketMQ消费组上线问题



消息发送到存储再到消费流程：

Producer：
1、发送消息时候，生产者获取本地topic信息（发布信息publishInfo），获取不到然后再从nameser更新一次，如果还是没获取到到，通过TBW102这个默认tocpic从nameser更新，然后复制它的路由信息创建publishInfo和subscribeInfo。publishInfo（可写mq）和subscribeInfo（可读的mq）都是MessageQueue信息（topic、brokerName，index）。
2、选择一个mq来发送：MessageQueue mqSelected = this.selectOneMessageQueue(topicPublishInfo, lastBrokerName);
3、根据mq中的brokerName获取broker地址，处理消息是否使用VIPChannel，是否压缩，hasCheckForbiddenHook执行、hasSendMessageHook执行等，this.remotingClient.invokeSync发送消息，hasSendMessageHook执行
4、.remotingClient包含invokeSync、invokeAsync、invokeOneway等，执行this.rpcHook.doBeforeRequest(addr, request);，然后网络发送消息RemotingCommand response = this.invokeSyncImpl(channel, request, timeoutMillis);，最后this.rpcHook.doAfterResponse(RemotingHelper.parseChannelRemoteAddr(channel), request, response);
5、在初始化Producer端的Client Netty时候，设置了NettyClientHandler来处理发送到Producer端的消息，不管是Sync还是Async，发送成功后都是由接收到Broker后由NettyClientHandler处理的，Sync释放等待，Async执行回调

Broker：
1、SendMessageProcessor处理接收到的消息，处理消息，调用AbstractPluginMessageStore.putMessage-->DefaultMessageStore.putMessage-->this.commitLog.putMessage-->mappedFile.appendMessage(msg, this.appendMessageCallback);-->Statistics-->handleDiskFlush消息刷盘（pagecache）-->handleHA主备同步

Consumer：
PULL：DefaultMQPullConsumer.pullBlockIfNotFound-->this.pullAPIWrapper.pullKernelImpl(-->this.remotingClient.invokeAsync通过网络发送请求，拉取消息
PUSH：this.checkConfig();-->  this.copySubscription();-->变量的构建，初始化，信息保存-->mQClientFactory.start();启动--》// Start request-response channel
                    this.mQClientAPIImpl.start();
                    // Start various schedule tasks
                    this.startScheduledTask();
                    // Start pull service
                    this.pullMessageService.start();
                    // Start rebalance service
                    this.rebalanceService.start();
                    // Start push service
                    this.defaultMQProducer.getDefaultMQProducerImpl().start(false);
--》pullMessageService会不停地拉取消息，返回给客户端



生产者：

broker消息存储过程：
1、SendMessageProcessor-----》2、this.brokerController.getMessageStore().putMessage(msgInner)【brokerController包含全部的类】-----》3、this.commitLog.putMessage(msg)-----》4、获取mapedFile，然后mapedFile.appendMessage(msg, this.appendMessageCallback);-----》5、回调commitlog的doAppend方法，AppendMessageResult result =
                    cb.doAppend(this.getFileFromOffset(), byteBuffer, this.fileSize - currentPos, msg);-----》6、如果文件满了，就使用特殊字符填充剩下的空间，重新创建新的mapedFile再次存放message


broker查找消息过程
消费消息过程：Broker收到pull消息请求，首先从CQ（）
根据offset找到对应的CQ，从CQ找到需要拉取的size，然后从对应的MapperFile中拉取消息返回Client
Client接收到消息Buffer，然后根据消息格式decode，然后交给对应的线程处理。


查看源码消费原理：定时拉取消息，消费成功后，把消费进度暂时保存到本地，定时更新到（CLUSTERING）Server或者（BROADCASTING）保存到本地文件（根据MessageQueue获取brokerName，根据BrokerName获取brokerId=0的节点Addr并更新，这个CLUSTERING和BROADCASTING是一样的逻辑，只是保存的位置不一样）



消费者：

RocketMQ消费组上线问题：
如果是老的消费组，就从上次消费过的位置继续消费
如果是新的消费组，默认从头开始消费，设置CONSUME_FROM_LAST_OFFSET，如果一个queue的minOffset为0时，消费端要从0开始消费这个queue上消息，不会从尾部开始消费，也就是只有一个queue的minOffset不等于0，也就是消息被删除过，这个从尾开始消费才会生效，这是因为避免topic扩容queue的时候，少消费消息，宁可重复消费消息，也绝不少消费消息，至少消费一次的原则


全新的消费组第一次上线，设置从尾部开始消费，但是实际从头开始消费：
https://docs.google.com/document/d/1IbmWqhkklBw_bIzDdlMx5ViM3WcvJXZWw0y9M0xZ2x0/edit
新消费组上线时还是要处理好历史消息的，无论怎样处理，要提前做好准备。有可能消费到大量历史消息，这是RocketMQ的本身机制导致的，RocketMQ的一个重大设计原则，宁可重复消费无数消息，也绝不漏掉一条消息，RocketMQ的设计是合理的，导致了重复消费是不可避免的

下面我们来分析下为什么一个queue的minOffset为0时，消费端要从0开始消费这个queue上消息，只有这种情况超出了正常的预期。
我们做个假设，假设新消费组上线时，都是从queue的maxOffset开始消费消息。又如果一个topic在一个broker上面有4个queue，新消费组上线后，开始从这四个queue的最后位置消费消息，这时我突然扩容这个topic到8个queue，那么消费端去namesrv上拿到这8个queue的信息需要一个心跳周期，按默认配置是30秒左右。这个心跳周期内，新扩展的queue上完全可能有新消息进来。
当消费端拿到4个新扩展queue的信息后，去broker端拉取消息时，broker还是把这4个扩容queue当作新queue来处理的。按照我们的假设，最终消费端会从这4个新queue的maxOffset开始消费。这就有可能丢失了这4个扩容queue的前面一些消息，有可能会很多消息，而这些消息完全是在新消费组上线后发送出来的！！
有消息漏消费了！这就是为什么新消费组不能都是从maxOffset开始消费的。

消费组是第一次上线，设置尾部开始消费，只要这个mq的最小Offset是0，就从0开始消费，否则从尾部开始消费
订阅组不存在情况下，如果这个队列的消息最小Offset是0，则表示这个Topic上线时间不长，服务器堆积的数据也不多，那么这个订阅组就从0开始消费。尤其对于Topic队列数动态扩容时，必须要从0开始消费。
如果订阅的queue不是从0开始的（minOffset大于0，已经删除过数据了），那么消费端将从maxOffset开始消费，即从最新位置开始消费；如果订阅的queue是从0开始的（minOffset等于0，没有删除过数据），那么消费端将从0开始消费这个queue。



---------------------------------------------------------------------------------------------------------------------
broker消息存储过程、创建文件机制、刷盘机制：
1、消息的格式
2、存储流程
3、创建文件机制
4、刷盘机制
5、MMAP(Memory Mapped Files，内存映射文件)技术


消息的格式：17个数据，第一个是整个消息的大小


存储流程：
Commitlog-->MappedFile-->MappedByteBuffer刷到内存

保存消息（handleDiskFlush中可以看出，即使配置同步，基本就可以100%保证存储成功，除非os宕机或者断电，甚至宕机都可以刷到磁盘）
1、PutMessageResult putMessageResult = this.brokerController.getMessageStore().putMessage(msgInner);
this.brokerController.getMessageStore()获取的是AbstractPluginMessageStore对象，AbstractPluginMessageStore中调用DefaultMessageStore的putMessage
2、再调用 this.commitLog.putMessage(msg);，首先获取MappedFile mappedFile = this.mappedFileQueue.getLastMappedFile();，获取锁（rocketmq的写文件锁使用的就是可重入锁或自旋锁（可配置）），再mappedFile.appendMessage(msg, this.appendMessageCallback);【此处其实只是构建内存对象，在后面的handleDiskFlush中从内存flush文件中】然后统计，然后根据配置，handleDiskFlush本机同步或者异步刷盘，handleHA主备同步或异步保存
3、第2步中，mappedFile.appendMessage，再调用AppendMessageCallback的doAppend方法，最后构建一个对象。

创建文件机制：
预先创建两个1G大小的空洞文件（文件大小可配置），直接把next和nextNext文件都创建好，如第一次接收消息的时候，就会创建两个文件如下，占用空间为2个1G（两个空洞文件），文件名字就是第一个offset的值，1073741824 = 1024 * 1024 * 1024 = 默认1G
-rw-rw-r--. 1 aifqa aifqa 1073741824 12月 22 15:13 00000000000000000000
-rw-rw-r--. 1 aifqa aifqa 1073741824 12月 22 15:12 00000000001073741824
【commitlog 是不是利用了 文件空洞的特性，预先创建1个固定大小的文件】
【如果在运行中，把文件删除，再发送的消息就会全部保存失败，但是返回客户端的是正常的成功信息，所以保存消息不是真正的同步】
空洞文件作用很大，例如迅雷下载文件，在未下载完成时就已经占据了全部文件大小的空间，这时候就是空洞文件。下载时如果没有空洞文件，多线程下载时文件就都只能从一个地方写入，这就不是多线程了。如果有了空洞文件，可以从不同的地址写入，就完成多线程的优势任务


刷盘机制：RocketMQ启动至少需要2G内存
使用java的文件映射内存机制，FileChannel提供了map方法把文件映射到虚拟内存，通常情况可以映射整个文件，如果文件比较大，可以进行分段映射。
java.nio.channels.FileChannel.force
java.nio.MappedByteBuffer.force

MappedByteBuffer 将文件直接映射到内存（这里的内存指的是虚拟内存，并不是物理内存）
程序访问文件时，有RandomAccessFile随机访问文件内容，字节流访问，缓冲区读入。但是这三种都没有内存直接读取，这里介绍下MappedByteBuffer,可以以内存的速度进行访问文件内容，提高文件内容，提高系统性能。
内存映射：内存映射是通过字节流的通道获取，然后由通道的map()方法来进行映射。
内存映射文件和之前说的标准IO操作最大的不同之处就在于它虽然最终也是要从磁盘读取数据，但是它并不需要将数据读取到OS内核缓冲区，而是直接将进程的用户私有地址空间中的一部分区域与文件对象建立起映射关系，就好像直接从内存中读、写文件一样，速度当然快了。内存映射文件的效率比标准IO高的重要原因就是因为少了把数据拷贝到OS内核缓冲区这一步。

mmap将一个文件或者其它对象映射进内存。文件被映射到多个页上，如果文件的大小不是所有页的大小之和，最后一个页不被使用的空间将会清零。mmap在用户空间映射调用系统中作用很大。
mmap()[1]  必须以PAGE_SIZE为单位进行映射，而内存也只能以页为单位进行映射，若要映射非PAGE_SIZE整数倍的地址范围，要先进行内存对齐，强行以PAGE_SIZE的倍数大小进行映射。
mmap操作提供了一种机制，让用户程序直接访问设备内存，这种机制，相比较在用户空间和内核空间互相拷贝数据，效率更高。在要求高性能的应用中比较常用。mmap映射内存必须是页面大小的整数倍，面向流的设备不能进行mmap，mmap的实现和硬件有关。

总结
MappedByteBuffer使用虚拟内存，因此分配(map)的内存大小不受JVM的-Xmx参数限制，但是也是有大小限制的。
如果当文件超出1.5G限制时，可以通过position参数重新map文件后面的内容。
MappedByteBuffer在处理大文件时的确性能很高，但也存在一些问题，如内存占用、文件关闭不确定，被其打开的文件只有在垃圾回收的才会被关闭，而且这个时间点是不确定的。
javadoc中也提到：A mapped byte buffer and the file mapping that it represents remain valid until the buffer itself is garbage-collected.*


MMAP(Memory Mapped Files，内存映射文件)技术
Linux 内存映射函数 mmap（）函数详解
http://blog.csdn.net/yangle4695/article/details/52139585

参考文章
https://www.jianshu.com/p/f90866dcbffc
http://blog.csdn.net/xx_ytm/article/details/54801983
http://blog.csdn.net/jdsjlzx/article/details/51648044
http://blog.csdn.net/dymkkj/article/details/52506130

物理内存：即内存条的内存空间。
虚拟内存：计算机系统内存管理的一种技术。它使得应用程序认为它拥有连续的可用的内存（一个连续完整的地址空间），而实际上，它通常是被分隔成多个物理内存碎片，还有部分暂时存储在外部磁盘存储器上，在需要时进行数据交换。
如果正在运行的一个进程，它所需的内存是有可能大于内存条容量之和的，如内存条是256M，程序却要创建一个2G的数据区，那么所有数据不可能都加载到内存（物理内存），必然有数据要放到其他介质中（比如硬盘），待进程需要访问那部分数据时，再调度进入物理内存。
操作系统有个页面失效（page fault）功能。操作系统找到一个最少使用的页帧，使之失效，并把它写入磁盘，随后把需要访问的页放到页帧中，并修改页表中的映射，保证了所有的页都会被调度。
举个例子，有一个虚拟地址它的页号是4，偏移量是20，那么他的寻址过程是这样的：首先到页表中找到页号4对应的页帧号（比如为8），如果页不在内存中，则用失效机制调入页，接着把页帧号和偏移量传给MMC组成一个物理上真正存在的地址，最后就是访问物理内存的数据了。

写文件机制：
写文件是串行的，利用了 pagecache 的方式，消息先到 pagecache 中，然后，变成脏页。然后，系统来处理 内存到文件的过程。
 pagecache 中cache变成脏页有时间、和数量的 策略。
脏页的持久化，得看系统的配置。
可以查看 rocketmq 里面的 os.sh 的配置参数，
pagecache 有 4k 。8k 各种规格的，可以自己设定

缓存
缓存是用来减少高速设备访问低速设备所需平均时间的组件，文件读写涉及到计算机内存和磁盘，内存操作速度远远大于磁盘，如果每次调用read,write都去直接操作磁盘，一方面速度会被限制，一方面也会降低磁盘使用寿命，因此不管是对磁盘的读操作还是写操作，操作系统都会将数据缓存起来
Page Cache
页缓存（Page Cache）是位于内存和文件之间的缓冲区，它实际上也是一块内存区域，所有的文件IO（包括网络文件）都是直接和页缓存交互，操作系统通过一系列的数据结构，比如inode, address_space, struct page，实现将一个文件映射到页的级别，这些具体数据结构及之间的关系我们暂且不讨论，只需知道页缓存的存在以及它在文件IO中扮演着重要角色，很大一部分程度上，文件读写的优化就是对页缓存使用的优化
Dirty Page
页缓存对应文件中的一块区域，如果页缓存和对应的文件区域内容不一致，则该页缓存叫做脏页（Dirty Page）。对页缓存进行修改或者新建页缓存，只要没有刷磁盘，都会产生脏页


上下文切换 非常厉害的，不一定多线程就比 单线程快。

---------------------------------------------------------------------------------------------------------------------
Broker常见问题：
1、下线一个Broker节点
2、监控数据
3、本机启动RocketMQ



下线一个Broker节点：
如果想把一台broker下掉，但不能影响线上消息。
wipeWritePerm这个命令会通知nsr，把指定broker的写权限禁用了，但读不受影响。-b后面只能写brokerName不能写IP，运维命令文档指导有误。
wipeWritePerm命令原理：发送命令到namesrv，namesrv把此broker的队列数据的权限的写权限给禁了（其实就是把所有的queue的写权限给关闭了）
理想的操作步骤是：1，修改当前broker上的所有队列为只读，2.等待所有消息都消费完成。3.执行shutdowm下线。


监控数据
RocketMQ消息缓存在pagecache中，当消费及时的时候，直接从pagecache中拉取消息，整个链路的RT会很短，否则出现堆积，要从disk拉取消息，就会比较慢，通过运维命令，查询哪些订阅组在消费磁盘的消息，把这些订阅组降级
broker记录了很多Statistics log，可以直接拿出来导入ELK平台，也可以直接查询，



本机启动RocketMQ
本地rocketmq debug环境构建：
NamesrvStartup中添加：namesrvConfig.setRocketmqHome("/Users/yangzl/src/RocketMQ-3.5.8");
BrokerStartup中添加如下：brokerConfig.setRocketmqHome("/Users/yangzl/src/RocketMQ-3.5.8");
brokerConfig.setNamesrvAddr("localhost:9876");

broker节点初始化时候加载如下参数，启动定时任务（定时任务的周期参数大多可以配置）（包括主备同步等，查看有哪些），根据设置刷盘方式（同步、异步）和主备同步方式（同步、异步）
				MixAll.properties2Object(properties, brokerConfig);
        MixAll.properties2Object(properties, nettyServerConfig);
        MixAll.properties2Object(properties, nettyClientConfig);
        MixAll.properties2Object(properties, messageStoreConfig);
DataVersion记录broker更新配置次数和最后更新时间




---------------------------------------------------------------------------------------------------------------------
生产端注意事项：
1、生产者属性
2、发送失败处理
3、事务消息



生产者属性：
多个节点需要保证producerInstanceName 实例名称唯一
若需要在同一台机器上启动多个produer  则需要设置不同的producerInstanceName
producerGroup这个东东建议保持全局唯一



发送失败处理
Producer send fail：
放在log、mem、外部存储hbase、redis等，等待合适的时机再次发送



事务消息：

V4.3.0开始，支持事务消息
事务消息是先把消息存放在RMQ_SYS_TRANS_HALF_TOPIC主题中，当endTransaction时，-》Commit时候把消息放在原来的正常队列中然后deletePrepareMessage，Rollback就直接deletePrepareMessage，deletePrepareMessage就是把消息放到操作队列RMQ_SYS_TRANS_OP_HALF_TOPIC中，消息体存放消息在HALF_TOPIC主题中的offset。
Broker启动的时候启动一个事务扫描线程，使用自己重写的CountDownLatch2类await定时回查客户端事务消息的状态，客户端根据事务ID查询到消息状态后进行endTransaction

主题RMQ_SYS_TRANS_HALF_TOPIC和主题RMQ_SYS_TRANS_OP_HALF_TOPIC的队列一一对应（队列数一样），

---------------------------------------------------------------------------------------------------------------------
消费端注意事项：
1、广播消费重复消费
2、消息重复原因
3、消费幂等的必要性
4、消息查询功能
5、消费失败处理
6、广播消费




消费组消费订阅信息一致
同一个消费组：消费端订阅的信息必须一致，包括topic和tag



广播消费重复消费：
重启会重新消费的问题，是不是你重启的时候，改变了消费者的消费组
对于广播消息，同一个消费组的消费者，已经被消费掉的消息，重启的消费者就不会再收到之前的消息的
如果你起的消费者是一个完全新的消费组，就会从新消费全部消息的
如果你只是停掉消费者再重启，未改变配置导致消费组改变，是肯定不会重新消费的
消息的commitOffset是有个5s的延迟的   如果你消费者进程在收到消息就立刻关掉的话 ，会导致offset未提交，会出现重复消费的



消息重复原因：
生产端同步发送，未接收到ack，重试发送，次数设置：defaultMQProducer.getRetryTimesWhenSendFailed()
消费端消费未返回ack，broker重复投递：msg.getReconsumeTimes()
消费者数量变动（宕机或者新增）带来的重新负载，导致的消费offset丢失，导致消费重复
重试次数会默认取消费组的配置，如果是3.4.9以后的版本就会取DefaultMQPushConsumer中的defaultMQPushConsumer.getMaxReconsumeTimes()，由于defaultMQPushConsumer中的默认次数是16，所以3.4.9一定是使用defaultMQPushConsumer中的重试次数的配置
解决：
RocketMQ Consumer去重：
消费端去重，使用业务端唯一的key保存在第三方存储来鉴别去重，不要使用msgId，在producer因为网络问题重发时，可能会导致msgId重复


消费幂等的必要性
在互联网应用中，尤其在网络不稳定的情况下，MQ 的消息有可能会出现重复，这个重复简单可以概括为以下两种情况：
发送时消息重复【消息 Message ID 不同】：
MQ Producer 发送消息场景下，消息已成功发送到服务端并完成持久化，此时网络闪断或者客户端宕机导致服务端应答给客户端失败。如果此时 MQ Producer 意识到消息发送失败并尝试再次发送消息，MQ 消费者后续会收到两条内容相同但是 Message ID 不同的消息。

投递时消息重复【消息 Message ID 相同】；
MQ Consumer 消费消息场景下，消息已投递到消费者并完成业务处理，当客户端给服务端反馈应答的时候网络闪断。为了保证消息至少被消费一次，MQ 服务端将在网络恢复后再次尝试投递之前已被处理过的消息，MQ 消费者后续会收到两条内容相同并且 Message ID 也相同的消息。



消息查询功能：SQL92查询
重试队列和DLQ队列：重试和死信：会创建%RETRY%或%DLQ%+consumerGroup的主题，把消息发进去，在消费组重置消费


消费失败处理：
BROADCASTING模式，直接drop，跳过
CLUSTERING模式，重试发送服务端失败或异常，会再次在本地重试消费，所以说RocketMQ提供的是At Least Once语义

消费失败放DB或者放DLQ中，再慢慢消费

RHMQTTQosLevelAtMostOnce = 0,               //至多一次，发完即丢弃，<=1  
RHMQTTQosLevelAtLeastOnce = 1,              //至少一次，需要确认回复，>=1  
RHMQTTQosLevelExactlyOnce = 2,              //只有一次，需要确认回复，＝1  



housekeeping
自动化巡检
基于client和mqadmin每分钟模拟Client发送消息，检测系统的运行状况
使用client模拟收发消息，rt是多少，期望是多少，什么时候可以收到消息，没有收到是为什么？broker配置一些hook拦截，内核命令来采集内核数据来分析


consumer端subscribe时候，首先把订阅主题保存，再自动保存retry topic，
 this.startScheduledTask();各种定时任务
this.pullMessageService.start();拉消息，处理消息
this.rebalanceService.start();重新负载


消费时消息不是拿走，而是拷贝给消费者，不管消费者是否消费消息成功，消息都始终存在在broker上，直到broker定时删除了消息，删除周期可配置，比如删除三天前的消息。

消息消费RT，rt啥意思请问？我理解是 response time

Consumer端的ConsumeFromWhere在数据量小的时候，是不生效的，始终从头开始消费，这又说明RocketMQ提供的的at least once语义，另一个证明是不处理重复消息，由客户端去重，当然还有就是Broker去重代价比较大
新集群或者是数据量较少的情况下  新的ConsumerGroup 这个枚举是不起作用的  用一段时间就可以了 这个跟策略有些关系


如果 master  slave  都活着 ，那就消费 master,如果master挂掉了 就会消费 slave
其实在消息积压的时候，也会从slave消费


严格消费模式：会阻塞
或者通过设置消息的方式，把单组顺序ABC放到重试队列，然后跳过单组的（业务实现），业务端ack broker消费成功，然后保存下来消费失败的单组消息自行处理



广播消费：
集群模式：一条消息被集群中任何一个消费者消费
广播模式：每条消息都被每一个消费者消费。

集群模式下，一条消息只能被集群内的一个消费者消费，进度不能保存在消费端，只能集中保存在一个地方，比较合适的是在Broker端。
广播模式，既然每条消息要被每一个消费者消费，则消费进度可以与消费者保存在一起，也就是本地保存，

集群模式：存储在Namesrv上，/rocketmq_home/store/config/consumerOffset.json
广播模式：存储在消费者机器上，LOCAL_OFFSET_STORE_DIR : offset存储根目录，默认为用户主目录，例如 /home/dingw,可以在消费者启动的JVM参数中，通过-Drocketmq.client.localOffsetStoreDir=路径

广播模式消息，如果返回CONSUME_LATER,竟然不会重试，而是直接丢弃


---------------------------------------------------------------------------------------------------------------------
Topic主题：
1、线上关闭topic自动创建
2、topic保存在broker的存储结构
3、topic的读写队列


线上关闭Topic自动创建：

topic自动创建过程：
在broker的sendMessage中的，有super.msgCheck(ctx, requestHeader, response);，在这个方法里面发现topic不存在，就会创建，并register到所有的broker（其实是所有的nameSrv）
读写数量设置的不一样：都是从前面来循环，所以如果设置的写大于读，会导致后面的mq数据无法消费，读大于写，会导致部分mq的消费者空轮询


线上应该关闭autoCreateTopicEnable，即在配置文件中将其设置为false。
RocketMQ在发送消息时，会首先获取路由信息。如果是新的消息，由于MQServer上面还没有创建对应的Topic，这个时候，如果上面的配置打开的话，会返回默认TOPIC的（RocketMQ会在每台broker上面创建名为TBW102的TOPIC）路由信息，然后Producer会选择一台Broker发送消息，选中的broker在存储消息时，发现消息的topic还没有创建，就会自动创建topic。后果就是：以后所有该TOPIC的消息，都将发送到这台broker上，达不到负载均衡的目的。
所以基于目前RocketMQ的设计，建议关闭自动创建TOPIC的功能，然后根据消息量的大小，手动创建TOPIC。



topic保存在broker的TopicConfigManger数据结构中，持久化在：
在创建 topic 的时候，会同时推送 topic 的信息到 name srv 中，也会同时里面进行持久化，保存 topic 的相关信息。configManager类里有一个persist方法


topic的读写队列如何理解
读写数量设置的不一样：都是从前面来循环，所以如果设置的写大于读，会导致后面的mq数据无法消费，读大于写，会导致部分mq的消费者空轮询
比如说有一个topic原来有8个queue，要缩小为4个queue，你就会发现有用了，就是动态调整过程中，会出现重复消费、丢失数据等情况，比较敏感
可以先把readQueueNums缩小，禁写后面的mq，等后面的mq消费完，再缩小writeQueueNums即可，未验证，不知是否可行
所以说队列数缩小的时候，被缩小的队列不会再被消费，其实只要readQueueNums缩小就是的
读写队列数不一样的时候，在broker只是保存topic配置，在Client端write和read是分开保存在TopicPublishInfo和subscribeInfo中的，分别创建publish的mq和subscribe的mq



---------------------------------------------------------------------------------------------------------------------
运维监控：
1、Console提供的功能
2、Console提供三种查询消息方式



Console提供的功能：
生产者、消费者、Broker、Topic、Message消息



MQAdmin：MQAdminExt接口提供的方法，实现类DefaultMQAdminExt（内部都是直接调用DefaultMQAdminExtImpl）和DefaultMQAdminExtImpl

CLI：MQAdminStartup类作为入口，commond进行解析和构建，然后调用SubCommand的子类命令，调用DefaultMQAdminExt发送指令到服务端（nameSrv和Broker）
具体命令的解析：查看SubCommand子类的commandName、commandDesc、buildCommandlineOptions

RequestCode和ResponseCode定义了请求和响应的类型

通过MQAdmin或CLI方式创建topic：先更新this.brokerController.getTopicConfigManager().updateTopicConfig(topicConfig);包括保存在缓存topicConfigTable中和this.persist();持久化到文件，再注册到全部Broker中this.brokerController.registerBrokerAll(false, true);，其实是把本地的全部topic配置register到全部的nameSrv中去，nameSrv接收到后，更新本地的mq信息。


Console提供三种查询消息方式:
1、根据Topic+beginTime+endTime【使用DefaultMQPullConsumer获取Topic的全部mq，循环mq，使用mq+begin获取minOffset，mq+begin获取maxOffset，使用consumer.pull(mq, subExpression, offset, 32)拉取消息，从minOffset开始，最大拉取到maxOffset，直到达到消息数量】
2、根据Topic+Key（+beginTime+endTime，默认是0到当前时间，就是当前存在的全部消息）【根据Topic+Key+maxNum+beginTime+endTime从Broker的index文件夹下的文件中搜索消息（数量是maxNum和Broker部署时候配置的MaxMsgsNumBatch中的min的那个决定）】
3、根据MessageId【根据MessageId可以解析出brokerAddr和commitLogOffset，传到对应的Broker，Broker根据commitLogOffset查询消息并返回】



Console：
1、目前使用连接为使用切面注入方式创建，线程退出就清除，频繁操作还是会频繁创建销毁连接
可以修改为连接使用连接缓存优化，定时清理连接，定时刷新，定时任务扫描超时的连接并清除，ConsumeMessageConcurrentlyService中的cleanExpireMsg
2、前台页面查询慢，建立连接和属性拷贝慢：1、建立连接慢，查询rocketmq一次，第一次查询260ms左右，不建立连接就是50ms左右，2、循环查询导致很慢，去除后台循环查询，简化前台查询功能，每次只查询一次，在前台分页，3、属性拷贝大概2~3个1ms，进来避免大数据属性拷贝

---------------------------------------------------------------------------------------------------------------------
常见问题：
1、事务的回查
2、消费端要做好幂等性
3、rocketmq的内存占用
4、刷盘方式
5、RocketMQ和ActiveMQ的区别
6、Rocketmq和kafka的区别

rocketmq的回调检查这块
应该是用数据库或者REDIS作为QUEUE然后模拟实现prepared加扫描回调吧
1）本地事务：业务操作+消息本地存储；
2）上面这一步是原子操作；失败则什么都没发生；成功则：向MQ发送消息；
3）发送成功，则删除第一步中的消息；发送失败，则重试；一直重试，直到成功；
4）如果重试过程中断电了，则下次重启后，继续将数据库中未删除的消息拿出来继续发送，同理，直到成功为止；
通过以上设计，可以确保，只要业务操作成功，则消息最终一定会发送到MQ。然后，消息消费者对消息做好幂等处理即可。
这个设计，不需要依赖RMQ的事务消息，自己实现的难度也很低。


消费端要做好幂等性，
消费端无论如何，总是要消息的幂等处理。消息幂等也很容易，你用你的消息的业务ID来做幂等判断就行了
消费失败就重试啊，直到成功为止
这个重试，是几乎所有MQ都支持的能力
消费端做好监控，如果重试次数太多了，立马告警。这个是非常重要的点。
如果很多消息都失败，肯定消息有堆积，也早就报警了


对于没有重试队列和死信队列的mq。可以自己封装一层
消费失败存下来。在慢慢消费，存的时候记录下 消费者是谁
定时重试任务很简答啊，现在的定时组件很多
消费端不需要定时重试任务，可以扔到重试队列即可


Java里面有Netty这种好东西，所以TCP通信业变得很easy了
剩下的就剩下消息存储了
我们现在是放到数据库，我始终觉得不是很好
当然，如果你要求更高，比如要求消息高可靠，多副本，那难点又在分布式了。
放数据库容易查询和统计分析，也有好处
SQL是个好东西


rocketmq的内存占用
堆外内存越大越好，堆内不大
都是利用操作系统的文件映射
所以会一直吃内存，不够的话会缺页
缺页性能刷就下去了
正常消费没有堆积的时候，从内存取消息，会比较快，否则就要从disk取消息，就比较慢了



刷盘方式：
同步刷盘就是立刻刷盘，不管内存页是否满一页，同步复制就是集群中，至少有一个slave的pagecache中与master的offset一致，即可认为高可用成功
如果数据写入分页，但未刷盘，这时，JVM 退出（kill -9 pid 这种），未刷盘的数据应该都会刷盘
操作系统会帮你做吧  
那其实，只要不是机器掉电，同步刷盘异、步刷盘的可靠性是一样的。
机器宕机是很小的概率事件，可以接受的。但broker得启这种，还是比较大的概率。
所以同步是安全的，哪怕kill -9 杀掉jvm，操作系统会帮刷盘
除非掉电，内存丢失


receive response, but not matched any request 这个错误，很有可能本地map缺失数据


2017-11-16 10:19:03 WARN FlushConsumeQueueService - findMapedFileByOffset offset not matched, request Offset: 50000000, index: 1, mapedFileSize: 50000000, mapedFiles count: 1, StackTrace:
mapedFile文件丢失，可能是主机磁盘被其他应用或者快速堆积，导致清理磁盘时候，把mapedFile文件删除，没有做确切的验证


com.alibaba.rocketmq.store.DefaultMessageStore$FlushConsumeQueueService.doFlush(DefaultMessageStore.java:1308)
        com.alibaba.rocketmq.store.DefaultMessageStore$FlushConsumeQueueService.run(DefaultMessageStore.java:1329)
RocketMQ版本3.4.6磁盘flush出错
原因是很多天前，系统时间发生了变化，我们做了次系统时间同步，导致日志生成的时间异常。然后这些文件由于时间异常，导致了这些文件的最后修改时间发生了混乱，导致了系统归档的时候，删除了一些中间的日志，反而留下较旧的日志，也就是minOffset还是最小的，但是中间的一些日志文件已经被删除，在计算最新index的时候，是按照最小和最新offset计算的。而实际上，之前的归档已经删除了部分中间的日志，导致日志文件个数比计算出来的应该落日志的数字要小，报warning reputService的index出错。其实这个时候已经没有办法更新consumerqueue和index了



延迟消息：设置延时级别，在服务端设置延时级别对应的延时时间
死信队列，重试消费超过一定次数默认是16次就放进死信队列，可配置重试次数和重试时间间隔
消息消费超时后将消息清除、重试消费此消息
消息压缩，发送的时候可以设置超过多大字节压缩
broker、mq、consumerGroup 消费开启和关闭，订阅组配置里有消费开启关闭和重试次数
consumer group 消费进度重置，根据时间重置
支持查询Broker的TPS
拉取消息的线程数，由于使用了无界队列，所以最大线程数不会生效，一定是最小线程数


---------------------------------------------------------------------------------------------------------------------


Rocketmq介绍文章
https://blog.csdn.net/prestigeding/article/details/78888290



---------------------------------------------------------------------------------------------------------------------


