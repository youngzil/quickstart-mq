这是生产者的代码 在向服务器发起连接后，在kafka的服务器配置中有zookeeper.connect=xx.xx.xx.xx：2181的配置 这时候kafka会查找zookeeper


解决办法：配置hosts文件 做zookeeper服务器的映射配置。
出现此种错误 还有一种情况
# Hostname the broker will advertise to producers and consumers. If not set, it uses the
# value for "host.name" if configured.  Otherwise, it will use the value returned from
# java.net.InetAddress.getCanonicalHostName().
advertised.host.name=192.168.1.118
远程连接的话 是根据这个配置来找broker的，默认是localhost ，所以如果不是本机运行的话 应该设置此值 来确保通信畅通。


