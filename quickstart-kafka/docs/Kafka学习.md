Kafka生产、保存、消费流程
Kafka在zk上注册的节点

Producer和Consumer说明.md

kafka架构内部原理：
在一套kafka架构中有多个Producer，多个Broker,多个Consumer，每个Producer可以对应多个Topic，每个Consumer只能对应一个ConsumerGroup。
整个Kafka架构对应一个ZK集群，通过ZK管理集群配置，选举Leader，以及在consumer group发生变化时进行rebalance。

查找消息
同一分区消息乱序

kafka监控工具.md

ISR and AR
ISR 的伸缩性


---------------------------------------------------------------------------------------------------------------------
kafka架构内部原理
https://blog.csdn.net/lp284558195/article/details/80297208

在一套kafka架构中有多个Producer，多个Broker,多个Consumer，每个Producer可以对应多个Topic，每个Consumer只能对应一个ConsumerGroup。
整个Kafka架构对应一个ZK集群，通过ZK管理集群配置，选举Leader，以及在consumer group发生变化时进行rebalance。


Producer发送到Broker，Consumer从Broker上pull消息
Zookeeper管理集群配置，选举leader，以及在consumer group发生变化时进行rebalance
Producer使用push(推)模式将消息发布到broker，Consumer使用pull(拉)模式从broker订阅并消费消息。


Topic：多个partition，每个partition有多个replication，每个partition又有多个segment
 leader处理partition的所有读写请求，与此同时，follower会被动定期地去复制leader上的数据。
在Kafka文件存储中，同一个topic下有多个不同的partition，每个partiton为一个目录，partition的名称规则为：topic名称+有序序号，第一个序号从0开始计，最大的序号为partition数量减1，partition是实际物理上的概念，而topic是逻辑上的概念。
每个Topic+partition序号目录下面有多个segment的.log文件+对应的.index文件
segment文件由两部分组成，分别为“.index”文件和“.log”文件
segment文件由两部分组成，分别为“.index”文件和“.log”文件，分别表示为segment索引文件和数据文件。
这两个文件的命令规则为：partition全局的第一个segment从0开始，后续每个segment文件名为上一个segment文件最后一条消息的offset值，数值大小为64位，20位数字字符长度，没有数字用0填充


每个partition在存储层面是append log文件。任何发布到此partition的消息都会被追加到log文件的尾部，每条消息在文件中的位置称为offset(偏移量)，offset为一个long型的数字，它唯一标记一条消息。每条消息都被append到partition中，是顺序写磁盘，因此效率非常高(经验证，顺序写磁盘效率比随机写内存还要高，这是Kafka高吞吐率的一个很重要的保证)。
partition还可以细分为segment，一个partition物理上由多个segment组成
segment的文件生命周期由服务端配置参数(log.segment.bytes，log.roll.{ms,hours}等若干参数)决定。


那么如何从partition中通过offset查找message呢
根据offset定位到该partition下面的哪个segment文件和从segment文件的什么位置进行读取。再根据消息的物理结构，从消息头中知道消息体的大小，可以确定一条消息的大小，即读取到哪里截止。
消息都具有固定的物理结构，包括：offset(8 Bytes)、消息体的大小(4 Bytes)、crc32(4 Bytes)、magic(1 Byte)、attributes(1 Byte)、key length(4 Bytes)、key(K Bytes)、payload(N Bytes)等等字段，可以确定一条消息的大小，即读取到哪里截止。


为了提高消息的可靠性，Kafka每个topic的partition有N个副本(replicas)，其中N(大于等于1)是topic的复制因子(replica fator)的个数。Kafka通过多副本机制实现故障自动转移，当Kafka集群中一个broker失效情况下仍然保证服务可用。在Kafka中发生复制时确保partition的日志能有序地写到其他节点上，N个replicas中，其中一个replica为leader，其他都为follower, leader处理partition的所有读写请求，与此同时，follower会被动定期地去复制leader上的数据。


对于leader新写入的消息，consumer不能立刻消费，leader会等待该消息被所有ISR中的replicas同步后更新HW，此时消息才能被consumer消费。这样就保证了如果leader所在的broker失效，该消息仍然可以从新选举的leader中获取。对于来自内部broKer的读取请求，没有HW的限制。
也就是Consumer只能消费到一个partition对应的ISR中最小的LEO作为HW（取一个partition对应的ISR中最小的LEO作为HW）

二个位置三个队列
LEO，LogEndOffset的缩写，表示每个partition的log最后一条Message的位置。
HW俗称高水位，HighWatermark的缩写，是指consumer能够看到的此partition的位置，取一个partition对应的ISR中最小的LEO作为HW，consumer最多只能消费到HW所在的位置。

ISR (In-Sync Replicas)，这个是指副本同步队列。副本数对Kafka的吞吐率是有一定的影响，但极大的增强了可用性。
所有的副本(replicas)统称为Assigned Replicas，即AR
OSR(Outof-Sync Replicas)列表，新加入的follower也会先存放在OSR中。AR=ISR+OSR。


而对于producer而言，它可以选择是否等待消息commit，这可以通过request.required.acks来设置。这种机制确保了只要ISR中有一个或者以上的follower，一条被commit的消息就不会丢失。
当producer向leader发送数据时，可以通过request.required.acks参数来设置数据可靠性的级别
1(默认)：这意味着producer在ISR中的leader已成功收到的数据并得到确认后发送下一条message。如果leader宕机了，则会丢失数据。
0：这意味着producer无需等待来自broker的确认而继续发送下一批消息。这种情况下数据传输效率最高，但是数据可靠性确是最低的。
-1：producer需要等待ISR中的所有follower都确认接收到数据后才算一次发送完成，可靠性最高。但是这样也不能保证数据不丢失，比如当ISR中只有leader时(前面ISR那一节讲到，ISR中的成员由于某些情况会增加也会减少，最少就只剩一个leader)，这样就变成了acks=1的情况。

如果要提高数据的可靠性，在设置request.required.acks=-1的同时，也要min.insync.replicas这个参数(可以在broker或者topic层面进行设置)的配合，这样才能发挥最大的功效。
min.insync.replicas这个参数设定ISR中的最小副本数是多少，默认值为1，当且仅当request.required.acks参数设置为-1时，此参数才生效。


Kafka在Zookeeper中为每一个partition动态的维护了一个ISR，这个ISR里的所有replica都跟上了leader，只有ISR里的成员才能有被选为leader的可能(unclean.leader.election.enable=false)。在这种模式下，对于f+1个副本，一个Kafka topic能在保证不丢失已经commit消息的前提下容忍f个副本的失败，在大多数使用场景下，这种模式是十分有利的。

可用性和一致性当中作出一个简单的抉择：
unclean.leader.election.enable=false：等待ISR中任意一个replica“活”过来，并且选它作为leader，一致性，保证已commit的消息不丢失，不可用的时间就可能会相对较长
unclean.leader.election.enable=true：（Kafka默认）选择第一个“活”过来的replica(并不一定是在ISR中)作为leader，可用性，可能不包含全部已经commit的消息，快速恢复保证可用



如果需要确保消息的可靠性，必须要将producer.type设置为sync。
如果设置成异步的模式，即producer.type=async，可以是producer以batch的形式push数据，这样会极大的提高broker的性能，但是这样会增加丢失数据的风险
batch的数量大小可以通过producer的参数(batch.num.messages)控制，在比较新的版本中还有batch.size这个参数


Producer模式：at least once
Kafka的消息传输保障机制非常直观。当producer向broker发送消息时，一旦这条消息被commit，由于副本机制(replication)的存在，它就不会丢失。但是如果producer发送数据给broker后，遇到的网络问题而造成通信中断，那producer就无法判断该条消息是否已经提交(commit)。虽然Kafka无法确定网络故障期间发生了什么，但是producer可以retry多次，确保消息已经正确传输到broker中，所以目前Kafka实现的是at least once。


Consumer模式：
当consumer读完消息之后先commit再处理消息，在这种模式下，如果consumer在commit后还没来得及处理消息就crash了，下次重新开始工作后就无法读到刚刚已提交而未处理的消息，这就对应于at most once了。
读完消息先处理再commit。这种模式下，如果处理完了消息在commit之前consumer crash了，下次重新开始工作时还会处理刚刚未commit的消息，实际上该消息已经被处理过了，这就对应于at least once。
要做到exactly once就需要引入消息去重机制。


要保证数据写入到Kafka是安全的，高可靠的，需要如下的配置：
topic的配置：replication.factor>=3,即副本数至少是3个;2<=min.insync.replicas<=replication.factor
broker的配置：leader的选举条件unclean.leader.election.enable=false
producer的配置：request.required.acks=-1(all)，producer.type=sync


客户端的acks策略对发送的TPS有较大的影响，TPS：acks_0 > acks_1 > ack_-1;
副本数越高，TPS越低;副本数一致时，min.insync.replicas不影响TPS;
acks=0/1时，TPS与min.insync.replicas参数以及副本数无关，仅受acks策略的影响。


各场景测试总结：
当acks=-1时，Kafka发送端的TPS受限于topic的副本数量(ISR中)，副本越多TPS越低;
acks=0时，TPS最高，其次为1，最差为-1，即TPS：acks_0 > acks_1 > ack_-1
min.insync.replicas参数不影响TPS;
partition的不同会影响TPS，随着partition的个数的增长TPS会有所增长，但并不是一直成正比关系，到达一定临界值时，partition数量的增加反而会使TPS略微降低;
Kafka在acks=-1,min.insync.replicas>=1时，具有高可靠性，所有成功返回的消息都可以落盘。 



消息的完整性和系统的吞吐量是互斥的，为了确保消息不丢失就必然会损失系统的吞吐量
producer：
1、ack设置-1
2、设置副本同步成功的最小同步个数为副本数-1
3、加大重试次数
4、同步发送
5、对于单条数据过大，要设置可接收的单条数据的大小
6、对于异步发送，通过回调函数来感知丢消息
7、配置不允许非ISR集合中的副本当leader
8、客户端缓冲区满了也可能会丢消息；或者异步情况下消息在客户端缓冲区还未发送，客户端就宕机
9、block.on.buffer.full = true
consumer：
1、enable.auto.commit=false  关闭自动提交位移


同一分区消息乱序：
假设a,b两条消息，a先发送后由于发送失败重试，这时顺序就会在b的消息后面，可以设置max.in.flight.requests.per.connection=1来避免

max.in.flight.requests.per.connection：限制客户端在单个连接上能够发送的未响应请求的个数。设置此值是1表示kafka broker在响应请求之前client不能再向同一个broker发送请求，但吞吐量会下降



kafka消息丢失情况：
https://www.cnblogs.com/qiaoyihang/p/9229854.html
https://www.cnblogs.com/huxi2b/p/6056364.html
https://www.cnblogs.com/felixzh/p/8027584.html

Producer发送端：
1、异步发送，acks的设置012
2、配置允许非ISR集合中的副本当leader，默认配置
3、网络负载很高或者磁盘很忙写入失败的情况下，没有自动重试重发消息。并且重试的时间间隔一定要长一些，默认1秒钟并不符合生产环境（网络中断时间有可能超过1秒）。

Broker端：
1、kafka的数据一开始就是存储在PageCache上的，定期flush到磁盘上的，也就是说，不是每个消息都被存储在磁盘了，如果出现断电或者机器故障等，PageCache上的数据就丢失了。

Consumer消费端：
1、消费端auto.commit.enable=true



---------------------------------------------------------------------------------------------------------------------
深入剖析kafka架构内部原理
https://blog.csdn.net/w372426096/article/details/81282320
https://blog.csdn.net/lp284558195/article/details/80297208




---------------------------------------------------------------------------------------------------------------------
ISR and AR
ISR 的伸缩性


ISR and AR
简单来说，分区中的所有副本统称为 AR (Assigned Replicas)。所有与leader副本保持一定程度同步的副本（包括leader副本在内）组成 ISR (In Sync Replicas)。 ISR 集合是 AR 集合的一个子集。消息会先发送到leader副本，然后follower副本才能从leader中拉取消息进行同步。同步期间，follow副本相对于leader副本而言会有一定程度的滞后。前面所说的 ”一定程度同步“ 是指可忍受的滞后范围，这个范围可以通过参数进行配置。于leader副本同步滞后过多的副本（不包括leader副本）将组成 OSR （Out-of-Sync Replied）由此可见，AR = ISR + OSR。正常情况下，所有的follower副本都应该与leader 副本保持 一定程度的同步，即AR=ISR，OSR集合为空。


ISR 的伸缩性
leader副本负责维护和跟踪 ISR 集合中所有follower副本的滞后状态，当follower副本落后太多或失效时，leader副本会把它从 ISR 集合中剔除。如果 OSR 集合中所有follower副本“追上”了leader副本，那么leader副本会把它从 OSR 集合转移至 ISR 集合。默认情况下，当leader副本发生故障时，只有在 ISR 集合中的follower副本才有资格被选举为新的leader，而在 OSR 集合中的副本则没有任何机会（不过这个可以通过配置来改变）。








---------------------------------------------------------------------------------------------------------------------



