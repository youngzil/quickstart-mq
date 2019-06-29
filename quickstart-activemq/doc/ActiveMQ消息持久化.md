ActiveMQ的消息持久化机制有JDBC，KahaDB和LevelDB，AMQ（废弃）




---------------------------------------------------------------------------------------------------------------------
https://blog.csdn.net/terrymanu/article/details/37566783
ActiveMQ的消息持久化机制有JDBC，KahaDB和LevelDB，AMQ（废弃），


KahaDB
  ActiveMQ 5.3以后，出现了KahaDB。她是一个基于文件支持事务的消息存储器，是一个可靠，高性能，可扩展的消息存储器。
     她的设计初衷就是使用简单并尽可能的快。KahaDB的索引使用一个transaction log，并且所有的destination只使用一个index，有人测试表明：如果用于生产环境，支持1万个active connection，每个connection有一个独立的queue。该表现已经足矣应付大部分的需求。
     KahaDB内部分为：data logs, 按照Message ID高度优化的索引，memory message cache。
     
     data logs扮演一个message journal，存储消息和命令。当大小超过了规定，将会新建一个data log
.所有在data log里的消息是引用计数的，所以当一个log里的消息不在需要了，可以被删除或者放入archived文件夹。每次消息的写入都是在log的末尾增加记录，所以存储速度很快。
     缓存则是临时持有那些有对应消费者在线的消息。如果消费者反馈消息已经成功接收，那么这些消息就不用写入磁盘。
     BTree索引，保存消息的引用，并按照message ID排序。Redo log是用来保证MQ broker未干净关闭情况下，用于Btree index的重建。
     KahaDB的目录会在你启动MQ后自动创建（使用了KAhaDB作为存储器），

     db log files：以db-递增数字.log命名。
     archive directory: 当配置支持archiving(默认不支持)并且存在，该文件夹才会创建。用于存储不再需要的data logs。
     db.data：存储btree索引
     db.redo：用于hard-stop broker后，btree索引的重建








