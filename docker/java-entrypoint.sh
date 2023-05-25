#!/bin/sh
cd /opt/video-platform
mv  target/*.jar target/openvidu_db-backend.jar
java  -jar target/openvidu_db-backend.jar
