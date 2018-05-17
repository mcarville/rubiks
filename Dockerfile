
FROM dordoka/tomcat

RUN java -version

COPY ./target/rubiks-app.war /opt/tomcat/webapps/

CMD ["/opt/tomcat/bin/catalina.sh", "run", "-d"]

Expose 8080