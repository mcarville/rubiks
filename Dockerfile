
FROM dordoka/tomcat

RUN java -version

COPY ./war/target/rubiks-app.war /opt/tomcat/webapps/

CMD ["/opt/tomcat/bin/catalina.sh", "run", "-d"]

Expose 8080