FROM wurstmeister/kafka

ADD prom-jmx-agent-config.yml /usr/app/prom-jmx-agent-config.yml
ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.16.1/jmx_prometheus_javaagent-0.16.1.jar /usr/app/jmx_prometheus_javaagent.jar

#构建
#docker build -t quickstart/kafka:2.0 .
#docker build -t quickstart/kafka:2.0 /home/zyt/tomcat-docker/Dockerfile .
