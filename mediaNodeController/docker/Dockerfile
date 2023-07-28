## Development image
FROM node:18-alpine as development

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

## Production container
FROM node:18-alpine as production

ARG NODE_ENV=production
ENV NODE_ENV=${NODE_ENV}

WORKDIR /usr/src/app

COPY package*.json ./

RUN apk add --update docker openrc \
    && rc-update add docker boot \
    && npm install --only=production

COPY . .

COPY --from=development /usr/src/app/dist ./dist
RUN chmod +x entrypoint.sh \
    && mv entrypoint.sh /usr/local/bin/entrypoint.sh

ENTRYPOINT ["entrypoint.sh"]
