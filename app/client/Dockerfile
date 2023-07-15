FROM node:lts-alpine
ENV TZ=Europe/Vienna
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime && \
    echo ${TZ} > /etc/timezone
WORKDIR /client
COPY ./package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx
ENV TZ=Europe/Vienna
EXPOSE 5000
WORKDIR /usr/share/nginx/html
COPY --from=0 /client/build/generated ./
COPY nginx.conf /etc/nginx/conf.d/default.conf
