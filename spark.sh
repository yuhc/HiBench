#!/bin/bash

sudo /root/spark-1.5.2/sbin/stop-slave.sh
sleep 1
sudo /root/spark-1.5.2/sbin/stop-master.sh
sleep 1
sudo /root/spark-1.5.2/sbin/start-master.sh
sleep 1
sudo /root/spark-1.5.2/sbin/start-slave.sh spark://osa-hadoop:7077
