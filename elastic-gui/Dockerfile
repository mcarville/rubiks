
FROM alpine

RUN apk --update add openjdk8-jre

RUN apk --update add unzip wget

RUN java -version

RUN mkdir /opt

RUN mkdir /opt/elastic-react-gui

COPY ./target/elastic-react-gui-0.1.0.jar /opt/elastic-react-gui/

COPY ./VERSION .

CMD ["java", "-jar", "/opt/elastic-react-gui/elastic-react-gui-0.1.0.jar"]

Expose 8085