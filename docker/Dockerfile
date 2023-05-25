# Build videoplatform for production
FROM node:lts-alpine3.16 as video-platform-build

WORKDIR /

ARG BASE_HREF=/

COPY . .

FROM maven

WORKDIR /opt/videoplatform

COPY --from=video-platform-build /VideoPlatformBackend .

# Install backend dependencies and build it for production
RUN mvn clean install package
RUN mv  target/*.jar target/openvidu_db-backend.jar
# Entrypoint
COPY ./java-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN cat /usr/local/bin/entrypoint.sh
RUN apt-get install curl && \
    chmod +x /usr/local/bin/entrypoint.sh

CMD ["/usr/local/bin/entrypoint.sh"]

