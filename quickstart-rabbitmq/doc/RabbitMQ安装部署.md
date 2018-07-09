
安装后打开浏览器并访问：http://localhost:15672/，并使用默认用户guest登录，密码也为guest。
执行rabbitmq-plugins enable rabbitmq_management命令，开启Web管理插件，这样我们就可以通过浏览器来进行管理了。


Mac OS X安装
在Mac OS X中使用brew工具，可以很容易的安装RabbitMQ的服务端，只需要按如下命令操作即可：
brew更新到最新版本，执行：brew update
安装Erlang，执行：brew install erlang
安装RabbitMQ Server，执行：brew install rabbitmq
通过上面的命令，RabbitMQ Server的命令会被安装到/usr/local/sbin，并不会自动加到用户的环境变量中去，所以我们需要在.bash_profile或.profile文件中增加下面内容：
PATH=$PATH:/usr/local/sbin
这样，我们就可以通过rabbitmq-server命令来启动RabbitMQ的服务端了。



Linux安装
安装：
yum install erlang

rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc
# this example assumes the CentOS 7 version of the package
yum install rabbitmq-server-3.7.0-1.el7.noarch.rpm

启动和停止：
chkconfig rabbitmq-server on

/sbin/service rabbitmq-server start
/sbin/service rabbitmq-server stop


Ubuntu安装
在Ubuntu中，我们可以使用APT仓库来进行安装
安装Erlang，执行：apt-get install erlang
执行下面的命令，新增APT仓库到/etc/apt/sources.list.d
echo 'deb http://www.rabbitmq.com/debian/ testing main' |
        sudo tee /etc/apt/sources.list.d/rabbitmq.list
更新APT仓库的package list，执行sudo apt-get update命令
安装Rabbit Server，执行sudo apt-get install rabbitmq-server命令


Windows安装
安装Erland，通过官方下载页面http://www.erlang.org/downloads获取exe安装包，直接打开并完成安装。
安装RabbitMQ，通过官方下载页面https://www.rabbitmq.com/download.html获取exe安装包。
下载完成后，直接运行安装程序。
RabbitMQ Server安装完成之后，会自动的注册为服务，并以默认配置启动起来。






