[kafka_exporter Github](https://github.com/danielqsj/kafka_exporter)


Kafka_exporter安装

#下载tar包
kafka_exporter-1.3.1.linux-amd64.tar.gz

#解压
tar -xzvf kafka_exporter-1.3.1.linux-amd64.tar.gz

进入到kafka_exporter目录，启动kafka监控端口：
cd kafka_exporter-1.3.1.linux-amd64

./kafka_exporter --kafka.server=kafkaIP或者域名:9092 &


比如：其实一个集群只要配置一个节点地址也是一个效果，下面其实就是一样的

./kafka_exporter --kafka.server=172.16.48.179:9081 --kafka.server=172.16.48.180:9081 --kafka.server=172.16.48.181:9081 &

./kafka_exporter --kafka.server=172.16.48.179:9081 &


./kafka_exporter --kafka.server=10.1.25.57:9092 &
/data/program/kafka/kafka_exporter-1.3.1.linux-amd64/kafka_exporter --kafka.server=10.1.25.57:9092 &

curl http://10.1.25.57:9308/metrics




[kafka exporter调研与改进](https://cloud.tencent.com/developer/article/1794971)
[Prometheus+Grafana+kafka_exporter监控kafka](https://blog.csdn.net/An1090239782/article/details/102994930)  
[Prometheus监控Kafka](https://blog.csdn.net/baidu_31618421/article/details/106774838)  

