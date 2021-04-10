Kafka跨集群数据镜像工具MirrorMaker



为了在多个Kafka集群间实现数据同步，Kafka提供了跨集群数据镜像工具MirrorMaker。通常，数据在单个集群下不同节点之间的拷贝称为备份，而数据在集群间的拷贝称为镜像（Mirroring）。

MirrorMaker本质是一个消费者+生产者的程序。消费者负责从源集群（Source Cluster）消费数据，生产者负责向目标集群（Target Cluster）发送消息。





4、其它跨集群数据镜像工具

（1）uReplicator
Uber公司在使用MirrorMaker过程中发现了一些缺陷，比如MirrorMaker中的消费者使用的是消费者组机制，会不可避免地会碰到很多Rebalance问题。因此，Uber研发了uReplicator，使用Apache Helix作为集中式的TOPIC分区管理组件来管理分区的分配，并且重写消费者程序，替代MirrorMaker下的消费者，从而避免Rebalance各种问题。

（2）Brooklin Mirror Maker
针对MirrorMaker工具不易实现管道化的缺陷，LinkedIn进行针对性的改进并对性能进行优化研发了Brooklin Mirror Maker。

（3）Confluent Replicator
Replicator是Confluent提供的企业级的跨集群镜像方案，可以便捷地提供Kafka TOPIC在不同集群间的迁移，同时还能自动在目标集群上创建与源集群上配置一模一样的TOPIC，极大地方便运维管理。




[MirrorMaker简介](https://blog.51cto.com/9291927/2497842#h44)  






