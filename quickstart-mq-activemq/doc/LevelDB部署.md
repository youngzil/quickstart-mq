1、brokerName配置：主备一致
2、persistenceAdapter配置
3、服务协议和服务端口配置


启动
bin/activemq start
bin/activemq stop


<persistenceAdapter>
  <replicatedLevelDB
   directory="${activemq.data}/leveldb"
     replicas="3"
     bind="tcp://0.0.0.0:0"
     zkAddress="10.20.16.210:2181,10.20.16.210:2182,10.20.16.210:2183"
     hostname="10.20.16.211"
     sync="local_disk"
     zkPath="/activemq/center-poc-g2/leveldb-stores"
  />
</persistenceAdapter>

 <persistenceAdapter>
  <replicatedLevelDB
   directory="${activemq.data}/leveldb"
     replicas="3"
     bind="tcp://0.0.0.0:0"
     zkAddress="10.20.16.210:2181,10.20.16.210:2182,10.20.16.210:2183"
     hostname="10.20.16.211"
     sync="local_disk"
     zkPath="/activemq/center-poc/leveldb-stores"
  />
</persistenceAdapter>