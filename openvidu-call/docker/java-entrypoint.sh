#!/bin/sh

[[ -z "${OPENVIDU_URL}" ]] && export OPENVIDU_URL=$(curl -s ifconfig.co)
[[ -z "${OPENVIDU_SECRET}" ]] && export OPENVIDU_SECRET=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)

cd /opt/openvidu-call

[ ! -z "${OPENVIDU_URL}" ] && JAVA_PROPERTIES=" -DOPENVIDU_URL=${OPENVIDU_URL}"
[ ! -z "${OPENVIDU_SECRET}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DOPENVIDU_SECRET=${OPENVIDU_SECRET}"
[ ! -z "${SERVER_PORT}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -Dserver.port=${SERVER_PORT}"
[ ! -z "${CALL_USER}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DCALL_USER=${CALL_USER}"
[ ! -z "${CALL_SECRET}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DCALL_SECRET=${CALL_SECRET}"
[ ! -z "${CALL_ADMIN_SECRET}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DCALL_ADMIN_SECRET=${CALL_ADMIN_SECRET}"
[ ! -z "${CALL_RECORDING}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DRECORDING=${CALL_RECORDING}"
[ ! -z "${SERVER_PORT}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -Dserver.port=${SERVER_PORT}"
[ ! -z "${Authorization}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DAuthorization=${Authorization}"
[ ! -z "${Token}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -DToken=${Token}"
[ ! -z "${UPLOAD_PATH}" ] && JAVA_PROPERTIES=" ${JAVA_PROPERTIES} -Dfile.upload-dir=${UPLOAD_PATH}"

java ${JAVA_PROPERTIES} -jar target/openvidu-call-back-java.jar
