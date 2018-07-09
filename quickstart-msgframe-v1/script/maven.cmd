
mvn install:install-file -Dfile=msgframe-common-1.7.3.jar
-DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-common
-Dversion=1.7.3 -Dpackaging=jar
mvn install:install-file
-Dfile=msgframe-consumer-1.7.3.jar
-DgroupId=com.ai.aif.msgframe
-DartifactId=msgframe-consumer
-Dversion=1.7.3 -Dpackaging=jar
mvn
install:install-file -Dfile=msgframe-producer-1.7.3.jar
-DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-producer
-Dversion=1.7.3 -Dpackaging=jar


<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-common</artifactId>
	<version>1.7.3</version>
</dependency>

<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-consumer</artifactId>
	<version>1.7.3</version>
</dependency>

<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-producer</artifactId>
	<version>1.7.3</version>
</dependency>