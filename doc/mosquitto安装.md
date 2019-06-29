2.1 启动代理服务
mosquitto -v
    【-v】打印更多的调试信息

2.2 订阅主题
mosquitto_sub -v -t sensor
    【-t】指定主题，此处为sensor
    【-v】打印更多的调试信息

2.3 发布内容
mosquitto_pub -t sensor  -m 12
    【-t】指定主题
    【-m】指定消息内容


安装问题：
1、In file included from /usr/include/openssl/ssl.h:165:0,
                 from ./mosquitto_internal.h:27,
                 from mosquitto.c:33:
/usr/include/openssl/kssl.h:72:18: fatal error: krb5.h: No such file or directory

解决：
yum install krb5-devel krb5-libs

再不行就可能是這是因為 Red Hat Linux 9.0 的 krb5-devel 套件把 kerberos 的 include file 放到了 /usr/kerberos/include 裡面，
而不是一般位置的 /usr/include 這個目錄。建立symbolic links
ln -s /usr/kerberos/include/com_err.h /usr/include/ 
ln -s /usr/kerberos/include/profile.h /usr/include/ 
ln -s /usr/kerberos/include/krb5.h /usr/include/

2、In file included from mosquitto.c:33:0:
./mosquitto_internal.h:40:20: fatal error: ares.h: No such file or directory

解决：
yum install c-ares-devel
yum install libc-ares-dev libc-ares2
                                       
3、
read_handle_server.c:31:25: fatal error: uuid/uuid.h: No such file or directory

解决：
 yum install libuuid-devel
 
4、或者直接全部依赖安装
全部依赖：
yum install gcc gcc-c++
yum install openssl-devel
yum install c-ares-devel
yum install libuuid-devel
yum install wget
yum install cmake
yum install build-essential python quilt devscripts python-setuptools python3 
yum install libssl-dev libc-ares-dev uuid-dev daemon openssl-devel

