查询
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --list --topic quickstart-events

报没有权限，因为使用--bootstrap-server是使用admin client API，必须配置认证信息，比如修改脚并且传--command-config设置认证参数
bin/kafka-acls.sh --list --topic quickstart-events --bootstrap-server localhost:9092

给writer配置发送produce权限
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --allow-principal User:writer --producer --topic quickstart-events --add

给reader配置消费consume权限
bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --allow-principal User:reader --consumer --group test-group --topic quickstart-events --add


bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --allow-principal User:* --operation All --topic * --add

bin/kafka-acls.sh --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:writer --operation Write --topic quickstart-events

bin/kafka-acls.sh --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:writer --operation Write --topic quickstart-events --allow-host 192.168.2.*

bin/kafka-acls.sh --authorizer kafka.security.auth.SimpleAclAuthorizer --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:writer --operation All --topic quickstart-events --allow-host *

bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --operation All --allow-principal User:* --allow-host 192.168.2.* --add --cluster

bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --allow-principal User:writer  --producer --topic quickstart-events --add --cluster



