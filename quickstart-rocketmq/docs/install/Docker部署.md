[RocketMQ Docker项目](https://github.com/apache/rocketmq-docker)  



手动部署了4.8.0，测试没有问题


Docker部署，4.6.0的默认开启了安全校验，broker根本注册不进去

apacherocketmq/rocketmq:4.5.0-alpine
注册到namesrv的地址不对，是内部的ip，在本机取到的是内部的比如173.17.0.3这样的，肯定连不上去

最后吐糟一下， 果然阿里的东西容易烂尾，dubbo是，这个也是，官方的docker镜像还没有个人上传的下载量的一般，最高的下载量的没有尝试，应该是没问题的吧



