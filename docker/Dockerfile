# Build OpenVidu Call for production
FROM node:lts-alpine3.16 as video-platform-build

WORKDIR /video-platform

ARG BASE_HREF=/

COPY . .

RUN npm install --location=global npm

# Build OpenVidu Call frontend
# RUN rm mcarbonPortal/package-lock.json && \
    # Install frontend dependencies and build it for production
RUN cd mcarbonPortal && npm install --force && \
    npm run prod:build-java ${BASE_HREF} && \
    cd ../ && rm -rf mcarbonPortal

FROM maven

WORKDIR /opt/video-platform

COPY --from=video-platform-call-build /video-platform/video-platform-java .

# Install backend dependencies and build it for production
RUN mvn clean install package

# Entrypoint
COPY ./java-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN cat /usr/local/bin/entrypoint.sh
RUN apt-get install curl && \
    chmod +x /usr/local/bin/entrypoint.sh

CMD ["/usr/local/bin/entrypoint.sh"]

