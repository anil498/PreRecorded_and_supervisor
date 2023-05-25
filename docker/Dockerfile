# Build videoplatform for production
FROM node:lts-alpine3.16 as video-platform-build

WORKDIR /

ARG BASE_HREF=/

COPY . .

FROM maven

WORKDIR /opt/video-platform

COPY --from=video-platform-build /VideoPlatformBackend .

# Install backend dependencies and build it for production
RUN mvn clean install package

# Entrypoint
COPY ./java-entrypoint.sh /usr/local/bin/entrypoint.sh
RUN cat /usr/local/bin/entrypoint.sh
RUN apt-get install curl && \
    chmod +x /usr/local/bin/entrypoint.sh

CMD ["/usr/local/bin/entrypoint.sh"]

