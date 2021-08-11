#!/bin/bash

#chmod +x queryConnect.sh
#crontab -e
#0 */2 * * * /data/program/kafka/queryConnect.sh


time=$(date "+%Y%m%d-%H%M%S")
netstat -anp | grep 9094 > /data/program/kafka/allConnects$time.log
