
# FROM tomcat:8.0
# FROM ubuntu

FROM debian

RUN apt-get update

RUN apt-get -y install openjdk-8-jdk

RUN apt-get -y install unzip wget

RUN cd /opt/ && wget http://apache.crihan.fr/dist/tomcat/tomcat-8/v8.5.31/bin/apache-tomcat-8.5.31.tar.gz && tar -zxf /opt/apache-tomcat-8.5.31.tar.gz && mv apache-tomcat-8.5.31 tomcat8 && rm apache-tomcat-8.5.31.tar.gz

# RUN apt-get -y install ssh

RUN java -version

COPY ./rubiks-app*.war /opt/tomcat8/webapps/

CMD ["/opt/tomcat8/bin/catalina.sh", "run", "-d"]

Expose 8080

# CMD ["service", "ssh", "start"]

# Expose 22


