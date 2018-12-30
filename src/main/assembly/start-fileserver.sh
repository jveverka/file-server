#!/bin/bash

java -Xmx16m -Xmx64m -jar file-server-1.0.1-SNAPSHOT.jar --spring.config.location=file:/opt/file-server/application.yml
