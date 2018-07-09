上传jar到本地仓库脚本：
mvn install:install-file -Dfile=msgframe-common-1.7.3.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-common -Dversion=1.7.3 -Dpackaging=jar 
mvn install:install-file -Dfile=msgframe-producer-1.7.3.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-producer -Dversion=1.7.3 -Dpackaging=jar 
mvn install:install-file -Dfile=msgframe-consumer-1.7.3.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-consumer -Dversion=1.7.3 -Dpackaging=jar 
