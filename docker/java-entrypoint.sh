#!/bin/sh
cd /opt/videoplatform
java  -jar target/openvidu_db-backend.jar --spring.config.location=application.properties
