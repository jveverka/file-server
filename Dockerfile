ARG ARCH="amd64"
FROM ${ARCH}/adoptopenjdk:11-jre
COPY build/libs/file-server-*-SNAPSHOT.jar /file-server.jar
COPY file-server-start.sh /file-server-start.sh
COPY src/main/resources/application.yml /application.yml
RUN chmod +x /file-server-start.sh
ENV XMX 128m
ENV APP_CONFIG_PATH /application.yml
ENV JVM_OPTS ""
ENTRYPOINT ["/file-server-start.sh"]