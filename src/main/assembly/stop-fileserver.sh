#!/bin/bash

FILE_SERVER_PID=`ps -ef | grep java | grep file-server- | awk '{print $2}'`
kill ${FILE_SERVER_PID}
