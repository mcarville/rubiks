
# FROM debian

# RUN apt-get update

# RUN apt-get -y install openjdk-8-jdk

FROM alpine

RUN apk --update add openjdk8-jre

RUN java -version

COPY ./target/rubiks-cube-robot-jar-with-dependencies.jar /opt/rubiks-cube/

COPY ./VERSION .

CMD ["java", "-jar", "/opt/rubiks-cube/rubiks-cube-robot-jar-with-dependencies.jar", "&"]
