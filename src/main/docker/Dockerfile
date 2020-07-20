FROM openjdk:8u171-jre-alpine
ENV JAR_NAME=jenkinsDocker-1.0-SNAPSHOT.jar
COPY ./target/$JAR_NAME  /apps/srv/instance/$JAR_NAME
EXPOSE 80
#ENTRYPOINT ["java","-jar","/apps/srv/instance/jenkinsDocker-1.0-SNAPSHOT.jar","--server.port=80"]
ENTRYPOINT java -jar /apps/srv/instance/$JAR_NAME --server.port=80
