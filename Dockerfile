FROM adoptopenjdk/openjdk11:latest
ADD target/go-0.0.1-SNAPSHOT.jar go-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/go-0.0.1-SNAPSHOT.jar"]
#FROM maven:3.6.0-jdk-13
#
#RUN useradd -m -u 1000 -s /bin/bash jenkins
#
#RUN yum install -y openssh-clients