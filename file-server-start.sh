#!/bin/sh

echo "starting file-server ..."
echo "APP_CONFIG_PATH=${APP_CONFIG_PATH}"

if [ "${APP_CONFIG_PATH}" = "false" ]; then
  echo "using default configuration"
  echo "SERVER_PORT=${SERVER_PORT}"
  echo "XMX=${XMX}"
  java -Xms32m -Xmx${XMX} ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /file-server.jar \
     --server.port=${SERVER_PORT}
else
  echo "using custom configuration"
  echo "APP_CONFIG_PATH=${APP_CONFIG_PATH}"
  echo "XMX=${XMX}"
  java -Xms32m -Xmx${XMX} ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /file-server.jar \
     --spring.config.location=file:${APP_CONFIG_PATH}
fi
