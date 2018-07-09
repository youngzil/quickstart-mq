
mvn install:install-file -Dfile=msgframe-client-2.0.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-client -Dversion=2.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=msgframe-common-2.0.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-common -Dversion=2.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=msgframe-consumer-2.0.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-consumer -Dversion=2.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=msgframe-producer-2.0.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-producer -Dversion=2.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=msgframe-server-2.0.jar -DgroupId=com.ai.aif.msgframe -DartifactId=msgframe-server -Dversion=2.0.0 -Dpackaging=jar


<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-common</artifactId>
	<version>2.0.0</version>
</dependency>

<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-consumer</artifactId>
	<version>2.0.0</version>
</dependency>

<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-producer</artifactId>
	<version>2.0.0</version>
</dependency>

<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-client</artifactId>
	<version>2.0.0</version>
</dependency>

<dependency>
	<groupId>com.ai.aif.msgframe</groupId>
	<artifactId>msgframe-server</artifactId>
	<version>2.0.0</version>
</dependency>