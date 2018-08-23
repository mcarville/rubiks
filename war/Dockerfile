
# FROM dordoka/tomcat

FROM alpine

RUN apk --update add openjdk8

RUN apk --update add openjdk8 unzip wget

RUN mkdir /opt

RUN cd /opt/ && wget http://apache.mirrors.ovh.net/ftp.apache.org/dist/tomcat/tomcat-8/v8.5.33/bin/apache-tomcat-8.5.33.tar.gz && tar -zxf /opt/apache-tomcat-8.5.33.tar.gz && mv apache-tomcat-8.5.33 tomcat && rm apache-tomcat-*

RUN java -version

COPY ./target/rubiks-app.war /opt/tomcat/webapps/

COPY ./VERSION .

CMD ["/opt/tomcat/bin/catalina.sh", "run", "-d"]

Expose 8080