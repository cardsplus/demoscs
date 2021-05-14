FROM node:alpine
ARG SERVER_URL
ENV SERVER_URL=${SERVER_URL}
RUN env
WORKDIR /client
COPY ./package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx
RUN env
EXPOSE 5000
COPY --from=0 /client/public /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
