FROM openjdk:17-jdk-slim
COPY target/dataservice-app.jar /dataservice-app.jar
ENTRYPOINT java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /dataservice-app.jar
