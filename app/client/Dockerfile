FROM node:lts-alpine
WORKDIR /client
COPY . .
RUN npm install
RUN npm run build

FROM nginx:stable-alpine
COPY --from=0 /client/public /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
