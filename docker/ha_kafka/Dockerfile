
FROM debian

RUN apt-get update

RUN apt-get install -y procps

RUN apt-get -y install openjdk-8-jdk

RUN java -version

COPY kafka /opt/kafka/

CMD ["/opt/kafka/start_all.sh", "-d"]

Expose 2181
Expose 9092
Expose 9000