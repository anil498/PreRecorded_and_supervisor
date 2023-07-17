# Build OpenVidu Call for production
FROM node:lts-alpine3.16 as openvidu-call-build

WORKDIR /openvidu-call

ARG BASE_HREF=session

COPY . .

RUN npm install --location=global npm

# Build OpenVidu Call frontend
RUN rm openvidu-call-front/package-lock.json && \
    # Install frontend dependencies and build it for production
    cd openvidu-call-front && npm install && \
    cd projects/openvidu-angular && npm install && \
    cd ../.. && \
    npm run lib:build && \
    npm install ./dist/openvidu-angular/openvidu-angular-*.tgz && \
    npm run prod:build-java ${BASE_HREF} && \
    cd ../ && rm -rf openvidu-call-front

FROM maven

WORKDIR /opt/openvidu-call

COPY --from=openvidu-call-build /openvidu-call/openvidu-call-back-java .
RUN sed -i 's@href="session"@href=""@' /opt/openvidu-call/src/main/resources/static/index.html
# Install backend dependencies and build it for production
RUN mvn clean install package

# Entrypoint
COPY ./java-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN cat /usr/local/bin/entrypoint.sh
RUN apt-get install curl && \
    chmod +x /usr/local/bin/entrypoint.sh

CMD ["/usr/local/bin/entrypoint.sh"]
WORKDIR /opt/videoplatform/prerecorded
WORKDIR /opt/openvidu/kurento-logs/prerecorded

